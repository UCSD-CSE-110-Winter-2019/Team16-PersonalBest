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
                            if(enteredEmail.equals(userDocString)){
                                mEmail.setText("");
                                //hideKeyboard();
                                Toast.makeText(ViewFriendsActivity.this, "You can't add yourself as a friend!",
                                        Toast.LENGTH_LONG).show();

                            }
                            else if(pref.getStringSet("friendsArray", friendsSet) != null &&
                                    pref.getStringSet("friendsArray", friendsSet).size() >= 0 ){
                                if(pref.getStringSet("friendsArray", friendsSet).contains(enteredEmail))
                                {
                                    mEmail.setText("");
                                    //hideKeyboard();
                                    Toast.makeText(ViewFriendsActivity.this, "You already have this person \n" +
                                                    "in your friends list!",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                            else {
                                System.out.println("INSIDE THIS ELSE CASE NOW!!!!!!!!!!!!!!!!!!!!!");
                                db.collection("users")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    int num = 0;
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        num = task.getResult().size() - 1;
                                                        if (num >= 0) {
                                                            if (document.getId().equals(enteredEmail)) {
                                                                System.out.println("Friend exists in the DB!");
                                                                //SharedPreferences.Editor editor = pref.edit();
                                                                addCurrUserToFriendsPendingArr(userDocString, enteredEmail);

                                                                //pendingFriendsArr.add(enteredEmail);
                                                                //Set<String> tempFriendsSet = new HashSet<String>(friendsArr);

                                                               // editor.putStringSet("friendsArray", tempFriendsSet);
                                                               // editor.apply();
                                                                System.out.println("yoooyoyoyoyoyoyoyoy" + friendsSet);
                                                                dialog.dismiss();
                                                               // hideKeyboard();



                                                            } else {
                                                                if (num == 0) {
                                                                    mEmail.setText("");
                                                                    //hideKeyboard();
                                                                    Toast.makeText(ViewFriendsActivity.this, "Sorry your friend is \n" +
                                                                            "not in our database!", Toast.LENGTH_LONG).show();
                                                                }
                                                                else{
                                                                    System.out.println("else case......................................");
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
                        else{
                            Toast.makeText(ViewFriendsActivity.this, "email field empty", Toast.LENGTH_LONG).show();

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
                   // if(task.getResult() != null) {
                        ArrayList<String> temp = (ArrayList<String>) task.getResult().get("pendingFriendsList");
                        ArrayList<String> temp2 = (ArrayList<String>) task.getResult().get("friendsList");

                        if(temp != null && temp.size() == 0){
                            db.collection("users").document(friend)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                               @Override
                                                               public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                   if (task.isSuccessful()) {
                                                                      // if (task.getResult() != null) {
                                                                           ArrayList<String> temp = (ArrayList<String>) task.getResult().get("pendingFriendsList");
                                                                           temp.add(currUser);
                                                                           db.collection("users").document(friend).update("pendingFriendsList", temp)
                                                                                   .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                       @Override
                                                                                       public void onComplete(@NonNull Task<Void> task) {
                                                                                           if (task.isSuccessful()) {
                                                                                               Toast.makeText(ViewFriendsActivity.this, "Friend Request Sent!",
                                                                                                       Toast.LENGTH_LONG).show();
                                                                                           } else {
                                                                                               Toast.makeText(ViewFriendsActivity.this, "Error Sending Friend Request, \n" +
                                                                                                               "try again later!",
                                                                                                       Toast.LENGTH_LONG).show();
                                                                                           }
                                                                                       }
                                                                                   });
                                                                      // }
                                                                   }
                                                               }
                                                           });



                        }

                        if(temp != null && temp.size() > 0) {
                            for (int i = 0; i <= temp.size() - 1; i++) {
                                if (temp.get(i).equals(friend)) { //if the friend was already in currUser's pending list
                                    SharedPreferences.Editor editor = pref.edit();

                                    friendsArr.add(enteredEmail);
                                    pendingFriendsArr.remove(enteredEmail);
                                    Set<String> tempFriendsSet = new HashSet<String>(friendsArr);
                                    editor.putStringSet("friendsArray", tempFriendsSet);

                                    Set<String> tempPendingSet = new HashSet<String>(pendingFriendsArr);
                                    editor.putStringSet("pendingFriendsArray", tempPendingSet);
                                    editor.apply();

                                    //add the friend to current user's friendList
                                    db.collection("users").document(currUser).update("friendsList", friendsArr);

                                    //remove the current user from friend's pending list
                                    temp.remove(i);
                                    db.collection("users").document(currUser).update("pendingFriendsList", temp);

                                    //add the current user to friend's friend list
                                    temp2.add(friend);
                                    db.collection("users").document(friend).update("friendsList", temp2);

                                    Toast.makeText(ViewFriendsActivity.this, "Friend added successfully!", Toast.LENGTH_LONG).show();



                                }
                                /*else{
                                    if(i == temp.size()-1){ //means that the friend hadn't added the current user before
                                        temp.add(currUser);
                                        db.collection("users").document(friend).update("pendingFriendsList", temp)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(ViewFriendsActivity.this, "Friend Request Sent!",
                                                                    Toast.LENGTH_LONG).show();
                                                        } else {
                                                            Toast.makeText(ViewFriendsActivity.this, "Error Sending Friend Request, \n" +
                                                                            "try again later!",
                                                                    Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });
                                    }
                                }*/
                            }


                        }
                    }
                    else{
                    Log.w(TAG, "Error getting documents.", task.getException());
                    System.out.println("ERROR GETTING STUFFFFFFFFFFFFFFFFFFFFFFFFFFF");
                }

                }
                /*else{
                    Log.w(TAG, "Error getting documents.", task.getException());
                }*/
           // }
        });
    }


    private void hideKeyboard(){
        View view = this.getCurrentFocus();
        if(view != null){
            InputMethodManager inputM = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            inputM.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}


