package com.mcadept.chatbot;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mcadept.chatbot.adapter.ChatAdapter;
import com.mcadept.chatbot.model.Message;
import com.mcadept.chatbot.service.ChatService;
import com.mcadept.chatbot.service.GeminiService;
import com.mcadept.chatbot.service.PdfService;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private RecyclerView chatRecyclerView;
    private TextInputEditText messageEditText;
    private FloatingActionButton sendButton;
    private FloatingActionButton uploadPdfButton;
    private ChatAdapter chatAdapter;
    private ChatService chatService;
    private GeminiService geminiService;
    private PdfService pdfService;
    private ArrayList<Message> messageHistory;

    private final ActivityResultLauncher<String> pdfPicker = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    processPdfDocument(uri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_main);
            Log.d(TAG, "onCreate: Setting up activity");

            // Initialize message history
            messageHistory = new ArrayList<>();

            // Initialize services
            geminiService = GeminiService.getInstance();
            pdfService = new PdfService(this);

            // Initialize UI components
            initializeViews();
            setupToolbar();
            setupRecyclerView();
            setupChatService();
            setupListeners();

            // Add welcome message if this is a fresh start
            if (savedInstanceState == null) {
                addBotMessage(getString(R.string.welcome_message));
            }
        } catch (Exception e) {
            Log.e(TAG, "onCreate: Error initializing activity", e);
        }
    }

    private void setupToolbar() {
        try {
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(R.string.app_name);
            }
        } catch (Exception e) {
            Log.e(TAG, "setupToolbar: Error setting up toolbar", e);
        }
    }

    private void initializeViews() {
        try {
            Log.d(TAG, "initializeViews: Finding views");
            chatRecyclerView = findViewById(R.id.chatRecyclerView);
            messageEditText = findViewById(R.id.messageEditText);
            sendButton = findViewById(R.id.sendButton);
            uploadPdfButton = findViewById(R.id.uploadPdfButton);
        } catch (Exception e) {
            Log.e(TAG, "initializeViews: Error finding views", e);
        }
    }

    private void setupRecyclerView() {
        try {
            Log.d(TAG, "setupRecyclerView: Setting up RecyclerView");
            chatAdapter = new ChatAdapter();
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setStackFromEnd(true);
            chatRecyclerView.setLayoutManager(layoutManager);
            chatRecyclerView.setAdapter(chatAdapter);
        } catch (Exception e) {
            Log.e(TAG, "setupRecyclerView: Error setting up RecyclerView", e);
        }
    }

    private void setupChatService() {
        try {
            Log.d(TAG, "setupChatService: Initializing ChatService");
            chatService = ChatService.getInstance();
        } catch (Exception e) {
            Log.e(TAG, "setupChatService: Error initializing ChatService", e);
        }
    }

    private void setupListeners() {
        try {
            Log.d(TAG, "setupListeners: Setting up click listeners");
            
            // Send button click listener
            sendButton.setOnClickListener(v -> {
                try {
                    sendMessage();
                } catch (Exception e) {
                    Log.e(TAG, "onClick: Error sending message", e);
                    addBotMessage(getString(R.string.error_message));
                }
            });

            // Upload PDF button click listener
            uploadPdfButton.setOnClickListener(v -> {
                try {
                    pdfPicker.launch("application/pdf");
                } catch (Exception e) {
                    Log.e(TAG, "onClick: Error launching PDF picker", e);
                    addBotMessage(getString(R.string.pdf_error));
                }
            });

            // EditText done/enter key listener
            messageEditText.setOnEditorActionListener((v, actionId, event) -> {
                try {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        sendMessage();
                        return true;
                    }
                } catch (Exception e) {
                    Log.e(TAG, "onEditorAction: Error handling editor action", e);
                    addBotMessage(getString(R.string.error_message));
                }
                return false;
            });
        } catch (Exception e) {
            Log.e(TAG, "setupListeners: Error setting up listeners", e);
        }
    }

    private void sendMessage() {
        try {
            String messageText = messageEditText.getText().toString().trim();
            if (!messageText.isEmpty()) {
                Log.d(TAG, "sendMessage: Sending message: " + messageText);
                
                // Add user message
                addUserMessage(messageText);
                
                // Clear input
                messageEditText.setText("");

                // Process message with Gemini
                geminiService.generateResponse(messageText, new GeminiService.ResponseCallback() {
                    @Override
                    public void onSuccess(String response) {
                        runOnUiThread(() -> addBotMessage(response));
                    }

                    @Override
                    public void onError(String errorMessage) {
                        runOnUiThread(() -> addBotMessage(errorMessage));
                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "sendMessage: Error processing message", e);
            addBotMessage(getString(R.string.error_message));
        }
    }

    private void processPdfDocument(Uri uri) {
        try {
            addBotMessage(getString(R.string.pdf_processing));
            
            pdfService.extractTextFromPdf(uri, new PdfService.PdfCallback() {
                @Override
                public void onSuccess(String text) {
                    geminiService.analyzePdfContent(text, new GeminiService.ResponseCallback() {
                        @Override
                        public void onSuccess(String response) {
                            runOnUiThread(() -> addBotMessage(response));
                        }

                        @Override
                        public void onError(String errorMessage) {
                            runOnUiThread(() -> addBotMessage(errorMessage));
                        }
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> addBotMessage(error));
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "processPdfDocument: Error processing PDF", e);
            addBotMessage(getString(R.string.pdf_error));
        }
    }

    private void addUserMessage(String text) {
        Message message = new Message(text, Message.TYPE_USER);
        messageHistory.add(message);
        chatAdapter.addMessage(message);
        scrollToBottom();
    }

    private void addBotMessage(String text) {
        Message message = new Message(text, Message.TYPE_BOT);
        messageHistory.add(message);
        chatAdapter.addMessage(message);
        scrollToBottom();
    }

    private void scrollToBottom() {
        chatRecyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("messageHistory", messageHistory);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            ArrayList<Message> savedMessages = savedInstanceState.getParcelableArrayList("messageHistory");
            if (savedMessages != null) {
                messageHistory = savedMessages;
                for (Message message : messageHistory) {
                    chatAdapter.addMessage(message);
                }
            }
        }
    }
}
