package com.example.mac.myinternet;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ImageView img;
    private Bitmap bmp;
    private UI handler;
    private File sdroot;
    private File Pdf;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    123);
        }else{
            init();
        }


    }
    private void init(){
        img = (ImageView) findViewById(R.id.img);
        handler=new UI();

        sdroot = Environment.getExternalStorageDirectory();
        progressBar=(ProgressBar)findViewById(R.id.ProgressBar);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 123){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                init();
            }else{
                finish();
            }
        }
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

    public void  test3(View view){
        progressBar.setVisibility(View.VISIBLE);
        new Thread(){
            @Override
            public void run() {
                try {
                     Pdf = new File(sdroot,"url.pdf");
                    BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(Pdf));

                    URL url = new URL("http://pdfmyurl.com/?url=www.yahoo.com.tw");
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.connect();
                    BufferedInputStream bin = new BufferedInputStream(conn.getInputStream());
                    byte[] buf = new byte[4096]; int len = 0;
                    while((len=bin.read(buf))!=-1){
                        bout.write(buf,0,len);
                        bin.close();
                        bout.flush();
                        bout.close();
                        handler.sendEmptyMessage(1);
                    }
                }catch (Exception e){
                    Log.i("test",e.toString());
                    handler.sendEmptyMessage(2);
                }

            }
        }.start();
    }

    public void test4(View view){
        new Thread(){
            @Override
            public void run() {
                try {
                    MultipartUtility multipartUtility = new MultipartUtility("http://10.21.200.71:8080/J2EE/J2EE11","UTF-8","");
                    multipartUtility.addFilePart("upload",Pdf);
                    List<String> ret = multipartUtility.finish();
                    for(String line: ret){
                        Log.i("test", line);
                    }

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
            switch (msg.what) {
                case 0:
                    img.setImageBitmap(bmp);
                    break;
                case 1:
                    Toast.makeText(MainActivity.this, "Save OK", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    //showPDF();
                    break;
                case 2:
                    Toast.makeText(MainActivity.this, "Save Fail", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    break;

            }
        }
    }

    private void showPDF(){
        Intent it = new Intent(Intent.ACTION_VIEW);
        it.setDataAndType(Uri.fromFile(Pdf), "application/pdf");
        it.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        Intent intent2 = Intent.createChooser(it, "Open File");
        try {
            startActivity(intent2);
        } catch (ActivityNotFoundException e) {
            Log.i("brad", e.toString());
            // Instruct the user to install a PDF reader here, or something
        }
        //startActivity(it);
    }


}
