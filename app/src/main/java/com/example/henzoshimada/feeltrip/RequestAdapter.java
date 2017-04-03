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

/**
 * Custom Adapter for Follow Request
 * This is to help organize and be a controller to request following another user
 */
public class RequestAdapter extends ArrayAdapter<FollowRequest> {

    //private ArrayList<Mood> dataSet;
    //Context mContext;

    // View lookup cache

    /**
     * View contains, a username, cancel button and accept button
     */
    private static class ViewHolder {
        /**
         * The User name.
         */
        TextView userName;
        /**
         * The Cancel button.
         */
        ImageButton cancelButton;
        /**
         * The Accept button.
         */
        ImageButton acceptButton;
    }

    /**
     * Instantiates a new Request adapter.
     *
     * @param requestArrayList the request array list
     * @param context          the context
     */
    public RequestAdapter(ArrayList<FollowRequest> requestArrayList, Context context) {
        super(context, R.layout.request_list_item, requestArrayList);
        //this.dataSet = moodArrayList;
        //this.mContext=context;
    }


    //private int lastPosition = -1;

    /**
     * set username
     * set On Click Listener for accept button, cancel button
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        FollowRequest followRequest = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.request_list_item, parent, false);

            viewHolder.userName = (TextView) convertView.findViewById(R.id.userName);
            viewHolder.acceptButton = (ImageButton) convertView.findViewById(R.id.accept_request);
            viewHolder.cancelButton = (ImageButton) convertView.findViewById(R.id.cancel_request);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.userName.setText(followRequest.getSender());

        /**
         * accept follow request and update server, then remove from UI
         */
        viewHolder.acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("requestTag","accept follow request");
                FollowRequest request = FeelTripApplication.getRequestsArray().get(position);

                FeelTripApplication.getRequestsArray().remove(position);
                FeelTripApplication.getRequestAdapter(getContext()).notifyDataSetChanged();

                // change the request acceptedStatus to true
                ElasticSearchController.EditRequestTask editRequestTask = new ElasticSearchController.EditRequestTask();
                editRequestTask.execute(request);

            }
        });

        /**
         * decline follow request and update server, then remove from UI
         */
        viewHolder.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("requestTag","cancel follow request");
                FollowRequest request = FeelTripApplication.getRequestsArray().get(position);

                FeelTripApplication.getRequestsArray().remove(position);
                FeelTripApplication.getRequestAdapter(getContext()).notifyDataSetChanged();

                // delete this request from server
                ElasticSearchController.DeleteRequestTask deleteRequestTask = new ElasticSearchController.DeleteRequestTask();
                deleteRequestTask.execute(request);
            }
        });

        return convertView;
    }
}