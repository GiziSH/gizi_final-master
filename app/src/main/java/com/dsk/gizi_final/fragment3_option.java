package com.dsk.gizi_final;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by suhyun on 2018-07-08.
 */

public class fragment3_option extends Fragment {



    public fragment3_option() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment3_option, container, false);

        ImageButton girl = (ImageButton)v.findViewById(R.id.girl);
        girl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//버튼 눌렸을 때
                fragment3_toiletstate ts = new fragment3_toiletstate();
                android.support.v4.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, ts);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });

        TextView waiting = (TextView)v.findViewById(R.id.wait);


        return v;
    }

}