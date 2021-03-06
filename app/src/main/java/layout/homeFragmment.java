package layout;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.example.henzoshimada.feeltrip.EditMoodActivity;
import com.example.henzoshimada.feeltrip.FeelTripApplication;
import com.example.henzoshimada.feeltrip.ListViewAdapter;
import com.example.henzoshimada.feeltrip.Mood;
import com.example.henzoshimada.feeltrip.R;

import java.util.ArrayList;

/**
 * The type Home fragmment.
 *
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link homeFragmment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link homeFragmment#newInstance} factory method to
 * create an instance of this fragment.
 *
 *
 * Home fragment is the fragment equivalent of the MainScreen activity
 */
public class homeFragmment extends Fragment{

    private ListView oldMoodListView;
    private ListViewAdapter adapter;
    private static final String frag = "main";

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        FrameLayout view = (FrameLayout) inflater.inflate(R.layout.home_fragmment,
                container, false);
        FloatingActionButton addEntry = (FloatingActionButton)  view.findViewById(R.id.add_mood);
        addEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Mytag","Entered button press for post");
                Intent intent = new Intent(getActivity(), EditMoodActivity.class);
                startActivity(intent);
            }
        });

        oldMoodListView = (ListView) view.findViewById(R.id.homeList);

        if(FeelTripApplication.getThemeID() == R.style.CustomTheme_Light || FeelTripApplication.getThemeID() == R.style.CustomTheme_Dark) {
            android.support.design.widget.FloatingActionButton floatingActionButton = (android.support.design.widget.FloatingActionButton) view.findViewById(R.id.add_mood);
            int[][] tintstates = new int[][] {
                    new int[] { android.R.attr.state_pressed},  // pressed
                    new int[] {-android.R.attr.state_pressed}  // unpressed
            };

            int[] tintcolors = new int[] {
                    FeelTripApplication.getCOLORPRIMARY(),
                    FeelTripApplication.getCOLORPRIMARY()
            };
            ColorStateList tintList = new ColorStateList(tintstates, tintcolors);
            floatingActionButton.setBackgroundTintList(tintList);

            oldMoodListView.setBackgroundColor(FeelTripApplication.getBACKGROUNDCOLOR());
        }

        else if(FeelTripApplication.getThemeID() == R.style.Simplicity) {
            addEntry.setImageResource(R.drawable.simplicity_icon_edit);
        }

        else if(FeelTripApplication.getThemeID() == R.style.Overwatch) {
            addEntry.setImageResource(R.drawable.overwatch_icon_edit);
        }

        else if(FeelTripApplication.getThemeID() == R.style.GalaxyTheme) {
        }

        //http://stackoverflow.com/questions/20922036/android-cant-call-setonitemclicklistener-from-a-listview
        //2017-02-02
        //when click an item in list
        oldMoodListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Log.d("listTag","on click");
                        Mood mood = FeelTripApplication.getMoodArrayList().get(position);
                        Log.d("listTag","on click2");
                        if (mood.getImage() == null){
                            Log.d("myTag","selected have no image ");
                        }else{
                            Log.d("myTag","image:"+mood.getImage());
                        }
                    }
                });

        return view;
    }


    //start when come back from add mood activity
    @Override
    public void onStart() {
        super.onStart();

        if(!FeelTripApplication.getFrag().equals(frag)) {
            FeelTripApplication.setFrag(frag);
        }
        adapter = FeelTripApplication.getListViewAdapter(getActivity());

        oldMoodListView.setAdapter(adapter);
        Log.d("listTag","done loading3");
    }

}
