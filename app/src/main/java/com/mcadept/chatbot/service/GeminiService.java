package com.mcadept.chatbot.service;

import android.util.Log;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.ai.client.generativeai.type.TextPart;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.Collections;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

public class GeminiService {
    private static final String TAG = "GeminiService";
    private static final String API_KEY = "AIzaSyCESSlIQkK4rZ74at7EZCQT_XT5yRY4K-I";
    private static GeminiService instance;
    private final GenerativeModelFutures model;
    private final Executor executor;

    private GeminiService() {
        try {
            GenerativeModel baseModel = new GenerativeModel("gemini-pro", API_KEY);
            model = GenerativeModelFutures.from(baseModel);
            executor = Executors.newSingleThreadExecutor();
        } catch (Exception e) {
            Log.e(TAG, "Error initializing GeminiService", e);
            throw new RuntimeException("Failed to initialize GeminiService: " + e.getMessage());
        }
    }

    public static synchronized GeminiService getInstance() {
        if (instance == null) {
            instance = new GeminiService();
        }
        return instance;
    }

    public void generateResponse(String prompt, final ResponseCallback callback) {
        if (prompt == null || prompt.trim().isEmpty()) {
            callback.onError("Please provide a valid input.");
            return;
        }

        try {
            Log.d(TAG, "Generating response for prompt: " + prompt);
            
            Content content = new Content.Builder()
                .addText("You are an AI assistant for the MCA department. Respond to: " + prompt)
                .build();
            
            ListenableFuture<GenerateContentResponse> future = model.generateContent(content);
            
            Futures.addCallback(future, new FutureCallback<GenerateContentResponse>() {
                @Override
                public void onSuccess(GenerateContentResponse result) {
                    try {
                        if (result == null) {
                            Log.e(TAG, "Received null response from Gemini");
                            callback.onError("Received empty response from AI model.");
                            return;
                        }
                        
                        String responseText = result.getText();
                        Log.d(TAG, "Received response: " + responseText);
                        
                        if (responseText == null || responseText.trim().isEmpty()) {
                            Log.e(TAG, "Received empty text from Gemini");
                            callback.onError("AI model returned empty response.");
                            return;
                        }
                        
                        callback.onSuccess(responseText);
                    } catch (Exception e) {
                        Log.e(TAG, "Error processing AI response", e);
                        handleError(e, callback);
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.e(TAG, "Error generating response", t);
                    handleError(t, callback);
                }
            }, MoreExecutors.directExecutor());  // Using directExecutor for immediate execution

        } catch (Exception e) {
            Log.e(TAG, "Error in generateResponse", e);
            handleError(e, callback);
        }
    }

    private void handleError(Throwable t, ResponseCallback callback) {
        String errorMessage;
        Log.e(TAG, "Error in Gemini service", t);
        
        if (t instanceof UnknownHostException || 
            (t.getMessage() != null && t.getMessage().toLowerCase().contains("network"))) {
            errorMessage = "Network error: Please check your internet connection and try again.";
        } else if (t instanceof TimeoutException || 
                   (t.getMessage() != null && t.getMessage().toLowerCase().contains("timeout"))) {
            errorMessage = "Request timed out. Please try again.";
        } else if (t instanceof IOException) {
            errorMessage = "Communication error: Please try again in a moment.";
        } else if (t.getMessage() != null) {
            String msg = t.getMessage().toLowerCase();
            if (msg.contains("permission") || msg.contains("denied")) {
                errorMessage = "Access denied: Please verify API permissions.";
            } else if (msg.contains("quota") || msg.contains("limit")) {
                errorMessage = "Service temporarily unavailable. Please try again later.";
            } else if (msg.contains("api key") || msg.contains("auth")) {
                errorMessage = "Authentication error: Please contact support.";
            } else if (msg.contains("invalid") || msg.contains("format")) {
                errorMessage = "Invalid request format. Please try rephrasing your message.";
            } else {
                errorMessage = "Error: " + t.getMessage();
            }
        } else {
            errorMessage = "Something unexpected happened. Please try again.";
        }
        
        Log.e(TAG, "Error details: " + errorMessage);
        callback.onError(errorMessage);
    }

    public void analyzePdfContent(String pdfContent, ResponseCallback callback) {
        if (pdfContent == null || pdfContent.trim().isEmpty()) {
            callback.onError("No PDF content to analyze.");
            return;
        }

        String prompt = "Based on this PDF content from the MCA department, please provide a relevant response: " + pdfContent;
        generateResponse(prompt, callback);
    }

    public interface ResponseCallback {
        void onSuccess(String response);
        void onError(String errorMessage);
    }
}
