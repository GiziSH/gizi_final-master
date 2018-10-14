package com.dsk.gizi_final;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
    String insert_pw;
    String num, title, author, content;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_writeconfirmfragment, container, false);

        titleView = (TextView) v.findViewById(R.id.titleview);
        authorView = (TextView) v.findViewById(R.id.authorview);
        contentView = (TextView) v.findViewById(R.id.contentview);

        GetData task = new GetData();
        task.execute("http://192.168.200.199/recent_getjson.php");

        btn_fix = (Button) v.findViewById(R.id.fixbutton);
        btn_delete = (Button) v.findViewById(R.id.deletebutton);
        btn_commit = (Button) v.findViewById(R.id.commitbutton);
        btn_fix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_fix.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder ad = new AlertDialog.Builder(getContext());
                        ad.setTitle("비밀번호 확인");
                        ad.setMessage("비밀번호를 입력해주세요.");
                        final EditText et = new EditText(getContext());
                        ad.setView(et);

                        ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                try {
                                    insert_pw = et.getText().toString();
                                } catch(NullPointerException e) {
                                    Log.e("err",e.getMessage());
                                }
                                UpdatePassword updatepw = new UpdatePassword();
                                updatepw.execute();
                            }
                        });
                        ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).show();
                    }
                });


            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder ad = new AlertDialog.Builder(getContext());
                ad.setTitle("비밀번호 확인");
                ad.setMessage("비밀번호를 입력해주세요.");
                final EditText et = new EditText(getContext());
                ad.setView(et);

                ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            insert_pw = et.getText().toString();
                        } catch(NullPointerException e) {
                            Log.e("err",e.getMessage());
                        }
                        DeletePassword deletepw = new DeletePassword();
                        deletepw.execute();
                    }
                });
                ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
            }
        });

        btn_commit.setOnClickListener(new View.OnClickListener(){
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

    private class UpdatePassword extends AsyncTask<String, Void, String>{
        String data = "";
        ProgressDialog progressDialog;

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
            Log.e("뭘까",data);

            if(data.equals("1")) {

                title = titleView.getText().toString();
                author = authorView.getText().toString();
                content = contentView.getText().toString();

                Bundle bundle = new Bundle();
                bundle.putString("num",num);
                bundle.putString("title",title);
                bundle.putString("author",author);
                bundle.putString("content",content);

                WriteFixFragment writeFixFragment = new WriteFixFragment();
                writeFixFragment.setArguments(bundle);
                android.support.v4.app.FragmentTransaction fragmenttransaction = getFragmentManager().beginTransaction();
                fragmenttransaction.replace(R.id.fragment_container, writeFixFragment);
                fragmenttransaction.addToBackStack(null);
                fragmenttransaction.commit();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String param = "u_num=" + num + "&insert_pw="+ insert_pw +"";
            //String serverURL = params[0];
            try {
                /* 서버연결 */
                URL url = new URL(
                        "http://192.168.200.199/get_password.php");
                //URL url = new URL(serverURL);
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
                Log.e("RECV DATA", data);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;

        }
    }

    private class DeletePassword extends AsyncTask<String, Void, String>{
        String data = "";
        ProgressDialog progressDialog;

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
            Log.e("뭘까",data);

            if(data.equals("1")) {
                DeleteDB deleteDB = new DeleteDB();
                deleteDB.execute("http://192.168.200.199/board_delete.php");
                DeleteDB admin_deleteDB = new DeleteDB();
                admin_deleteDB.execute("http://192.168.200.199/admin_delete.php");
                Fragment4 fragment4 = new Fragment4();
            }
        }
        @Override
        protected String doInBackground(String... params) {

            String param = "u_num=" + num + "&insert_pw="+ insert_pw +"";
            //String serverURL = params[0];
            try {
                /* 서버연결 */
                URL url = new URL(
                        "http://192.168.200.199/get_password.php");
                //URL url = new URL(serverURL);
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
                Log.e("RECV DATA", data);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;

        }
    }

    public class DeleteDB extends AsyncTask<String, Integer, String> {
        String data = "";
        @Override
        protected String doInBackground(String... params) {
            /* 인풋 파라메터값 생성 */
            String param = "u_num=" + num ;
            Log.e("POST", param);
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
                Log.e("받았니", data);

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
            Log.e("서버연결", data);
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());

            if(data.equals("1"))
            {
                Log.e("RESULT","성공적으로 처리되었습니다!");
                alertBuilder
                        .setTitle("알림")
                        .setMessage("삭제되었습니다!")
                        .setCancelable(true)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Fragment4 fragment4 = new Fragment4();
                                android.support.v4.app.FragmentTransaction fragmenttransaction = getFragmentManager().beginTransaction();
                                fragmenttransaction.replace(R.id.fragment_container, fragment4);
                                fragmenttransaction.addToBackStack(null);
                                fragmenttransaction.commit();
                            }
                        });
                AlertDialog dialog = alertBuilder.create();
                dialog.show();
            }
        }
    }
}