package edu.ucsd.cse110.firebaselab.notification;

import com.google.android.gms.tasks.Task;

import java.util.function.Consumer;

public interface NotificationService {
    void subscribeToNotificationsTopic(String topic, Consumer<Task<Void>> callback);
}
