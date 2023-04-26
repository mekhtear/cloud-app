package edu.stevens.cs522.chat.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;
import java.util.UUID;

import edu.stevens.cs522.base.DateUtils;

@Entity(tableName = "messages",
        foreignKeys = {
                @ForeignKey(entity = Chatroom.class,
                        parentColumns = "name",
                        childColumns = "chatroom",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Peer.class,
                        parentColumns = "id",
                        childColumns = "senderId",
                        onDelete = ForeignKey.SET_NULL)
        },
        indices = {@Index(value = {"chatroom"}), @Index(value = {"senderId"})})
public class Message implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String chatroom;

    public String messageText;

    // Unique id, assigned by server
    public long seqNum;

    // The id of the app that created this message
    @TypeConverters({UUIDConverter.class})
    public UUID appID;
    @TypeConverters({DateConverter.class})
    public Date timestamp;

    public Double latitude;

    public Double longitude;

    public String sender;

    public long senderId;

    public Message() {
    }

    public Message(Parcel in) {
        id = in.readLong();
        chatroom = in.readString();
        messageText = in.readString();
        seqNum = in.readLong();
        appID = UUID.fromString(in.readString());
        timestamp = DateUtils.readDate(in);
        latitude = in.readDouble();
        longitude = in.readDouble();
        sender = in.readString();
        senderId = in.readLong();
    }

    @Override
    public String toString() {
        return messageText;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(id);
        out.writeString(chatroom);
        out.writeString(messageText);
        out.writeLong(seqNum);
        out.writeString(appID.toString());
        DateUtils.writeDate(out, timestamp);
        out.writeDouble(latitude);
        out.writeDouble(longitude);
        out.writeString(sender);
        out.writeLong(senderId);
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {

        @Override
        public Message createFromParcel(Parcel source) {
            return new Message(source);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }

    };

}
