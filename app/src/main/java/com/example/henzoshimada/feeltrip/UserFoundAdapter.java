package com.example.henzoshimada.feeltrip;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.henzoshimada.feeltrip.Mood;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Custom Adapter for searching users to follow
 */
public class UserFoundAdapter extends ArrayAdapter<String> {

    //private ArrayList<Mood> dataSet;
    //Context mContext;


    /**
     * View contains, a username, follow button
     */
    // View lookup cache
    private static class ViewHolder {
        /**
         * The User name.
         */
        TextView userName;
        /**
         * The Follow button.
         */
        ImageButton followButton;

    }

    /**
     * Instantiates a new User found adapter.
     *
     * @param userFoundArrayList the user found array list
     * @param context            the context
     */
    public UserFoundAdapter(ArrayList<String> userFoundArrayList, Context context) {
        super(context, R.layout.search_user_list_item, userFoundArrayList);
        //this.dataSet = moodArrayList;
        //this.mContext=context;
    }


    //private int lastPosition = -1;

    /**
     * set username
     * set On Click Listener for follow button
     * This method is to get the View for whether a user has been found for the following/follower
     * functionality
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final String userName = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.search_user_list_item, parent, false);

            viewHolder.userName = (TextView) convertView.findViewById(R.id.userName);
            viewHolder.followButton = (ImageButton) convertView.findViewById(R.id.follow_button);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.userName.setText(userName);

        /**
         * onClick, send request to server if there is no pending request
         * else, Toast and do nothing
         */
        viewHolder.followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("requestTag","send follow request");
                String receiver = FeelTripApplication.getUsersFoundArray().get(position);
                String sender = FeelTripApplication.getParticipant().getUserName();

                FeelTripApplication.getUsersFoundArray().remove(position);
                FeelTripApplication.getUserFoundAdapter(getContext()).notifyDataSetChanged();

                ElasticSearchController.GetRequestTask getRequestTask = new ElasticSearchController.GetRequestTask(false);
                getRequestTask.execute(sender, receiver);

                try {
                    if (getRequestTask.get().isEmpty()){
                        // add request to server
                        FollowRequest followRequest = new FollowRequest(sender, receiver);
                        ElasticSearchController.AddRequestTask addRequestTask = new ElasticSearchController.AddRequestTask();
                        addRequestTask.execute(followRequest);
                    }
                    else{
                        // request already exist
                        Toast.makeText(getContext(), "Follow request is pending",
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (InterruptedException e) {
                    return;
                } catch (ExecutionException e) {
                    return;
                }


            }
        });


        return convertView;
    }
}