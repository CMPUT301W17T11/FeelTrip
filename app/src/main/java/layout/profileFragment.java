package layout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.example.henzoshimada.feeltrip.EditMoodActivity;
import com.example.henzoshimada.feeltrip.ElasticSearchController;
import com.example.henzoshimada.feeltrip.FeelTripApplication;
import com.example.henzoshimada.feeltrip.Mood;
import com.example.henzoshimada.feeltrip.MoodAdapter;
import com.example.henzoshimada.feeltrip.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;


public class profileFragment extends Fragment {

    private ListView oldMoodListView;
    private ArrayList<Mood> moodArrayList;

    private ArrayAdapter<Mood> adapter;

    private static final String frag = "profile";

    //Swiping
    private boolean mSwiping = false; // detects if user is swiping on ACTION_UP
    private boolean mItemPressed = false; // Detects if user is currently holding down a view
    private static final int SWIPE_DURATION = 250; // needed for velocity implementation
    private static final int MOVE_DURATION = 150;
    float mDownX;
    private int mSwipeSlop = -1;
    boolean swiped;
    HashMap<Long, Integer> mItemIdTopMap = new HashMap<Long, Integer>();

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("myTag","onCreateView");
        // Inflate the layout for this fragment
        FrameLayout view = (FrameLayout) inflater.inflate(R.layout.fragment_profile,
                container, false);

        oldMoodListView = (ListView) view.findViewById(R.id.profileList);

        //http://stackoverflow.com/questions/20922036/android-cant-call-setonitemclicklistener-from-a-listview
        //2017-02-02
        //when click an item in list
        oldMoodListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Log.d("listTag","on click");
//                        Mood mood = FeelTripApplication.getMoodArrayList().get(position);
                        //if (mood.getImage() == null){
                        //    Log.d("myTag","selected have no image ");
                        // }else{
                        //    Log.d("myTag","image:"+mood.getImage());
                        //}

                        Intent intent = new Intent(view.getContext(), EditMoodActivity.class);
//                        Mood selected = FeelTripApplication.getMoodArrayList().get(position);
                        Bundle bundle = new Bundle();
                        bundle.putInt("editmood", position);
                        intent.putExtras(bundle);
                        try {
                            startActivity(intent);
                        }
                        catch(Exception e) {
                            Log.d("tag", "Fail while loading edit mood");
                        }


                    }
                });

/*
        // 20 march 2017 https://github.com/KevCron/AndroidTutorials
        oldMoodListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, MotionEvent event) {
                if (mSwipeSlop < 0)
                {
                    mSwipeSlop = ViewConfiguration.get(getActivity()).getScaledTouchSlop();
                }
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (mItemPressed)
                        {
                            // Doesn't allow swiping two items at same time
                            return false;
                        }
                        mItemPressed = true;
                        mDownX = event.getX();
                        swiped = false;
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        v.setTranslationX(0);
                        mItemPressed = false;
                        break;
                    case MotionEvent.ACTION_MOVE:
                    {
                        float x = event.getX() + v.getTranslationX();
                        float deltaX = x - mDownX;
                        float deltaXAbs = Math.abs(deltaX);

                        if (!mSwiping)
                        {
                            if (deltaXAbs > mSwipeSlop) // tells if user is actually swiping or just touching in sloppy manner
                            {
                                mSwiping = true;
                                oldMoodListView.requestDisallowInterceptTouchEvent(true);
                            }
                        }
                        if (mSwiping && !swiped) // Need to make sure the user is both swiping and has not already completed a swipe action (hence mSwiping and swiped)
                        {
                            //v.setTranslationX((x - mDownX)); // moves the view as long as the user is swiping and has not already swiped

                            if (deltaX < -1 * (v.getWidth() / 3)) // swipe to left
                            {

                                v.setEnabled(false); // need to disable the view for the animation to run

                                // stacked the animations to have the pause before the views flings off screen
                                v.animate().setDuration(300).translationX(-v.getWidth()/3).withEndAction(new Runnable() {
                                    @Override
                                    public void run()
                                    {
                                        v.animate().setDuration(300).alpha(0).translationX(-v.getWidth()).withEndAction(new Runnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                mSwiping = false;
                                                mItemPressed = false;
                                                //animateRemoval(oldMoodListView, v);
                                                Log.d("swipeTag","animate removal");
                                            }
                                        });
                                    }
                                });
                                mDownX = x;
                                swiped = true;
                                return true;
                            }
                        }

                    }
                    break;
                    case MotionEvent.ACTION_UP:
                    {
                        if (mSwiping) // if the user was swiping, don't go to the and just animate the view back into position
                        {
                            Log.d("swipeTag", "not full swipe");
                            v.animate().setDuration(300).translationX(0).withEndAction(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    mSwiping = false;
                                    mItemPressed = false;
                                    oldMoodListView.setEnabled(true);
                                }
                            });
                        }
                        else // user was not swiping; registers as a click
                        {
                            mItemPressed = false;
                            oldMoodListView.setEnabled(true);

                            int i = oldMoodListView.getPositionForView(v);

                            //Toast.makeText(MainActivity.this, array.get(i).toString(), Toast.LENGTH_LONG).show();
                            Log.d("swipeTag","register as click on index: "+ i);
                            return false;
                        }
                    }
                    default:
                        return false;
                }
                return true;
            }
        });
        */

        return view;
    }

    //start when we click home button
/*
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("myTag","onAttach");
        //moodArrayList = new ArrayList<Mood>();
        loadFromElasticSearch();
    }
*/

    //start when come back from add mood activity

    @Override
    public void onStart() {
        Log.d("myTag","onStart");
        super.onStart();

        if(!FeelTripApplication.getFrag().equals(frag)) {
            FeelTripApplication.setFrag(frag);
        }
//        FeelTripApplication.loadFromElasticSearch();
        adapter = FeelTripApplication.getMoodAdapter(getActivity());



        //adapter = new ArrayAdapter<Mood>(getActivity(), R.layout.list_item, moodArrayList); //view,dataArray
        oldMoodListView.setAdapter(adapter);
        Log.d("listTag","done loading3");
    }

    @Override
    public void onResume() {
        super.onResume();
        FeelTripApplication.getMoodAdapter(getActivity()).notifyDataSetChanged();
    }

/*
    @Override
    public void onResume(){
        super.onResume();
        Log.d("myTag","onResume");
        loadFromElasticSearch();
        adapter.notifyDataSetChanged();
    }
*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // If the activity result was received from the "Get Car" request
        if (1 == requestCode) {
            // If the activity confirmed a selection
            if (Activity.RESULT_OK == resultCode) {
                // Grab whatever data identifies that car that was sent in
                // setResult(int, Intent)
                //final int carId = data.getIntExtra(CarActivity.EXTRA_CAR_ID, -1);
                Log.d("myTag", "we are back"+moodArrayList.size());
                //FeelTripApplication.loadFromElasticSearch();

                Log.d("myTag", "done load from ES after back"+moodArrayList.size());
                adapter.notifyDataSetChanged();
                Log.d("myTag", "done notify");
            } else {
                // You can handle a case where no selection was made if you want
                Log.d("myTag", "we are back2");
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


}
