package com.dsk.gizi_final;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment1 extends Fragment {

    Button mBtn; //편집버튼

    private LinearLayout container;
    private ArrayList<Toilet> checkedList;

    private List<String> list_bookmark;
    private SharedPreferences pref1;
    private SharedPreferences.Editor editor1;
    private ListView bm_listview;

    public Fragment1() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_fragment1, container, false);

        pref1 = getActivity().getSharedPreferences("pref1",getActivity().MODE_PRIVATE);
        editor1 = pref1.edit();

        bm_listview = (ListView) v.findViewById(R.id.bookmark_listview);
        TextView tx = (TextView)v.findViewById(R.id.empty2_text);
        bm_listview.setEmptyView(tx);


        list_bookmark = new ArrayList<>();
        showbookmark();
        final ArrayAdapter<String> bmadapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,list_bookmark);
        bm_listview.setAdapter(bmadapter);



        mBtn = (Button)v.findViewById(R.id.popup_menu);
        mBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getContext(),v); //팝업메뉴 객체만듦

                //xml파일에 메뉴 정의한 것 가져오기  위한 전개자 선언
                MenuInflater inflater = popup.getMenuInflater();
                Menu menu = popup.getMenu();

                //실제 메뉴 정의한 것 가져오는 부분 menu객체 넣어주기
                inflater.inflate(R.menu.popupmenu, menu);

                //메뉴 클릭했을 때 처리하는 부분
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch(item.getItemId()){
                            case R.id.popup_select:
                                mBtn.setBackgroundColor(Color.RED);
                                break;
                            case R.id.popup_delete:
                                mBtn.setBackgroundColor(Color.BLUE);
                                break;

                        }
                        return false;
                    }
                });
                popup.show();
            }
        });

        //container = (LinearLayout) v.findViewById(R.id.linearLayout);
        //checkedList = new ArrayList<Toilet>(); //initializing list
        //getDataFromIntent(); // 데이터받기 F3에서 보낸
        //generateDataToContainerLayout();

        return v;
    }

    private void getDataFromIntent() {
        //checkedList = getIntent().getParcelableArrayListExtra("Checked List");
        Log.i("ListActivity", "size" + checkedList.size());
    }

    @SuppressLint("InflateParams")
    private void generateDataToContainerLayout() {

        int i = 0;
        if (checkedList.size() == i) { //do nothing
        }
        while (checkedList.size() > i) {
            final Toilet toilet = checkedList.get(i);
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.row_listview, null,false);

            ImageView Image = (ImageView) view.findViewById(R.id.imgsearch);
            TextView toiletName = (TextView) view.findViewById(R.id.toiletname);
            TextView toiletLine = (TextView) view.findViewById(R.id.toiletline);
            CheckBox checked = (CheckBox) view.findViewById(R.id.checkboxbookmark);
            checked.setVisibility(View.GONE);
            if (toilet.isSelected()) {
                Log.i("ListActivity", "here" + toilet.getToiletname());
                toiletName.setText(toilet.getToiletname());
                toiletLine.setText(toilet.getToiletline());
                /*if (friend.isGender()) {
                    genderImage.setImageResource(R.drawable.male);
                } else {
                    genderImage.setImageResource(R.drawable.female);
                }*/

                // add view after all
                container.addView(view);
            }

            i++; // rise i
        }
    }
    //내부메모리에서 불러오기
    public void showbookmark(){
        String json = pref1.getString("bookmark", null);
        if (json != null){
            try{
                JSONArray array = new JSONArray(json);
                list_bookmark.clear();

                for(int i = array.length() - 1; i>=0;i--){
                    String url = array.optString(i);
                    list_bookmark.add(url);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


}
