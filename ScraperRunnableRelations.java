package jarachnea;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.LogManager;
import java.util.Random;
import java.util.regex.Pattern;

class ScraperRunnableRelations implements Runnable {
    private static final int PROFILE_TOOT_AGE_CUTOFF_DAYS = 7;
    private DataStore dataStoreObj;
    private Client clientObj;
    private Logger loggerObj;

    private static final Pattern PROFILE_URL_REGEX = Pattern.compile(
                                                     "^https://[A-Za-z0-9_.-]+\\.[a-z]+/@[A-Za-z0-9_.-]+$");
    private static final Pattern FOLLOWING_URL_REGEX = Pattern.compile(
                                                       "^https://[A-Za-z0-9_.-]+\\.[a-z]+/users/[A-Za-z0-9_.-]+/following(?:\\?page=[0-9]+)?$");
    private static final Pattern FOLLOWERS_URL_REGEX = Pattern.compile(
                                                       "^https://[A-Za-z0-9_.-]+\\.[a-z]+/users/[A-Za-z0-9_.-]+/followers(?:\\?page=[0-9]+)?$");

    private ConcurrentHashMap<String, Instance> instanceMap;
    private ConcurrentLinkedQueue<Handle> unfetchedHandlesQueue;

    public ScraperRunnableRelations(final ConcurrentHashMap<String, Instance> instanceMapObj,
                                    final ConcurrentLinkedQueue<Handle> unfetchedHandlesQueueObj,
                                    final String host, final String username, final String password,
                                    final String database) throws IOException, SQLException {

        Handler[] existingHandlers;
        ConsoleHandler handlerObj;

        LogManager.getLogManager().reset();
        loggerObj = Logger.getLogger("scraper_" + random4CharHexString());
        loggerObj.setLevel(Level.INFO);
        handlerObj = new ConsoleHandler();
        handlerObj.setFormatter(new LogFormatter());
        loggerObj.addHandler(handlerObj);

        dataStoreObj = new DataStore(host, username, password, database);
        clientObj = new Client();

        instanceMap = instanceMapObj;
        unfetchedHandlesQueue = unfetchedHandlesQueueObj;
    }

    public void run() {
        PageInterpreter profilePageInterpreterObj;
        PageInterpreter followingPageInterpreterObj;
        PageInterpreter followersPageInterpreterObj;
        String[] sqlColumns = {"profile_handle_id"};
        String[] sqlWhereKeys = {"username", "instance"};
        Profile profileObj;

        handleloop: while (unfetchedHandlesQueue.size() > 0) {
            Handle nextHandleObj;
            Instance instanceObj;
            Response responseObj;
            String profileBioStr;
            String[] sqlWhereValues = new String[2];
            URL nextFollowersURL;
            URL nextFollowingURL;
            URL profileURL;
            float rateLimitSecondsLeft;
            int parsingOutcomeFlag;
            int statusCode;

            nextHandleObj = unfetchedHandlesQueue.poll();
            loggerObj.log(Level.INFO, "loaded handle " + nextHandleObj.toHandle() + " from database");

            instanceObj = getOrPutInstanceWithMap(nextHandleObj.getInstance());

            if (instanceObj.getInstanceStatus() > 0) {
                loggerObj.log(Level.INFO, "instance " + instanceObj.getInstanceHostname() + " has "
                                          + instanceObj.getInstanceStatusString().toLowerCase() + " status, skipping....");

                try {
                    profileObj = new Profile(nextHandleObj, true, "");
                } catch (MalformedURLException exceptionObj) {
                    loggerObj.log(Level.SEVERE, "instantiating profile from " + nextHandleObj.toHandle() + " failed with malformed url error: "
                                                + exceptionObj.getMessage());
                    continue;
                }

                try {
                    dataStoreObj.storeProfile(profileObj);
                    loggerObj.log(Level.INFO, "stored handle " + profileObj.getProfileHandle().toHandle() + " null bio in database");
                } catch (SQLException exceptionObj) {
                    loggerObj.log(Level.SEVERE, "storing profile " + profileObj.getProfileHandle().toHandle() + " bio to database"
                                                + " failed with SQL error: " + exceptionObj.getMessage());
                }

                continue;
            } else if (!instanceObj.rateLimitExpiredYet()) {
                rateLimitSecondsLeft = instanceObj.rateLimitRemainingSeconds();
                loggerObj.log(Level.INFO, "instance access has been rate-limited, " + rateLimitSecondsLeft
                                          + " seconds remaining, saving for later....");

                unfetchedHandlesQueue.add(nextHandleObj);
                continue;
            }

            try {
                profileURL = nextHandleObj.toProfileURL();
            } catch (ProcessingException exceptionObj) {
                loggerObj.log(Level.SEVERE, "generating profile URL for handle " + nextHandleObj.toHandle()
                                            + " failed with processing error: " + exceptionObj.getMessage());
                continue;
            }

            try {
                loggerObj.log(Level.INFO, "fetching profile page url for " + nextHandleObj.toHandle() + ": " + profileURL.toString());
                profilePageInterpreterObj = fetchAndParseURL(profileURL, nextHandleObj);
            } catch (ProcessingException exceptionObj) {
                loggerObj.log(Level.SEVERE, "fetching url " + profileURL + " failed with processing error: " + exceptionObj.getMessage());
                continue;
            } catch (IOException exceptionObj) {
                loggerObj.log(Level.INFO, "fetching url " + profileURL + " failed with IO error: " + exceptionObj.getMessage() + "; saving for later....");
                unfetchedHandlesQueue.add(nextHandleObj);
                continue;
            }

            if (profilePageInterpreterObj == null) {
                loggerObj.log(Level.INFO, "interpreting profile from " + profileURL + " failed");
                continue;
            }

            profileBioStr = profilePageInterpreterObj.getProfileBio();
            if (profileBioStr != null) {
                loggerObj.log(Level.INFO, "at URL " + profileURL.toString() + " found profile bio, length " + profileBioStr.length() + " chars");

                try {
                    profileObj = new Profile(profilePageInterpreterObj.getUserHandle(), false, profileBioStr);
                } catch (MalformedURLException exceptionObj) {
                    loggerObj.log(Level.SEVERE, "instantiating profile from " + profileURL + " failed with malformed url error: "
                                                + exceptionObj.getMessage());
                    continue;
                }

                try {
                    dataStoreObj.storeProfile(profileObj);
                    loggerObj.log(Level.INFO, "stored handle " + profileObj.getProfileHandle().toHandle() + " bio, length " + profileBioStr.length() + " in database");
                } catch (SQLException exceptionObj) {
                    loggerObj.log(Level.INFO, "storinghandle " + profileObj.getProfileHandle().toHandle() + " bio to database failed with SQL error: "
                                              + exceptionObj.getMessage());
                }
            }

            nextFollowingURL = profilePageInterpreterObj.getFollowingPageURL();

            if (nextFollowingURL != null) {
                loggerObj.log(Level.INFO, "generated following page url for " + nextHandleObj.toHandle() + ": " + nextFollowingURL.toString());

                while (nextFollowingURL != null) {
                    try {
                        loggerObj.log(Level.INFO, "fetching following page url for " + nextHandleObj.toHandle() + ": " + nextFollowingURL.toString());
                        followingPageInterpreterObj = fetchAndParseURL(nextFollowingURL, nextHandleObj);
                    } catch (ProcessingException exceptionObj) {
                        loggerObj.log(Level.SEVERE, "fetching url " + nextFollowingURL.toString() + " failed with malformed url error: "
                                                    + exceptionObj.getMessage());
                        break;
                    } catch (IOException exceptionObj) {
                        loggerObj.log(Level.INFO, "fetching url " + nextFollowingURL.toString() + " failed with IO error: "
                                                  + exceptionObj.getMessage() + "; saving for later....");
                        unfetchedHandlesQueue.add(nextHandleObj);
                        break;
                    }

                    if (followingPageInterpreterObj == null) {
                        break;
                    }

                    try {
                        dataStoreObj.storeRelationSet(followingPageInterpreterObj.getRelationSet());
                        loggerObj.log(Level.INFO, "retrieved " + followingPageInterpreterObj.getRelationSet().size() + " handles from following page "
                                                  + nextFollowingURL.toString() + "; stored to database");
                    } catch (SQLException exceptionObj) {
                        loggerObj.log(Level.SEVERE, "storing following page " + nextFollowingURL.toString() + " failed with SQL error: "
                                                    + exceptionObj.getMessage());
                        break;
                    }

                    if (followingPageInterpreterObj.getPageInterpretationOutcome() == PageInterpreter.FOUND_NO_NEXT_PAGE_URL) {
                        break;
                    }

                    nextFollowingURL = followingPageInterpreterObj.getNextPageURL();
                    loggerObj.log(Level.INFO, "generated following page url for " + nextHandleObj.toHandle() + ": " + nextFollowingURL.toString());
                }
            }

            nextFollowersURL = profilePageInterpreterObj.getFollowersPageURL();

            if (nextFollowersURL != null) {
                loggerObj.log(Level.INFO, "generated followers page url for " + nextHandleObj.toHandle() + ": " + nextFollowersURL.toString());

                while (nextFollowersURL != null) {
                    try {
                        loggerObj.log(Level.INFO, "fetching followers page url for " + nextHandleObj.toHandle() + ": " + nextFollowersURL.toString());
                        followersPageInterpreterObj = fetchAndParseURL(nextFollowersURL, nextHandleObj);
                    } catch (ProcessingException exceptionObj) {
                        loggerObj.log(Level.SEVERE, "fetching url " + nextFollowersURL.toString()
                                                    + " failed with processing error: " + exceptionObj.getMessage());
                        break;
                    } catch (IOException exceptionObj) {
                        loggerObj.log(Level.INFO, "fetching url " + nextFollowersURL.toString() + " failed with IO error: "
                                                  + exceptionObj.getMessage() + "; saving for later....");
                        unfetchedHandlesQueue.add(nextHandleObj);
                        break;
                    }

                    if (followersPageInterpreterObj == null) {
                        break;
                    }

                    try {
                        dataStoreObj.storeRelationSet(followersPageInterpreterObj.getRelationSet());
                        loggerObj.log(Level.SEVERE, "retrieved " + followersPageInterpreterObj.getRelationSet().size()
                                                    + " handles from followers page " + nextFollowersURL.toString()
                                                    + "; stored to database");
                    } catch (SQLException exceptionObj) {
                        loggerObj.log(Level.SEVERE, "storing followers page " + nextFollowersURL.toString()
                                                    + " failed with SQL error: " + exceptionObj.getMessage());
                        break;
                    }

                    if (followersPageInterpreterObj.getPageInterpretationOutcome() == PageInterpreter.FOUND_NO_NEXT_PAGE_URL) {
                        break;
                    }

                    nextFollowersURL = followersPageInterpreterObj.getNextPageURL();
                    loggerObj.log(Level.INFO, "generated followers page url for " + nextHandleObj.toHandle() + ": " + nextFollowersURL.toString());
                }
            }
        }
    }

    private PageInterpreter fetchAndParseURL(final URL mastodonURL, final Handle nextHandleObj) throws ProcessingException, IOException {
        PageInterpreter pageInterpreterObj;
        Profile profileObj;
        Response responseObj;
        String instanceHost;
        String mastodonURLString;
        int statusCode;
        int parsingOutcomeFlag;

        mastodonURLString = mastodonURL.toString();
        instanceHost = mastodonURL.getHost();
        try {
            responseObj = clientObj.retrieveUrl(mastodonURLString);
        } catch (IllegalArgumentException exceptionObj) {
            loggerObj.log(Level.WARNING, "tried to load page at URL " + mastodonURL.toString()
                                         + ", got illegal argument exception: " + exceptionObj.getMessage());
            return null;
        } catch (NullPointerException exceptionObj) {
            loggerObj.log(Level.WARNING, "tried to load page at URL " + mastodonURL.toString()
                                         + ", got null pointer exception: " + exceptionObj.getMessage());
            return null;
        }

        statusCode = responseObj.getStatusCode();

        if (statusCode == 429) {
            int rateLimitSeconds;

            loggerObj.log(Level.INFO, "tried to load page at URL " + responseObj.getRequestURL().toString()
                                      + ", got status code " + statusCode);

            rateLimitSeconds = responseObj.getXRatelimitLimit();

            Instance instanceObj;

            instanceObj = getOrPutInstanceWithMap(instanceHost);
            instanceObj.setRateLimit(rateLimitSeconds);

            return null;
        } else if (statusCode != 200) {
            loggerObj.log(Level.INFO, "tried to load page at URL " + responseObj.getRequestURL().toString()
                                      + ", got status code " + statusCode);

            try {
                profileObj = new Profile(nextHandleObj, true, "");
            } catch (MalformedURLException exceptionObj) {
                loggerObj.log(Level.SEVERE, "instantiating profile from " + nextHandleObj.toHandle() + " failed with malformed url error: "
                                            + exceptionObj.getMessage());
                return null;
            }

            try {
                dataStoreObj.storeProfile(profileObj);
                loggerObj.log(Level.INFO, "stored handle " + profileObj.getProfileHandle().toHandle() + " null bio in database");
            } catch (SQLException exceptionObj) {
                loggerObj.log(Level.SEVERE, "storing profile " + profileObj.getProfileHandle().toHandle() + " bio to database"
                                            + " failed with SQL error: " + exceptionObj.getMessage());
            }

            if ((statusCode >= 500 && statusCode < 600) || statusCode == 400 || statusCode == 401 || statusCode == 403 || statusCode == 406) {
                Instance instanceObj;
                loggerObj.log(Level.INFO, "instance " + nextHandleObj.getInstance() + " is malfunctioning");
//
//                instanceObj = getOrPutInstanceWithMap(instanceHost);
//                instanceObj.setInstanceStatus(Instance.MALFUNCTIONING);
//                try {
//                    dataStoreObj.storeInstance(instanceObj);
//                    loggerObj.log(Level.INFO, "stored instance " + instanceObj.getInstanceHostname() +
//                                              " malfunctioning status to database");
//                } catch (SQLException exceptionObj) {
//                    ;
//                }

                return null;
            } else if (statusCode == 404 || statusCode == 410) {
                loggerObj.log(Level.INFO, "user " + nextHandleObj.toHandle() + " has been deleted from instance; skipping....");
                return null;
            } else {
                loggerObj.log(Level.INFO, "unrecognized status code" + statusCode + "; skipping....");
                return null;
            }
        } else {
            int pageTypeFlag;

            loggerObj.log(Level.INFO, "loaded page at URL " + responseObj.getRequestURL().toString()
                                      + ", got status code " + statusCode);

            if (PROFILE_URL_REGEX.matcher(mastodonURLString).matches()) {
                pageTypeFlag = PageInterpreter.PROFILE_PAGE;
            } else if (FOLLOWING_URL_REGEX.matcher(mastodonURLString).matches()) {
                pageTypeFlag = PageInterpreter.FOLLOWING_PAGE;
            } else if (FOLLOWERS_URL_REGEX.matcher(mastodonURLString).matches()) {
                pageTypeFlag = PageInterpreter.FOLLOWERS_PAGE;
            } else {
                throw new ProcessingException("unable to match url " + mastodonURLString + " against known url type regexes");
            }

            pageInterpreterObj = new PageInterpreter(responseObj.getBodyDocument(), nextHandleObj, pageTypeFlag, PROFILE_TOOT_AGE_CUTOFF_DAYS);
            try {
                parsingOutcomeFlag = pageInterpreterObj.interpretPage();
            } catch (ProcessingException exceptionObj) {
                throw new ProcessingException("unable interpret page " + mastodonURLString + "; failed with error: " + exceptionObj.getMessage());
            }

            if (parsingOutcomeFlag == PageInterpreter.PAGE_TIMES_UNPARSEABLE
                || parsingOutcomeFlag == PageInterpreter.PAGE_FORWARDING_UNPARSEABLE
                || parsingOutcomeFlag == PageInterpreter.PAGE_BIO_UNPARSEABLE) {
                Instance instanceObj;

                try {
                    profileObj = new Profile(nextHandleObj, true, "");
                } catch (MalformedURLException exceptionObj) {
                    loggerObj.log(Level.SEVERE, "instantiating profile from " + nextHandleObj.toHandle() + " failed with malformed url error: "
                                                + exceptionObj.getMessage());
                    return null;
                }

                try {
                    dataStoreObj.storeProfile(profileObj);
                    loggerObj.log(Level.INFO, "stored handle " + profileObj.getProfileHandle().toHandle() + " null bio in database");
                } catch (SQLException exceptionObj) {
                    loggerObj.log(Level.SEVERE, "storing profile " + profileObj.getProfileHandle().toHandle() + " bio to database"
                                                + " failed with SQL error: " + exceptionObj.getMessage());
                }

                instanceObj = getOrPutInstanceWithMap(instanceHost);
//                instanceObj.setInstanceStatus(Instance.MALFUNCTIONING);
                loggerObj.log(Level.INFO, "tried to parse page at URL " + responseObj.getRequestURL().toString()
                                          + ", unable to parse");

                try {
                    dataStoreObj.storeInstance(instanceObj);
                    loggerObj.log(Level.INFO, "stored instance " + instanceObj.getInstanceHostname()
                                              + " unparseable status to database");
                } catch (SQLException exceptionObj) {
                    loggerObj.log(Level.INFO, "storinginstance " + instanceObj.getInstanceHostname()
                                              + " unparseable status to database failed with SQL error: "
                                              + exceptionObj.getMessage());
                }
            } else if (parsingOutcomeFlag == PageInterpreter.PAGE_IS_FORWARDING_PAGE) {
                Handle forwardingHandle;

                try {
                    profileObj = new Profile(nextHandleObj, true, "");
                } catch (MalformedURLException exceptionObj) {
                    loggerObj.log(Level.SEVERE, "instantiating profile from " + nextHandleObj.toHandle() + " failed with malformed url error: "
                                                + exceptionObj.getMessage());
                    return null;
                }

                try {
                    dataStoreObj.storeProfile(profileObj);
                    loggerObj.log(Level.INFO, "stored handle " + profileObj.getProfileHandle().toHandle() + " null bio in database");
                } catch (SQLException exceptionObj) {
                    loggerObj.log(Level.SEVERE, "storing profile " + profileObj.getProfileHandle().toHandle() + " bio to database"
                                                + " failed with SQL error: " + exceptionObj.getMessage());
                }

                loggerObj.log(Level.INFO, "page at URL " + responseObj.getRequestURL().toString()
                                          + " is a forwarding page");
                forwardingHandle = pageInterpreterObj.getForwardingAddressHandle();

                try {
                    dataStoreObj.storeHandle(forwardingHandle);
                    loggerObj.log(Level.INFO, "stored handle " + forwardingHandle.toHandle() + " to database");
                } catch (SQLException exceptionObj) {
                    loggerObj.log(Level.INFO, "storinghandle " + forwardingHandle.toHandle() + " to database failed with SQL error: "
                                  + exceptionObj.getMessage());
                }
                unfetchedHandlesQueue.add(forwardingHandle);
                loggerObj.log(Level.INFO, "stored new handle " + forwardingHandle.toHandle() + " in database");
            } else if (parsingOutcomeFlag == PageInterpreter.PAGE_HAS_NO_POSTS || parsingOutcomeFlag == PageInterpreter.PAGE_POSTS_OUT_OF_DATE) {
                try {
                    profileObj = new Profile(nextHandleObj, true, "");
                } catch (MalformedURLException exceptionObj) {
                    loggerObj.log(Level.SEVERE, "instantiating profile from " + nextHandleObj.toHandle() + " failed with malformed url error: "
                                                + exceptionObj.getMessage());
                    return null;
                }

                if (parsingOutcomeFlag == PageInterpreter.PAGE_HAS_NO_POSTS) {
                    loggerObj.log(Level.INFO, "page at URL " + responseObj.getRequestURL().toString()
                                              + " has no public posts");
                } else {
                    loggerObj.log(Level.INFO, "page at URL " + responseObj.getRequestURL().toString()
                                              + " has no posts more recent than " + PROFILE_TOOT_AGE_CUTOFF_DAYS + " days");
                }

                try {
                    dataStoreObj.storeProfile(profileObj);
                    loggerObj.log(Level.INFO, "stored handle " + profileObj.getProfileHandle().toHandle() + " null bio in database");
                } catch (SQLException exceptionObj) {
                    loggerObj.log(Level.SEVERE, "storing profile " + profileObj.getProfileHandle().toHandle() + " bio to database"
                                                + " failed with SQL error: " + exceptionObj.getMessage());
                }
            } else if (parsingOutcomeFlag != PageInterpreter.FOUND_PAGE_BIO
                       && parsingOutcomeFlag != PageInterpreter.FOUND_NEXT_PAGE_URL
                       && parsingOutcomeFlag != PageInterpreter.FOUND_NO_NEXT_PAGE_URL) {
                throw new ProcessingException("got unrecognized PageInterpreter flag of int value " + parsingOutcomeFlag);
            }

            return pageInterpreterObj;
        }
    }

    private Instance getOrPutInstanceWithMap(final String instanceHost) {
        Instance instanceObj;
        String instanceStatusStr;

        if (instanceMap.containsKey(instanceHost)) {
            instanceObj = instanceMap.get(instanceHost);
        } else {
            instanceObj = new Instance(instanceHost, Instance.IN_GOOD_STANDING);
            instanceMap.put(instanceHost, instanceObj);
        }

        instanceStatusStr = instanceObj.getInstanceStatusString();
        loggerObj.log(Level.INFO, "loaded instance " + instanceHost + ", status " + instanceStatusStr.toLowerCase().replace('_', ' '));

        return instanceObj;
    }

    private String random4CharHexString() {
        Random randomObj;
        int numChars;
        StringBuffer stringBufferObj;

        randomObj = new Random();
        numChars = 4;
        stringBufferObj = new StringBuffer();

        while (stringBufferObj.length() < numChars) {
            stringBufferObj.append(Integer.toHexString(randomObj.nextInt()));
        }

        return stringBufferObj.toString().substring(0, numChars);
    }
}
