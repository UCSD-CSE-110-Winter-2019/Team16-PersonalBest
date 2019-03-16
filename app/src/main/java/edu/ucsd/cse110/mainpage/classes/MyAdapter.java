package edu.ucsd.cse110.mainpage.classes;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import edu.ucsd.cse110.mainpage.R;
import edu.ucsd.cse110.mainpage.StepsChart;
import edu.ucsd.cse110.mainpage.ViewFriendsStatsActivity;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        private String[] mDataset;
        private Context myContext;

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
        public MyAdapter(Context mContext, String[] myDataset) {
            mDataset = myDataset;
            myContext = mContext;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
            // create a new view
            Button v = (Button) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_friend_view, parent, false);
            MyViewHolder vh = new MyViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            final String userEmail = mDataset[position];
            holder.button.setText(mDataset[position]);
            holder.button.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    System.out.println("username is:........" + userEmail);
                    Intent chartsIntent = new Intent(myContext, ViewFriendsStatsActivity.class);
                    chartsIntent.putExtra("userEmail", userEmail);
                    myContext.startActivity(chartsIntent);
                }
            });

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.length;
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            Button userBtn;

            public ViewHolder(View itemView){
                super(itemView);
                userBtn = itemView.findViewById(R.id.friendViewID);
            }

        }

}
