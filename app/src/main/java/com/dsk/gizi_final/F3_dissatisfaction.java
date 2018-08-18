package com.dsk.gizi_final;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by suhyun on 2018-08-13.
 */

public class F3_dissatisfaction extends Fragment {
    public F3_dissatisfaction() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.f3_dissatisfaction, container, false);

        return v;
    }
}
