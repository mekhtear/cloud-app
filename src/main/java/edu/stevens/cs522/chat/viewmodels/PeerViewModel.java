package edu.stevens.cs522.chat.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import edu.stevens.cs522.chat.databases.ChatDatabase;
import edu.stevens.cs522.chat.entities.Message;
import edu.stevens.cs522.chat.entities.Peer;

public class PeerViewModel extends AndroidViewModel {

    public final static String TAG = PeerViewModel.class.getCanonicalName();

    private ChatDatabase chatDatabase;

    private LiveData<List<Message>> messages;

    private Peer currentPeer;

    public PeerViewModel(Application context) {
        super(context);
        Log.d(TAG, "Getting database in PeerViewModel");
        chatDatabase = ChatDatabase.getInstance(context);
    }

    public void savePeer(Peer peer) {
        chatDatabase.peerDao().insert(peer);
    }

    public void deletePeer(Peer peer) {
        chatDatabase.peerDao().deletePeer(peer);
    }

    public LiveData<List<Message>> fetchMessagesFromPeer(Peer peer) {
        if (messages == null || !currentPeer.equals(peer)) {
            messages = loadMessages(peer);
        }
        return messages;
    }

    private LiveData<List<Message>> loadMessages(Peer peer) {
        currentPeer = peer;
        return chatDatabase.messageDao().fetchMessagesFromPeer(peer.name);
    }

    public void setCurrentPeer(Peer peer) {
        currentPeer = peer;
    }

    @Override
    public void onCleared() {
        super.onCleared();
        Log.d(TAG, "Clearing PeerViewModel...");
        currentPeer = null;
        chatDatabase = null;
    }
}
