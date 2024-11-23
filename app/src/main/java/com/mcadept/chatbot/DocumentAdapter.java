package com.mcadept.chatbot;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder> {
    private List<DocumentItem> documents;
    private OnDocumentDeleteListener deleteListener;
    private SimpleDateFormat dateFormat;

    public interface OnDocumentDeleteListener {
        void onDelete(DocumentItem document);
    }

    public DocumentAdapter(List<DocumentItem> documents, OnDocumentDeleteListener deleteListener) {
        this.documents = documents;
        this.deleteListener = deleteListener;
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public DocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_document, parent, false);
        return new DocumentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentViewHolder holder, int position) {
        DocumentItem document = documents.get(position);
        holder.nameText.setText(document.getName());
        holder.dateText.setText(dateFormat.format(new Date(document.getUploadTime())));
        holder.sizeText.setText(formatFileSize(document.getSize()));
        
        holder.deleteButton.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDelete(document);
            }
        });
    }

    @Override
    public int getItemCount() {
        return documents.size();
    }

    public void updateDocuments(List<DocumentItem> newDocuments) {
        this.documents.clear();
        this.documents.addAll(newDocuments);
        notifyDataSetChanged();
    }

    private String formatFileSize(long size) {
        if (size <= 0) return "0 B";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return String.format("%.1f %s", size / Math.pow(1024, digitGroups), units[digitGroups]);
    }

    static class DocumentViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        TextView dateText;
        TextView sizeText;
        ImageButton deleteButton;

        DocumentViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.documentName);
            dateText = itemView.findViewById(R.id.documentDate);
            sizeText = itemView.findViewById(R.id.documentSize);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
