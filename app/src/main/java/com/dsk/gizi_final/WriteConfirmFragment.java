package com.dsk.gizi_final;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class WriteConfirmFragment extends Fragment {
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
    String title, author, content;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_writeconfirmfragment, container, false);

        titleView = (TextView) v.findViewById(R.id.titleview);
        authorView = (TextView) v.findViewById(R.id.authorview);
        contentView = (TextView) v.findViewById(R.id.contentview);

        GetData task = new GetData();
        task.execute("http://192.168.219.109/recent_getjson.php");

        btn_fix = (Button) v.findViewById(R.id.fixbutton);
        btn_delete = (Button) v.findViewById(R.id.deletebutton);
        btn_commit = (Button) v.findViewById(R.id.commitbutton);
        btn_fix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = titleView.getText().toString();
                author = authorView.getText().toString();
                content = contentView.getText().toString();

                Bundle bundle = new Bundle();
                bundle.putString("title",title);
                bundle.putString("author",author);
                bundle.putString("content",content);

                WriteFixFragment writeFixFragment = new WriteFixFragment();
                writeFixFragment.setArguments(bundle);
                android.support.v4.app.FragmentTransaction fragmenttransaction = getFragmentManager().beginTransaction();
                fragmenttransaction.replace(R.id.fragment_container, writeFixFragment);
                fragmenttransaction.commit();

            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DeleteFragment deleteFragment = new DeleteFragment();
                android.support.v4.app.FragmentTransaction fragmenttransaction = getFragmentManager().beginTransaction();
                fragmenttransaction.replace(R.id.fragment_container, deleteFragment);
                fragmenttransaction.commit();
            }
        });

        btn_commit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Fragment4 fragment4 = new Fragment4();
                android.support.v4.app.FragmentTransaction fragmenttransaction = getFragmentManager().beginTransaction();
                fragmenttransaction.replace(R.id.fragment_container, fragment4);
                fragmenttransaction.commit();
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
            //mTextViewResult.setText(result);
            Log.d(TAG, "response  - " + result);

            if (result == null){
                // mTextViewResult.setText(errorString);
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
        String title = " ";
        String author = " ";
        String content = " ";
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            JSONObject item = jsonArray.getJSONObject(jsonArray.length()-1);
            title = item.getString(TAG_TITLE);
            author = item.getString(TAG_AUTHOR);
            content = item.getString(TAG_CONTENT);

            titleView.setText(title);
            authorView.setText(author);
            contentView.setText(content);

        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }
}
