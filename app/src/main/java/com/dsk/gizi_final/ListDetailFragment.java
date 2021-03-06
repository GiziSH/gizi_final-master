package com.dsk.gizi_final;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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

public class ListDetailFragment extends Fragment {
    private static final String TAG_JSON = "webnautes";
    private static final String TAG_NUM = "num";
    private static final String TAG_PASSWORD = "password";
    private Button btn_fix, btn_delete, btn_commit;
    private TextView titleView, authorView, contentView;
    private String num, title, author, content;
    String mJsonString;
    String insert_pw;
    String password;

    private static String TAG = "phptest_MainActivity";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_listdetailfragment, container, false);
        num = getArguments().getString("num");
        title = getArguments().getString("title");
        author = getArguments().getString("author");
        content = getArguments().getString("content");

        Log.d(TAG, "response  - " + "title : " + title + ", " + "author : " + author + ", " + "content : " + content);

        titleView = (TextView) v.findViewById(R.id.titleview);
        authorView = (TextView) v.findViewById(R.id.authorview);
        contentView = (TextView) v.findViewById(R.id.contentview);

        titleView.setText(title);
        authorView.setText(author);
        contentView.setText(content);

        btn_fix = (Button) v.findViewById(R.id.fixbutton);
        btn_delete = (Button) v.findViewById(R.id.deletebutton);
        btn_commit = (Button) v.findViewById(R.id.commitbutton);
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

        btn_commit.setOnClickListener(new View.OnClickListener() {
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
            /*AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());

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

                            }
                        });
                AlertDialog dialog = alertBuilder.create();
                dialog.show();
            }*/
        }
    }
}
