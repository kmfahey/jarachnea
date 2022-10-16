package jarachnea.junit;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

import junit.framework.TestCase;
import org.jsoup.nodes.Document;

import jarachnea.Fetcher;
import jarachnea.Handle;
import jarachnea.PageInterpreter;
import jarachnea.ProcessingException;


public final class TestPageInterpreter extends TestCase {
    private Path sampleProfilePath = new File(
        "jarachnea/junit/https:__mastodon.social_@Gargron.html"
    ).toPath().toAbsolutePath();

    private Path sampleFollowersPath = new File(
        "jarachnea/junit/https:__mastodon.social_users_Gargron_followers_page=1.html"
    ).toPath().toAbsolutePath();

    private Path sampleFollowingPath = new File(
        "jarachnea/junit/https:__mastodon.social_users_Gargron_following_page=1.html"
    ).toPath().toAbsolutePath();

    private Handle userHandle = new Handle("Gargron", "mastodon.social");

    private Path pageTimesUnparseablePath = new File(
        "jarachnea/junit/https:__mastodon.social_@Gargron_unparseable_times.html"
    ).toPath().toAbsolutePath();

    private Path pageHasNoPostsPath = new File(
        "jarachnea/junit/https:__mastodon.social_@szyczyx.html"
    ).toPath().toAbsolutePath();

    private Path pagePostsOutOfDatePath = new File(
        "jarachnea/junit/https:__kolektiva.social_@Anarchotranny.html"
    ).toPath().toAbsolutePath();

    private Path pageForwardingUnparseablePath = new File(
        "jarachnea/junit/https:__mastodon.social_@clintlalonde_unparseable_forwarding.html"
    ).toPath().toAbsolutePath();

    private Path pageIsForwardingPagePath = new File(
        "jarachnea/junit/https:__mastodon.social_@clintlalonde.html"
    ).toPath().toAbsolutePath();

    private Path pageBioUnparseablePath = new File(
        "jarachnea/junit/https:__mastodon.social_@Gargron_unparseable_bio.html"
    ).toPath().toAbsolutePath();

    private Path pageNextUrlsUnparseablePath = new File(
        "jarachnea/junit/https:__mastodon.social_users_Gargron_followers_page=1.html"
    ).toPath().toAbsolutePath();

    private Path foundPageBioPath = new File(
        "jarachnea/junit/https:__mastodon.social_@Gargron.html"
    ).toPath().toAbsolutePath();
    
    private Path foundNextFollowingPageUrlPath = new File(
        "jarachnea/junit/https:__mastodon.social_users_Gargron_following_page=1.html"
    ).toPath().toAbsolutePath();

    private Path foundNextFollowersPageUrlPath = new File(
        "jarachnea/junit/https:__mastodon.social_users_Gargron_followers_page=1.html"
    ).toPath().toAbsolutePath();

    private Path foundNoNextFollowingPageUrlPath = new File(
        "jarachnea/junit/https:__mastodon.social_users_Gargron_following_page=26.html"
    ).toPath().toAbsolutePath();

    private Path foundNoNextFollowersPageUrlPath = new File(
        "jarachnea/junit/https:__mastodon.social_users_Gargron_followers_page=9979.html"
    ).toPath().toAbsolutePath();

    private int tenSecondsInMilliseconds = 10_000;
    private int interpreterCutoffSevenDays = 7;
    private int interpreterCutoffThreeDays = 3;
    private int interpreterCutoffOneDay = 1;

    public void testPageInterpreterConstructor() throws ProcessingException {
        Fetcher fetcherObj;
        Document sampleProfileDocument;
        Document sampleFollowersDocument;
        Document sampleFollowingDocument;
        PageInterpreter profilePageInterpreterObj;
        PageInterpreter followingPageInterpreterObj;
        PageInterpreter followersPageInterpreterObj;
        URL sampleProfileURL;
        URL sampleFollowersURL;
        URL sampleFollowingURL;

        fetcherObj = new Fetcher(tenSecondsInMilliseconds);

        try {
            sampleProfileURL = sampleProfilePath.toUri().toURL();
            sampleFollowersURL = sampleFollowersPath.toUri().toURL();
            sampleFollowingURL = sampleFollowingPath.toUri().toURL();

            sampleProfileDocument = fetcherObj.fetchContentDocument(sampleProfileURL);
            sampleFollowersDocument = fetcherObj.fetchContentDocument(sampleFollowersURL);
            sampleFollowingDocument = fetcherObj.fetchContentDocument(sampleFollowingURL);
        } catch (IOException exceptionObj) {
            fail(exceptionObj.getMessage());
            return;
        }

        profilePageInterpreterObj = new PageInterpreter(
            sampleProfileDocument, userHandle, PageInterpreter.PROFILE_PAGE, interpreterCutoffSevenDays);

        assertTrue(profilePageInterpreterObj.getPageDocument() == sampleProfileDocument);
        assertTrue(profilePageInterpreterObj.getUserHandle() == userHandle);
        assertEquals(profilePageInterpreterObj.getPageType(), PageInterpreter.PROFILE_PAGE);
        assertEquals(profilePageInterpreterObj.getRecentPostDaysCutoff(), interpreterCutoffSevenDays);
        assertTrue(profilePageInterpreterObj.getForwardingAddressHandle() == null);

        followersPageInterpreterObj = new PageInterpreter(
            sampleFollowersDocument, userHandle, PageInterpreter.FOLLOWERS_PAGE, interpreterCutoffThreeDays);

        assertTrue(followersPageInterpreterObj.getPageDocument() == sampleFollowersDocument);
        assertTrue(followersPageInterpreterObj.getUserHandle() == userHandle);
        assertEquals(followersPageInterpreterObj.getPageType(), PageInterpreter.FOLLOWERS_PAGE);
        assertEquals(followersPageInterpreterObj.getRecentPostDaysCutoff(), interpreterCutoffThreeDays);
        assertTrue(profilePageInterpreterObj.getForwardingAddressHandle() == null);

        followingPageInterpreterObj = new PageInterpreter(
            sampleFollowingDocument, userHandle, PageInterpreter.FOLLOWING_PAGE, interpreterCutoffOneDay);

        assertTrue(followingPageInterpreterObj.getPageDocument() == sampleFollowingDocument);
        assertTrue(followingPageInterpreterObj.getUserHandle() == userHandle);
        assertEquals(followingPageInterpreterObj.getPageType(), PageInterpreter.FOLLOWING_PAGE);
        assertEquals(followingPageInterpreterObj.getRecentPostDaysCutoff(), interpreterCutoffOneDay);
        assertTrue(profilePageInterpreterObj.getForwardingAddressHandle() == null);
    }

    public void testPageTimesUnparseable() throws ProcessingException {
        // "jarachnea/junit/https:__mastodon.social_@Gargron_unparseable_times.html"
        Fetcher fetcherObj;
        URL pageTimesUnparseableURL;
        Document pageTimesUnparseableDocument;
        PageInterpreter pageTimesUnparseableInterpreter;
        int pageInterpretationResult;

        fetcherObj = new Fetcher(tenSecondsInMilliseconds);

        try {
            pageTimesUnparseableURL = pageTimesUnparseablePath.toUri().toURL();
            pageTimesUnparseableDocument = fetcherObj.fetchContentDocument(pageTimesUnparseableURL);
        } catch (IOException exceptionObj) {
            fail(exceptionObj.getMessage());
            return;
        }

        pageTimesUnparseableInterpreter = new PageInterpreter(
            pageTimesUnparseableDocument, new Handle("Gargron", "mastodon.social"), PageInterpreter.PROFILE_PAGE, interpreterCutoffSevenDays);
        pageInterpretationResult = pageTimesUnparseableInterpreter.interpretPage();

        assertEquals(pageInterpretationResult, PageInterpreter.PAGE_TIMES_UNPARSEABLE);
    }

    public void testPageHasNoPosts() throws ProcessingException {
        // "jarachnea/junit/https:__mastodon.social_@szyczy"
        Fetcher fetcherObj;
        URL pageHasNoPostsURL;
        Document pageHasNoPostsDocument;
        PageInterpreter pageHasNoPostsInterpreter;
        int pageInterpretationResult;

        fetcherObj = new Fetcher(tenSecondsInMilliseconds);

        try {
            pageHasNoPostsURL = pageHasNoPostsPath.toUri().toURL();
            pageHasNoPostsDocument = fetcherObj.fetchContentDocument(pageHasNoPostsURL);
        } catch (IOException exceptionObj) {
            fail(exceptionObj.getMessage());
            return;
        }

        pageHasNoPostsInterpreter = new PageInterpreter(
            pageHasNoPostsDocument, new Handle("szyczy", "mastodon.social"), PageInterpreter.PROFILE_PAGE, interpreterCutoffSevenDays);
        pageInterpretationResult = pageHasNoPostsInterpreter.interpretPage();

        assertEquals(pageInterpretationResult, PageInterpreter.PAGE_HAS_NO_POSTS);
    }

    public void testPagePostsOutOfDate() throws ProcessingException {
        // "jarachnea/junit/https:__kolektiva.social_@Anarchotranny"
        Fetcher fetcherObj;
        URL pagePostsOutOfDateURL;
        Document pagePostsOutOfDateDocument;
        PageInterpreter pagePostsOutOfDateInterpreter;
        int pageInterpretationResult;

        fetcherObj = new Fetcher(tenSecondsInMilliseconds);

        try {
            pagePostsOutOfDateURL = pagePostsOutOfDatePath.toUri().toURL();
            pagePostsOutOfDateDocument = fetcherObj.fetchContentDocument(pagePostsOutOfDateURL);
        } catch (IOException exceptionObj) {
            fail(exceptionObj.getMessage());
            return;
        }

        pagePostsOutOfDateInterpreter = new PageInterpreter(
            pagePostsOutOfDateDocument, new Handle("Anarchotranny", "kolektiva.social"), PageInterpreter.PROFILE_PAGE, interpreterCutoffSevenDays);
        pageInterpretationResult = pagePostsOutOfDateInterpreter.interpretPage();

        assertEquals(pageInterpretationResult, PageInterpreter.PAGE_POSTS_OUT_OF_DATE);
    }

    public void testPageForwardingUnparseable() throws ProcessingException {
        // "jarachnea/junit/https:__mastodon.social_@clintlalonde_unparseable_forwarding.html"
        Fetcher fetcherObj;
        URL pageForwardingUnparseableURL;
        Document pageForwardingUnparseableDocument;
        PageInterpreter pageForwardingUnparseableInterpreter;
        int pageInterpretationResult;

        fetcherObj = new Fetcher(tenSecondsInMilliseconds);

        try {
            pageForwardingUnparseableURL = pageForwardingUnparseablePath.toUri().toURL();
            pageForwardingUnparseableDocument = fetcherObj.fetchContentDocument(pageForwardingUnparseableURL);
        } catch (IOException exceptionObj) {
            fail(exceptionObj.getMessage());
            return;
        }

        pageForwardingUnparseableInterpreter = new PageInterpreter(
            pageForwardingUnparseableDocument, new Handle("clintlalonde", "mastodon.social"),
            PageInterpreter.PROFILE_PAGE, interpreterCutoffSevenDays);
        pageInterpretationResult = pageForwardingUnparseableInterpreter.interpretPage();

        assertEquals(pageInterpretationResult, PageInterpreter.PAGE_FORWARDING_UNPARSEABLE);
    }

    public void testPageIsForwardingPage() throws ProcessingException {
        // "jarachnea/junit/https:__mastodon.social_@clintlalonde.html"
        Fetcher fetcherObj;
        URL pageIsForwardingPageURL;
        Document pageIsForwardingPageDocument;
        PageInterpreter pageIsForwardingPageInterpreter;
        int pageInterpretationResult;

        fetcherObj = new Fetcher(tenSecondsInMilliseconds);

        try {
            pageIsForwardingPageURL = pageIsForwardingPagePath.toUri().toURL();
            pageIsForwardingPageDocument = fetcherObj.fetchContentDocument(pageIsForwardingPageURL);
        } catch (IOException exceptionObj) {
            fail(exceptionObj.getMessage());
            return;
        }

        pageIsForwardingPageInterpreter = new PageInterpreter(
            pageIsForwardingPageDocument, new Handle("clintlalonde", "mastodon.social"), PageInterpreter.PROFILE_PAGE, interpreterCutoffSevenDays);
        pageInterpretationResult = pageIsForwardingPageInterpreter.interpretPage();

        assertEquals(pageInterpretationResult, PageInterpreter.PAGE_IS_FORWARDING_PAGE);
    }

    public void testPageBioUnparseable() throws ProcessingException {
        // "jarachnea/junit/https:__mastodon.social_@Gargron_unparseable_bio.html"
        Fetcher fetcherObj;
        URL pageBioUnparseableURL;
        Document pageBioUnparseableDocument;
        PageInterpreter pageBioUnparseableInterpreter;
        int pageInterpretationResult;

        fetcherObj = new Fetcher(tenSecondsInMilliseconds);

        try {
            pageBioUnparseableURL = pageBioUnparseablePath.toUri().toURL();
            pageBioUnparseableDocument = fetcherObj.fetchContentDocument(pageBioUnparseableURL);
        } catch (IOException exceptionObj) {
            fail(exceptionObj.getMessage());
            return;
        }

        pageBioUnparseableInterpreter = new PageInterpreter(
            pageBioUnparseableDocument, new Handle("Gargron", "mastodon.social"), PageInterpreter.PROFILE_PAGE, interpreterCutoffSevenDays);
        pageInterpretationResult = pageBioUnparseableInterpreter.interpretPage();

        assertEquals(pageInterpretationResult, PageInterpreter.PAGE_BIO_UNPARSEABLE);
    }

    public void testPageNextUrlsUnparseable() throws ProcessingException {
        // "jarachnea/junit/https:__mastodon.social_users_Gargron_followers_page=1.html"
        Fetcher fetcherObj;
        URL pageNextUrlsUnparseableURL;
        Document pageNextUrlsUnparseableDocument;
        PageInterpreter pageNextUrlsUnparseableInterpreter;
        int pageInterpretationResult;

        fetcherObj = new Fetcher(tenSecondsInMilliseconds);

        try {
            pageNextUrlsUnparseableURL = pageNextUrlsUnparseablePath.toUri().toURL();
            pageNextUrlsUnparseableDocument = fetcherObj.fetchContentDocument(pageNextUrlsUnparseableURL);
        } catch (IOException exceptionObj) {
            fail(exceptionObj.getMessage());
            return;
        }

        pageNextUrlsUnparseableInterpreter = new PageInterpreter(
            pageNextUrlsUnparseableDocument, new Handle("Gargron", "mastodon social"), PageInterpreter.FOLLOWERS_PAGE, interpreterCutoffSevenDays);
        pageInterpretationResult = pageNextUrlsUnparseableInterpreter.interpretPage();

        assertEquals(pageInterpretationResult, PageInterpreter.PAGE_NEXT_URLS_UNPARSEABLE);
    }

    public void testFoundPageBio() throws ProcessingException {
        // "jarachnea/junit/https:__mastodon.social_@Gargron.html"
        Fetcher fetcherObj;
        URL foundPageBioURL;
        Document foundPageBioDocument;
        PageInterpreter foundPageBioInterpreter;
        int pageInterpretationResult;

        fetcherObj = new Fetcher(tenSecondsInMilliseconds);

        try {
            foundPageBioURL = foundPageBioPath.toUri().toURL();
            foundPageBioDocument = fetcherObj.fetchContentDocument(foundPageBioURL);
        } catch (IOException exceptionObj) {
            fail(exceptionObj.getMessage());
            return;
        }

        foundPageBioInterpreter = new PageInterpreter(
            foundPageBioDocument, new Handle("Gargron", "mastodon.social"), PageInterpreter.PROFILE_PAGE, interpreterCutoffSevenDays);
        pageInterpretationResult = foundPageBioInterpreter.interpretPage();

        assertEquals(pageInterpretationResult, PageInterpreter.FOUND_PAGE_BIO);
    }

    public void testFoundNextFollowingPageUrl() throws ProcessingException {
        // "jarachnea/junit/https:__mastodon.social_users_Gargron_following_page=1.html"
        Fetcher fetcherObj;
        URL foundNextFollowingPageUrlURL;
        Document foundNextFollowingPageUrlDocument;
        PageInterpreter foundNextFollowingPageUrlInterpreter;
        int pageInterpretationResult;

        fetcherObj = new Fetcher(tenSecondsInMilliseconds);

        try {
            foundNextFollowingPageUrlURL = foundNextFollowingPageUrlPath.toUri().toURL();
            foundNextFollowingPageUrlDocument = fetcherObj.fetchContentDocument(foundNextFollowingPageUrlURL);
        } catch (IOException exceptionObj) {
            fail(exceptionObj.getMessage());
            return;
        }

        foundNextFollowingPageUrlInterpreter = new PageInterpreter(
            foundNextFollowingPageUrlDocument, new Handle("Gargron", "mastodon.social"), PageInterpreter.FOLLOWING_PAGE, interpreterCutoffSevenDays);
        pageInterpretationResult = foundNextFollowingPageUrlInterpreter.interpretPage();

        assertEquals(pageInterpretationResult, PageInterpreter.FOUND_NEXT_PAGE_URL);
    }

    public void testFoundNextFollowersPageUrl() throws ProcessingException {
        // "jarachnea/junit/https:__mastodon.social_users_Gargron_followers_page=1.html"
        Fetcher fetcherObj;
        URL foundNextFollowersPageUrlURL;
        Document foundNextFollowersPageUrlDocument;
        PageInterpreter foundNextFollowersPageUrlInterpreter;
        int pageInterpretationResult;

        fetcherObj = new Fetcher(tenSecondsInMilliseconds);

        try {
            foundNextFollowersPageUrlURL = foundNextFollowersPageUrlPath.toUri().toURL();
            foundNextFollowersPageUrlDocument = fetcherObj.fetchContentDocument(foundNextFollowersPageUrlURL);
        } catch (IOException exceptionObj) {
            fail(exceptionObj.getMessage());
            return;
        }

        foundNextFollowersPageUrlInterpreter = new PageInterpreter(
            foundNextFollowersPageUrlDocument, new Handle("Gargron", "mastodon.social"), PageInterpreter.FOLLOWERS_PAGE, interpreterCutoffSevenDays);
        pageInterpretationResult = foundNextFollowersPageUrlInterpreter.interpretPage();

        assertEquals(pageInterpretationResult, PageInterpreter.FOUND_NEXT_PAGE_URL);
    }

    public void testFoundNoNextFollowingPageUrl() throws ProcessingException {
        // "jarachnea/junit/https:__mastodon.social_users_Gargron_following?page=26"
        Fetcher fetcherObj;
        URL foundNoNextFollowingPageUrlURL;
        Document foundNoNextFollowingPageUrlDocument;
        PageInterpreter foundNoNextFollowingPageUrlInterpreter;
        int pageInterpretationResult;

        fetcherObj = new Fetcher(tenSecondsInMilliseconds);

        try {
            foundNoNextFollowingPageUrlURL = foundNoNextFollowingPageUrlPath.toUri().toURL();
            foundNoNextFollowingPageUrlDocument = fetcherObj.fetchContentDocument(foundNoNextFollowingPageUrlURL);
        } catch (IOException exceptionObj) {
            fail(exceptionObj.getMessage());
            return;
        }

        foundNoNextFollowingPageUrlInterpreter = new PageInterpreter(
            foundNoNextFollowingPageUrlDocument, new Handle("Gargron", "mastodon.social"),
            PageInterpreter.FOLLOWING_PAGE, interpreterCutoffSevenDays);
        pageInterpretationResult = foundNoNextFollowingPageUrlInterpreter.interpretPage();

        assertEquals(pageInterpretationResult, PageInterpreter.FOUND_NO_NEXT_PAGE_URL);
    }

    public void testFoundNoNextFollowersPageUrl() throws ProcessingException {
        // "jarachnea/junit/https:__mastodon.social_users_Gargron_followers?page=9979"
        Fetcher fetcherObj;
        URL foundNoNextFollowersPageUrlURL;
        Document foundNoNextFollowersPageUrlDocument;
        PageInterpreter foundNoNextFollowersPageUrlInterpreter;
        int pageInterpretationResult;

        fetcherObj = new Fetcher(tenSecondsInMilliseconds);

        try {
            foundNoNextFollowersPageUrlURL = foundNoNextFollowersPageUrlPath.toUri().toURL();
            foundNoNextFollowersPageUrlDocument = fetcherObj.fetchContentDocument(foundNoNextFollowersPageUrlURL);
        } catch (IOException exceptionObj) {
            fail(exceptionObj.getMessage());
            return;
        }

        foundNoNextFollowersPageUrlInterpreter = new PageInterpreter(
            foundNoNextFollowersPageUrlDocument, new Handle("Gargron", "mastodon.social"),
            PageInterpreter.FOLLOWERS_PAGE, interpreterCutoffSevenDays);
        pageInterpretationResult = foundNoNextFollowersPageUrlInterpreter.interpretPage();

        assertEquals(pageInterpretationResult, PageInterpreter.FOUND_NO_NEXT_PAGE_URL);
    }
}
