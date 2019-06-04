package com.example.myudp;

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

public class MainActivity extends AppCompatActivity {

    //서버주서
    public static String sIP = "";
    //사용할 통신 포트
    public static final int sPORT = 7777;

    //데이터 보낼 클랙스
    public SendData mSendData = null;

    //화면 표시용 TextView
    public TextView txtView = null;
    public String str_note = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //btnHello 버튼을 layout의 버튼과 연결
        Button btnHello = (Button) findViewById(R.id.Hello);

        Button accept = (Button) findViewById(R.id.accept);

        //txtView를 layout의 TextView와 연결
        txtView = (TextView) findViewById(R.id.textView);


        //버튼이 눌렸다면
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText ip = (EditText) findViewById(R.id.ip);
                sIP = ip.getText().toString();
            }
        });

        //버튼이 눌렸다면
        btnHello.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText note = (EditText) findViewById(R.id.note);

                if (note.getText().toString().length() == 0) {
                    str_note = "empty";
                } else {
                    str_note = note.getText().toString();
                    Toast.makeText(MainActivity.this, str_note + "보낸다", Toast.LENGTH_SHORT).show();
                }
                //SendData 클래스 생성
                mSendData = new SendData();
                //보내기 시작
                mSendData.start();
            }
        });

    }

    class SendData extends Thread{
        public void run(){
            try{
                //UDP 통신용 소켓 생성
                DatagramSocket socket = new DatagramSocket();
                //서버 주소 변수
                InetAddress serverAddr = InetAddress.getByName(sIP);

                //보낼 데이터 생성
                byte[] buf = (str_note).getBytes();

                //패킷으로 변경
                DatagramPacket packet = new DatagramPacket(buf, buf.length, serverAddr, sPORT);

                //패킷 전송!
                socket.send(packet);

                Log.d("@@@", "send");

                //데이터 수신 대기
                socket.receive(packet);
                //데이터 수신되었다면 문자열로 변환
                String msg = new String(packet.getData());

                //txtView에 표시
                txtView.setText(msg);
            }catch (Exception e){

            }
        }
    }
}
