package com.example.myapplication;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

    private Button btnRequestLocation;
    private Button btnSendLocation;
    private TextView txtvLocation;

    private GpsTracker gpsTracker;

    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    private boolean isPermission = false;

    private String locationInfo;

    public static String sIP = "";

    public UDPClient udpClient = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btnRequestLocation = (Button) findViewById(R.id.button);

        btnRequestLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (!isPermission) {
                    Log.d("@@@", "onCreate :: if (!isPermission)");
                    // 승인요청
                    callPermission();
                }

                gpsTracker = new GpsTracker(MainActivity.this);

                if (gpsTracker.getLocationStatus()) {
                    // 위치 정보를 받아올 수 있다면

                    Log.d("@@@", "onCreate :: if (gpsTracker.getLocationStatus())");

                    double latitude = gpsTracker.getLatitude();
                    double longitude = gpsTracker.getLongitude();
                    String provider = gpsTracker.getProvider();

                    locationInfo = "Your location is... \nlatitude : " + latitude
                            + "\nlongitude : " + longitude
                            + "\nprovider : " + provider;

                    txtvLocation = (TextView) findViewById(R.id.textview);
                    txtvLocation.setText(locationInfo);

                    Toast.makeText(MainActivity.this, locationInfo, Toast.LENGTH_LONG).show();
                } else {
                    // 위치 정보를 받아올 수 없으면 알람을 띄어줌
                    Log.d("@@@", "onCreate :: 위치 정보 못 받아온다");
                    gpsTracker.showSettingAlert();
                }
            }
        });

        callPermission(); // 권한 승인 여부 확인
        Log.d("@@@", "onCreate :: 권한 승인 여부 확인");

        btnSendLocation = (Button) findViewById(R.id.button_send);

        btnSendLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText ip = (EditText) findViewById(R.id.edittext_ip);
                sIP = ip.getText().toString();

                Toast.makeText(MainActivity.this, locationInfo + " \n보낸다", Toast.LENGTH_SHORT).show();
                //SendData 클래스 생성
                udpClient = new UDPClient(sIP, locationInfo);
                //보내기 시작
                udpClient.start();
                try {
                    udpClient.join();
                } catch (InterruptedException e) {
                    Log.d("@@@", e.getMessage());
                    // 서버가 없다면(와이파이 잘못 연결, ip주소 잘못입력, 서버 안킴 등,,) -> UDP Client 수신 대기 상태 -> 쓰레드 종료 못함 -> 메인 쓰레드도 종료 못함 -> 에러
                }
                Toast.makeText(MainActivity.this, udpClient.getData() + " \n잘 받았나?", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions, @NonNull int[] grandResults) {
        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {
            isPermission = true;

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    isPermission = false;
                    break;
                }
            }
        }

        Log.d("@@@", "onRequestPermissionsResult :: isPermission = " + isPermission);
    }

    void callPermission(){
        Log.d("@@@", "callPermission ::");
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 이 앱에게 권한이 없으면
            Log.d("@@@", "callPermission :: 권한 없대");
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, REQUIRED_PERMISSIONS[0])) {
                // 권한에 관해 설명하고 권한을 요청한다.
                Log.d("@@@", "callPermission :: 권한에 관해 설명하고 권한을 요청한다.");
                Toast.makeText(MainActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            } else { // 권한에 관한 설명 없이, 권한을 요청한다.
                Log.d("@@@", "callPermission :: 권한에 관한 설명 없이, 권한을 요청한다.");
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        } else {
            Log.d("@@@", "callPermission :: 권한 있대");
            isPermission = true;
        }
    }
}
