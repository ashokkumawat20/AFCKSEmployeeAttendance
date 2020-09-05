package in.afckstechnologies.employeeattendance.utils;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import static org.apache.http.protocol.HTTP.UTF_8;


@SuppressWarnings({"deprecation", "deprecation"})
public class WebClient {
    Context context;

    String TAG = "ServiceAccess";
    String response = "";
    String baseURL = "";
    String username = "asafcks";
    String password = "Ashok@19#20";

    String auth = new String(username + ":" + password);
    byte[] data1;

    {
        try {
            data1 = auth.getBytes(UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    String base64 = Base64.encodeToString(data1, Base64.NO_WRAP);

    @SuppressWarnings({"deprecation", "resource"})
    public String SendHttpPost(String URL, JSONObject jsonObjSend) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(URL);
            post.setHeader("Content-type", "application/json; charset=UTF-8");
            post.setHeader("Accept", "application/json");
            post.setHeader("X-API-KEY", "CODEX@123");
            post.setHeader("Authorization", "Basic "+base64);
            post.setEntity(new StringEntity(jsonObjSend.toString(), "UTF-8"));
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpConnectionParams.setSoTimeout(httpClient.getParams(), 10 * 1000);
            HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 10 * 1000);
            HttpResponse response = client.execute(post);
            Log.i(TAG, "resoponse" + response);
            HttpEntity entity = response.getEntity();

            return EntityUtils.toString(entity);

        } catch (Exception e) {
            // TODO: handle exception
            Log.i(TAG, "exception" + e);
        }
        Log.i(TAG, "response" + response);
        return response;
    }


    public String doGet(String url) {

        HttpResponse response = null;
        String resultantResponse = "";
        try {

            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(url);
            response = httpclient.execute(httpget);
        } catch (Exception e) {
            // TODO: handle exception
            Log.i(TAG, "exception" + e);
        }
        return response.toString();

    }

}


