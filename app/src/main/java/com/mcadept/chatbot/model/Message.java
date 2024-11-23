package com.mcadept.chatbot.model;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

public class Message implements Parcelable {
    public static final int TYPE_USER = 1;
    public static final int TYPE_BOT = 2;

    private final String text;
    private final int type;
    private final long timestamp;

    public Message(@NonNull String text, int type) {
        if (type != TYPE_USER && type != TYPE_BOT) {
            throw new IllegalArgumentException("Invalid message type");
        }
        
        this.text = text;
        this.type = type;
        this.timestamp = System.currentTimeMillis();
    }

    protected Message(Parcel in) {
        text = in.readString();
        type = in.readInt();
        timestamp = in.readLong();
    }

    @NonNull
    public String getText() {
        return text;
    }

    public int getType() {
        return type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(text);
        dest.writeInt(type);
        dest.writeLong(timestamp);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    @NonNull
    @Override
    public String toString() {
        return "Message{" +
                "text='" + text + '\'' +
                ", type=" + type +
                ", timestamp=" + timestamp +
                '}';
    }
}
