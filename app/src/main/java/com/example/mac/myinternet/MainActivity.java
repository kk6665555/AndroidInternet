package com.example.mac.myinternet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
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
    private Bitmap bmp;
    private UI handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img = (ImageView) findViewById(R.id.img);
        handler=new UI();
    }

    public void test1(View view){
        //網際網路 必須寫在執行緒
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
                    URL url = new URL("http://www.penghu-nsa.gov.tw/FileDownload/Album/Big/20161012162551758864338.jpg");
                    HttpURLConnection conn =(HttpURLConnection) url.openConnection();
                    conn.connect();
                    bmp = BitmapFactory.decodeStream(conn.getInputStream());
                    handler.sendEmptyMessage(0);
                }catch (Exception e){
                    Log.i("test",e.toString());
                }
            }
        }.start();
    }

    private class UI extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            img.setImageBitmap(bmp);
        }
    }
}
