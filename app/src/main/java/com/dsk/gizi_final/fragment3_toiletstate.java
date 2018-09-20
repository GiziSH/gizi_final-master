package com.dsk.gizi_final;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import java.util.HashMap;

/**
 * Created by suhyun on 2018-07-08.
 */

public class fragment3_toiletstate extends Fragment {
    //
    private static String TAG = "phptest_MainActivity";

    private static final String TAG_JSON="webnautes";
    private static final String TAG_doorNumber = "doorNumber";
    private static final String TAG_state = "state";
    private static final String TAG_cong = "$result";

    private TextView mTextViewResult;
    String mJsonString;
    private TextView text_sati;
    private TextView text_cong;
    private String Tnames; //어느 화장실인가
    private String Tcomplain;
    private static final String TAG_sati = "$result";

    //팝업창
    private ImageView showDialog;

    public static fragment3_toiletstate Tname(String str){
        fragment3_toiletstate fragment = new fragment3_toiletstate();
        Bundle args = new Bundle();
        args.putString("Tname", str);
        fragment.setArguments(args);
        return fragment;
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            Tnames = getArguments().getString("Tname");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment3_toiletstate, container, false);
        mTextViewResult = (TextView)v.findViewById(R.id.textView_main_result);
        fragment3_toiletstate.GetData task = new fragment3_toiletstate.GetData();
        task.execute("http://192.168.200.199/gizitest.php");
        //task.execute("http://172.17.108.227/gizitest.php");

        //팝업창 부분~
        final int[] selectedItem = {0};

        showDialog = (ImageView) v.findViewById(R.id.click);
        showDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] items = new String[]{"만족", "불만족"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog
                        .setTitle("화장실 사용 만족하셨나요?")
                        .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                selectedItem[0] = which;
                            }
                        })

                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getContext()
                                        , "응해주셔서 감사합니다."
                                        , Toast.LENGTH_SHORT).show();
                                updatecntDB cntDB = new updatecntDB();
                                cntDB.execute("http://192.168.200.199/update_allcnt.php");
                                //cntDB.execute("http://172.17.108.227/update_allcnt.php");

                                if (items[selectedItem[0]]=="불만족"){
                                    final String[] items = new String[]{"변기가 막힘","너무 지저분함","휴지가 없음","기타"};
                                    AlertDialog.Builder dialog2 = new AlertDialog.Builder(getContext());
                                    dialog2.setTitle("불만족하신 이유를 골라주세요")
                                            .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    selectedItem[0] = which;

                                                }
                                            })
                                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Log.e("불만족", items[selectedItem[0]]);
                                                    switch (items[selectedItem[0]]){
                                                        case "변기가 막힘": Tcomplain= new String("0"); break;
                                                        case "너무 지저분함":Tcomplain= new String("1");break;
                                                        case "휴지가 없음":Tcomplain= new String("2");break;
                                                        case "기타":Tcomplain= new String("3");break;
                                                    }
                                                    updateCP_DB CP = new updateCP_DB();
                                                    CP.execute("http://192.168.200.199/update_complain.php");

                                                }
                                            })
                                            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Toast.makeText(getContext()
                                                            , "취소 버튼을 눌렀습니다."
                                                            , Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                    dialog2.create();
                                    dialog2.show();


                                } else { //만족일 경우
                                    updatecntDB goodcntDB = new updatecntDB();
                                    goodcntDB.execute("http://192.168.200.199/update_goodcnt.php");
                                    //goodcntDB.execute("http://172.17.108.227/update_goodcnt.php");

                                }
                            }
                        })
                        .setNeutralButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getContext()
                                        , "취소 버튼을 눌렀습니다."
                                        , Toast.LENGTH_SHORT).show();
                            }
                        });
                dialog.create();
                dialog.show();
            }
        });
        satiDB satisati = new satiDB();
        satisati.execute("http://192.168.200.199/select_sati.php");
        //satisati.execute("http://172.17.108.227/select_sati.php");

        text_sati = (TextView)v.findViewById(R.id.sati); //만족도

        GetCong task1 = new GetCong();
        task1.execute("http://192.168.200.199/select_congestion.php");
        //task1.execute("http://172.17.108.227/select_congestion.php");
        text_cong = (TextView)v.findViewById(R.id.wait); //혼잡도


        return v;
    }
    private class GetData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(getContext(),
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            mTextViewResult.setText(result);
            Log.d(TAG, "response  - " + result);

            if (result == null){

                mTextViewResult.setText(errorString);
            }
            else {

                mJsonString = result;
                showResult();

            }
        }


        @Override
        protected String doInBackground(String... params) {

            String data="";
            /* 인풋 파라메터값 생성 */
            String param = "t_name=" + Tnames +  "";
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


    } //화장실 상태

    private void showResult(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String DoorNumber = item.getString(TAG_doorNumber);
                String State = item.getString(TAG_state);

                if (State.equals("1")) {

                    colortextview("text"+DoorNumber,getView());

                    System.out.println("text"+DoorNumber);
                }

            }


        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }//화장실 상태 확인

    private void colortextview(String str, View view){
        HashMap<String, Integer> hashMap = new HashMap<>();
        hashMap.put("text1",  R.id.text1);
        hashMap.put("text2",  R.id.text2);
        hashMap.put("text3",  R.id.text3);
        hashMap.put("text4",  R.id.text4);
        hashMap.put("text5",  R.id.text5);
        hashMap.put("text6",  R.id.text6);

        ((ImageView)view.findViewById(hashMap.get(str))).setImageResource(R.drawable.closedoor);
        //view.findViewById(hashMap.get(str)).setBackgroundColor(Color.rgb(255, 0, 0));
        //((TextView)view.findViewById(hashMap.get(str))).setText("사용");
    } //상태별로 색칠하기

    private class satiDB extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String data = "";
        String errorString = null;

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
            Log.d(TAG, "response  - " + data);

            if (data == null){

            }
            else {

                mJsonString = data;
                showsati();

            }
        }
        @Override
        protected String doInBackground(String... params) {

            /* 인풋 파라메터값 생성 */
            String param = "t_name=" + Tnames +  "";
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


    } //만족도 불러오기 DB

    private void showsati(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String $result = item.getString(TAG_sati);

                String res_sati = new String($result+"%");
                text_sati.setText(res_sati);
            }


        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }//만족도 보여주기

    public class updatecntDB extends AsyncTask<String, Integer, String> {
        String data = "";

        @Override
        protected String doInBackground(String... params) {

            /* 인풋 파라메터값 생성 */
            String param = "t_name=" + Tnames + "";
            Log.e("파라메터값", param);
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
                while ((line = in.readLine()) != null) {
                    buff.append(line + "\n");
                }
                data = buff.toString().trim();
                Log.e("만족도 all", data);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return data;
        }

        @Override
        protected void onPostExecute(String data) {
            super.onPostExecute(data);
            /* 서버에서 응답 */
            Log.e("만족도 all", data);

        }
    }

    public class updateCP_DB extends AsyncTask<String, Integer, String> {
        String data = "";

        @Override
        protected String doInBackground(String... params) {

            /* 인풋 파라메터값 생성 */
            String param = "t_name=" + Tnames + "&t_complain"+ Tcomplain+ "";
            Log.e("파라메터값", param);
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
                while ((line = in.readLine()) != null) {
                    buff.append(line + "\n");
                }
                data = buff.toString().trim();
                Log.e("컴플레인 all", data);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return data;
        }

        @Override
        protected void onPostExecute(String data) {
            super.onPostExecute(data);
            /* 서버에서 응답 */
            Log.e("만족도 all", data);

        }
    }


    private class GetCong extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String data = "";
        String errorString = null;

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
            Log.d(TAG, "response  - " + data);

            if (data == null){

            }
            else {

                mJsonString = data;
                CongResult();

            }
        }
        @Override
        protected String doInBackground(String... params) {

            /* 인풋 파라메터값 생성 */
            String param = "t_name=" + Tnames +  "";
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

    private void CongResult(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                int $result = item.getInt(TAG_cong);
                if ($result>60 && $result<100) {
                    String res_cong = new String("보통");
                    text_cong.setText(res_cong);
                    text_cong.setTextColor(Color.parseColor("#FF7F00"));

                } else if($result==100) {
                    String res_cong = new String("혼잡");
                    text_cong.setText(res_cong);
                    text_cong.setTextColor(Color.parseColor("#FF0000"));

                }else {
                    String res_cong = new String("여유");
                    text_cong.setText(res_cong);
                    text_cong.setTextColor(Color.parseColor("#00FF00"));

                }

            }


        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }

}
