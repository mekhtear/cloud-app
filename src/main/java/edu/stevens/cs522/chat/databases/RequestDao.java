package edu.stevens.cs522.chat.databases;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
import java.util.UUID;

import edu.stevens.cs522.chat.entities.Message;

@Dao
public abstract class RequestDao {

    @Query("SELECT MAX(seqNum) AS max_seq_num FROM messages WHERE seqNum <> 0")
    public abstract long getLastSequenceNumber();

    @Query("SELECT * FROM messages WHERE seqNum = 0")
    public abstract List<Message> getUnsentMessages();

    @Query("UPDATE messages SET seqNum = :seqNum WHERE id = :id")
    public abstract void updateSeqNum(long id, long seqNum);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long insert(Message message);

    @Update
    protected abstract void update(Message message);

    public void upsert(UUID appID, Message message) {
        if (appID.equals(message.appID)) {
            update(message);
        } else {
            message.id = 0;
            insert(message);
        }
    }

}
