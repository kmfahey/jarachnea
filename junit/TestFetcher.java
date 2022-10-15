package jarachnea.junit;

import java.io.*;
import java.nio.file.*;
import java.net.*;
import java.text.*;

import junit.framework.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import jarachnea.*;

public class TestFetcher extends TestCase {
    Path sampleProfilePath = new File("jarachnea/junit/https:__mastodon.social_@Gargron.html").toPath().toAbsolutePath();

    public void testFetcherConstructor() throws IOException {
        Fetcher fetcherObj;

        fetcherObj = new Fetcher(10_000);

        assertEquals(fetcherObj.connectionTimeout, 10_000);
    }

    public void testFetcherParseURLToDocument() {
        Fetcher fetcherObj;
        Document sampleProfileDocument;
        Element profileBioElement;
        URL sampleProfileURL;

        try {
            sampleProfileURL = sampleProfilePath.toUri().toURL();
            fetcherObj = new Fetcher(10_000);
            sampleProfileDocument = fetcherObj.fetchContentDocument(sampleProfileURL);
        } catch (IOException exceptionObj) {
            fail(exceptionObj.getMessage());
            return;
        }

        profileBioElement = sampleProfileDocument.getElementsByClass("public-account-bio").first();
        assertEquals(profileBioElement.tag().getName(), "div");
    }
}

