package jarachnea;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.methods.GetMethod;

public final class Client {
    private static final int DEFAULT_SOCKET_TIMEOUT = 5_000;
    private static final long DEFAULT_CONNECTION_MANAGER_TIMEOUT = 5_000L;
    private static final int DEFAULT_CONNECTION_TIMEOUT = 5_000;

    private HttpClient httpClientObj;

    public Client() {
        HttpClientParams httpClientParamsObj;

        httpClientParamsObj = new HttpClientParams();
        httpClientParamsObj.setParameter(HttpConnectionParams.SO_TIMEOUT, DEFAULT_SOCKET_TIMEOUT);
        httpClientParamsObj.setParameter(HttpConnectionParams.CONNECTION_TIMEOUT, DEFAULT_CONNECTION_TIMEOUT);
        httpClientParamsObj.setParameter(HttpClientParams.CONNECTION_MANAGER_TIMEOUT, DEFAULT_CONNECTION_MANAGER_TIMEOUT);
        httpClientObj = new HttpClient(httpClientParamsObj);
    }

    public Response retrieveUrl(final String urlString) throws IOException, NullPointerException {
        GetMethod getMethodObj;
        Response responseObj;

        getMethodObj = new GetMethod(urlString);
        httpClientObj.executeMethod(getMethodObj);

        responseObj = new Response(getMethodObj);

        return responseObj;
    }
}
