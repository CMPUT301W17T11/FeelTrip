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

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Custom Adapter for currently following users
 */
public class FollowingAdapter extends ArrayAdapter<String> {

    //private ArrayList<Mood> dataSet;
    //Context mContext;

    // View lookup cache
    /**
     * View contains, a username, unfollow button
     */
    private static class ViewHolder {
        /**
         * The User name.
         */
        TextView userName;
        /**
         * The Unfollow button.
         */
        ImageButton unfollowButton;

    }

    /**
     * Instantiates a new Following adapter.
     *
     * @param RequestArrayList the request array list
     * @param context          the context
     */
    public FollowingAdapter(ArrayList<String> RequestArrayList, Context context) {
        super(context, R.layout.following_list_item, RequestArrayList);
        //this.dataSet = moodArrayList;
        //this.mContext=context;
    }


    //private int lastPosition = -1;
    /**
     * set username
     * set On Click Listener for unfollow button
     * This method is to get the View for the follower/following functionality
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
            convertView = inflater.inflate(R.layout.following_list_item, parent, false);

            viewHolder.userName = (TextView) convertView.findViewById(R.id.userName);
            viewHolder.unfollowButton = (ImageButton) convertView.findViewById(R.id.unfollow_button);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.userName.setText(userName);

        viewHolder.unfollowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("requestTag","unfollow follow request");
                Participant participant = FeelTripApplication.getParticipant();
                participant.unFollow(userName);

                FeelTripApplication.getFollowingArray().remove(position);
                FeelTripApplication.getFollowingAdapter(getContext()).notifyDataSetChanged();

                ElasticSearchController.EditParticipantTask editParticipantTask = new ElasticSearchController.EditParticipantTask("following");
                editParticipantTask.execute(participant);


            }
        });


        return convertView;
    }
}