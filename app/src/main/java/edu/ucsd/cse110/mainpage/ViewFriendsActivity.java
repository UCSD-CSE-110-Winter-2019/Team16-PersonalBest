package edu.ucsd.cse110.mainpage;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import edu.ucsd.cse110.mainpage.classes.MyAdapter;

public class ViewFriendsActivity extends AppCompatActivity {

    ArrayList<String> friendsArray;
    FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_friends);
        RecyclerView friendsList = (RecyclerView) findViewById(R.id.friendsList);
        db = FirebaseFirestore.getInstance();


        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        friendsList.setLayoutManager(layoutManager);

        Button addFriendBtn = (Button) findViewById(R.id.addFriendBtn);
        addFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder addFriendDialog = new AlertDialog.Builder(ViewFriendsActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.activity_add_friend_dialog, null);
                final EditText mEmail = (EditText) mView.findViewById(R.id.friendEmailField);
                Button mAddFrndBtn = (Button) mView.findViewById(R.id.addFriendDialogBtn);

                mAddFrndBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       /* if(!mEmail.getText().toString().isEmpty()){
                            GoogleSignInAccount accnt = GoogleSignIn.getClient(mEmail.getText().toString());
                            db.collection("users")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                                }
                                            } else {
                                                Log.w(TAG, "Error getting documents.", task.getException());
                                            }
                                        }
                                    });

                            Toast.makeText(ViewFriendsActivity.this, "Friend successfully added!",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
                addFriendDialog.setMessage("Enter your friend's google email address!")
                        .setCancelable(true)
                        .setPositiveButton("Add friend!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                currGoalNum = currGoalNum + 500;
                                //updating the currentGoal
                                SharedPreferences.Editor editor = userSharedPref.edit();
                                editor.putLong("stepGoal", currGoalNum).apply();
                                currentGoal.setText("" + currGoalNum);
                                dialog.dismiss();
                                onlyShowGoalAlertOnceCounter = 0;

                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });


                AlertDialog alert = updateGoalDialog.create();
                alert.setTitle("Update Goal!");
                alert.show();

            }
        });*/

                        // specify an adapter (see also next example)
                        String[] myDataset = new String[]{};


                        RecyclerView.Adapter mAdapter = new MyAdapter(myDataset);
                        //friendsList.setAdapter(mAdapter);
                    }
                });
            }
        });
    }
}

