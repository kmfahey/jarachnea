package jarachnea;

import java.io.IOException;
import java.lang.Math;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.GregorianCalendar;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.LogManager;
import java.util.Random;
import java.util.regex.Pattern;

class ScraperLoop {
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

    public ScraperLoop(final ConcurrentHashMap<String, Instance> instanceMapObj, final ConcurrentLinkedQueue<Handle> unfetchedHandlesQueueObj,
                       final String host, final String username, final String password, final String database) throws IOException, SQLException {

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

    public void executeMainLoop(final boolean saveRelations) {
        ArrayList<Float> processingRateHistory;
        GregorianCalendar calendarObj;
        PageInterpreter profilePageInterpreterObj;
        float mostRecentHandlesQty;
        float secondMostRecentHandlesQty;
        Integer secondMostRecentMinute;
        int mostRecentMinute;
        float processingRate;
        float processingRateTotal;
        float projectedHoursRemaining;
        float projectedMinutesRemaining;
        float processingRateAverage;

        processingRateHistory = new ArrayList<Float>();
        calendarObj = new GregorianCalendar();
        secondMostRecentMinute = null;
        mostRecentMinute = calendarObj.get(Calendar.MINUTE);
        secondMostRecentHandlesQty = unfetchedHandlesQueue.size();
        mostRecentHandlesQty = unfetchedHandlesQueue.size();

        while (unfetchedHandlesQueue.size() > 0) {
            Handle nextHandleObj;
            Instance instanceObj;
            URL nextFollowersURL;
            URL nextFollowingURL;
            URL profileURL;

            nextHandleObj = unfetchedHandlesQueue.poll();

            loggerObj.log(Level.INFO, "loaded handle " + nextHandleObj.toHandle() + " from queue");

            instanceObj = getOrPutInstanceWithMap(nextHandleObj.getInstance());

            profileURL = handleInstancePreprocessing(instanceObj, nextHandleObj);

            if (profileURL == null) {
                continue;
            }

            profilePageInterpreterObj = handleProcessingProfile(profileURL, nextHandleObj);

            if (saveRelations) {
                nextFollowingURL = profilePageInterpreterObj.getFollowingPageURL();

                processRelations(nextFollowingURL, "following", nextHandleObj);

                nextFollowersURL = profilePageInterpreterObj.getFollowersPageURL();

                processRelations(nextFollowersURL, "followers", nextHandleObj);
            }

            calendarObj = new GregorianCalendar();
            mostRecentMinute = calendarObj.get(Calendar.MINUTE);

            if (secondMostRecentMinute == null) {
                secondMostRecentMinute = mostRecentMinute;
                mostRecentMinute = calendarObj.get(Calendar.MINUTE);
                continue;
            } else if (mostRecentMinute > secondMostRecentMinute) {
                secondMostRecentMinute = mostRecentMinute;
                secondMostRecentHandlesQty = mostRecentHandlesQty;
                mostRecentHandlesQty = unfetchedHandlesQueue.size();
                processingRate = secondMostRecentHandlesQty - mostRecentHandlesQty;
                processingRateHistory.add((float) processingRate);
                processingRateTotal = 0;

                for (int index = 0; index < processingRateHistory.size(); index++) {
                    processingRateTotal += processingRateHistory.get(index);
                }

                processingRateAverage = processingRateTotal / ((float) processingRateHistory.size());

                if (processingRateHistory.size() > 1) {
                    loggerObj.log(Level.INFO, "completed " + ((int) processingRate) + " records in the past minute;"
                                              + " average rate over the last " + processingRateHistory.size()
                                              + " minutes is " + String.format("%.3f", processingRateAverage)
                                              + " records per minute");
                } else {
                    loggerObj.log(Level.INFO, "completed " + processingRate + " records in the past minute");
                }

                projectedMinutesRemaining = mostRecentHandlesQty / processingRateAverage;

                if (projectedMinutesRemaining > 60) {
                    projectedHoursRemaining = projectedMinutesRemaining / 60.0F;
                    projectedMinutesRemaining = projectedMinutesRemaining % 60.0F;
                    loggerObj.log(Level.INFO, ((int) mostRecentHandlesQty) + " records remaining; projected time until completion "
                                  + ((int) projectedHoursRemaining) + "h" + ((int) projectedMinutesRemaining) + "m");
                } else {
                    loggerObj.log(Level.INFO, mostRecentHandlesQty + " records remaining; projected time until completion "
                                  + ((int) projectedMinutesRemaining) + "min");
                }
            }
        }
    }

    private final URL handleInstancePreprocessing(final Instance instanceObj, final Handle nextHandleObj) {
        Profile profileObj;
        URL profileURL;
        float rateLimitSecondsLeft;

        if (instanceObj.getInstanceStatus() > 0) {
            loggerObj.log(Level.INFO, "instance " + instanceObj.getInstanceHostname() + " has "
                                      + instanceObj.getInstanceStatusString().toLowerCase() + " status, storing null bio to database");

            try {
                profileObj = new Profile(nextHandleObj, true, "");
            } catch (MalformedURLException exceptionObj) {
                loggerObj.log(Level.WARNING, "instantiating profile from " + nextHandleObj.toHandle() + " failed with malformed url error: "
                                            + exceptionObj.getMessage());
                return null;
            }

            try {
                dataStoreObj.storeProfile(profileObj);
                loggerObj.log(Level.INFO, "stored profile " + nextHandleObj.toHandle() + " null bio in database");
            } catch (SQLException exceptionObj) {
                loggerObj.log(Level.WARNING, "storing profile " + nextHandleObj.toHandle() + " null bio to database"
                                            + " failed with SQL error: " + exceptionObj.getMessage());
            }

            return null;
        } else if (!instanceObj.rateLimitExpiredYet()) {
            rateLimitSecondsLeft = instanceObj.rateLimitRemainingSeconds();
            loggerObj.log(Level.INFO, "instance access has been rate-limited, " + rateLimitSecondsLeft
                                      + " seconds remaining, saving for later....");

            unfetchedHandlesQueue.add(nextHandleObj);

            return null;
        }

        try {
            profileURL = nextHandleObj.toProfileURL();
            loggerObj.log(Level.INFO, "generated profile URL for handle " + nextHandleObj.toHandle());
        } catch (ProcessingException exceptionObj) {
            loggerObj.log(Level.WARNING, "generating profile URL for handle " + nextHandleObj.toHandle()
                                        + " failed with processing error: " + exceptionObj.getMessage());
            return null;
        }

        return profileURL;
    }
            
    private final PageInterpreter handleProcessingProfile(final URL profileURL, final Handle nextHandleObj) {
        PageInterpreter profilePageInterpreterObj;
        Profile profileObj;
        String profileBioStr;

        try {
            loggerObj.log(Level.INFO, "fetching profile page url for " + nextHandleObj.toHandle() + ": " + profileURL.toString());
            profilePageInterpreterObj = fetchAndParseURL(profileURL, nextHandleObj);
        } catch (ProcessingException exceptionObj) {
            loggerObj.log(Level.WARNING, "fetching url " + profileURL + " failed with processing error: " + exceptionObj.getMessage());
            return null;
        } catch (IOException exceptionObj) {
            loggerObj.log(Level.WARNING, "fetching url " + profileURL + " failed with IO error: " + exceptionObj.getMessage() + "; saving null bio to database");
//            unfetchedHandlesQueue.add(nextHandleObj);

            try {
                profileObj = new Profile(nextHandleObj, true, "");
            } catch (MalformedURLException newExceptionObj) {
                loggerObj.log(Level.WARNING, "instantiating profile from " + nextHandleObj.toHandle() + " failed with malformed url error: "
                                            + newExceptionObj.getMessage());
                return null;
            }

            return null;
        }

        if (profilePageInterpreterObj == null) {
            loggerObj.log(Level.INFO, "interpreting profile from " + profileURL + " failed");
            return null;
        }

        profileBioStr = profilePageInterpreterObj.getProfileBio();

        if (profileBioStr != null) {
            loggerObj.log(Level.INFO, "at URL " + profileURL.toString() + " found profile bio, length " + profileBioStr.length() + " chars");

            try {
                profileObj = new Profile(profilePageInterpreterObj.getUserHandle(), false, profileBioStr);
            } catch (MalformedURLException exceptionObj) {
                loggerObj.log(Level.WARNING, "instantiating profile from " + profileURL + " failed with malformed url error: "
                                            + exceptionObj.getMessage());
                return null;
            }

            try {
                dataStoreObj.storeProfile(profileObj);
                loggerObj.log(Level.INFO, "stored handle " + nextHandleObj.toHandle() + " bio, length " + profileBioStr.length() + " in database");
            } catch (SQLException exceptionObj) {
                loggerObj.log(Level.WARNING, "storinghandle " + nextHandleObj.toHandle() + " bio to database failed with SQL error: "
                                          + exceptionObj.getMessage());
            }
        }

        return profilePageInterpreterObj;
    }

    private final void processRelations(final URL nextRelationsURLObj, final String relationsStr, final Handle nextHandleObj) {
        PageInterpreter relationsPageInterpreterObj;
        URL nextRelationsURL;
        Profile profileObj;

        nextRelationsURL = nextRelationsURLObj;

        if (nextRelationsURL != null) {
            loggerObj.log(Level.INFO, "generated " + relationsStr + " page url for " + nextHandleObj.toHandle() + ": " + nextRelationsURL.toString());

            while (nextRelationsURL != null) {
                try {
                    loggerObj.log(Level.INFO, "fetching " + relationsStr + " page url for " + nextHandleObj.toHandle() + ": " + nextRelationsURL.toString());
                    relationsPageInterpreterObj = fetchAndParseURL(nextRelationsURL, nextHandleObj);
                } catch (ProcessingException exceptionObj) {
                    loggerObj.log(Level.WARNING, "fetching url " + nextRelationsURL.toString()
                                                + " failed with processing error: " + exceptionObj.getMessage());
                    return;
                } catch (IOException exceptionObj) {
                    loggerObj.log(Level.WARNING, "fetching url " + nextRelationsURL.toString() + " failed with IO error: "
                                              + exceptionObj.getMessage() + "; saving null bio to database");
//                    unfetchedHandlesQueue.add(nextHandleObj);

                    try {
                        profileObj = new Profile(nextHandleObj, true, "");
                    } catch (MalformedURLException newExceptionObj) {
                        loggerObj.log(Level.WARNING, "instantiating profile from " + nextHandleObj.toHandle() + " failed with malformed url error: "
                                                    + newExceptionObj.getMessage());
                        return;
                    }

                    return;
                }

                if (relationsPageInterpreterObj == null) {
                    return;
                }

                try {
                    dataStoreObj.storeRelationSet(relationsPageInterpreterObj.getRelationSet());
                    loggerObj.log(Level.INFO, "retrieved " + relationsPageInterpreterObj.getRelationSet().size()
                                                + " handles from " + relationsStr + " page " + nextRelationsURL.toString()
                                                + "; stored to database");
                } catch (SQLException exceptionObj) {
                    loggerObj.log(Level.WARNING, "storing " + relationsStr + " page " + nextRelationsURL.toString()
                                                + " failed with SQL error: " + exceptionObj.getMessage());
                    return;
                }

                if (relationsPageInterpreterObj.getPageInterpretationOutcome() == PageInterpreter.FOUND_NO_NEXT_PAGE_URL) {
                    return;
                }

                nextRelationsURL = relationsPageInterpreterObj.getNextPageURL();
                loggerObj.log(Level.INFO, "generated " + relationsStr + " page url for " + nextHandleObj.toHandle() + ": " + nextRelationsURL.toString());
            }
        }
    }

    private PageInterpreter fetchAndParseURL(final URL mastodonURL, final Handle nextHandleObj) throws ProcessingException, IOException {
        Response responseObj;
        String instanceHost;
        String mastodonURLString;
        int statusCode;

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
            handleStatusCode429(responseObj, nextHandleObj, statusCode);
            return null;
        } else if (statusCode != 200) {
            handleStatusCodesIn400sOr500s(responseObj, nextHandleObj, statusCode);
            return null;
        } else {
            return handleStatusCode200(responseObj, nextHandleObj, mastodonURLString);
        }
    }

    private final void handleStatusCode429(final Response responseObj, final Handle nextHandleObj, final int statusCode) {
        Instance instanceObj;
        int rateLimitSeconds;

        loggerObj.log(Level.INFO, "tried to load page at URL " + responseObj.getRequestURL().toString()
                                  + ", got status code " + statusCode);

        rateLimitSeconds = responseObj.getXRatelimitLimit();


        instanceObj = getOrPutInstanceWithMap(nextHandleObj.getInstance());
        instanceObj.setRateLimit(rateLimitSeconds);

        loggerObj.log(Level.INFO, "scraper is rate-limited, " + rateLimitSeconds + " seconds remaining");
    }

    private final void handleStatusCodesIn400sOr500s(final Response responseObj, final Handle nextHandleObj, final int statusCode) {
        Instance instanceObj;
        Profile profileObj;

        loggerObj.log(Level.INFO, "tried to load page at URL " + responseObj.getRequestURL().toString()
                                  + ", got status code " + statusCode);

        try {
            profileObj = new Profile(nextHandleObj, true, "");
        } catch (MalformedURLException exceptionObj) {
            loggerObj.log(Level.WARNING, "instantiating profile from " + nextHandleObj.toHandle() + " failed with malformed url error: "
                                        + exceptionObj.getMessage());
            return;
        }

        if ((statusCode >= 500 && statusCode < 600) || statusCode == 400 || statusCode == 401 || statusCode == 403 || statusCode == 406) {
            loggerObj.log(Level.INFO, "got status code " + statusCode + "; instance " + nextHandleObj.getInstance() + " is malfunctioning; storing null bio to database");
//            instanceObj = getOrPutInstanceWithMap(instanceHost);
//            instanceObj.setInstanceStatus(Instance.MALFUNCTIONING);
//            try {
//                dataStoreObj.storeInstance(instanceObj);
//                loggerObj.log(Level.INFO, "stored instance " + instanceObj.getInstanceHostname() +
//                                          " malfunctioning status to database");
//            } catch (SQLException exceptionObj) {
//                loggerObj.log(Level.WARNING, "storing instance " + instanceObj.getInstanceHostname() + " to database"
//                                            + " failed with SQL error: " + exceptionObj.getMessage());
//            }
        } else if (statusCode == 404 || statusCode == 410) {
            loggerObj.log(Level.INFO, "user " + nextHandleObj.toHandle() + " has been deleted from instance; storing null bio to database");
        } else {
            loggerObj.log(Level.INFO, "unrecognized status code" + statusCode + "; storing null bio to database");
        }

        try {
            dataStoreObj.storeProfile(profileObj);
            loggerObj.log(Level.INFO, "stored profile " + nextHandleObj.toHandle() + " null bio in database");
        } catch (SQLException exceptionObj) {
            loggerObj.log(Level.WARNING, "storing profile " + nextHandleObj.toHandle() + " null bio to database"
                                        + " failed with SQL error: " + exceptionObj.getMessage());
        }
    }

    private final PageInterpreter handleStatusCode200(Response responseObj, Handle nextHandleObj, String mastodonURLString) throws ProcessingException, MalformedURLException {
        PageInterpreter pageInterpreterObj;
        int pageTypeFlag;
        int parsingOutcomeFlag;

        loggerObj.log(Level.INFO, "loaded page at URL " + responseObj.getRequestURL().toString()
                                  + ", got status code 200");

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
            if (!handleProfileIsUnparseable(responseObj, nextHandleObj, parsingOutcomeFlag)) {
                return null;
            }
        } else if (parsingOutcomeFlag == PageInterpreter.PAGE_IS_FORWARDING_PAGE) {
            if (!handleForwardingPageProfile(pageInterpreterObj, responseObj, nextHandleObj)) {
                return null;
            }
        } else if (parsingOutcomeFlag == PageInterpreter.PAGE_HAS_NO_POSTS || parsingOutcomeFlag == PageInterpreter.PAGE_POSTS_OUT_OF_DATE) {
            if (!handleProfileWithNoOrOldPosts(responseObj, nextHandleObj, parsingOutcomeFlag)) {
                return null;
            }
        } else if (parsingOutcomeFlag != PageInterpreter.FOUND_PAGE_BIO
                   && parsingOutcomeFlag != PageInterpreter.FOUND_NEXT_PAGE_URL
                   && parsingOutcomeFlag != PageInterpreter.FOUND_NO_NEXT_PAGE_URL) {
            throw new ProcessingException("got unrecognized PageInterpreter flag of int value " + parsingOutcomeFlag);
        }

        return pageInterpreterObj;
    }

    private final boolean handleProfileIsUnparseable(final Response responseObj, final Handle nextHandleObj, int parsingOutcomeFlag) throws ProcessingException {
        Profile profileObj;
        Instance instanceObj;

        try {
            profileObj = new Profile(nextHandleObj, true, "");
            if (parsingOutcomeFlag == PageInterpreter.PAGE_TIMES_UNPARSEABLE) {
                loggerObj.log(Level.WARNING, "profile for " + nextHandleObj.toHandle()
                                            + " has unparseable page times, storing null bio to database");
            } else if (parsingOutcomeFlag == PageInterpreter.PAGE_FORWARDING_UNPARSEABLE) {
                loggerObj.log(Level.WARNING, "profile for " + nextHandleObj.toHandle()
                                            + " has unparseable forwarding address, storing null bio to database");
            } else if (parsingOutcomeFlag == PageInterpreter.PAGE_BIO_UNPARSEABLE) {
                loggerObj.log(Level.WARNING, "profile for " + nextHandleObj.toHandle()
                                            + " has unparseable page bio, storing null bio to database");
            } else {
                throw new ProcessingException("handling unparseable profile for " + nextHandleObj.toHandle()
                                              + " yielded unrecognizable parsing error flag " + parsingOutcomeFlag);
            }
        } catch (MalformedURLException exceptionObj) {
            loggerObj.log(Level.WARNING, "instantiating profile from " + nextHandleObj.toHandle() + " failed with malformed url error: "
                                        + exceptionObj.getMessage());
            return false;
        }

        try {
            dataStoreObj.storeProfile(profileObj);
            loggerObj.log(Level.INFO, "stored profile " + nextHandleObj.toHandle() + " null bio in database");
        } catch (SQLException exceptionObj) {
            loggerObj.log(Level.WARNING, "storing profile " + nextHandleObj.toHandle() + " null bio to database"
                                        + " failed with SQL error: " + exceptionObj.getMessage());
        }

        instanceObj = getOrPutInstanceWithMap(nextHandleObj.getInstance());
        instanceObj.setInstanceStatus(Instance.UNPARSEABLE);
        loggerObj.log(Level.INFO, "tried to parse page at URL " + responseObj.getRequestURL().toString()
                                  + ", unable to parse");

        try {
            dataStoreObj.storeInstance(instanceObj);
            loggerObj.log(Level.INFO, "stored instance " + instanceObj.getInstanceHostname()
                                      + " unparseable status to database");
        } catch (SQLException exceptionObj) {
            loggerObj.log(Level.WARNING, "storinginstance " + instanceObj.getInstanceHostname()
                                      + " unparseable status to database failed with SQL error: "
                                      + exceptionObj.getMessage());
        }
        return true;
    }

    private final boolean handleForwardingPageProfile(final PageInterpreter pageInterpreterObj, final Response responseObj, final Handle nextHandleObj) {
        Handle forwardingHandle;
        Profile profileObj;

        try {
            profileObj = new Profile(nextHandleObj, true, "");
        } catch (MalformedURLException exceptionObj) {
            loggerObj.log(Level.WARNING, "instantiating profile from " + nextHandleObj.toHandle() + " failed with malformed url error: "
                                        + exceptionObj.getMessage());
            return false;
        }

        try {
            dataStoreObj.storeProfile(profileObj);
            loggerObj.log(Level.INFO, "stored profile " + nextHandleObj.toHandle() + " null bio in database");
        } catch (SQLException exceptionObj) {
            loggerObj.log(Level.WARNING, "storing profile " + nextHandleObj.toHandle() + " null bio to database"
                                        + " failed with SQL error: " + exceptionObj.getMessage());
        }

        loggerObj.log(Level.INFO, "page at URL " + responseObj.getRequestURL().toString()
                                  + " is a forwarding page");
        forwardingHandle = pageInterpreterObj.getForwardingAddressHandle();

        try {
            dataStoreObj.storeHandle(forwardingHandle);
            loggerObj.log(Level.INFO, "stored handle " + forwardingHandle.toHandle() + " to database");
        } catch (SQLException exceptionObj) {
            loggerObj.log(Level.WARNING, "storinghandle " + forwardingHandle.toHandle() + " to database failed with SQL error: "
                          + exceptionObj.getMessage());
        }
        unfetchedHandlesQueue.add(forwardingHandle);
        loggerObj.log(Level.INFO, "stored new handle " + forwardingHandle.toHandle() + " in database");

        return true;
    }

    private final boolean handleProfileWithNoOrOldPosts(final Response responseObj, final Handle nextHandleObj, final int parsingOutcomeFlag) {
        Profile profileObj;

        try {
            profileObj = new Profile(nextHandleObj, true, "");
        } catch (MalformedURLException exceptionObj) {
            loggerObj.log(Level.WARNING, "instantiating profile from " + nextHandleObj.toHandle() + " failed with malformed url error: "
                                        + exceptionObj.getMessage());
            return false;
        }

        if (parsingOutcomeFlag == PageInterpreter.PAGE_HAS_NO_POSTS) {
            loggerObj.log(Level.INFO, "page at URL " + responseObj.getRequestURL().toString()
                                      + " has no public posts");
        } else {
            loggerObj.log(Level.INFO, "page at URL " + responseObj.getRequestURL().toString()
                                      + " has no posts more recent than " + PROFILE_TOOT_AGE_CUTOFF_DAYS + " days old");
        }

        try {
            dataStoreObj.storeProfile(profileObj);
            loggerObj.log(Level.INFO, "stored profile " + nextHandleObj.toHandle() + " null bio in database");
        } catch (SQLException exceptionObj) {
            loggerObj.log(Level.WARNING, "storing profile " + nextHandleObj.toHandle() + " null bio to database"
                                        + " failed with SQL error: " + exceptionObj.getMessage());
        }

        return true;
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
