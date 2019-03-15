package edu.ucsd.cse110.firebaselab.chatmessage;

import com.google.android.gms.tasks.Task;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public interface ChatMessageService {
    Task<?> addMessage(Map<String, String> data);

    void addOrderedMessagesListener(Consumer<List<ChatMessage>> listener);
}
