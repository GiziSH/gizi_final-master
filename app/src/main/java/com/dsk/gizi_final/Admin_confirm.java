package com.dsk.gizi_final;
import android.app.AlertDialog;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Admin_confirm extends Fragment {
    private static final String TAG_JSON = "webnautes";
    private static final String TAG_NUM = "num";
    private static final String TAG_PASSWORD = "password";
    private Button btn_commit;
    private TextView titleView, authorView, contentView;
    private String num, title, author, content;
    String mJsonString;
    String insert_pw;
    String password;
    String snum, sname, stitle, scontent, spassword;

    private static String TAG = "phptest_MainActivity";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.admin_confirm, container, false);
        snum = getArguments().getString("num");
        stitle = getArguments().getString("title");
        sname = getArguments().getString("author");
        scontent = getArguments().getString("content");

        Log.d(TAG, "response  - " + "title : " + stitle + ", " + "author : " + sname + ", " + "content : " + scontent);

        titleView = (TextView) v.findViewById(R.id.titleview);
        authorView = (TextView) v.findViewById(R.id.authorview);
        contentView = (TextView) v.findViewById(R.id.contentview);

        titleView.setText(stitle);
        authorView.setText(sname);
        contentView.setText(scontent);

        btn_commit = (Button) v.findViewById(R.id.commitbutton);
        btn_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDB save = new saveDB();
                save.execute("http://192.168.200.199/after_insert.php");

            }
        });
        return v;
    }

    public class saveDB extends AsyncTask<String, Integer, String> {
        String data = "";

        @Override
        protected String doInBackground(String... params) {

            /* 인풋 파라메터값 생성 */
            String param = "u_name=" + sname + "&u_title=" + stitle + "&u_content=" + scontent;
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
                Log.e("RECV DATA", data);

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
            Log.e("RECV DATA", data);
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());

            if(data.equals("1"))
            {
                Log.e("입력됐고",data);
                DeleteDB delete = new DeleteDB();
                delete.execute("http://192.168.200.199/admin_delete.php");
            }
        }
    }

    public class DeleteDB extends AsyncTask<String, Integer, String> {
        String data = "";
        @Override
        protected String doInBackground(String... params) {
            /* 인풋 파라메터값 생성 */
            String param = "u_num=" + snum ;
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
                Log.e("삭제됐다", data);
                alertBuilder
                        .setTitle("알림")
                        .setMessage("완료되었습니다!")
                        .setCancelable(true)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Fragment7 fragment7 = new Fragment7();
                                android.support.v4.app.FragmentTransaction fragmenttransaction = getFragmentManager().beginTransaction();
                                fragmenttransaction.replace(R.id.fragment_container, fragment7);
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
