package com.example.pantrypal.ai;

import android.util.Log;

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
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.1-flash-lite:generateContent";

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
                    .addHeader("x-goog-api-key", BuildConfig.GEMINI_API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .post(requestBody)
                    .build();
            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {

                    Log.e("AI_DEBUG", "Network Failure", e);

                    callback.onError(e.getMessage());

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    android.util.Log.d("AI_DEBUG", "HTTP Code = " + response.code());

                    if (!response.isSuccessful()) {

                        String errorBody = response.body() != null
                                ? response.body().string()
                                : "No response body";

                        android.util.Log.e("AI_DEBUG", "HTTP " + response.code());
                        android.util.Log.e("AI_DEBUG", errorBody);

                        callback.onError(errorBody);

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
