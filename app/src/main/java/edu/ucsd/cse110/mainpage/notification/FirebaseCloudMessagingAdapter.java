package edu.ucsd.cse110.firebaselab.notification;

import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.function.Consumer;

import edu.ucsd.cse110.firebaselab.chatmessage.FirebaseFirestoreAdapter;

public class FirebaseCloudMessagingAdapter implements NotificationService {
    private final String TAG = FirebaseFirestoreAdapter.class.getSimpleName();
    private static NotificationService instance;

    public static NotificationService getInstance() {
        if (instance == null) {
            instance = new FirebaseCloudMessagingAdapter();
        }
        return instance;
    }

    @Override
    public void subscribeToNotificationsTopic(String topic, Consumer<Task<Void>> callback) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnCompleteListener(callback::accept);
    }
}
