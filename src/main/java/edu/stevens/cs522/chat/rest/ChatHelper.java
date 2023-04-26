package edu.stevens.cs522.chat.rest;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import edu.stevens.cs522.base.DateUtils;
import edu.stevens.cs522.chat.entities.Message;
import edu.stevens.cs522.chat.location.CurrentLocation;
import edu.stevens.cs522.chat.rest.work.PostMessageWorker;
import edu.stevens.cs522.chat.rest.work.SynchronizeWorker;
import edu.stevens.cs522.chat.services.RegisterService;
import edu.stevens.cs522.chat.settings.Settings;
import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

/**
 * Created by dduggan.
 */

public class ChatHelper {

    private static final String TAG = ChatHelper.class.getCanonicalName();

    public static final int SYNC_INTERVAL = 5;

    private final Context context;

    private final WorkManager workManager;

    private final CurrentLocation location;

    public ChatHelper(Context context) {
        this.context = context;
        this.workManager = WorkManager.getInstance(context);
        this.location = new CurrentLocation(context);
    }

    public void register(Uri chatServer, String chatName) {
        if (chatName != null && !chatName.isEmpty()) {
            // Register with the cloud chat service
            Intent intent = new Intent(context, RegisterService.class);
            intent.setData(chatServer);
            intent.putExtra(RegisterService.CHAT_NAME_KEY, chatName);
            context.startService(intent);
        }
    }

    public void postMessage(String chatRoom, String messageText) {
        if (messageText != null && !messageText.isEmpty()) {
            Log.d(TAG, "Posting message: " + messageText);
            Message mesg = new Message();
            mesg.messageText = messageText;
            mesg.appID = Settings.getAppId(context);
            mesg.chatroom = chatRoom;
            mesg.timestamp = DateUtils.now();
            mesg.latitude = location.getLatitude();
            mesg.longitude = location.getLongitude();
            mesg.sender = Settings.getChatName(context);

            Gson gson = new Gson();
            String messageJson = gson.toJson(mesg);

            Data inputData = new Data.Builder()
                    .putString(PostMessageWorker.MESSAGE_KEY, messageJson)
                    .build();

            OneTimeWorkRequest postMessageRequest = new OneTimeWorkRequest.Builder(PostMessageWorker.class)
                    .setInputData(inputData)
                    .build();

            workManager.enqueue(postMessageRequest);
        }
    }

    private PeriodicWorkRequest syncRequest;

    public void startMessageSync() {
        if (Settings.SYNC) {
            Log.d(TAG, "Enabling background synchronization of message database.");

            if (syncRequest != null) {
                throw new IllegalStateException("Trying to schedule sync when it is already scheduled!");
            }

            // Schedule periodic synchronization with message database
            syncRequest = new PeriodicWorkRequest.Builder(SynchronizeWorker.class, SYNC_INTERVAL, TimeUnit.MINUTES)
                    .build();
            workManager.enqueue(syncRequest);
        }
    }

    public void stopMessageSync() {
        if (Settings.SYNC) {
            Log.d(TAG, "Canceling background synchronization of message database.");

            if (syncRequest == null) {
                throw new IllegalStateException("Trying to cancel sync when it is not scheduled!");
            }

            // Cancel periodic synchronization with message database
            workManager.cancelWorkById(syncRequest.getId());
            syncRequest = null;
        }
    }
}