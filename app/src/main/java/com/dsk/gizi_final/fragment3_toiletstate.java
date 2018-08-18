package com.dsk.gizi_final;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by suhyun on 2018-07-08.
 */

public class fragment3_toiletstate extends Fragment {

    private static String TAG = "phptest_MainActivity";

    private static final String TAG_JSON="webnautes";
    private static final String TAG_doorNumber = "doorNumber";
    private static final String TAG_state = "state";
    private TextView mTextViewResult;
    String mJsonString;

    //팝업창
    private ImageView showDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment3_toiletstate, container, false);
        mTextViewResult = (TextView)v.findViewById(R.id.textView_main_result);
        fragment3_toiletstate.GetData task = new fragment3_toiletstate.GetData();
        task.execute("http://192.168.200.199/gizitest.php");



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
                                        , "응해주셔서 감사합니다."+items[selectedItem[0]]//만족or불만족으로 뜸
                                        , Toast.LENGTH_SHORT).show();
                                if (items[selectedItem[0]]=="불만족"){
                                    F3_dissatisfaction f3_dis = new F3_dissatisfaction();
                                    android.support.v4.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                                    fragmentTransaction.replace(R.id.fragment_container, f3_dis);
                                    fragmentTransaction.addToBackStack(null);
                                    fragmentTransaction.commit();
                                } else {

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

            String serverURL = params[0];


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.connect();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString().trim();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }



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

    }



    private void colortextview(String str, View view){
        HashMap<String, Integer> hashMap = new HashMap<>();
        hashMap.put("text1",  R.id.text1);
        hashMap.put("text2",  R.id.text2);
        hashMap.put("text3",  R.id.text3);
        hashMap.put("text4",  R.id.text4);
        hashMap.put("text5",  R.id.text5);
        hashMap.put("text6",  R.id.text6);


        view.findViewById(hashMap.get(str)).setBackgroundColor(Color.rgb(255, 0, 0));
        ((TextView)view.findViewById(hashMap.get(str))).setText("사용");
    }

}
