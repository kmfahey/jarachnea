package jarachnea;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import java.io.InputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.methods.GetMethod;

public final class Response {
    private int statusCode;
    private int xRatelimitLimit;
    private String bodyString;
    private Document bodyDocument;
    private String charSetName;
    private URL requestURL;

    public URL getRequestURL() {
        return requestURL;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public int getXRatelimitLimit() {
        return xRatelimitLimit;
    }

    public Document getBodyDocument() {
        return bodyDocument;
    }

    public String getBodyString() {
        return bodyString;
    }

    public String getCharSetName() {
        return charSetName;
    }

    public Response(final GetMethod getMethodObj) throws IOException, MalformedURLException {
        Header xRatelimitLimitHeader;
        InputStream responseBodyStream;

        requestURL = new URL(getMethodObj.getURI().toString());
        statusCode = getMethodObj.getStatusCode();
        if (statusCode == 429) {
            xRatelimitLimitHeader = getMethodObj.getResponseHeader("X-Ratelimit-Limit");
            if (xRatelimitLimitHeader != null) {
                xRatelimitLimit = Integer.valueOf(xRatelimitLimitHeader.getValue());
            } else {
                xRatelimitLimit = 300;
            }
        } else if (statusCode == 200) {
            charSetName = getMethodObj.getResponseCharSet().toUpperCase();
            responseBodyStream = getMethodObj.getResponseBodyAsStream();
            if (responseBodyStream != null) {
                bodyDocument = Jsoup.parse(responseBodyStream, charSetName, getMethodObj.getURI().toString());
            }
        }
        getMethodObj.releaseConnection();
    }
}
