package jarachnea;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.methods.GetMethod;

public class Response {
    private int statusCode;
    private int xRatelimitLimit;
    private String bodyString;
    private Document bodyDocument;
    private GetMethod methodObj;
    private String charsetName;

    public int getStatusCode() {
        return statusCode;
    }

    public int getXRatelimitLimit() {
        return xRatelimitLimit;
    }

    public String getBodyString() {
        return bodyString;
    }

    public Document getBodyDocument() {
        return bodyDocument;
    }

    public String getCharsetName() {
        return charsetName;
    }

    public Response(GetMethod methodObj) throws IOException {
        Header xRatelimitLimitHeader;

        statusCode = methodObj.getStatusCode();
        if (statusCode == 429) {
            xRatelimitLimitHeader = methodObj.getResponseHeader("X-Ratelimit-Limit");
            if (xRatelimitLimitHeader != null) {
                xRatelimitLimit = Integer.valueOf(xRatelimitLimitHeader.getValue());
            } else {
                xRatelimitLimit = 300;
            }
        } else if (statusCode == 200) {
            bodyString = methodObj.getResponseBodyAsString();
            if (bodyString != null) {
                bodyDocument = Jsoup.parse(bodyString, methodObj.getURI().toString());
            }
        }
    }
}
