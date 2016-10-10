package jcsoluciones.com.socialfootball.provider;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Response;

/**
 * Created by ADMIN on 10/10/2016.
 */
public class DBaccess {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public OkHttpClient client;

    public DBaccess() {
        client = new OkHttpClient();
    }

    private String get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        okhttp3.Response response = client.newCall(request).execute();
        return response.body().string();
    }

    private String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        okhttp3.Response response = client.newCall(request).execute();
        return response.body().string();
    }


}
