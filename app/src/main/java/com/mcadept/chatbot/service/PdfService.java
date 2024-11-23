package com.mcadept.chatbot.service;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.text.PDFTextStripper;
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;
import java.io.InputStream;

public class PdfService {
    private static final String TAG = "PdfService";
    private final Context context;

    public PdfService(Context context) {
        this.context = context;
        // Initialize PdfBox
        PDFBoxResourceLoader.init(context);
    }

    public void extractTextFromPdf(Uri pdfUri, PdfCallback callback) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(pdfUri);
            PDDocument document = PDDocument.load(inputStream);
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            document.close();
            callback.onSuccess(text);

        } catch (Exception e) {
            Log.e(TAG, "Error extracting text from PDF", e);
            callback.onError("Error processing PDF file: " + e.getMessage());
        }
    }

    public interface PdfCallback {
        void onSuccess(String text);
        void onError(String error);
    }
}
