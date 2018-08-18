package com.dsk.gizi_final;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by suhyun on 2018-08-06.
 */

public class ToiletAdapter extends ArrayAdapter<Toilet> {

    //private Fragment fragment;
    private Activity activity;
    private ArrayList<Toilet> toilets;
    private final String TAG = ToiletAdapter.class.getSimpleName();

    private List<String> list_bookmark;
    private SharedPreferences pref1;
    private SharedPreferences.Editor editor1;
    private boolean checkData;

    private ArrayList<Toilet> newitems;
    private ArrayList<Toilet> temp;



    public ToiletAdapter(Activity activity, int resource,ArrayList<Toilet> toilets){
        super(activity, resource, toilets);
        this.activity = activity;
        this.toilets = toilets;
        //this.newitems = new ArrayList<Toilet>();
    }

    @Override
    public int getCount()
    {
        return toilets.size();
    }

    @Override
    public Toilet getItem(int position)
    {
        return toilets.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final int pos = position;
        ViewHolder holder = null;
        final Context context = parent.getContext();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_listview, parent, false);
            // get all UI view
            holder = new ViewHolder(convertView);
            // set tag for holder
            convertView.setTag(holder);
        }else {
            // if holder created, get tag from view
            holder = (ViewHolder) convertView.getTag();
        }

        Toilet listViewItem = toilets.get(position);

        holder.image.setImageDrawable(listViewItem.getImg());
        holder.name.setText(listViewItem.getToiletname());
        holder.line.setText(listViewItem.getToiletline());
        pref1 = getContext().getSharedPreferences("pref1",MODE_PRIVATE);
        editor1 = pref1.edit();
        list_bookmark  = new ArrayList<>();
        showbookmark();

        if (list_bookmark.size()!=0){
            holder.check.setChecked(list_bookmark.contains(listViewItem.getToiletname()));
            System.out.println(list_bookmark);
        }



        holder.check.setOnCheckedChangeListener(onCheckedChangeListener(listViewItem));


        return convertView;
    }


        private CompoundButton.OnCheckedChangeListener onCheckedChangeListener(final Toilet t) {
            return new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        pref1 = getContext().getSharedPreferences("pref1",MODE_PRIVATE);
                        editor1 = pref1.edit();
                        list_bookmark  = new ArrayList<>();
                        showbookmark();
                        String str = new String(t.getToiletname());
                        addbookmark(str);
                        savebookmark();
                        //System.out.println(getItemId(getPosition(t))); //출력값 6 =>금정역
                        //System.out.println(getItem(getPosition(t))); //출력값 com.suhyun.gizi2.Toilet@af9f822

                        t.setSelected(true);
                    } else {
                        showbookmark();
                        String str = new String(t.getToiletname());
                        deletebookmark(str);
                        savebookmark();
                        t.setSelected(false);
                    }
                }
            };
        }


    private class ViewHolder {
        private ImageView image;
        private TextView name;
        private TextView line;
        private CheckBox check;

        public ViewHolder(View v) {
            image = (ImageView) v.findViewById(R.id.imgsearch);
            name = (TextView) v.findViewById(R.id.toiletname);
            line = (TextView) v.findViewById(R.id.toiletline);
            check = (CheckBox) v.findViewById(R.id.checkboxbookmark);
        }
    }

    //즐겨찾기
    //배열안에 집어넣기
    public void addbookmark(String value) {

        String str1 = new String();

        for(int i =0; i<list_bookmark.size(); i++){//중복검사
            str1 = list_bookmark.get(i);
            if (str1.equals(value)){
                list_bookmark.remove(value);
                list_bookmark.add(value);
                return;
            }
        }
        list_bookmark.add(value);
    }
    //배열에서 제거
    public void deletebookmark(String value) {

        String str1 = new String();

        for(int i =0; i<list_bookmark.size(); i++){//중복검사
            str1 = list_bookmark.get(i);
            if (str1.equals(value)){
                list_bookmark.remove(value);
                return;
            }
        }
    }
    //내부메모리에 저장
    public void savebookmark(){
        ViewHolder holder = null;
        JSONArray array = new JSONArray();
        for(int i=0; i<list_bookmark.size();i++){
            array.put(list_bookmark.get(i));
        }
        String a = array.toString();

        //editor1.putBoolean("cb_bookmark",holder.check.isChecked());
        editor1.putString("bookmark", a);
        editor1.commit();
    }
    //내부메모리에서 불러오기
    public void showbookmark(){
        //checkData = pref1.getBoolean("cb_bookmark",false);
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



    //각 화장실 데이터 넣기
    public void addtoilet(Drawable icon, String toiletname, String toiletline){
        Toilet toilet = new Toilet();

        toilet.setImg(icon);
        toilet.setToiletname(toiletname);
        toilet.setToiletline(toiletline);

        toilets.add(toilet);
    }




    public void filter(String charText) {
        //Pattern p = Pattern.compile("^[a-zA-Z가-힣]*$");
        //Matcher m = p.matcher(charText);
        //Toilet to = new Toilet();
        newitems = new ArrayList<>();
        newitems.clear();


        if (charText.length() == 0) {
            newitems.addAll(toilets);

        }
        else {
            /*
            for(Toilet to : toilets ) {
                if (to.getToiletname().toLowerCase(Locale.getDefault()).contains(charText));
                newitems.add(to);
            }*/

            for (int i=0;i<toilets.size();i++){
                if (toilets.get(i).getToiletname().toLowerCase().contains(charText)){
                    //System.out.println(toilets.get(i));
                    newitems.add(toilets.get(i));
                }
            }
        }

        this.toilets = newitems;
        notifyDataSetChanged();

    }


}
