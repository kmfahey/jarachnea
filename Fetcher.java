package jarachnea;

import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;
import java.io.InputStream;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public final class Fetcher {
    private int connectionTimeout;

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public Fetcher(final int timeoutArg) {
        connectionTimeout = timeoutArg;
    }

    public Document fetchContentDocument(final URL contentURL) throws MalformedURLException, IOException {
        URLConnection connectObj;
        InputStream contentStream;
        String contentType;
        String[] contentTypeParts;
        String contentCharset;
        Document contentDocument;

        connectObj = contentURL.openConnection();
        connectObj.setReadTimeout(connectionTimeout);
        contentType = connectObj.getContentType();
        contentStream = connectObj.getInputStream();

        if (contentType.contains("charset=")) {
            contentTypeParts = contentType.split("charset=");
            contentCharset = contentTypeParts[1];
        } else {
            contentCharset = "UTF-8";
        }

        contentDocument = Jsoup.parse(contentStream, contentCharset, contentURL.toString());
        return contentDocument;
    }
}
