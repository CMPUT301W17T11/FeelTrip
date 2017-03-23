package com.example.henzoshimada.feeltrip;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.henzoshimada.feeltrip.Mood;

import java.util.ArrayList;

public class MoodAdapter extends ArrayAdapter<Mood> {

    //private ArrayList<Mood> dataSet;
    //Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView userName;
        TextView date;
        TextView description;
        TextView append;
        ImageView emoji;
        TextView socialSituation;
        ImageView image;
    }

    public MoodAdapter(ArrayList<Mood> moodArrayList, Context context) {
        super(context, R.layout.list_item, moodArrayList);
        //this.dataSet = moodArrayList;
        //this.mContext=context;
    }


    //private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Mood mood = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item, parent, false);

            viewHolder.userName = (TextView) convertView.findViewById(R.id.userName);
            viewHolder.date = (TextView) convertView.findViewById(R.id.date);
            viewHolder.description = (TextView) convertView.findViewById(R.id.description);
            viewHolder.append = (TextView) convertView.findViewById(R.id.append);
            viewHolder.emoji = (ImageView) convertView.findViewById(R.id.emoji);
            viewHolder.socialSituation = (TextView) convertView.findViewById(R.id.socialSituation);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.image);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }
/*
        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;
*/
        Log.d("imageTag","new array list");
        viewHolder.userName.setText(mood.getUsername());
        viewHolder.date.setText(mood.getDate().toString());
        viewHolder.description.setText(Html.fromHtml(mood.getDescription())); //TODO: This is depreciated, maybe replace?
        viewHolder.append.setText(mood.getAppend() + mood.getEmotionalState());
        viewHolder.emoji.setImageBitmap(null); //TODO: pass the emoji into here
        viewHolder.socialSituation.setText(mood.getSocialSit());

        String encodedImageString = mood.getImage();
        if(encodedImageString != null) {
            byte[] decodedString = Base64.decode(encodedImageString, Base64.DEFAULT);
            Bitmap photo = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            viewHolder.image.setImageBitmap(photo);
            Log.d("imageTag", "have image");
        }else {
            viewHolder.image.setImageBitmap(null);
            Log.d("imageTag", "no image");
        }
        return convertView;
    }
}