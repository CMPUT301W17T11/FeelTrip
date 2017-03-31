package com.example.henzoshimada.feeltrip;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
        TextView emotion;
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) // TODO: Update min API to 21
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

//        getContext().setTheme(R.style.NaughtyPenguins); //TODO - theme
//        getContext().setTheme(R.style.DefaultTheme);
        getContext().setTheme(FeelTripApplication.getThemeID());

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
            viewHolder.append = (TextView) convertView.findViewById(R.id.append);
            viewHolder.emotion = (TextView) convertView.findViewById(R.id.emotion);
            viewHolder.socialSituation = (TextView) convertView.findViewById(R.id.socialSituation);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.image);

            if(FeelTripApplication.getThemeID() == R.style.CustomTheme_Light || FeelTripApplication.getThemeID() == R.style.CustomTheme_Dark) {
                viewHolder.userName.setTextColor(FeelTripApplication.getTEXTCOLORPRIMARY());
                viewHolder.date.setTextColor(FeelTripApplication.getTEXTCOLORTERTIARY());
                viewHolder.description.setTextColor(FeelTripApplication.getTEXTCOLORPRIMARY());
                viewHolder.append.setTextColor(FeelTripApplication.getTEXTCOLORPRIMARY());
                viewHolder.socialSituation.setTextColor(FeelTripApplication.getTEXTCOLORPRIMARY());
            }


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
        assert mood != null;
        viewHolder.userName.setText(mood.getUsername());
        viewHolder.date.setText(mood.getDate().toString());
        viewHolder.description.setText(Html.fromHtml(mood.getDescription())); //TODO: This is depreciated, maybe replace?
        viewHolder.append.setText(" - Feeling ");

        viewHolder.emotion.setText(mood.getEmotionalState());
        switch (mood.getEmotionalState()) {
            case "Angry":
                viewHolder.emotion.setTextColor(Color.RED);
                break;
            case "Confused":
                viewHolder.emotion.setTextColor(0xFF9900CC);
                break;
            case "Disgusted":
                viewHolder.emotion.setTextColor(Color.GREEN);
                break;
            case "Fearful":
                viewHolder.emotion.setTextColor(Color.CYAN);
                break;
            case "Happy":
                viewHolder.emotion.setTextColor(Color.YELLOW);
                break;
            case "Sad":
                viewHolder.emotion.setTextColor(Color.BLUE);
                break;
            case "Shameful":
                viewHolder.emotion.setTextColor(Color.MAGENTA);
                break;
            case "Cool":
                viewHolder.emotion.setTextColor(0xFFFF9966);
                break;
            default:
                break;
        }

        viewHolder.socialSituation.setText(mood.getSocialSit());

        //#######################################################
        Log.i("Mytag","Emoooooo: "+mood.getEmotionalState());
        int emojiID = getContext().getApplicationContext().getResources().getIdentifier("emoji" + String.valueOf(mood.getEmoji()),"drawable",getContext().getApplicationContext().getPackageName());
        if(emojiID != 0) {
            viewHolder.emojiImage.setImageResource(emojiID);
        }
        else { // This field can only be accessed if something goes wrong, or if someone alters the main database. It's mainly a fallback safety.
            viewHolder.emojiImage.setImageResource(getContext().getApplicationContext().getResources().getIdentifier("err","drawable",getContext().getApplicationContext().getPackageName()));
        }
        //######################################################
        String encodedImageString = mood.getImage();
        if(encodedImageString != null) {
            byte[] decodedString = Base64.decode(encodedImageString, Base64.DEFAULT);
            Log.d("Bitmap","Length: "+decodedString.length);
            Bitmap photo = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            viewHolder.image.setImageBitmap(photo);
            Log.d("imageTag", "have image");
        }else {
            Log.d("imageTag", "no image");
        }

        return convertView;
    }
}