package com.dsk.gizi_final;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment2 extends Fragment {
    Button mBtn;
    Button deleteBtn;
    private List<String> list_names;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private ListView rs_listview;
    private ArrayAdapter rsadapter2;

    public Fragment2() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_fragment2, container, false);

        pref = getActivity().getSharedPreferences("pref",getActivity().MODE_PRIVATE);
        editor = pref.edit();

        rs_listview = (ListView) v.findViewById(R.id.recent_listview);
        TextView tx = (TextView)v.findViewById(R.id.empty_text);
        rs_listview.setEmptyView(tx);


        list_names = new ArrayList<>();
        showLately();
        final ArrayAdapter<String> rsadapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,list_names);
        rs_listview.setAdapter(rsadapter);


        mBtn = (Button)v.findViewById(R.id.popup_menu);
        mBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                final PopupMenu popup = new PopupMenu(getContext(),v); //팝업메뉴 객체만듦
                //xml파일에 메뉴 정의한 것 가져오기  위한 전개자 선언
                MenuInflater inflater = popup.getMenuInflater();
                Menu menu = popup.getMenu();
                //실제 메뉴 정의한 것 가져오는 부분 menu객체 넣어주기
                inflater.inflate(R.menu.popupmenu, menu);

                //메뉴 클릭했을 때 처리하는 부분
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){
                    public int position;

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch(item.getItemId()){
                            case R.id.popup_select:
                                ArrayAdapter<String> rsadapter2 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_multiple_choice,list_names);
                                rs_listview.setAdapter(rsadapter2);
                                rs_listview.setItemsCanFocus(true);
                                rs_listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                                popup.dismiss();
                                break;
                            case R.id.popup_delete:
                                SparseBooleanArray checkedItems = rs_listview.getCheckedItemPositions();
                                int count = rsadapter.getCount() ;
                                for (int i = count-1; i >= 0; i--) {
                                    if (checkedItems.get(i)) {
                                        list_names.remove(i) ;
                                    }
                                }
                                // 모든 선택 상태 초기화.
                                rs_listview.clearChoices() ;
                                saveLately();
                                rsadapter.notifyDataSetChanged();
                                rs_listview.setAdapter(rsadapter);
                                break;
                            case R.id.popup_allselect:
                                rsadapter2 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_multiple_choice,list_names);
                                rs_listview.setAdapter(rsadapter2);
                                rs_listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                                popup.dismiss();

                                count = rsadapter.getCount() ;

                                for (int i=0; i<count; i++) {
                                    rs_listview.setItemChecked(i, true) ;
                                }
                                popup.dismiss();

                                break;

                        }
                        return false;
                    }
                });
                popup.show();
            }
        });

        rs_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                fragment3_option op = new fragment3_option();
                android.support.v4.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, op);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        return v;
    }
    //내부메모리에 저장
    public void saveLately(){
        JSONArray array = new JSONArray();
        for(int i=0; i<list_names.size();i++){
            array.put(list_names.get(i));
        }
        String a = array.toString();

        editor.putString("lately", a);
        editor.commit();
    }

    //내부메모리에서 불러오기
    public void showLately(){
        String json = pref.getString("lately", null);
        if (json != null){
            try{
                JSONArray array = new JSONArray(json);
                list_names.clear();

                for(int i = array.length() - 1; i>=0;i--){
                    String url = array.optString(i);
                    list_names.add(url);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
