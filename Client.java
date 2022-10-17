package jarachnea;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.methods.GetMethod;

public class Client {
    private static final int DEFAULT_SOCKET_TIMEOUT = 5_000;

    private HttpClient httpClientObj;

    public Client() {
        HttpClientParams httpClientParamsObj;

        httpClientParamsObj = new HttpClientParams();
        httpClientParamsObj.setSoTimeout(DEFAULT_SOCKET_TIMEOUT);
        httpClientObj = new HttpClient(httpClientParamsObj);
    }

    public Response retrieveUrl(String urlString) throws IOException {
        GetMethod getMethodObj;
        Response responseObj;

        getMethodObj = new GetMethod(urlString);
        httpClientObj.executeMethod(getMethodObj);
        responseObj = new Response(getMethodObj);

        return responseObj;
    }
}
