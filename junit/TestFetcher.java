package jarachnea.junit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.net.URL;

import junit.framework.TestCase;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import jarachnea.Fetcher;


public final class TestFetcher extends TestCase {
    Path sampleProfilePath = new File("jarachnea/junit/https:__mastodon.social_@Gargron.html").toPath().toAbsolutePath();

    public void testFetcherConstructor() throws IOException {
        Fetcher fetcherObj;

        fetcherObj = new Fetcher(10_000);

        assertEquals(fetcherObj.getConnectionTimeout(), 10_000);
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

