package net.pwnhub;

import okhttp3.*;

import java.io.IOException;

public class HttpUtils {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static OkHttpClient client = new OkHttpClient();

    public static Response post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response;
    }

    public static Response get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        return response;
    }

    public static Response anniePost(String server_ak, String text,String desp) throws IOException {
        //text:标题 desp:内容
        //https://sc.ftqq.com/SCU99919T9ba41e35e126d6f83c63703e87dce7a35ed2621d362a5.send
        RequestBody requestBody = new FormBody.Builder()
                .add("text", text)
                .add("desp",desp)
                .build();
        Request request = new Request.Builder()
                .url("https://sc.ftqq.com/" + server_ak + ".send")
                .post(requestBody)
                .build();
        Response response = client.newCall(request).execute();
        return response;
    }
}
