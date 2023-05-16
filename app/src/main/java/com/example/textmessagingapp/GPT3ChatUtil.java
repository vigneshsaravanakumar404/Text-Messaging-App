package com.example.textmessagingapp;


import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GPT3ChatUtil {


    private static final String API_URL = "https://api.openai.com/v1/engines/gpt-3.5-turbo/completions";
    private static final String API_TOKEN = APIKey.API_KEY;
    private static final OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.parse("application/json");
    private static final Handler handler = new Handler();

    public static void getChatResponse(String prompt, GPT3ChatCallback callback) {
        try {
            String requestBody = "{\"prompt\":\"" + prompt + "\",\"max_tokens\":50}";
            RequestBody body = RequestBody.create(JSON, requestBody);

            Request request = new Request.Builder()
                    .url(API_URL)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + API_TOKEN)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure("An error occurred.");
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String result = response.body().string();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onSuccess(result.split("\"text\":")[1]);
                            }
                        });
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onFailure("An error occurred.");
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            Log.e("TAG", "oops :(", e);
            callback.onFailure("something went wrong :(");
        }
    }

    public interface GPT3ChatCallback {
        void onSuccess(String response);

        void onFailure(String errorMessage);
    }
}
