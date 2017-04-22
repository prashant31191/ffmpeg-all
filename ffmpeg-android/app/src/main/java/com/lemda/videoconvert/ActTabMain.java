package com.lemda.videoconvert;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.fragment.UploadFragment;

/**
 * Created by Admin on 8/30/2016.
 */

public class ActTabMain extends Activity {


    FrameLayout flData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_tab_main);

        if (savedInstanceState == null) {
            /*Fragment newFragment = new DebugExampleTwoFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(R.id.flData, newFragment).commit();*/
            Fragment newFragment = new UploadFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(R.id.flData, newFragment).commit();
        }
    }

    public static class DebugExampleTwoFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            EditText v = new EditText(getActivity());
            v.setText("Hello Fragment!");
            return v;
        }
    }
}
