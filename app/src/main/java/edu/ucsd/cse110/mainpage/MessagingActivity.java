package edu.ucsd.cse110.mainpage;

import android.content.Intent;
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

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

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

    String TEXT_KEY = "text";
    String TIMESTAMP_KEY = "timestamp";
    String FROM_KEY = "from";
    SharedPreferences userSharedPref;

    ChatMessageService chat;
    ChatMessageService chat2;
    String meUser;
    String otherUser;

    private static final String COLLECTION_KEY = "chats";
    public static final String CHAT_MESSAGE_SERVICE_EXTRA = "CHAT_MESSAGE_SERVICE";
    private static final String TAG = "MainActivity";//TODO: #consider changing to private static final String TAG = MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        // Keep track of user preferences
        userSharedPref = getSharedPreferences("userdata", MODE_PRIVATE);

        meUser = userSharedPref.getString("userIDinDB", null);//#changed name to userIDinDB

        Intent intent= getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle!=null)
        {
            otherUser = (String)bundle.getString("userEmail");
        }

        CollectionReference collection = FirebaseFirestore.getInstance()
                .collection(COLLECTION_KEY)
                .document(meUser)
                .collection(otherUser);

        CollectionReference collection2 = FirebaseFirestore.getInstance()
                .collection(COLLECTION_KEY)
                .document(otherUser)
                .collection(meUser);

        FirebaseFirestoreAdapter fb = new FirebaseFirestoreAdapter(collection);
        FirebaseFirestoreAdapter fb2 = new FirebaseFirestoreAdapter(collection2);
        chat = fb;
        chat2 = fb2;
        initMessageUpdateListener();

        //TODO:# need to create some sort of message send button
        findViewById(R.id.btn_send).setOnClickListener(view -> sendMessage());
    }

    private void sendMessage() {
        if (meUser == null || meUser.isEmpty()) {
            Toast.makeText(this, "Do you even exist", Toast.LENGTH_SHORT).show();
            //return;
            meUser = "Me";
        }

        //TODO: #change where the message gets displayed
        EditText messageView = findViewById(R.id.text_message);

        Map<String, String> newMessage = new HashMap<>();
        newMessage.put(FROM_KEY, meUser);//#changed name to userIDinDB
        newMessage.put(TIMESTAMP_KEY, String.valueOf(new Date().getTime()));
        newMessage.put(TEXT_KEY, messageView.getText().toString());

        chat.addMessage(newMessage).addOnSuccessListener(result -> {
            messageView.setText("");
        }).addOnFailureListener(error -> {
            Log.e(TAG, error.getLocalizedMessage());
        });
        chat2.addMessage(newMessage).addOnSuccessListener(result -> {
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
}
