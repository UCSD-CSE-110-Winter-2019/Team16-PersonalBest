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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import edu.ucsd.cse110.mainpage.classes.MyAdapter;

public class ViewFriendsActivity extends AppCompatActivity {

    FirebaseFirestore db;
    private static final String TAG = "ViewFriendsActivity";
    SharedPreferences pref;
    ArrayList<String> friendsArr;
    String[] myDataset;
    Set<String> friendsSet;
    RecyclerView friendsList;
    String userDocString;
    AlertDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_friends);
        friendsList = (RecyclerView) findViewById(R.id.friendsList);
        db = FirebaseFirestore.getInstance();

        pref = getSharedPreferences("userdata", MODE_PRIVATE);
        friendsArr = new ArrayList<String>();
        userDocString = pref.getString("userIDinDB", "");


                // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        friendsList.setLayoutManager(layoutManager);

        Button addFriendBtn = (Button) findViewById(R.id.addFriendBtn);
        addFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder addFriendDialog = new AlertDialog.Builder(ViewFriendsActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.activity_add_friend_dialog, null);
                final EditText mEmail = (EditText) mView.findViewById(R.id.friendEmailField);
                Button mAddFrndBtn = (Button) mView.findViewById(R.id.addFriendDialogBtn);
                addFriendDialog.setView(mView);
                dialog = addFriendDialog.create();

                mAddFrndBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!mEmail.getText().toString().isEmpty()){
                            if(!mEmail.getText().toString().equals(userDocString)){
                                mEmail.setText("");
                                hideKeyboard();
                                Toast.makeText(ViewFriendsActivity.this, "You can't add yourself as a friend!",
                                        Toast.LENGTH_LONG).show();

                            }
                            else if(pref.getStringSet("friendsArray", friendsSet) != null &&
                                    pref.getStringSet("friendsArray", friendsSet).size() >= 0 ){
                                if(pref.getStringSet("friendsArray", friendsSet).contains(mEmail.getText().toString()))
                                {
                                    mEmail.setText("");
                                    hideKeyboard();
                                    Toast.makeText(ViewFriendsActivity.this, "You already have this person \n" +
                                                    "in your friends list!",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                            else {
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
                                                            if (document.getId().equals(mEmail.getText().toString())) {
                                                                System.out.println("Friend exists in the DB!");
                                                                SharedPreferences.Editor editor = pref.edit();
                                                                friendsArr.add(mEmail.getText().toString());
                                                                friendsSet = new HashSet<String>(friendsArr);
                                                                editor.putStringSet("friendsArray", friendsSet);
                                                                editor.apply();
                                                                System.out.println("yoooyoyoyoyoyoyoyoy" + friendsSet);
                                                                dialog.dismiss();
                                                                hideKeyboard();
                                                                Toast.makeText(ViewFriendsActivity.this, "Friend successfully added!" +
                                                                                "\n please refresh page or come back \n" +
                                                                                " to page for updated friendList",
                                                                        Toast.LENGTH_LONG).show();

                                                            } else {
                                                                if (num == 0) {
                                                                    mEmail.setText("");
                                                                    hideKeyboard();
                                                                    Toast.makeText(ViewFriendsActivity.this, "Sorry your friend is \n" +
                                                                            "not in our database!", Toast.LENGTH_LONG).show();
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
                    }
                });


                dialog.show();

            }
        });

        // specify an adapter
        myDataset = new String[]{};
        friendsArr.clear();
        friendsArr.addAll(pref.getStringSet("friendsArray", friendsSet));

        myDataset = friendsArr.toArray(new String[friendsArr.size()]);
        RecyclerView.Adapter mAdapter = new MyAdapter(myDataset);
        friendsList.setAdapter(mAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        myDataset = friendsArr.toArray(new String[friendsArr.size()]);
        RecyclerView.Adapter mAdapter = new MyAdapter(myDataset);
        friendsList.setAdapter(mAdapter);

    }

    private void hideKeyboard(){
        View view = this.getCurrentFocus();
        if(view != null){
            InputMethodManager inputM = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            inputM.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}


