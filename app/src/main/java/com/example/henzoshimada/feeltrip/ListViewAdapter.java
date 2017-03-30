package com.example.henzoshimada.feeltrip;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.SystemClock;
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

import static com.example.henzoshimada.feeltrip.R.id.userName;
import static java.security.AccessController.getContext;

public class ListViewAdapter extends BaseSwipeAdapter {


    private Context mContext;
    private ArrayList<Mood> mood = FeelTripApplication.getMoodArrayList();
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
        if(!mood.get(position).getPrivate()){
            swipeLayout.setSwipeEnabled(false);
        }
        swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.swipeDelete));
            }
        });
        swipeLayout.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
            @Override
            public void onDoubleClick(SwipeLayout layout, boolean surface) {
                Toast.makeText(mContext, "DoubleClick", Toast.LENGTH_SHORT).show();
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

        try {
            Mood moodItem = mood.get(position);
            Log.d("swipe","Mood Length: "+mood.size());
            Log.d("swipe","Pos Length: "+position);
            TextView userName = (TextView) convertView.findViewById(R.id.userName);
            TextView date = (TextView) convertView.findViewById(R.id.date);
            ImageView emojiImage = (ImageView) convertView.findViewById(R.id.emojiImage);
            TextView description = (TextView) convertView.findViewById(R.id.description);
            TextView append = (TextView) convertView.findViewById(R.id.append);
            TextView socialSituation = (TextView) convertView.findViewById(R.id.socialSituation);
            ImageView image = (ImageView) convertView.findViewById(R.id.image);
            userName.setText(moodItem.getUsername());
            date.setText(moodItem.getDate().toString());
            description.setText(Html.fromHtml(moodItem.getDescription())); //TODO: This is depreciated, maybe replace?
            append.setText(" - Feeling " + moodItem.getEmotionalState());
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
        if (mood.size() < 10) {
            return mood.size();
        } else {
            return 10;
        }
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
