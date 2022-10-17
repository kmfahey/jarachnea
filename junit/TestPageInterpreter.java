package jarachnea.junit;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Iterator;

import junit.framework.TestCase;
import org.jsoup.nodes.Document;

import jarachnea.Fetcher;
import jarachnea.Handle;
import jarachnea.PageInterpreter;
import jarachnea.ProcessingException;
import jarachnea.Relation;
import jarachnea.RelationSet;


public final class TestPageInterpreter extends TestCase {
    private Path sampleProfilePath = new File(
        "jarachnea/junit/https:__mastodon.social_@Gargron.html"
    ).toPath().toAbsolutePath();

    private Path sampleFollowersPath = new File(
        "jarachnea/junit/https:__mastodon.social_users_Gargron_followers?page=1.html"
    ).toPath().toAbsolutePath();

    private Path sampleFollowingPath = new File(
        "jarachnea/junit/https:__mastodon.social_users_Gargron_following?page=1.html"
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
        "jarachnea/junit/https:__mastodon.social_users_Gargron_followers?page=1.html"
    ).toPath().toAbsolutePath();

    private Path foundPageBioPath = new File(
        "jarachnea/junit/https:__mastodon.social_@Gargron.html"
    ).toPath().toAbsolutePath();

    private Path foundNextFollowingPageUrlPath = new File(
        "jarachnea/junit/https:__mastodon.social_users_Gargron_following?page=1.html"
    ).toPath().toAbsolutePath();

    private Path foundNextFollowersPageUrlPath = new File(
        "jarachnea/junit/https:__mastodon.social_users_Gargron_followers?page=1.html"
    ).toPath().toAbsolutePath();

    private Path foundNoNextFollowingPageUrlPath = new File(
        "jarachnea/junit/https:__mastodon.social_users_Gargron_following?page=26.html"
    ).toPath().toAbsolutePath();

    private Path foundNoNextFollowersPageUrlPath = new File(
        "jarachnea/junit/https:__mastodon.social_users_Gargron_followers?page=9979.html"
    ).toPath().toAbsolutePath();

    private static final int TEN_SECONDS_IN_MILLISECONDS = 10_000;
    private static final int INTERPRETER_CUTOFF_SEVEN_DAYS = 7;
    private static final int INTERPRETER_CUTOFF_THREE_DAYS = 3;
    private static final int INTERPRETER_CUTOFF_ONE_DAY = 1;

    private static final Handle profileHandle = new Handle("Gargron", "mastodon.social");
    private static final Handle[] gargronFollowingPage1 = {new Handle("Gargron", "mastodon.social"),
                                                           new Handle("Mastodon", "mastodon.social"),
                                                           new Handle("ashfurrow", "masto.ashfurrow.com"),
                                                           new Handle("danarel", "fosstodon.org"),
                                                           new Handle("taviso", "mastodon.sdf.org"),
                                                           new Handle("ThomasWaldmann", "chaos.social"),
                                                           new Handle("akryum", "mastodon.social"),
                                                           new Handle("vivaldibrowser", "mastodon.online"),
                                                           new Handle("NotFrauKadse", "mastodon.social"),
                                                           new Handle("muffinista", "mastodon.lol"),
                                                           new Handle("camilabrun", "mastodon.art"),
                                                           new Handle("a2_4am", "mastodon.social"),
                                                           new Handle("_astronoMay", "mastodon.online")};
    private static final Handle[] gargronFollowersPage1 = {new Handle("Mastodon", "mastodon.social"),
                                                           new Handle("conr", "notacult.social"),
                                                           new Handle("UnderscoreTalk", "mastodon.social"),
                                                           new Handle("senor_massage", "mastodon.social"),
                                                           new Handle("zachwood", "mastodon.social"),
                                                           new Handle("Muzaffaralam", "c.im"),
                                                           new Handle("stoom", "mastodon.social"),
                                                           new Handle("JanaJaja1002", "mastodon.social"),
                                                           new Handle("Lilalaunebaer", "sueden.social"),
                                                           new Handle("airisdamon", "mastodon.social"),
                                                           new Handle("Rahul355", "mastodon.social"),
                                                           new Handle("kesch", "mastodon.social"),
                                                           new Handle("vassie", "mastodon.social")};

    public void testPageInterpreterProfilePageConstructor() throws ProcessingException {
        Fetcher fetcherObj;
        Document sampleProfileDocument;
        PageInterpreter profilePageInterpreterObj;
        URL sampleProfileURL;

        fetcherObj = new Fetcher(TEN_SECONDS_IN_MILLISECONDS);

        try {
            sampleProfileURL = sampleProfilePath.toUri().toURL();
            sampleProfileDocument = fetcherObj.fetchContentDocument(sampleProfileURL);
        } catch (IOException exceptionObj) {
            fail(exceptionObj.getMessage());
            return;
        }

        profilePageInterpreterObj = new PageInterpreter(
            sampleProfileDocument, userHandle, PageInterpreter.PROFILE_PAGE, INTERPRETER_CUTOFF_SEVEN_DAYS);

        assertTrue(profilePageInterpreterObj.getPageDocument() == sampleProfileDocument);
        assertTrue(profilePageInterpreterObj.getUserHandle() == userHandle);
        assertEquals(profilePageInterpreterObj.getPageType(), PageInterpreter.PROFILE_PAGE);
        assertEquals(profilePageInterpreterObj.getRecentPostDaysCutoff(), INTERPRETER_CUTOFF_SEVEN_DAYS);
        assertTrue(profilePageInterpreterObj.getForwardingAddressHandle() == null);
    }

    public void testPageInterpreterFollowersPageConstructor() throws ProcessingException {
        Fetcher fetcherObj;
        Document sampleFollowersDocument;
        PageInterpreter followersPageInterpreterObj;
        URL sampleFollowersURL;
        Iterator pageInterpreterRelationSetIter;
        RelationSet relationSetObj;
        HashSet<Boolean> booleanSet;

        fetcherObj = new Fetcher(TEN_SECONDS_IN_MILLISECONDS);

        try {
            sampleFollowersURL = sampleFollowersPath.toUri().toURL();
            sampleFollowersDocument = fetcherObj.fetchContentDocument(sampleFollowersURL);
        } catch (IOException exceptionObj) {
            fail(exceptionObj.getMessage());
            return;
        }

        followersPageInterpreterObj = new PageInterpreter(
            sampleFollowersDocument, userHandle, PageInterpreter.FOLLOWERS_PAGE, INTERPRETER_CUTOFF_THREE_DAYS);

        assertTrue(followersPageInterpreterObj.getPageDocument() == sampleFollowersDocument);
        assertTrue(followersPageInterpreterObj.getUserHandle() == userHandle);
        assertEquals(followersPageInterpreterObj.getPageType(), PageInterpreter.FOLLOWERS_PAGE);
        assertEquals(followersPageInterpreterObj.getRecentPostDaysCutoff(), INTERPRETER_CUTOFF_THREE_DAYS);
        assertTrue(followersPageInterpreterObj.getForwardingAddressHandle() == null);
    }

    public void testPageInterpreterFollowingPageConstructor() throws ProcessingException {
        Fetcher fetcherObj;
        Document sampleFollowingDocument;
        PageInterpreter followingPageInterpreterObj;
        URL sampleFollowingURL;

        fetcherObj = new Fetcher(TEN_SECONDS_IN_MILLISECONDS);

        try {
            sampleFollowingURL = sampleFollowingPath.toUri().toURL();
            sampleFollowingDocument = fetcherObj.fetchContentDocument(sampleFollowingURL);
        } catch (IOException exceptionObj) {
            fail(exceptionObj.getMessage());
            return;
        }

        followingPageInterpreterObj = new PageInterpreter(
            sampleFollowingDocument, userHandle, PageInterpreter.FOLLOWING_PAGE, INTERPRETER_CUTOFF_ONE_DAY);

        assertTrue(followingPageInterpreterObj.getPageDocument() == sampleFollowingDocument);
        assertTrue(followingPageInterpreterObj.getUserHandle() == userHandle);
        assertEquals(followingPageInterpreterObj.getPageType(), PageInterpreter.FOLLOWING_PAGE);
        assertEquals(followingPageInterpreterObj.getRecentPostDaysCutoff(), INTERPRETER_CUTOFF_ONE_DAY);
        assertTrue(followingPageInterpreterObj.getForwardingAddressHandle() == null);
    }

    public void testFollowingInterpretPageGeneratesResultSet() throws ProcessingException, MalformedURLException {
        Fetcher fetcherObj;
        Document sampleFollowingDocument;
        PageInterpreter followingPageInterpreterObj;
        URL sampleFollowingURL;
        Iterator<Relation> pageInterpreterRelationSetIter;
        RelationSet relationSetObj;
        HashSet<Boolean> booleanSet;

        fetcherObj = new Fetcher(TEN_SECONDS_IN_MILLISECONDS);

        try {
            sampleFollowingURL = sampleFollowingPath.toUri().toURL();
            sampleFollowingDocument = fetcherObj.fetchContentDocument(sampleFollowingURL);
        } catch (IOException exceptionObj) {
            fail(exceptionObj.getMessage());
            return;
        }

        followingPageInterpreterObj = new PageInterpreter(
            sampleFollowingDocument, userHandle, PageInterpreter.FOLLOWING_PAGE, INTERPRETER_CUTOFF_THREE_DAYS);
        followingPageInterpreterObj.interpretPage();

        relationSetObj = followingPageInterpreterObj.getRelationSet();

        assertEquals((Integer) relationSetObj.getRelationType(), (Integer) Relation.IS_FOLLOWED_BY);
        assertEquals(relationSetObj.getRelationPageNumber(), 1);
        assertEquals(relationSetObj.getProfileHandle().getUsername(), profileHandle.getUsername());
        assertEquals(relationSetObj.getProfileHandle().getInstance(), profileHandle.getInstance());

        pageInterpreterRelationSetIter = relationSetObj.iterator();
        
        while (pageInterpreterRelationSetIter.hasNext()) {
            Relation relationObj;

            booleanSet = new HashSet<Boolean>();
            relationObj = pageInterpreterRelationSetIter.next();

            for (int index = 0; index < gargronFollowingPage1.length; index++) {
                Handle handleObj;

                handleObj = gargronFollowingPage1[index];

                booleanSet.add(relationObj.getRelationHandle().getUsername().equals(handleObj.getUsername()) &&
                               relationObj.getRelationHandle().getInstance().equals(handleObj.getInstance()));
            }

            assertTrue(booleanSet.contains(true));
        }

        booleanSet = new HashSet<Boolean>();
        pageInterpreterRelationSetIter = relationSetObj.iterator();

        while (pageInterpreterRelationSetIter.hasNext()) {
            Relation relationObj;

            relationObj = (Relation) pageInterpreterRelationSetIter.next();

            booleanSet.add(relationObj.getRelationHandle().getUsername().equals(profileHandle.getUsername()) &&
                           relationObj.getRelationHandle().getInstance().equals(profileHandle.getInstance()));
        }

        assertFalse(booleanSet.contains(true));
    }

    public void testFollowersInterpretPageGeneratesResultSet() throws ProcessingException, MalformedURLException {
        Fetcher fetcherObj;
        Document sampleFollowersDocument;
        PageInterpreter followersPageInterpreterObj;
        URL sampleFollowersURL;
        Iterator<Relation> pageInterpreterRelationSetIter;
        RelationSet relationSetObj;
        HashSet<Boolean> booleanSet;

        fetcherObj = new Fetcher(TEN_SECONDS_IN_MILLISECONDS);

        try {
            sampleFollowersURL = sampleFollowersPath.toUri().toURL();
            sampleFollowersDocument = fetcherObj.fetchContentDocument(sampleFollowersURL);
        } catch (IOException exceptionObj) {
            fail(exceptionObj.getMessage());
            return;
        }

        followersPageInterpreterObj = new PageInterpreter(
            sampleFollowersDocument, userHandle, PageInterpreter.FOLLOWING_PAGE, INTERPRETER_CUTOFF_THREE_DAYS);
        followersPageInterpreterObj.interpretPage();

        relationSetObj = followersPageInterpreterObj.getRelationSet();

        assertEquals((Integer) relationSetObj.getRelationType(), (Integer) Relation.IS_FOLLOWER_OF);
        assertEquals(relationSetObj.getRelationPageNumber(), 1);
        assertEquals(relationSetObj.getProfileHandle().getUsername(), profileHandle.getUsername());
        assertEquals(relationSetObj.getProfileHandle().getInstance(), profileHandle.getInstance());

        pageInterpreterRelationSetIter = relationSetObj.iterator();
        
        while (pageInterpreterRelationSetIter.hasNext()) {
            Relation relationObj;

            booleanSet = new HashSet<Boolean>();
            relationObj = pageInterpreterRelationSetIter.next();

            for (int index = 0; index < gargronFollowersPage1.length; index++) {
                Handle handleObj;

                handleObj = gargronFollowersPage1[index];

                booleanSet.add(relationObj.getRelationHandle().getUsername().equals(handleObj.getUsername()) &&
                               relationObj.getRelationHandle().getInstance().equals(handleObj.getInstance()));
            }

            assertTrue(booleanSet.contains(true));
        }

        booleanSet = new HashSet<Boolean>();
        pageInterpreterRelationSetIter = relationSetObj.iterator();

        while (pageInterpreterRelationSetIter.hasNext()) {
            Relation relationObj;

            relationObj = (Relation) pageInterpreterRelationSetIter.next();

            booleanSet.add(relationObj.getRelationHandle().getUsername().equals(profileHandle.getUsername()) &&
                           relationObj.getRelationHandle().getInstance().equals(profileHandle.getInstance()));
        }

        assertFalse(booleanSet.contains(true));
    }

    public void testPageTimesUnparseable() throws ProcessingException, MalformedURLException {
        // "jarachnea/junit/https:__mastodon.social_@Gargron_unparseable_times.html"
        Fetcher fetcherObj;
        URL pageTimesUnparseableURL;
        Document pageTimesUnparseableDocument;
        PageInterpreter pageTimesUnparseableInterpreter;
        int pageInterpretationResult;

        fetcherObj = new Fetcher(TEN_SECONDS_IN_MILLISECONDS);

        try {
            pageTimesUnparseableURL = pageTimesUnparseablePath.toUri().toURL();
            pageTimesUnparseableDocument = fetcherObj.fetchContentDocument(pageTimesUnparseableURL);
        } catch (IOException exceptionObj) {
            fail(exceptionObj.getMessage());
            return;
        }

        pageTimesUnparseableInterpreter = new PageInterpreter(
            pageTimesUnparseableDocument, new Handle("Gargron", "mastodon.social"), PageInterpreter.PROFILE_PAGE, INTERPRETER_CUTOFF_SEVEN_DAYS);
        pageInterpretationResult = pageTimesUnparseableInterpreter.interpretPage();

        assertEquals(pageInterpretationResult, PageInterpreter.PAGE_TIMES_UNPARSEABLE);
    }

    public void testPageHasNoPosts() throws ProcessingException, MalformedURLException {
        // "jarachnea/junit/https:__mastodon.social_@szyczy"
        Fetcher fetcherObj;
        URL pageHasNoPostsURL;
        Document pageHasNoPostsDocument;
        PageInterpreter pageHasNoPostsInterpreter;
        int pageInterpretationResult;

        fetcherObj = new Fetcher(TEN_SECONDS_IN_MILLISECONDS);

        try {
            pageHasNoPostsURL = pageHasNoPostsPath.toUri().toURL();
            pageHasNoPostsDocument = fetcherObj.fetchContentDocument(pageHasNoPostsURL);
        } catch (IOException exceptionObj) {
            fail(exceptionObj.getMessage());
            return;
        }

        pageHasNoPostsInterpreter = new PageInterpreter(
            pageHasNoPostsDocument, new Handle("szyczy", "mastodon.social"), PageInterpreter.PROFILE_PAGE, INTERPRETER_CUTOFF_SEVEN_DAYS);
        pageInterpretationResult = pageHasNoPostsInterpreter.interpretPage();

        assertEquals(pageInterpretationResult, PageInterpreter.PAGE_HAS_NO_POSTS);
    }

    public void testPagePostsOutOfDate() throws ProcessingException, MalformedURLException {
        // "jarachnea/junit/https:__kolektiva.social_@Anarchotranny"
        Fetcher fetcherObj;
        URL pagePostsOutOfDateURL;
        Document pagePostsOutOfDateDocument;
        PageInterpreter pagePostsOutOfDateInterpreter;
        int pageInterpretationResult;

        fetcherObj = new Fetcher(TEN_SECONDS_IN_MILLISECONDS);

        try {
            pagePostsOutOfDateURL = pagePostsOutOfDatePath.toUri().toURL();
            pagePostsOutOfDateDocument = fetcherObj.fetchContentDocument(pagePostsOutOfDateURL);
        } catch (IOException exceptionObj) {
            fail(exceptionObj.getMessage());
            return;
        }

        pagePostsOutOfDateInterpreter = new PageInterpreter(
            pagePostsOutOfDateDocument, new Handle("Anarchotranny", "kolektiva.social"), PageInterpreter.PROFILE_PAGE, INTERPRETER_CUTOFF_SEVEN_DAYS);
        pageInterpretationResult = pagePostsOutOfDateInterpreter.interpretPage();

        assertEquals(pageInterpretationResult, PageInterpreter.PAGE_POSTS_OUT_OF_DATE);
    }

    public void testPageForwardingUnparseable() throws ProcessingException, MalformedURLException {
        // "jarachnea/junit/https:__mastodon.social_@clintlalonde_unparseable_forwarding.html"
        Fetcher fetcherObj;
        URL pageForwardingUnparseableURL;
        Document pageForwardingUnparseableDocument;
        PageInterpreter pageForwardingUnparseableInterpreter;
        int pageInterpretationResult;

        fetcherObj = new Fetcher(TEN_SECONDS_IN_MILLISECONDS);

        try {
            pageForwardingUnparseableURL = pageForwardingUnparseablePath.toUri().toURL();
            pageForwardingUnparseableDocument = fetcherObj.fetchContentDocument(pageForwardingUnparseableURL);
        } catch (IOException exceptionObj) {
            fail(exceptionObj.getMessage());
            return;
        }

        pageForwardingUnparseableInterpreter = new PageInterpreter(
            pageForwardingUnparseableDocument, new Handle("clintlalonde", "mastodon.social"),
            PageInterpreter.PROFILE_PAGE, INTERPRETER_CUTOFF_SEVEN_DAYS);
        pageInterpretationResult = pageForwardingUnparseableInterpreter.interpretPage();

        assertEquals(pageInterpretationResult, PageInterpreter.PAGE_FORWARDING_UNPARSEABLE);
    }

    public void testPageIsForwardingPage() throws ProcessingException, MalformedURLException {
        // "jarachnea/junit/https:__mastodon.social_@clintlalonde.html"
        Fetcher fetcherObj;
        URL pageIsForwardingPageURL;
        Document pageIsForwardingPageDocument;
        PageInterpreter pageIsForwardingPageInterpreter;
        int pageInterpretationResult;

        fetcherObj = new Fetcher(TEN_SECONDS_IN_MILLISECONDS);

        try {
            pageIsForwardingPageURL = pageIsForwardingPagePath.toUri().toURL();
            pageIsForwardingPageDocument = fetcherObj.fetchContentDocument(pageIsForwardingPageURL);
        } catch (IOException exceptionObj) {
            fail(exceptionObj.getMessage());
            return;
        }

        pageIsForwardingPageInterpreter = new PageInterpreter(
            pageIsForwardingPageDocument, new Handle("clintlalonde", "mastodon.social"), PageInterpreter.PROFILE_PAGE, INTERPRETER_CUTOFF_SEVEN_DAYS);
        pageInterpretationResult = pageIsForwardingPageInterpreter.interpretPage();

        assertEquals(pageInterpretationResult, PageInterpreter.PAGE_IS_FORWARDING_PAGE);
    }

    public void testPageBioUnparseable() throws ProcessingException, MalformedURLException {
        // "jarachnea/junit/https:__mastodon.social_@Gargron_unparseable_bio.html"
        Fetcher fetcherObj;
        URL pageBioUnparseableURL;
        Document pageBioUnparseableDocument;
        PageInterpreter pageBioUnparseableInterpreter;
        int pageInterpretationResult;

        fetcherObj = new Fetcher(TEN_SECONDS_IN_MILLISECONDS);

        try {
            pageBioUnparseableURL = pageBioUnparseablePath.toUri().toURL();
            pageBioUnparseableDocument = fetcherObj.fetchContentDocument(pageBioUnparseableURL);
        } catch (IOException exceptionObj) {
            fail(exceptionObj.getMessage());
            return;
        }

        pageBioUnparseableInterpreter = new PageInterpreter(
            pageBioUnparseableDocument, new Handle("Gargron", "mastodon.social"), PageInterpreter.PROFILE_PAGE, INTERPRETER_CUTOFF_SEVEN_DAYS);
        pageInterpretationResult = pageBioUnparseableInterpreter.interpretPage();

        assertEquals(pageInterpretationResult, PageInterpreter.PAGE_BIO_UNPARSEABLE);
    }

    public void testPageNextUrlsUnparseable() throws ProcessingException, MalformedURLException {
        // "jarachnea/junit/https:__mastodon.social_users_Gargron_followers?page=1.html"
        Fetcher fetcherObj;
        URL pageNextUrlsUnparseableURL;
        Document pageNextUrlsUnparseableDocument;
        PageInterpreter pageNextUrlsUnparseableInterpreter;
        int pageInterpretationResult;

        fetcherObj = new Fetcher(TEN_SECONDS_IN_MILLISECONDS);

        try {
            pageNextUrlsUnparseableURL = pageNextUrlsUnparseablePath.toUri().toURL();
            pageNextUrlsUnparseableDocument = fetcherObj.fetchContentDocument(pageNextUrlsUnparseableURL);
        } catch (IOException exceptionObj) {
            fail(exceptionObj.getMessage());
            return;
        }

        pageNextUrlsUnparseableInterpreter = new PageInterpreter(
            pageNextUrlsUnparseableDocument, new Handle("Gargron", "mastodon social"), PageInterpreter.FOLLOWERS_PAGE, INTERPRETER_CUTOFF_SEVEN_DAYS);
        pageInterpretationResult = pageNextUrlsUnparseableInterpreter.interpretPage();

        assertEquals(pageInterpretationResult, PageInterpreter.PAGE_NEXT_URLS_UNPARSEABLE);
    }

    public void testFoundPageBio() throws ProcessingException, MalformedURLException {
        // "jarachnea/junit/https:__mastodon.social_@Gargron.html"
        Fetcher fetcherObj;
        URL foundPageBioURL;
        Document foundPageBioDocument;
        PageInterpreter foundPageBioInterpreter;
        int pageInterpretationResult;

        fetcherObj = new Fetcher(TEN_SECONDS_IN_MILLISECONDS);

        try {
            foundPageBioURL = foundPageBioPath.toUri().toURL();
            foundPageBioDocument = fetcherObj.fetchContentDocument(foundPageBioURL);
        } catch (IOException exceptionObj) {
            fail(exceptionObj.getMessage());
            return;
        }

        foundPageBioInterpreter = new PageInterpreter(
            foundPageBioDocument, new Handle("Gargron", "mastodon.social"), PageInterpreter.PROFILE_PAGE, INTERPRETER_CUTOFF_SEVEN_DAYS);
        pageInterpretationResult = foundPageBioInterpreter.interpretPage();

        assertEquals(pageInterpretationResult, PageInterpreter.FOUND_PAGE_BIO);
    }

    public void testFoundNextFollowingPageUrl() throws ProcessingException, MalformedURLException {
        // "jarachnea/junit/https:__mastodon.social_users_Gargron_following?page=1.html"
        Fetcher fetcherObj;
        URL foundNextFollowingPageUrlURL;
        Document foundNextFollowingPageUrlDocument;
        PageInterpreter foundNextFollowingPageUrlInterpreter;
        int pageInterpretationResult;

        fetcherObj = new Fetcher(TEN_SECONDS_IN_MILLISECONDS);

        try {
            foundNextFollowingPageUrlURL = foundNextFollowingPageUrlPath.toUri().toURL();
            foundNextFollowingPageUrlDocument = fetcherObj.fetchContentDocument(foundNextFollowingPageUrlURL);
        } catch (IOException exceptionObj) {
            fail(exceptionObj.getMessage());
            return;
        }

        foundNextFollowingPageUrlInterpreter = new PageInterpreter(
            foundNextFollowingPageUrlDocument, new Handle("Gargron", "mastodon.social"), PageInterpreter.FOLLOWING_PAGE, INTERPRETER_CUTOFF_SEVEN_DAYS);
        pageInterpretationResult = foundNextFollowingPageUrlInterpreter.interpretPage();

        assertEquals(pageInterpretationResult, PageInterpreter.FOUND_NEXT_PAGE_URL);
    }

    public void testFoundNextFollowersPageUrl() throws ProcessingException, MalformedURLException {
        // "jarachnea/junit/https:__mastodon.social_users_Gargron_followers?page=1.html"
        Fetcher fetcherObj;
        URL foundNextFollowersPageUrlURL;
        Document foundNextFollowersPageUrlDocument;
        PageInterpreter foundNextFollowersPageUrlInterpreter;
        int pageInterpretationResult;

        fetcherObj = new Fetcher(TEN_SECONDS_IN_MILLISECONDS);

        try {
            foundNextFollowersPageUrlURL = foundNextFollowersPageUrlPath.toUri().toURL();
            foundNextFollowersPageUrlDocument = fetcherObj.fetchContentDocument(foundNextFollowersPageUrlURL);
        } catch (IOException exceptionObj) {
            fail(exceptionObj.getMessage());
            return;
        }

        foundNextFollowersPageUrlInterpreter = new PageInterpreter(
            foundNextFollowersPageUrlDocument, new Handle("Gargron", "mastodon.social"), PageInterpreter.FOLLOWERS_PAGE, INTERPRETER_CUTOFF_SEVEN_DAYS);
        pageInterpretationResult = foundNextFollowersPageUrlInterpreter.interpretPage();

        assertEquals(pageInterpretationResult, PageInterpreter.FOUND_NEXT_PAGE_URL);
    }

    public void testFoundNoNextFollowingPageUrl() throws ProcessingException, MalformedURLException {
        // "jarachnea/junit/https:__mastodon.social_users_Gargron_following?page=26"
        Fetcher fetcherObj;
        URL foundNoNextFollowingPageUrlURL;
        Document foundNoNextFollowingPageUrlDocument;
        PageInterpreter foundNoNextFollowingPageUrlInterpreter;
        int pageInterpretationResult;

        fetcherObj = new Fetcher(TEN_SECONDS_IN_MILLISECONDS);

        try {
            foundNoNextFollowingPageUrlURL = foundNoNextFollowingPageUrlPath.toUri().toURL();
            foundNoNextFollowingPageUrlDocument = fetcherObj.fetchContentDocument(foundNoNextFollowingPageUrlURL);
        } catch (IOException exceptionObj) {
            fail(exceptionObj.getMessage());
            return;
        }

        foundNoNextFollowingPageUrlInterpreter = new PageInterpreter(
            foundNoNextFollowingPageUrlDocument, new Handle("Gargron", "mastodon.social"),
            PageInterpreter.FOLLOWING_PAGE, INTERPRETER_CUTOFF_SEVEN_DAYS);
        pageInterpretationResult = foundNoNextFollowingPageUrlInterpreter.interpretPage();

        assertEquals(pageInterpretationResult, PageInterpreter.FOUND_NO_NEXT_PAGE_URL);
    }

    public void testFoundNoNextFollowersPageUrl() throws ProcessingException, MalformedURLException {
        // "jarachnea/junit/https:__mastodon.social_users_Gargron_followers?page=9979"
        Fetcher fetcherObj;
        URL foundNoNextFollowersPageUrlURL;
        Document foundNoNextFollowersPageUrlDocument;
        PageInterpreter foundNoNextFollowersPageUrlInterpreter;
        int pageInterpretationResult;

        fetcherObj = new Fetcher(TEN_SECONDS_IN_MILLISECONDS);

        try {
            foundNoNextFollowersPageUrlURL = foundNoNextFollowersPageUrlPath.toUri().toURL();
            foundNoNextFollowersPageUrlDocument = fetcherObj.fetchContentDocument(foundNoNextFollowersPageUrlURL);
        } catch (IOException exceptionObj) {
            fail(exceptionObj.getMessage());
            return;
        }

        foundNoNextFollowersPageUrlInterpreter = new PageInterpreter(
            foundNoNextFollowersPageUrlDocument, new Handle("Gargron", "mastodon.social"),
            PageInterpreter.FOLLOWERS_PAGE, INTERPRETER_CUTOFF_SEVEN_DAYS);
        pageInterpretationResult = foundNoNextFollowersPageUrlInterpreter.interpretPage();

        assertEquals(pageInterpretationResult, PageInterpreter.FOUND_NO_NEXT_PAGE_URL);
    }
}
