package edu.stevens.cs522.chat.databases;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import edu.stevens.cs522.chat.entities.Message;

@Dao
public interface MessageDao {

    @Query("SELECT * FROM messages WHERE chatroom = :chatroom")
    LiveData<List<Message>> fetchAllMessages(String chatroom);

    @Query("SELECT * FROM messages WHERE sender = :peerName")
    LiveData<List<Message>> fetchMessagesFromPeer(String peerName);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long persist(Message message);

}
