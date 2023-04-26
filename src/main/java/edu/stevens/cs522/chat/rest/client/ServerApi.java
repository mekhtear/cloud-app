package edu.stevens.cs522.chat.rest.client;

import edu.stevens.cs522.chat.entities.Message;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/*
 * The API for the chat server.
 */
public interface ServerApi {

    public final static String CHAT_NAME = "chat-name";

    public final static String LAST_SEQ_NUM = "last-seq-num";

    @POST("chat/register/{chatName}")
    public Call<Void> register(@Path("chatName") String chatName,
                               @Query("clientID") Object clientID,
                               @Query("password") Object password);

    @POST("chat/{chatName}/messages")
    public Call<Void> postMessage(@Path("chatName") String chatName, @Body Message chatMessage);

    @GET("chat/{chatName}/messages/sync")
    public Call<ResponseBody> syncMessages(@Path("chatName") String chatName,
                                           @Query("last-seq-num") long lastSeqNum,
                                           @Query("some_param") String someParam);
}
