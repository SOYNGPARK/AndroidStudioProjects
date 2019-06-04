package com.example.androidclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.PrintWriter;

public class MainActivity extends AppCompatActivity {
    String msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText input = (EditText) findViewById(R.id.input);
        Button button = (Button) findViewById(R.id.button);

        msg = input.getText().toString();

        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                TCPClient tcpClient = new TCPClient(msg);
                tcpClient.start();


            }
        });
    }
}
