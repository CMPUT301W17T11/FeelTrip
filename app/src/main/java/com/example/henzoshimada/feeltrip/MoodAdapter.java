package com.example.henzoshimada.feeltrip;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
        TextView socialSituation;
        ImageView emojiImage;
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
            viewHolder.emojiImage = (ImageView) convertView.findViewById(R.id.emojiImage);
            viewHolder.description = (TextView) convertView.findViewById(R.id.description);
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
        viewHolder.description.setText(mood.getDescription());
        viewHolder.socialSituation.setText(mood.getSocialSit());

        //#######################################################
        Log.i("Mytag","Emoooooo: "+mood.getEmotionalState());
        if(mood.getEmotionalState().equals("Happy")){
            viewHolder.emojiImage.setImageResource(R.drawable.happy);
        }
        if(mood.getEmotionalState().equals("Angry")){
            viewHolder.emojiImage.setImageResource(R.drawable.angry);
        }
        if(mood.getEmotionalState().equals("Confused")){
            viewHolder.emojiImage.setImageResource(R.drawable.confused);
        }
        if(mood.getEmotionalState().equals("Disgust")){
            viewHolder.emojiImage.setImageResource(R.drawable.disgusted);
        }
        if(mood.getEmotionalState().equals("Fearful")){
            viewHolder.emojiImage.setImageResource(R.drawable.fearful);
        }
        if(mood.getEmotionalState().equals("Sad")){
            viewHolder.emojiImage.setImageResource(R.drawable.sad);
        }
        if(mood.getEmotionalState().equals("Shameful")){
            viewHolder.emojiImage.setImageResource(R.drawable.shameful);
        }
        if(mood.getEmotionalState().equals("Cool")){
            viewHolder.emojiImage.setImageResource(R.drawable.cool);
        }
        //######################################################
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