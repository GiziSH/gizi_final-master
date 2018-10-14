package com.dsk.gizi_final;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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

public class FinalConfirm extends Fragment{
    private static String TAG = "phptest_MainActivity";
    private static final String TAG_JSON="webnautes";
    private static final String TAG_NUM = "num";
    private static final String TAG_AUTHOR ="author";
    private static final String TAG_TITLE = "title";
    private static final String TAG_TIME ="time";
    private static final String TAG_CONTENT ="content";
    private Button btn_fix, btn_delete, btn_commit;
    private TextView titleView, authorView, contentView;
    ArrayList mArrayList;
    String mJsonString;
    String num, title, author, content;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_finalconfirm, container, false);

        num = getArguments().getString("num");
        Log.e("수정된 게시물 num", num);
        titleView = (TextView) v.findViewById(R.id.titleview);
        authorView = (TextView) v.findViewById(R.id.authorview);
        contentView = (TextView) v.findViewById(R.id.contentview);

        GetData task = new GetData();
        task.execute("http://192.168.200.199/board_confirm.php");

        btn_commit = (Button) v.findViewById(R.id.commitbutton);
        btn_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment4 fragment4 = new Fragment4();
                android.support.v4.app.FragmentTransaction fragmenttransaction = getFragmentManager().beginTransaction();
                fragmenttransaction.replace(R.id.fragment_container, fragment4);
                fragmenttransaction.addToBackStack(null);
                fragmenttransaction.commit();

            }
        });
        return v;
    }

    private class GetData extends AsyncTask<String, Void, String> {
        String data = "";
        ProgressDialog progressDialog;
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
            //mTextViewResult.setText(result);
            Log.d(TAG, "response  - " + data);

            if (data == null){
                // mTextViewResult.setText(errorString);
            }
            else {
                mJsonString = data;
                showResult();
            }
        }


        @Override
        protected String doInBackground(String... params) {
            String param = "u_num=" + num + "";

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

            for(int i=0;i<jsonArray.length();i++) {

                JSONObject item = jsonArray.getJSONObject(i);


                //JSONObject item = jsonArray.getJSONObject(jsonArray.length()-1);
                String title = item.getString(TAG_TITLE);
                String author = item.getString(TAG_AUTHOR);
                String content = item.getString(TAG_CONTENT);
                Log.e("수정된 게시물", title + author + content);
                titleView.setText(title);
                authorView.setText(author);
                contentView.setText(content);
            }
        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }
}