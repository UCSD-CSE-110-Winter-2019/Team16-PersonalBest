package edu.ucsd.cse110.mainpage;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import edu.ucsd.cse110.mainpage.chatmessage.ChatMessageService;
import edu.ucsd.cse110.mainpage.chatmessage.ChatMessageServiceFactory;
import edu.ucsd.cse110.mainpage.chatmessage.FirebaseFirestoreAdapter;
import edu.ucsd.cse110.mainpage.notification.FirebaseCloudMessagingAdapter;
import edu.ucsd.cse110.mainpage.notification.NotificationService;
import edu.ucsd.cse110.mainpage.notification.NotificationServiceFactory;

public class MessagingActivity extends AppCompatActivity {

    String DOCUMENT_KEY = "chat1";
    String TEXT_KEY = "text";
    String TIMESTAMP_KEY = "timestamp";
    SharedPreferences userSharedPref;

    ChatMessageService chat;
    String from;

    public static final String CHAT_MESSAGE_SERVICE_EXTRA = "CHAT_MESSAGE_SERVICE";
    public static final String NOTIFICATION_SERVICE_EXTRA = "NOTIFICATION_SERVICE";
    private static final String TAG = "MainActivity";//TODO: #consider changing to private static final String TAG = MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        // Keep track of user preferences
        userSharedPref = getSharedPreferences("userdata", MODE_PRIVATE);

        from = userSharedPref.getString("userIDinDB", null);//#changed name to userIDinDB

        String stringExtra = getIntent().getStringExtra(CHAT_MESSAGE_SERVICE_EXTRA);
        chat = ChatMessageServiceFactory.getInstance().getOrDefault(stringExtra, FirebaseFirestoreAdapter::getInstance);

        initMessageUpdateListener();

        //TODO:# need to create some sort of message send button
        findViewById(R.id.btn_send).setOnClickListener(view -> sendMessage());
        subscribeToNotificationsTopic();
    }

    private void sendMessage() {
        if (from == null || from.isEmpty()) {
            Toast.makeText(this, "Enter your name", Toast.LENGTH_SHORT).show();
            //return;
            from = "Anything";
        }

        //TODO: #change where the message gets displayed
        EditText messageView = findViewById(R.id.text_message);

        Map<String, String> newMessage = new HashMap<>();
        newMessage.put("userIDinDB", from);//#changed name to userIDinDB
        newMessage.put(TIMESTAMP_KEY, String.valueOf(new Date().getTime()));
        newMessage.put(TEXT_KEY, messageView.getText().toString());

        chat.addMessage(newMessage).addOnSuccessListener(result -> {
            messageView.setText("");
        }).addOnFailureListener(error -> {
            Log.e(TAG, error.getLocalizedMessage());
        });
    }

    private void initMessageUpdateListener() {
        TextView chatView = findViewById(R.id.chat);//TODO: #change location
        chat.addOrderedMessagesListener(
                chatMessagesList -> {
                    chatMessagesList.forEach(chatMessage -> { //#requires APK 24 to work.
                        chatView.append(chatMessage.toString());
                    });
                });
    }

    private void subscribeToNotificationsTopic() {
        NotificationServiceFactory notificationServiceFactory = NotificationServiceFactory.getInstance();
        String notificationServiceKey = getIntent().getStringExtra(NOTIFICATION_SERVICE_EXTRA);
        NotificationService notificationService = notificationServiceFactory.getOrDefault(notificationServiceKey, FirebaseCloudMessagingAdapter::getInstance);

        notificationService.subscribeToNotificationsTopic(DOCUMENT_KEY, task -> {
            String msg = "Subscribed to notifications";
            if (!task.isSuccessful()) {
                msg = "Subscribe to notifications failed";
            }
            Log.d(TAG, msg);
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });
    }
}
