package jarachnea;

import java.net.*;
import java.io.*;
import java.text.*;

import org.jsoup.*;
import org.jsoup.nodes.*;

public class Fetcher {
    public int connectionTimeout;

    public Fetcher(int timeoutArg) {
        connectionTimeout = timeoutArg;
    }

    public Document fetchContentDocument(URL contentURL) throws MalformedURLException, IOException {
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
