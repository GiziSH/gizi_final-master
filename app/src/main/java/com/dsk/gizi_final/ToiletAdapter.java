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
        this.toilets = toilets;
        this.toiletsOri = toilets;
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
                    t.setBookmark("1");
                    updatebmDB Ubm = new updatebmDB();
                    Ubm.execute();
                } else { //체크해제
                    t.setBookmark("0");
                    updatebmDB Ubm = new updatebmDB();
                    Ubm.execute();
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
