package com.vsnmobil.valrt.voip;
//import
import java.io.IOException;
//import
import java.io.InputStream;
//import
import java.io.InputStreamReader;
//import
import java.io.Reader;
//import
import org.apache.http.HttpResponse;
//import
import org.apache.http.client.HttpClient;
//import
import org.apache.http.client.methods.HttpGet;
//import
import org.apache.http.conn.scheme.PlainSocketFactory;
//import
import org.apache.http.conn.scheme.Scheme;
//import
import org.apache.http.conn.scheme.SchemeRegistry;
//import
import org.apache.http.conn.ssl.SSLSocketFactory;
//import
import org.apache.http.impl.client.DefaultHttpClient;
//import
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
//import
import org.apache.http.params.BasicHttpParams;
//import
import org.apache.http.params.HttpConnectionParams;
/**
 * HttpHelper.java
 *
 * This is a helper class which is used to communicate with the VOIP and SMS Web-services.
 *
 */
public abstract class HttpHelper{
    /** The http client. */
    private static HttpClient httpClient;
    /**
     * Ensure http client.
     */
    private static void ensureHttpClient()
    {
        if (httpClient != null)
            return;
        BasicHttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, 45000);
        HttpConnectionParams.setSoTimeout(params, 30000);

        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", new PlainSocketFactory(), 80));
        try {
            registry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        } catch (Exception e) {}

        ThreadSafeClientConnManager connManager = new ThreadSafeClientConnManager(params, registry);
        httpClient = new DefaultHttpClient(connManager, params);
    }
    /**
     * String from input stream.
     *
     * @param is the is
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private static String stringFromInputStream(InputStream is) throws IOException
    {
        char[] buf = new char[1024];
        StringBuilder out = new StringBuilder();

        Reader in = new InputStreamReader(is, "UTF-8");

        int bin;
        while ((bin = in.read(buf, 0, buf.length)) >= 0) {
            out.append(buf, 0, bin);
        }

        return out.toString();
    }
    /**
     * Http get.
     *
     * @param url the url
     * @return the string
     * @throws Exception the exception
     */
    public static String httpGet(String url) throws Exception
    {
        ensureHttpClient();

        HttpGet request = new HttpGet(url);
        HttpResponse response = httpClient.execute(request);
        if (response != null)
        {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200)
                return stringFromInputStream(response.getEntity().getContent());
            else
                throw new Exception("Got error code " + statusCode + " from server");
        }
        throw new Exception("Unable to connect to server");
    }
}
