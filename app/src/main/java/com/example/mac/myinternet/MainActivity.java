package com.example.mac.myinternet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void test1(View view){
        new Thread(){
            @Override
            public void run() {
                try {
                    URL url = new URL("http://www.iii.org.tw/");
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.connect();

                    InputStream in = conn.getInputStream();

                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(in));
                    String line ;
                    while((line=br.readLine())!= null){
                        Log.i("test",line);
                    }

                    br.close();
                } catch (Exception e) {
                    Log.i("test",e.toString());
                }

            }
        }.start();


    }

    public void test2(View view){


        new Thread(){
            @Override
            public void run() {
                try {
                    URL url = new URL("");
                    HttpURLConnection conn =(HttpURLConnection) url.openConnection();
                    conn.connect();

                }catch (Exception e){

                }
            }
        }.start();
    }
}
