package layout;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.example.henzoshimada.feeltrip.EditMoodActivity;
import com.example.henzoshimada.feeltrip.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link homeFragmment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link homeFragmment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class homeFragmment extends Fragment{
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
        return view;
    }


}
