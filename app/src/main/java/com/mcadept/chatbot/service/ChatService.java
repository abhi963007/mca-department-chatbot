package com.mcadept.chatbot.service;

import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;

public class ChatService {
    private static volatile ChatService instance;
    private final Handler mainHandler;
    
    private ChatService() {
        mainHandler = new Handler(Looper.getMainLooper());
    }
    
    public static ChatService getInstance() {
        if (instance == null) {
            synchronized (ChatService.class) {
                if (instance == null) {
                    instance = new ChatService();
                }
            }
        }
        return instance;
    }
    
    public void processMessage(@NonNull String message, @NonNull ChatCallback callback) {
        // Process on background thread
        new Thread(() -> {
            try {
                // Simulate network delay
                Thread.sleep(500);
                
                // Get response from knowledge base
                final String response = KnowledgeBase.getResponse(message);
                
                // Post response on main thread
                mainHandler.post(() -> callback.onResponse(response));
            } catch (Exception e) {
                e.printStackTrace();
                mainHandler.post(() -> callback.onResponse(null));
            }
        }).start();
    }
    
    public interface ChatCallback {
        void onResponse(String response);
    }
}
