package com.mcadept.chatbot.voice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import java.util.ArrayList;
import java.util.Locale;

public class VoiceManager implements RecognitionListener {
    private static final String TAG = "VoiceManager";
    private static final String UTTERANCE_ID = "CHATBOT_SPEECH";

    private final Context context;
    private final VoiceCallback callback;
    private SpeechRecognizer speechRecognizer;
    private TextToSpeech textToSpeech;
    private boolean isListening = false;
    private boolean isSpeaking = false;

    public interface VoiceCallback {
        void onSpeechRecognized(String text);
        void onSpeechError(String error);
        void onSpeakStart();
        void onSpeakDone();
    }

    public VoiceManager(Context context, VoiceCallback callback) {
        this.context = context;
        this.callback = callback;
        initializeSpeechRecognizer();
        initializeTextToSpeech();
    }

    private void initializeSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
            speechRecognizer.setRecognitionListener(this);
        } else {
            callback.onSpeechError("Speech recognition is not available on this device");
        }
    }

    private void initializeTextToSpeech() {
        textToSpeech = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(Locale.US);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    callback.onSpeechError("Text-to-Speech language not supported");
                }
            } else {
                callback.onSpeechError("Text-to-Speech initialization failed");
            }
        });

        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                isSpeaking = true;
                callback.onSpeakStart();
            }

            @Override
            public void onDone(String utteranceId) {
                isSpeaking = false;
                callback.onSpeakDone();
            }

            @Override
            public void onError(String utteranceId) {
                isSpeaking = false;
                callback.onSpeechError("Error while speaking");
            }
        });
    }

    public void startListening() {
        if (speechRecognizer == null) {
            callback.onSpeechError("Speech recognizer not available");
            return;
        }

        if (isListening) {
            stopListening();
        }

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);

        try {
            isListening = true;
            speechRecognizer.startListening(intent);
        } catch (Exception e) {
            isListening = false;
            callback.onSpeechError("Error starting speech recognition: " + e.getMessage());
        }
    }

    public void stopListening() {
        if (speechRecognizer != null && isListening) {
            speechRecognizer.stopListening();
            isListening = false;
        }
    }

    public void speak(String text) {
        if (textToSpeech == null) {
            callback.onSpeechError("Text-to-Speech not available");
            return;
        }

        Bundle params = new Bundle();
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, UTTERANCE_ID);
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, params, UTTERANCE_ID);
    }

    public void release() {
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
            speechRecognizer = null;
        }
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
        }
    }

    public boolean isListening() {
        return isListening;
    }

    public boolean isSpeaking() {
        return isSpeaking;
    }

    // RecognitionListener implementation
    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.d(TAG, "Ready for speech");
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.d(TAG, "Beginning of speech");
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        // Can be used to show voice amplitude
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        // Not used in this implementation
    }

    @Override
    public void onEndOfSpeech() {
        isListening = false;
        Log.d(TAG, "End of speech");
    }

    @Override
    public void onError(int error) {
        isListening = false;
        String errorMessage;
        switch (error) {
            case SpeechRecognizer.ERROR_AUDIO:
                errorMessage = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                errorMessage = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                errorMessage = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                errorMessage = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                errorMessage = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                errorMessage = "No speech input";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                errorMessage = "Recognition service busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                errorMessage = "Server error";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                errorMessage = "No speech input";
                break;
            default:
                errorMessage = "Unknown error";
                break;
        }
        callback.onSpeechError(errorMessage);
    }

    @Override
    public void onResults(Bundle results) {
        isListening = false;
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (matches != null && !matches.isEmpty()) {
            String text = matches.get(0);
            callback.onSpeechRecognized(text);
        }
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        // Not used in this implementation
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        // Not used in this implementation
    }
}
