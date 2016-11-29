package com.example.jake.caastme;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by jake on 2016/10/3.
 */
public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {


    private ZXingScannerView mScannerView;

    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.test);

         url = Constants.getProperty("url",this.getApplicationContext());
         QrScanner();

    }

    public void QrScanner(){
        mScannerView = new ZXingScannerView(this); // Programmatically initialize the scanner view
        mScannerView.startCamera(); // Start camera
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        setContentView(mScannerView);

        //确保startCamera这个线程完成
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera(); // Stop camera on pause
    }


    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here

        String redirect_url = handleShare();
        try {
            redirect_url = URLEncoder.encode(redirect_url,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        String code =  rawResult.getText();
        Log.i("caastinfo1", code); // Prints scan results
        final String url_address = url+"redirect?code="+code+"&redirect_url="+redirect_url;
        if(url!=null && !url.isEmpty()){

                // 开启新的线程
                new Thread(){
                    public void run() {
                        try {
                        // 耗时操作
                            Log.i("xxxxd",url_address);
                            String result = NetUtil.getCall(url_address);
                            Log.i("caastinfo_result", result==null?"null":result); // Prints scan results
                            Message msg = Message.obtain();
                            msg.obj = result;
                            mHandler.sendMessage(msg);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    };
                }.start();

        }

// show the scanner result into dialog box.
       /* AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Scan Result");
        builder.setMessage(rawResult.getText());
        AlertDialog alert1 = builder.create();
        alert1.show();*/

// If you would like to resume scanning, call this method below:
// mScannerView.resumeCameraPreview(this);
    }



    public String handleShare(){
        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        String url = null;
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                if (sharedText != null) {
                    Log.i("caastinfo","text:"+sharedText);
                    // Update UI to reflect text being shared
                    //extract url
                    url = sharedText.substring(sharedText.indexOf("http"));
                }

            }
        }

        return url;
    }



    //工作线程
    //http://blog.csdn.net/withiter/article/details/19908679
    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            // 更新UI
            super.handleMessage(msg);
            Log.i("caastinfo","请求结果11:" + msg.obj.toString());
            JSONObject jsonObject = null;
            try {
                 jsonObject = new JSONObject(msg.obj.toString());
                 Toast.makeText(ScannerActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            


        }
    }

    MyHandler mHandler = new MyHandler() ;





    public void goToListActivity(){

    }




}