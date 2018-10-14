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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class WriteFixFragment extends Fragment {
    EditText et_name, et_title, et_content;
    String num, title, author, content;
    String sname, stitle, scontent, spassword;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_writefixfragment, container, false);
        num = getArguments().getString("num");
        title = getArguments().getString("title");
        author = getArguments().getString("author");
        content = getArguments().getString("content");

        et_name = (EditText) v.findViewById(R.id.editname);
        et_title = (EditText) v.findViewById(R.id.edittitle);
        et_content = (EditText) v.findViewById(R.id.editcontent);

        et_name.setText(author);
        et_title.setText(title);
        et_content.setText(content);

        Button bt_save = (Button) v.findViewById(R.id.savebutton);
        bt_save.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                sname = et_name.getText().toString();
                stitle = et_title.getText().toString();
                scontent = et_content.getText().toString();

                UpdateDB update = new UpdateDB();
                update.execute("http://192.168.200.199/board_update.php");
                UpdateDB admin_update = new UpdateDB();
                admin_update.execute("http://192.168.200.199/admin_update.php");
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());


                    Log.e("RESULT","성공적으로 처리되었습니다!");
                    alertBuilder
                            .setTitle("알림")
                            .setMessage("성공적으로 등록되었습니다!")
                            .setCancelable(true)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("num", num);

                                    FinalConfirm finalconfirm = new FinalConfirm();
                                    finalconfirm.setArguments(bundle);
                                    android.support.v4.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                                    fragmentTransaction.replace(R.id.fragment_container, finalconfirm);
                                    fragmentTransaction.addToBackStack(null);
                                    fragmentTransaction.commit();
                                }
                            });
                    AlertDialog dialog = alertBuilder.create();
                    dialog.show();
                
            }
        });
        return v;
    }

    public class UpdateDB extends AsyncTask<String, Integer, String> {
        String data = "";
        @Override
        protected String doInBackground(String... params) {

            /* 인풋 파라메터값 생성 */
            String param = "u_num=" + num + "&u_name=" + sname + "&u_title=" + stitle + "&u_content=" + scontent + "&u_password=" + spassword;
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

        }
    }
}