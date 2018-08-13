package com.dsk.gizi_final;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savednstanceState) {
        super.onCreate(savednstanceState);

        try{
            Thread.sleep(1000); //대기 초 설정
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}