package com.dsk.gizi_final;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by suhyun on 2018-07-08.
 */
//
public class fragment3_option extends Fragment {
    private static String TAG = "phptest_MainActivity";

    private static final String TAG_JSON="webnautes";
    private static final String TAG_sati = "$result";
    private static final String TAG_cong = "$result";
    String mJsonString;

    private String Tnames; //어느 화장실인가
    private TextView text_sati;
    private TextView text_cong;

    public fragment3_option() {
        // Required empty public constructor
    }

    public static fragment3_option Tname(String str){
        fragment3_option fragment = new fragment3_option();
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
        View v = inflater.inflate(R.layout.fragment3_option, container, false);

        ImageButton girl = (ImageButton)v.findViewById(R.id.girl);
        girl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//버튼 눌렸을 때
                fragment3_toiletstate ts = new fragment3_toiletstate();
                android.support.v4.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment3_toiletstate.Tname(Tnames));
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });

        GetSati task = new GetSati();
        //task.execute("http://192.168.200.199/select_sati.php");
        task.execute("http://172.17.108.227/select_sati.php");

        GetCong task1 = new GetCong();
        //task1.execute("http://192.168.200.199/select_congestion.php");
        task1.execute("http://172.17.108.227/select_congestion.php");

        text_sati = (TextView)v.findViewById(R.id.sati); //만족도
        text_cong = (TextView)v.findViewById(R.id.wait); //혼잡도

        return v;
    }

    private class GetSati extends AsyncTask<String, Void, String> {
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
                SatiResult();

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

    private void SatiResult(){
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