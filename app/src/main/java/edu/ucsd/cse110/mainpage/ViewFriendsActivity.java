package edu.ucsd.cse110.mainpage;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import edu.ucsd.cse110.mainpage.classes.MyAdapter;
import edu.ucsd.cse110.mainpage.classes.PendingFriendAdapter;

public class ViewFriendsActivity extends AppCompatActivity {

    FirebaseFirestore db;
    private static final String TAG = "ViewFriendsActivity";
    SharedPreferences pref;
    ArrayList<String> friendsArr;
    ArrayList<String> pendingFriendsArr;
    String[] myDataset;
    String[] pendingFriendDataSet;
    Set<String> friendsSet;
    Set<String> pendingFriendsSet;
    RecyclerView friendsList;
    RecyclerView pendingFriendsList;
    String userDocString;
    AlertDialog dialog;
    View mView;
    EditText mEmail;
    String enteredEmail;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_friends);
        friendsList = (RecyclerView) findViewById(R.id.friendsList);
        pendingFriendsList = (RecyclerView) findViewById(R.id.pendingFriendsList);
        db = FirebaseFirestore.getInstance();

        pref = getSharedPreferences("userdata", MODE_PRIVATE);
        userDocString = pref.getString("userIDinDB", "");
        friendsArr = new ArrayList<String>();
        pendingFriendsArr = new ArrayList<String>();
        friendsSet = new HashSet<String>(friendsArr);
        pendingFriendsSet = new HashSet<String>(pendingFriendsArr);

        db.collection("users").document(userDocString)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            friendsArr = (ArrayList<String>) task.getResult().get("friendsList");
                            pendingFriendsArr = (ArrayList<String>) task.getResult().get("pendingFriendsList");
                            Set<String> yo = new HashSet<String>(friendsArr);
                            Set<String> yo2 = new HashSet<String>(pendingFriendsArr);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putStringSet("friendsArray", yo);
                            editor.putStringSet("pendingFriendsArray", yo2);
                            editor.apply();
                        }
                    }
                });

        friendsSet = pref.getStringSet("friendsArray", friendsSet);
        pendingFriendsSet = pref.getStringSet("pendingFriendsArray", pendingFriendsSet);
        friendsArr.addAll(pref.getStringSet("friendsArray", friendsSet));
        pendingFriendsArr.addAll(pref.getStringSet("pendingFriendsArray", pendingFriendsSet));

        //db.collection("users").document(userDocString).update("friendsList", friendsArr);
        //db.collection("users").document(userDocString).update("pendingFriendsList", pendingFriendsArr);


                // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        LinearLayoutManager anotherLM = new LinearLayoutManager(this);
        friendsList.setLayoutManager(layoutManager);
        pendingFriendsList.setLayoutManager(anotherLM);

        Button addFriendBtn = (Button) findViewById(R.id.addFriendBtn);
        addFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder addFriendDialog = new AlertDialog.Builder(ViewFriendsActivity.this);
                mView = getLayoutInflater().inflate(R.layout.activity_add_friend_dialog, null);
                mEmail = (EditText) mView.findViewById(R.id.friendEmailField);
                Button mAddFrndBtn = (Button) mView.findViewById(R.id.addFriendDialogBtn);
                addFriendDialog.setView(mView);
                dialog = addFriendDialog.create();

                mAddFrndBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        enteredEmail = mEmail.getText().toString();
                        if(!enteredEmail.isEmpty()){

                            // If the entered email is the user's email, prevent them from adding themselves
                            if(enteredEmail.equals(userDocString)){
                                mEmail.setText("");
                                Toast.makeText(ViewFriendsActivity.this, "You can't add yourself as a friend!",
                                        Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }

                            // If the entered email is already in the friendsSet, prevent them from adding that email
                            else if(pref.getStringSet("friendsArray", friendsSet) != null &&
                                    pref.getStringSet("friendsArray", friendsSet).size() >= 0 &&
                                    pref.getStringSet("friendsArray", friendsSet).contains(enteredEmail)) {
                                mEmail.setText("");
                                Toast.makeText(ViewFriendsActivity.this, "You already have this person \n" +
                                                "in your friends list!",
                                        Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }

                            // Add the new email to the pending friends list, assuming that they can be found in the system.
                            else {
                                db.collection("users")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    int num = task.getResult().size();

                                                    // Iterate through every single user
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        if (num > 0) {
                                                            if (document.getId().equals(enteredEmail)) {
                                                                addCurrUserToFriendsPendingArr(userDocString, enteredEmail);
                                                                dialog.dismiss();
                                                                break;
                                                            } else {
                                                                if (num == 1) {
                                                                    mEmail.setText("");
                                                                    Toast.makeText(ViewFriendsActivity.this, "Sorry we couldn't \n" +
                                                                            "find your friend!", Toast.LENGTH_LONG).show();
                                                                    dialog.dismiss();
                                                                }
                                                            }
                                                            num = num - 1;
                                                        }
                                                    }

                                                } else {
                                                    Log.w(TAG, "Error getting documents.", task.getException());
                                                }
                                            }
                                        });

                            }
                        }

                        // There is no email being entered
                        else{
                            Toast.makeText(ViewFriendsActivity.this, "Please enter an email!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                dialog.show();
            }
        });

        // specify an adapter
        myDataset = new String[]{};
        pendingFriendDataSet = new String[]{};
        friendsArr.clear();
        pendingFriendsArr.clear();
        friendsArr.addAll(pref.getStringSet("friendsArray", friendsSet));
        pendingFriendsArr.addAll(pref.getStringSet("pendingFriendsArray", pendingFriendsSet));

        myDataset = friendsArr.toArray(new String[friendsArr.size()]);
        pendingFriendDataSet = pendingFriendsArr.toArray(new String[friendsArr.size()]);
        RecyclerView.Adapter mAdapter = new MyAdapter(myDataset);
        RecyclerView.Adapter pfAdapter = new PendingFriendAdapter(pendingFriendDataSet);
        friendsList.setAdapter(mAdapter);
        pendingFriendsList.setAdapter(pfAdapter);
    }


    public void addCurrUserToFriendsPendingArr(final String currUser, final String friend){
        System.out.println("INSIDE ADD CURR USER TO FRIENDS...........................");
        db.collection("users").document(currUser)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    // Get the current pending friends list and the current friends list
                    ArrayList<String> currPendingList = (ArrayList<String>) task.getResult().get("pendingFriendsList");
                    ArrayList<String> currFriendsList = (ArrayList<String>) task.getResult().get("friendsList");

                    // If the pending friends list does not contain the friend, send them a friend request
                    if(currPendingList != null && !currPendingList.contains(friend)) {

                        // Get the other user's friend list
                        db.collection("users").document(friend)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                // Get their current friends list and add the current user to it
                                if (task.isSuccessful()) {
                                    ArrayList<String> theirPendingFriendsList = (ArrayList<String>) task.getResult().get("pendingFriendsList");
                                    theirPendingFriendsList.add(currUser);

                                    // Update the other user's pending friends list
                                    db.collection("users").document(friend).update("pendingFriendsList", theirPendingFriendsList)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(ViewFriendsActivity.this, "You've sent " + friend + "\n" +
                                                            "a friend request!", Toast.LENGTH_LONG).show();

                                                } else {
                                                    Toast.makeText(ViewFriendsActivity.this, "Error Sending Friend Request, \n" +
                                                                    "try again later!",
                                                            Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                }
                            }
                        });
                    }

                    // If the friend is in your pending friends list, update both friend's lists
                    else {

                        // Remove the friend from your own pending list and add it to your friends list
                        currFriendsList.add(friend);
                        currPendingList.remove(friend);

                        db.collection("users").document(currUser).update("friendsList", currFriendsList);
                        db.collection("users").document(currUser).update("pendingFriendsList", currPendingList);
                        db.collection("users").document(friend)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {

                                        // Get the current pending friends list and the current friends list
                                        ArrayList<String> theirPendingList = (ArrayList<String>) task.getResult().get("pendingFriendsList");
                                        ArrayList<String> theirFriendsList = (ArrayList<String>) task.getResult().get("friendsList");

                                        if (theirPendingList.contains(currUser)) {
                                            theirPendingList.remove(currUser);
                                        }
                                        if (!theirFriendsList.contains(currUser)) {
                                            theirFriendsList.add(currUser);
                                        }

                                        db.collection("users").document(friend).update("friendsList", theirFriendsList);
                                        db.collection("users").document(friend).update("pendingFriendsList", theirPendingList);
                                    }
                                    Toast.makeText(ViewFriendsActivity.this, "Friend added successfully!", Toast.LENGTH_LONG).show();
                                }
                            });
                    }

                        // Remove yourself from their pending friends list and update their friends list

                        // Add the friend

                        //for (int i = 0; i < currPendingList.size(); i++) {
                        //    if (currPendingList.get(i).equals(friend)) {
                        //SharedPreferences.Editor editor = pref.edit();

                        //friendsArr.add(enteredEmail);
                       // Set<String> tempFriendsSet = new HashSet<String>(friendsArr);
                        //editor.putStringSet("friendsArray", tempFriendsSet);
                      //  editor.apply();

                        //Add the friend to current user's friendList

                        //Remove the current user from friend's pending list


                        //Add the current user to friend's friend list
                          //  }
                        //}
                    //}
                } else{
                    Log.w(TAG, "Error getting documents.", task.getException());
                }

            }
        });
    }
}


