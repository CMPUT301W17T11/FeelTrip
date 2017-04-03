package com.example.henzoshimada.feeltrip;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import java.util.ArrayList;

/**
 * The type List view adapter.
 */
public class ListViewAdapter extends BaseSwipeAdapter {


    private Context mContext;
    private ArrayList<Mood> mood = FeelTripApplication.getMoodArrayList();

    /**
     * Instantiates a new List view adapter.
     *
     * @param mContext the m context
     */
    public ListViewAdapter(Context mContext) {
        this.mContext = mContext.getApplicationContext();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public View generateView(final int position, ViewGroup parent) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.list_item, null);
        return v;
    }

    @Override
    public void fillValues(final int position, View convertView) {
        final SwipeLayout swipeLayout = (SwipeLayout)convertView.findViewById(getSwipeLayoutResourceId(position));
        Log.d("swipe","Name Global: "+FeelTripApplication.getUserName());
        if(!(FeelTripApplication.getUserName().equals((mood.get(position).getUsername())))){
            swipeLayout.setSwipeEnabled(false);
        }
        swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.swipeDelete));
            }
        });

        ImageButton ib = (ImageButton) convertView.findViewById(R.id.swipeDelete);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Mood moodItem = mood.get(position);
                mood.remove(position);
                ElasticSearchController.DeleteMoodTask delTask = new ElasticSearchController.DeleteMoodTask();
                delTask.execute(moodItem);
                closeAllItems();
                FeelTripApplication.getListViewAdapter(mContext).notifyDataSetChanged();
            }
        });

        if(!FeelTripApplication.getUserName().equals(mood.get(position).getUsername())){
            closeAllItems();
        }

        try {
            Mood moodItem = mood.get(position);
            //Log.d("swipe","Mood Length: "+mood.size());
            //Log.d("swipe","Pos Length: "+position);
            TextView userName = (TextView) convertView.findViewById(R.id.userName);
            TextView date = (TextView) convertView.findViewById(R.id.date);
            ImageView emojiImage = (ImageView) convertView.findViewById(R.id.emojiImage);
            TextView description = (TextView) convertView.findViewById(R.id.description);
            TextView append = (TextView) convertView.findViewById(R.id.append);
            TextView emotion = (TextView) convertView.findViewById(R.id.emotion);
            TextView socialSituation = (TextView) convertView.findViewById(R.id.socialSituation);
            ImageView image = (ImageView) convertView.findViewById(R.id.image);

            if(FeelTripApplication.getThemeID() == R.style.CustomTheme_Light || FeelTripApplication.getThemeID() == R.style.CustomTheme_Dark) {
                userName.setTextColor(FeelTripApplication.getTEXTCOLORPRIMARY());
                date.setTextColor(FeelTripApplication.getTEXTCOLORTERTIARY());
                description.setTextColor(FeelTripApplication.getTEXTCOLORPRIMARY());
                append.setTextColor(FeelTripApplication.getTEXTCOLORPRIMARY());
                socialSituation.setTextColor(FeelTripApplication.getTEXTCOLORPRIMARY());
            }

            userName.setText(moodItem.getUsername());
            date.setText(moodItem.getDate().toString());
            description.setText(Html.fromHtml(moodItem.getDescription())); //TODO: This is depreciated, maybe replace?
            append.setText(" - Feeling ");

            emotion.setText(moodItem.getEmotionalState());
            switch (moodItem.getEmotionalState()) {
                case "Angry":
                    emotion.setTextColor(Color.RED);
                    break;
                case "Confused":
                    emotion.setTextColor(0xFF9900CC);
                    break;
                case "Disgusted":
                    emotion.setTextColor(Color.GREEN);
                    break;
                case "Fearful":
                    emotion.setTextColor(Color.BLUE);
                    break;
                case "Happy":
                    emotion.setTextColor(Color.YELLOW);
                    break;
                case "Sad":
                    emotion.setTextColor(Color.CYAN);
                    break;
                case "Shameful":
                    emotion.setTextColor(Color.MAGENTA);
                    break;
                case "Cool":
                    emotion.setTextColor(0xFFFF9966);
                    break;
                case "Surprised":
                    emotion.setTextColor(0xFF996600);
                    break;
                default:
                    break;
            }

            socialSituation.setText(moodItem.getSocialSit());

            int emojiID = mContext.getResources().getIdentifier("emoji" + String.valueOf(moodItem.getEmoji()), "drawable", mContext.getPackageName());
            if (emojiID != 0) {
                emojiImage.setImageResource(emojiID);
            } else { // This field can only be accessed if something goes wrong, or if someone alters the main database. It's mainly a fallback safety.
                emojiImage.setImageResource(mContext.getResources().getIdentifier("err", "drawable", mContext.getPackageName()));
            }
            //######################################################
            String encodedImageString = moodItem.getImage();
            if (encodedImageString != null) {
                byte[] decodedString = Base64.decode(encodedImageString, Base64.DEFAULT);
                Log.d("Bitmap", "Length: " + decodedString.length);
                Bitmap photo = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                image.setImageBitmap(photo);
                Log.d("imageTag", "have image");
            } else {
                image.setImageBitmap(null);
                Log.d("imageTag", "no image");
            }
        }
        catch(Exception e){
            Toast.makeText(mContext, "Error: "+e, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getCount() {
        return mood.size();
    }

    @Override
    public Object getItem(int position) {
        return mood.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
