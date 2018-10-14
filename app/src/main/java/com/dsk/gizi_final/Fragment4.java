package com.dsk.gizi_final;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class Fragment4 extends Fragment {
    private static String TAG = "phptest_MainActivity";

    private static final String TAG_JSON="webnautes";
    private static final String TAG_NUM = "num";
    private static final String TAG_AUTHOR ="author";
    private static final String TAG_TITLE = "title";
    private static final String TAG_TIME ="time";
    private static final String TAG_CONTENT ="content";

    //private TextView mTextViewResult;
    ArrayList<HashMap<String, String>> mArrayList;
    ListView mlistView;
    String mJsonString;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_fragment4, container, false);
        //mTextViewResult = (TextView)v.findViewById(R.id.textView_main_result);
        mlistView = (ListView) v.findViewById(R.id.listView_main_list);
        mArrayList = new ArrayList<>();

        Button writebutton = (Button) v.findViewById(R.id.write_button);
        writebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WriteFragment writeFragment = new WriteFragment();
                android.support.v4.app.FragmentTransaction fragmenttransaction = getFragmentManager().beginTransaction();
                fragmenttransaction.replace(R.id.fragment_container, writeFragment);
                fragmenttransaction.commit();
            }
        });

        GetData refresh = new GetData();
        refresh.execute("http://192.168.200.199/num_refresh.php");

        GetData task = new GetData();
        task.execute("http://192.168.200.199/getjson.php");

        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String num_result = new String(mArrayList.get(position).get(TAG_NUM));
                String title_result = new String(mArrayList.get(position).get(TAG_TITLE));
                String author_result = new String(mArrayList.get(position).get(TAG_AUTHOR));
                String content_result = new String(mArrayList.get(position).get(TAG_CONTENT));

                Bundle bundle = new Bundle();
                bundle.putString("num", num_result);
                bundle.putString("title",title_result);
                bundle.putString("author",author_result);
                bundle.putString("content",content_result);

                ListDetailFragment listDetailFragment = new ListDetailFragment();
                listDetailFragment.setArguments(bundle);
                android.support.v4.app.FragmentTransaction fragmenttransaction = getFragmentManager().beginTransaction();
                fragmenttransaction.replace(R.id.fragment_container, listDetailFragment);
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
            Log.d(TAG, "result  - " + result);

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
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String num = item.getString(TAG_NUM);
                String title = item.getString(TAG_TITLE);
                String author = item.getString(TAG_AUTHOR);
                String time = item.getString(TAG_TIME);
                String content = item.getString(TAG_CONTENT);

                HashMap<String,String> hashMap = new HashMap<>();

                hashMap.put(TAG_NUM, num);
                hashMap.put(TAG_TITLE, title);
                hashMap.put(TAG_AUTHOR, author);
                hashMap.put(TAG_TIME, time);
                hashMap.put(TAG_CONTENT, content);

                mArrayList.add(hashMap);
            }

            ListAdapter adapter = new SimpleAdapter(
                    getActivity(), mArrayList, R.layout.item_list,
                    new String[]{TAG_NUM,TAG_TITLE, TAG_AUTHOR, TAG_TIME, TAG_CONTENT},
                    new int[]{R.id.textView_list_num, R.id.textView_list_title, R.id.textView_list_author, R.id.textView_list_time}
            );

            mlistView.setAdapter(adapter);

        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }

}