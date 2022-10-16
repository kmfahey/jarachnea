package jarachnea.junit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.net.URL;

import junit.framework.TestCase;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import jarachnea.Fetcher;


public final class TestFetcher extends TestCase {
    private Path sampleProfilePath = new File("jarachnea/junit/https:__mastodon.social_@Gargron.html").toPath().toAbsolutePath();

    private int tenSecondsInMilliseconds = 10_000;

    public void testFetcherConstructor() throws IOException {
        Fetcher fetcherObj;

        fetcherObj = new Fetcher(tenSecondsInMilliseconds);

        assertEquals(fetcherObj.getConnectionTimeout(), tenSecondsInMilliseconds);
    }

    public void testFetcherParseURLToDocument() {
        Fetcher fetcherObj;
        Document sampleProfileDocument;
        Element profileBioElement;
        URL sampleProfileURL;

        try {
            sampleProfileURL = sampleProfilePath.toUri().toURL();
            fetcherObj = new Fetcher(tenSecondsInMilliseconds);
            sampleProfileDocument = fetcherObj.fetchContentDocument(sampleProfileURL);
        } catch (IOException exceptionObj) {
            fail(exceptionObj.getMessage());
            return;
        }

        profileBioElement = sampleProfileDocument.getElementsByClass("public-account-bio").first();
        assertEquals(profileBioElement.tag().getName(), "div");
    }
}

