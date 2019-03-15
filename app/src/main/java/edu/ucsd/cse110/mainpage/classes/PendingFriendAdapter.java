package edu.ucsd.cse110.mainpage.classes;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import edu.ucsd.cse110.mainpage.R;
import edu.ucsd.cse110.mainpage.ViewFriendsActivity;

public class PendingFriendAdapter extends RecyclerView.Adapter<PendingFriendAdapter.MyViewHolder>{

        private String[] pFDataset;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public static class MyViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public Button button;
            public MyViewHolder(Button v) {
                super(v);
                button = v;
            }

        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public PendingFriendAdapter(String[] pDataset) {
            pFDataset = pDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            Button v = (Button) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_pending_friend_view, parent, false);
           MyViewHolder vh = new MyViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.button.setText(pFDataset[position]);

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return pFDataset.length;
        }





}
