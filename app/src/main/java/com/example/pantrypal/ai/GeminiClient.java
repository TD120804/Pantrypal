package com.example.pantrypal.ai;

import com.example.pantrypal.BuildConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GeminiClient {

    private static final String URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" +
                    BuildConfig.GEMINI_API_KEY;

    private final OkHttpClient client = new OkHttpClient();

    public interface GeminiCallback {
        void onSuccess(String response);
        void onError(String error);
    }
    public void generateRecipe(String prompt, GeminiCallback callback) {

        try {

            JSONObject text = new JSONObject();
            text.put("text", prompt);

            JSONArray parts = new JSONArray();
            parts.put(text);

            JSONObject content = new JSONObject();
            content.put("parts", parts);

            JSONArray contents = new JSONArray();
            contents.put(content);

            JSONObject body = new JSONObject();
            body.put("contents", contents);

            RequestBody requestBody = RequestBody.create(
                    body.toString(),
                    MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url(URL)
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {

                    callback.onError(e.getMessage());

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    if (!response.isSuccessful()) {

                        callback.onError(response.message());
                        return;
                    }
                    String json = response.body().string();
                    callback.onSuccess(extractText(json));
                }
            });

        } catch (Exception e) {

            callback.onError(e.getMessage());
        }
    }
    private String extractText(String json) {

        try {

            JSONObject root = new JSONObject(json);

            return root
                    .getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text");

        } catch (Exception e) {

            return "Failed to read AI response.";

        }
    }

}
