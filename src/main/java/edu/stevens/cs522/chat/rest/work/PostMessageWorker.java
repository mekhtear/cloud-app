package edu.stevens.cs522.chat.rest.work;

import android.app.Activity;
import android.content.Context;
import android.os.ResultReceiver;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.gson.Gson;

import edu.stevens.cs522.chat.entities.Message;
import edu.stevens.cs522.chat.rest.RequestProcessor;
import edu.stevens.cs522.chat.rest.request.ChatServiceResponse;
import edu.stevens.cs522.chat.rest.request.ErrorResponse;
import edu.stevens.cs522.chat.rest.request.PostMessageRequest;

public class PostMessageWorker extends Worker {

    private static final String TAG = PostMessageWorker.class.getCanonicalName();

    public static final String MESSAGE_KEY = "message";

    public static final String RESULT_RECEIVER_KEY = "receiver";

    private final Message message;

    private final ResultReceiver receiver;

    public PostMessageWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        Gson gson = new Gson();
        Data data = workerParams.getInputData();
        String messageJson = data.getString(MESSAGE_KEY);
        if (messageJson == null) {
            throw new IllegalStateException("Missing message for post message worker!");
        }
        message = gson.fromJson(messageJson, Message.class);

        String receiverJson = data.getString(RESULT_RECEIVER_KEY);
        if (receiverJson != null) {
            receiver = gson.fromJson(receiverJson, ResultReceiver.class);
        } else {
            receiver = null;
            Log.d(TAG, "Missing receiver");
        }
    }

    @NonNull
    @Override
    public Result doWork() {

        PostMessageRequest postMessageRequest = new PostMessageRequest(message);

        RequestProcessor processor = RequestProcessor.getInstance(getApplicationContext());

        ChatServiceResponse response = processor.process(postMessageRequest);

        if (receiver != null) {
            // Use receiver to call back to UI

            if (response instanceof ErrorResponse) {
                Log.d(TAG, String.format("Failed to send message ('%s'): %s", message.messageText, response.httpResponseMessage));
                // TODO let activity know request failed
                receiver.send(Activity.RESULT_CANCELED, null);
            } else {
                Log.d(TAG, String.format("Message sent ('%s')!", message.messageText));
                // TODO let activity know request succeeded
                receiver.send(Activity.RESULT_OK, null);
            }
        }

        if (response instanceof ErrorResponse) {
            Log.i(TAG, "Failed to upload chat message: " + response.httpResponseMessage);
            return Result.failure();
        }

        return Result.success();
    }
}
