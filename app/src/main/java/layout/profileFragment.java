package layout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.henzoshimada.feeltrip.EditMoodActivity;
import com.example.henzoshimada.feeltrip.FeelTripApplication;
import com.example.henzoshimada.feeltrip.ListViewAdapter;
import com.example.henzoshimada.feeltrip.Mood;
import com.example.henzoshimada.feeltrip.R;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * The type Profile fragment.
 * This fragment is for the participants profile page where they can see all the moods that they
 * created, which they will be able to edit as well
 */
public class profileFragment extends Fragment {

    private ListView oldMoodListView;
    private ArrayList<Mood> moodArrayList;
    private ListViewAdapter adapter;


    private static final String frag = "profile";

    /**
     * The M down x.
     */
    float mDownX;
    private int mSwipeSlop = -1;
    /**
     * The Swiped.
     */
    boolean swiped;
    /**
     * The M item id top map.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("myTag","onCreateView");
        // Inflate the layout for this fragment
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.fragment_profile,
                container, false);

        oldMoodListView = (ListView) view.findViewById(R.id.profileList);

        if(FeelTripApplication.getThemeID() == R.style.CustomTheme_Light || FeelTripApplication.getThemeID() == R.style.CustomTheme_Dark) {
            oldMoodListView.setBackgroundColor(FeelTripApplication.getBACKGROUNDCOLOR());
        }

        else if(FeelTripApplication.getThemeID() == R.style.GalaxyTheme) {
            oldMoodListView.setBackground(getActivity().getDrawable(R.drawable.galaxy_bg));
        }

        //http://stackoverflow.com/questions/20922036/android-cant-call-setonitemclicklistener-from-a-listview
        //2017-02-02
        //when click an item in list
        oldMoodListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Log.d("listTag","on click");

                        Intent intent = new Intent(view.getContext(), EditMoodActivity.class);
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
        return view;
    }


    //start when come back from add mood activity

    @Override
    public void onStart() {
        Log.d("myTag","onStart");
        super.onStart();

        if(!FeelTripApplication.getFrag().equals(frag)) {
            FeelTripApplication.setFrag(frag);
        }
        adapter = FeelTripApplication.getListViewAdapter(getContext());

        //adapter = new ArrayAdapter<Mood>(getActivity(), R.layout.list_item, moodArrayList); //view,dataArray
        oldMoodListView.setAdapter(adapter);
        Log.d("listTag","done loading3");
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // If the activity result was received from the "Get Car" request
        if (1 == requestCode) {
            // If the activity confirmed a selection
            if (Activity.RESULT_OK == resultCode) {
                // Grab whatever data identifies that car that was sent in
                // setResult(int, Intent)
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
