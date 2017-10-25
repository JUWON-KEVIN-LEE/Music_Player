package com.immymemine.kevin.musicplayer.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.immymemine.kevin.musicplayer.R;

/**
 * Created by quf93 on 2017-10-23.
 */

public abstract class PermissionUtil extends AppCompatActivity {
    private static final int REQ_CODE = 999;
    private static final String permissions[] = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    public abstract void init();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 앱 버전 체크 - 호환성 처리
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            checkPermission();
        } else {
            init();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermission(){
        // 1. 권한이 있는지 여부 확인
        if( checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED ) {
            init();
        } else {
            // 2.1 요청할 권한을 정의
            String permissions[] = {
                    Manifest.permission.READ_EXTERNAL_STORAGE
            };
            // 2.2 권한 요청
            requestPermissions( permissions , REQ_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 3. 권한 승인 여부 체크
        switch (requestCode){
            case REQ_CODE:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // 진행 허용 처리
                    init();
                }
                break;
        }
    }
}
