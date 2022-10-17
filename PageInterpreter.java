package jarachnea;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public final class PageInterpreter {
    public static final int PROFILE_PAGE = 0;
    public static final int FOLLOWERS_PAGE = 1;
    public static final int FOLLOWING_PAGE = 2;

    public static final int PAGE_TIMES_UNPARSEABLE = 0;
    public static final int PAGE_HAS_NO_POSTS = 1;
    public static final int PAGE_POSTS_OUT_OF_DATE = 2;
    public static final int PAGE_FORWARDING_UNPARSEABLE = 3;
    public static final int PAGE_IS_FORWARDING_PAGE = 4;
    public static final int PAGE_BIO_UNPARSEABLE = 5;
    public static final int PAGE_NEXT_URLS_UNPARSEABLE = 6;
    public static final int FOUND_PAGE_BIO = 7;
    public static final int FOUND_NEXT_PAGE_URL = 8;
    public static final int FOUND_NO_NEXT_PAGE_URL = 9;

    private final long numberOfMillisecondsInAWeek = 1000L * 60L * 60L * 24L * 7L;

    private final Pattern handleRegex = Pattern.compile("^.* (@[A-Za-z0-9._]+@[A-Za-z0-9._]+\\.[a-z]+):$");
    private final Pattern hrefRegex = Pattern.compile("^/users/[A-Za-z0-9_.-]+/follow...\\?page=[0-9]+$");
    private final Pattern relationRegex = Pattern.compile("^https://([A-Za-z0-9_.-]+\\.[a-z]+)/@([A-Za-z0-9_.-]+)$");
    private final Pattern followersUrlRegex = Pattern.compile("^.*followers.*$");
    private final Pattern followingUrlRegex = Pattern.compile("^.*following.*$");
    private final Pattern pageNumberUrlQsaRegex = Pattern.compile("^.*(?:\\?|%3F)page=([0-9]+)\\b.*?$");
    private ArrayList<Date> postDateList;

    private Document pageDocument;
    private int pageType;
    private int recentPostDaysCutoff;
    private Handle userHandle;
    private Handle forwardingAddressHandle;
    private URL nextPageURL;
    private String profileBio;
    private RelationSet relationSetObj;

    public Document getPageDocument() {
        return pageDocument;
    }

    public int getPageType() {
        return pageType;
    }

    public int getRecentPostDaysCutoff() {
        return recentPostDaysCutoff;
    }

    public Handle getUserHandle() {
        return userHandle;
    }

    public Handle getForwardingAddressHandle() {
        return forwardingAddressHandle;
    }

    public URL getNextPageURL() {
        return nextPageURL;
    }

    public String getProfileBio() {
        return profileBio;
    }

    public RelationSet getRelationSet() {
        return relationSetObj;
    }

    public PageInterpreter(final Document pageDocumentObj, final Handle userHandleObj, final int pageTypeFlag, final int recentPostDaysCutoffVal)
                           throws ProcessingException {
        pageDocument = pageDocumentObj;
        userHandle = userHandleObj;
        pageType = pageTypeFlag;
        recentPostDaysCutoff = recentPostDaysCutoffVal;
        forwardingAddressHandle = null;
    }

    public int interpretPage() throws ProcessingException, MalformedURLException {
        if (pageType == PROFILE_PAGE) {
            try {
                loadPostDateList();
            } catch (HTMLReadingException exceptionObj) {
                return PAGE_TIMES_UNPARSEABLE;
            }

            try {
                if (isForwardingPage()) {
                    return PAGE_IS_FORWARDING_PAGE;
                }
            } catch (HTMLReadingException exceptionObj) {
                return PAGE_FORWARDING_UNPARSEABLE;
            }

            if (((Integer) postDateList.size()).equals(0)) {
                return PAGE_HAS_NO_POSTS;
            }

            if (!isMostRecentPostWithinCutoff()) {
                return PAGE_POSTS_OUT_OF_DATE;
            }

            try {
                detectProfileBio();
            } catch (HTMLReadingException exceptionObj) {
                return PAGE_BIO_UNPARSEABLE;
            }
            return FOUND_PAGE_BIO;
        } else {
            generateRelationSet();

            try {
                if (detectNextPageURL()) {
                    return FOUND_NEXT_PAGE_URL;
                } else {
                    return FOUND_NO_NEXT_PAGE_URL;
                }
            } catch (HTMLReadingException | MalformedURLException exceptionObj) {
                return PAGE_NEXT_URLS_UNPARSEABLE;
            }
        }
    }

    private boolean generateRelationSet() throws ProcessingException, MalformedURLException {
        Elements matchingATags;
        String documentLocation;
        Iterator aTagsIter;
        Matcher urlPageNumberMatcher;
        int relationTypeFlag;
        int relationPageNumber;

        documentLocation = pageDocument.location();
        if (followersUrlRegex.matcher(documentLocation).matches()) {
            relationTypeFlag = Relation.IS_FOLLOWER_OF;
        } else if (followingUrlRegex.matcher(documentLocation).matches()) {
            relationTypeFlag = Relation.IS_FOLLOWED_BY;
        } else {
            throw new ProcessingException("unable to discern whether Document is a following page or a follower page given URL " + documentLocation);
        }

        urlPageNumberMatcher = pageNumberUrlQsaRegex.matcher(documentLocation);

        if (urlPageNumberMatcher.matches()) {
            relationPageNumber = Integer.valueOf(urlPageNumberMatcher.group(1));
        } else {
            throw new ProcessingException("unable to detect page number in URL " + documentLocation + " using regular expression " + pageNumberUrlQsaRegex.pattern());
        }

        matchingATags = pageDocument.getElementsByAttributeValueMatching("href", relationRegex);

        if (matchingATags.size() == 0) {
            return false;
        }

        relationSetObj = new RelationSet(userHandle, relationTypeFlag, relationPageNumber);
        aTagsIter = matchingATags.iterator();

        while (aTagsIter.hasNext()) {
            Element aTag;
            Relation relationObj;
            Matcher hrefMatcher;
            Handle relationHandleObj;
            String hrefUrlString;
            String relationUsername;
            String relationInstance;
            String profileURLString;

            aTag = (Element) aTagsIter.next();
            hrefUrlString = aTag.attr("href");
            hrefMatcher = relationRegex.matcher(hrefUrlString);

            if (! hrefMatcher.matches()) {
                throw new ProcessingException("unable to match URL " + hrefUrlString + " against regular expression " + relationRegex.pattern());
            }

            try {
                profileURLString = userHandle.toProfileURL().toString();
            } catch (ProcessingException exceptionObj) {
                continue;
            }
            if (hrefMatcher.group().equals(userHandle.toProfileURL().toString())) {
                continue;
            }

            relationInstance = hrefMatcher.group(1);
            relationUsername = hrefMatcher.group(2);
            relationHandleObj = new Handle(relationUsername, relationInstance);
            relationObj = new Relation(userHandle, relationHandleObj, relationTypeFlag, relationPageNumber);
            relationSetObj.add(relationObj);
        }
        
        return true;
    }

    private String pageTypeString() {
        String pageTypeString;
        if (pageType == PROFILE_PAGE) {
            pageTypeString = "profile";
        } else if (pageType == FOLLOWERS_PAGE) {
            pageTypeString = "followers";
        } else {
            pageTypeString = "following";
        }
        return pageTypeString;
    }

    private boolean detectNextPageURL() throws HTMLReadingException, MalformedURLException {
        Elements matchingElementsObj;
        Element aHrefElementObj;
        String hrefAttributeString;
        String fullURLString;

        matchingElementsObj = pageDocument.getElementsByAttributeValueMatching("href", hrefRegex);
        matchingElementsObj.removeIf((element) -> (element.attr("rel").equals("")));
        matchingElementsObj.removeIf((element) -> (element.attr("rel").equals("prev")));

        if (((Integer) matchingElementsObj.size()).equals(0)) {
            return false;
        }

        aHrefElementObj = matchingElementsObj.first();
        hrefAttributeString = aHrefElementObj.toString();
        fullURLString = "https://" + userHandle.getInstance() + hrefAttributeString;

        nextPageURL = new URL(fullURLString);

        return true;
    }

    private void loadPostDateList() throws HTMLReadingException {
        Elements matchingElementsObj;
        SimpleDateFormat dateFormatObj;

        matchingElementsObj = pageDocument.getElementsByClass("time-ago");
        dateFormatObj = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        postDateList = new ArrayList<Date>();

        int arrayIndex = 0;
        try {
            while (arrayIndex < matchingElementsObj.size()) {
                postDateList.add(dateFormatObj.parse(matchingElementsObj.get(arrayIndex).attr("datetime")));
                arrayIndex += 1;
            }
        } catch (ParseException exceptionObj) {
            throw new HTMLReadingException("unable to parse <time> tag 'datetime' attribute value '" + matchingElementsObj.get(arrayIndex) + "'");
        }

        postDateList.sort((lhsDate, rhsDate) -> ((Long) rhsDate.getTime()).compareTo(lhsDate.getTime()));
    }

    private boolean isForwardingPage() throws HTMLReadingException, ProcessingException {
        Elements matchingElementsObj;
        Element forwardingElementObj;
        Matcher handleMatcher;
        String matchedHandleString;

        matchingElementsObj = pageDocument.getElementsByClass("moved-account-widget__message");

        if (((Integer) matchingElementsObj.size()).equals(0)) {
            return false;
        }

        forwardingElementObj = matchingElementsObj.first();

        handleMatcher = handleRegex.matcher(forwardingElementObj.text());

        if (!handleMatcher.matches()) {
            throw new HTMLReadingException("found <div> with class 'moved-account-widget__message' but could not match handle with a regular expression");
        }

        matchedHandleString = handleMatcher.group(1);
        forwardingAddressHandle = new Handle(matchedHandleString);
        return true;
    }

    private boolean isMostRecentPostWithinCutoff() {
        Date mostRecentDate;
        Date sevenDaysAgoDate;

        sevenDaysAgoDate = new Date((new Date().getTime()) - numberOfMillisecondsInAWeek);

        mostRecentDate = postDateList.get(0);

        return mostRecentDate.getTime() > sevenDaysAgoDate.getTime();
    }

    private void detectProfileBio() throws HTMLReadingException {
        Elements matchingElementsFirstTryObj;
        Elements matchingElementsSecondTryObj;
        Element profileBioDivTag;

        matchingElementsFirstTryObj = pageDocument.getElementsByClass("public-account-bio");
        matchingElementsSecondTryObj = pageDocument.getElementsByClass("account__header__content");
        if (((Integer) matchingElementsFirstTryObj.size()).equals(0)) {
            if (((Integer) matchingElementsSecondTryObj.size()).equals(0)) {
                throw new HTMLReadingException("unable to detect a profile bio <div> tag with either class 'public-account-bio' or class 'account__header__content'");
            } else {
                profileBioDivTag = matchingElementsSecondTryObj.first();
            }
        } else {
            profileBioDivTag = matchingElementsSecondTryObj.first();
        }

        profileBio = profileBioDivTag.text();
    }
//
}
