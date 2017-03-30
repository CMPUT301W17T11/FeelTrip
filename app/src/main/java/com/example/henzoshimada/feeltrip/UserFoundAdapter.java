package com.example.henzoshimada.feeltrip;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.henzoshimada.feeltrip.Mood;

import java.util.ArrayList;

public class UserFoundAdapter extends ArrayAdapter<String> {

    //private ArrayList<Mood> dataSet;
    //Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView userName;
        ImageButton followButton;

    }

    public UserFoundAdapter(ArrayList<String> RequestArrayList, Context context) {
        super(context, R.layout.search_user_list_item, RequestArrayList);
        //this.dataSet = moodArrayList;
        //this.mContext=context;
    }


    //private int lastPosition = -1;

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

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.userName.setText(userName);

        viewHolder.followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("requestTag","accept follow request");
                String receiver = FeelTripApplication.getUsersFoundArray().get(position);
                String sender = FeelTripApplication.getParticipant().getUserName();

                FeelTripApplication.getUsersFoundArray().remove(position);
                FeelTripApplication.getUserFoundAdapter(getContext()).notifyDataSetChanged();

                // TODO: sqecial cases

                // add request to server
                FollowRequest followRequest = new FollowRequest(sender, receiver);
                ElasticSearchController.AddRequestTask addRequestTask = new ElasticSearchController.AddRequestTask();
                addRequestTask.execute(followRequest);
            }
        });


        return convertView;
    }
}