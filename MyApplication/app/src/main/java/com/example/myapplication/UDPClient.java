package com.example.myapplication;

import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class UDPClient extends Thread{

    //사용할 통신 포트
    public static final int sPORT = 7777;
    String sIP;
    String locationInfo;
    String msg = "fail";

    public UDPClient(String sIP, String locationInfo) {
        this.sIP = sIP;
        this.locationInfo = locationInfo;
    }

    public void run(){
        try{
            //UDP 통신용 소켓 생성
            DatagramSocket socket = new DatagramSocket();

            Log.d("@@@", "소켓생성");
            //서버 주소 변수
            InetAddress serverAddr = InetAddress.getByName(sIP);

            //보낼 데이터 생성
            byte[] buf = (locationInfo).getBytes();

            Log.d("@@@", "왜안되지"+ locationInfo);
            //패킷으로 변경
            DatagramPacket packet = new DatagramPacket(buf, buf.length, serverAddr, sPORT);

            Log.d("@@@", "패킷어디야");
            //패킷 전송!
            socket.send(packet);

            Log.d("@@@", "send");

            try {
                //데이터 수신 대기



                socket.receive(packet);
                //데이터 수신되었다면 문자열로 변환
                msg = new String(packet.getData());
                Log.d("@@@", "receive" + msg);
            } catch (Exception e) {
                Log.d("@@@", e.getMessage());
            }

        }catch (Exception e){
            Log.d("@@@", "에러?");
            Log.d("@@@", e.getMessage());

        }
    }

    public String getData() {
        return msg;
    }
}