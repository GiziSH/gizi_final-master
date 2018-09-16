package com.dsk.gizi_final;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
    private ArrayList<Toilet> toiletsOri; //원래 태초의 리스트
    private ArrayList<Toilet> toilets; //원래 태초의 리스트
    private final String TAG = ToiletAdapter.class.getSimpleName();

    List<String> list_bookmark;
    private SharedPreferences pref1;
    private SharedPreferences.Editor editor1;
    private boolean checkData;

    private ArrayList<Toilet> newitems; //새리스트
    private ArrayList<Toilet> temp;
    String Tnames;
    Boolean Tbookmark;


    public ToiletAdapter(Activity activity, int resource,ArrayList<Toilet> toilets){
        super(activity, resource, toilets);
        this.activity = activity;
        this.toilets = toilets; // 이거 바뀌ㅏ면
        this.toiletsOri = toilets; // 이거도 바뀜?모름ㅇㅅㅇ 해보지 멍ㅇ
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
        //list_bookmark  = new ArrayList<>();
        //showbookmark();
/*
        if (list_bookmark.size()!=0){
            holder.check.setChecked(list_bookmark.contains(listViewItem.getToiletname()));
            System.out.println(list_bookmark);
        }
*/


        holder.check.setOnCheckedChangeListener(onCheckedChangeListener(listViewItem));
        holder.check.setChecked(listViewItem.getBookmark());

        return convertView;
    }


    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener(final Toilet t) {
        return new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //updatebmDB Ubm = new updatebmDB();
                //list_bookmark = new ArrayList<>();
                Tnames = new String(t.getToiletname());

                if (isChecked) { //체크했음
                    //pref1 = getContext().getSharedPreferences("pref1",MODE_PRIVATE);
                    //editor1 = pref1.edit();
                    //list_bookmark  = new ArrayList<>();
                    //showbookmark();
                    //list_bookmark.add(t.getToiletname());
                    //System.out.println(list_bookmark);
                    //System.out.println(list_bookmark.get(0));
                    t.setBookmark("1");
                    //Tbookmark = new Boolean(t.getBookmark());
                    updatebmDB Ubm = new updatebmDB();
                    Ubm.execute();

                    //addbookmark(str);
                    //savebookmark();

                    //t.setSelected(true);
                } else { //체크해제
                    t.setBookmark("0");
                    updatebmDB Ubm = new updatebmDB();
                    //Tbookmark = new Boolean(t.getBookmark());
                    Ubm.execute();
                    //list_bookmark.add(t.getToiletname());
                    //Tbookmark = false;
                    //showbookmark();
                    //String str = new String(t.getToiletname());
                    //deletebookmark(str);
                    //savebookmark();
                    //t.setSelected(false);
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
    public class updatebmDB extends AsyncTask<Void, Integer, Void> {
        String data = "";
        @Override
        protected Void doInBackground(Void... unused) {
            Log.e("name",Tnames+Tbookmark);
            /* 인풋 파라메터값 생성 */
            //String param = "t_name=" + Tnames + "&t_bookmark=" + Tbookmark + "";
            String param = "t_name=" +Tnames + "";
            try {//
                /* 서버연결 */
                URL url = new URL(
                        "http://192.168.200.199/update_bookmark.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();

                /* 안드로이드 -> 서버 파라메터값 전달 */
                OutputStream outs = conn.getOutputStream();
                outs.write(param.getBytes("UTF-8"));
                outs.flush();
                outs.close();

                /* 서버 -> 안드로이드 파라메터값 전달 */
                InputStream is = null;
                BufferedReader in = null;


                is = conn.getInputStream();
                in = new BufferedReader(new InputStreamReader(is), 8 * 1024);
                String line = null;
                StringBuffer buff = new StringBuffer();
                while ( ( line = in.readLine() ) != null )
                {
                    buff.append(line + "\n");
                }
                data = buff.toString().trim();
                Log.e("RECV DATA1",data);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            /* 서버에서 응답 */
            Log.e("RECV DATA2",data);

        }
    }

/*
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
*/


    //각 화장실 데이터 넣기
    public void addtoilet(Drawable icon, String toiletname, String toiletline, String bookmark){
        Toilet toilet = new Toilet();



        toilet.setImg(icon);
        toilet.setToiletname(toiletname);
        toilet.setToiletline(toiletline);
        toilet.setBookmark(bookmark);
        Log.e("bookmark test ",toiletname+toilet.getBookmark());
        toiletsOri.add(toilet);
    }
    //각 화장실 데이터 지우기
    public void deletetoilet(){
        toiletsOri.clear();
        toilets.clear();
    }


    public void filter(String charText) {
        //Pattern p = Pattern.compile("^[a-zA-Z가-힣]*$");
        //Matcher m = p.matcher(charText);
        //Toilet to = new Toilet();
        newitems = new ArrayList<>();
        newitems.clear();


        if (charText.length() == 0) {
            newitems.addAll(toiletsOri);

        }
        else {
            /*
            for(Toilet to : toilets ) {
                if (to.getToiletname().toLowerCase(Locale.getDefault()).contains(charText));
                newitems.add(to);
            }*/

            for (int i=0;i<toiletsOri.size();i++){
                if (toiletsOri.get(i).getToiletname().toLowerCase().contains(charText)){
                    //System.out.println(toilets.get(i));
                    newitems.add(toiletsOri.get(i));
                }
            }
        }

        this.toilets = newitems;
        notifyDataSetChanged();

    }


}

/*ㄱ
public void search(String charText){


        // 어댑터 없을 때 리스트 두개로 검색하기 방법
        Pattern p = Pattern.compile("^[a-zA-Z가-힣]*$");
        Matcher m = p.matcher(charText);

        mArrayList2.clear();

        if (charText.length() == 0) {
            mArrayList2.addAll(mArrayList);
        }
        else {
            for (int i=0; i<mArrayList.size();i++){
                String str = mArrayList.get(i).get("name");



                if (str.toLowerCase().contains(charText)) {

                    mArrayList2.add(mArrayList.get(i));
                }
            }
        }
        ListAdapter madapter = new SimpleAdapter(
                getContext(), mArrayList2, R.layout.row_listview,
                new String[]{TAG_name,TAG_line},
                new int[]{R.id.toiletname, R.id.toiletline}

        );
        mlistView.setAdapter(madapter);

    }
 */