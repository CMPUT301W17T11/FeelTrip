package sideMenuPackage.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.henzoshimada.feeltrip.R;

/**
 * Created by Ivan on 3/8/2017.
 */

public class mainFragment extends Fragment{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.content_side_menu,container,false);

        return rootView;
    }
}
