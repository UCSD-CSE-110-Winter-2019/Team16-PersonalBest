package edu.ucsd.cse110.mainpage.chatmessage;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class FirebaseFirestoreAdapter implements ChatMessageService {
    private static FirebaseFirestoreAdapter singeleton;

    private static final String TAG = FirebaseFirestoreAdapter.class.getSimpleName();

    private static final String COLLECTION_KEY = "chats";
    private static String meUser;
    private static String otherUser;
    private static final String TIMESTAMP_KEY = "timestamp";
    private static final String FROM_KEY = "from";
    private static final String TEXT_KEY = "text";


    private CollectionReference chat;

    public FirebaseFirestoreAdapter(CollectionReference chat) {
        this.chat = chat;
    }

    public static ChatMessageService getInstance() {
        if (singeleton == null) {
            CollectionReference collection = FirebaseFirestore.getInstance()
                    .collection(COLLECTION_KEY)
                    .document(meUser)
                    .collection(otherUser);
            singeleton = new FirebaseFirestoreAdapter(collection);
        }
        return singeleton;
    }

    @Override
    public Task<?> addMessage(Map<String, String> message) {
        return chat.add(message);
    }

    @Override
    public void addOrderedMessagesListener(Consumer<List<ChatMessage>> listener) {
        chat.orderBy(TIMESTAMP_KEY, Query.Direction.ASCENDING)
                .addSnapshotListener((newChatSnapShot, error) -> {
                    if (error != null) {
                        Log.e(TAG, error.getLocalizedMessage());
                        return;
                    }

                    if (newChatSnapShot != null && !newChatSnapShot.isEmpty()) {
                        if (!newChatSnapShot.getMetadata().hasPendingWrites()) {
                            List<DocumentChange> documentChanges = newChatSnapShot.getDocumentChanges();

                            List<ChatMessage> newMessages = documentChanges.stream()
                                    .map(DocumentChange::getDocument)
                                    .map(doc -> new ChatMessage(doc.getString(FROM_KEY), doc.getString(TEXT_KEY)))
                                    .collect(Collectors.toList());

                            listener.accept(newMessages);
                        }
                    }
                });
    }
}
