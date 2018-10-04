package com.dsk.gizi_final;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment3 extends Fragment  {
    private static String TAG = "phptest_MainActivity";
    private static final String TAG_JSON="webnautes";
    private static final String TAG_name = "name";
    private static final String TAG_line = "line";
    private static final String TAG_bookmark="bookmark";

    private TextView mTextViewResult;
    ArrayList<HashMap<String, String>> mArrayList;
    ArrayList<HashMap<String, String>> mArrayList2;
    ListView mlistView;
    String mJsonString;

    //즐겨찾기
    private ArrayList<Toilet> toilets;
    private ToiletAdapter madapter;
    private int[] img = {R.drawable.search};

    //검색 선택
    private Spinner MySpinner1;
    private EditText editSearch;

    //최근검색
    private List<String> list_names;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    String SRname;
    public Fragment3() {
        // Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_fragment3, container, false);

        mTextViewResult = (TextView)v.findViewById(R.id.textView_main_result);
        //즐겨찾기
        mlistView = (ListView) v.findViewById(R.id.listView);
        setListViewAdapter();



        Toilet toilet = new Toilet();


        mArrayList = new ArrayList<>();
        mArrayList2 = new ArrayList<>();

        // 지하철역,휴게소 선택
        String [] values1 = {"선택","지하철역","휴게소"};
        MySpinner1 = (Spinner)v.findViewById(R.id.option);
        /*ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(
                this.getActivity(),
                R.layout.spinner_simple
        );*/
        final ArrayAdapter<String> adapterSpinner1 = new ArrayAdapter(this.getActivity(), R.layout.spinner_simple);
        adapterSpinner1.setDropDownViewResource( R.layout.spinner_simple);
        MySpinner1.setAdapter(adapterSpinner1);
        adapterSpinner1.addAll(values1);
        MySpinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:

                        madapter.deletetoilet();
                        mlistView.setAdapter(madapter);
                        break;
                    case 1:

                        madapter.deletetoilet();

                        SRname = "subway";
                        GetData task = new GetData();
                        //task.execute("http://192.168.200.199/select_toilet.php");
                        //task.execute("http://172.17.108.227/select_toilet.php");
                        task.execute("http://192.168.200.186/select_toilet.php");

                        break;
                    case 2:

                        madapter.deletetoilet();

                        SRname = "restarea";
                        GetData task2 = new GetData();
                        task2.execute("http://192.168.200.199/select_toilet.php");
                        //task2.execute("http://172.17.108.227/select_toilet.php");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        editSearch = (EditText) v.findViewById(R.id.editSearch);

        pref = getActivity().getSharedPreferences("pref",getActivity().MODE_PRIVATE);
        editor = pref.edit();
        list_names = new ArrayList<>();
        showLately(); //전에 저장한 것 안지워지게 하기 위함


        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String str1 = new String(mArrayList.get(position).get(TAG_name));
                addLately(str1);
                saveLately();

                Toast.makeText(getActivity(),str1,Toast.LENGTH_SHORT).show();

                fragment3_option op = new fragment3_option();
                android.support.v4.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment3_option.Tname(str1));
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();


            }
        });
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //start 지점에서 시작되는 count갯수만큼 글자들이 after길이만큼의 글자로 대치되려고 할때 호출됨
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //start 지점에서 시작되는 before 갯수만큼의 글자들이 count갯수만큼의 글자드롤 대치되었을 때 호출
            }

            @Override
            public void afterTextChanged(Editable s) {//edittext의 텍스트가 변경되면 호출
                // input창에 문자를 입력할때마다 호출된다.
                // search 메소드를 호출한다.
                String text = editSearch.getText().toString();
                madapter.filter(text);
                //search(text);
            }
        });


        return v;
    }
    private void setListViewAdapter(){
        toilets = new ArrayList<Toilet>();
        madapter = new ToiletAdapter(this.getActivity(), R.layout.row_listview, toilets);
        mlistView.setAdapter(madapter);
    }

    // 최근검색
    //배열안에 집어넣기
    public void addLately(String value) {

        String str1 = new String();

        for(int i =0; i<list_names.size(); i++){//중복검사
            str1 = list_names.get(i);
            if (str1.equals(value)){
                list_names.remove(value);
                list_names.add(value);
                return;
            }
        }
        list_names.add(value);
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

    private class GetData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;
        String data = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(getContext(),
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String data) {
            super.onPostExecute(data);
            progressDialog.dismiss();
            mTextViewResult.setText(data);
            Log.d(TAG, "response  - " + data);

            if (data == null){

                mTextViewResult.setText(errorString);
            }
            else {

                mJsonString = data;
                showResult();

            }
        }
        @Override
        protected String doInBackground(String... params) {

            /* 인풋 파라메터값 생성 */
            String param = "t_SR=" + SRname +  "";
            Log.e("POST",param);
            String serverURL = params[0];
            try {
                /* 서버연결 */
                URL url = new URL(serverURL);
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

                /* 서버에서 응답 */
                Log.e("RECV DATA",data);


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return data;
        }

    }

    private void showResult(){

        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);


                String name = item.getString(TAG_name);
                String line = item.getString(TAG_line);
                String bookmark = item.getString(TAG_bookmark);

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put(TAG_name, name);
                hashMap.put(TAG_line, line);
                hashMap.put(TAG_bookmark, bookmark);
                mArrayList.add(hashMap);

                //System.out.println(bookmark);

                madapter.addtoilet(ContextCompat.getDrawable(getContext(),img[0]),name,line,bookmark);

            }


            /*
            ListAdapter madapter = new SimpleAdapter(
                    getContext(), mArrayList, R.layout.row_listview,
                    new String[]{TAG_name,TAG_line},
                    new int[]{R.id.toiletname, R.id.toiletline}
            );*/

            mlistView.setAdapter(madapter);
            //checkbookmark();
            //System.out.println("전체 이름 나와라"+);
        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }

}