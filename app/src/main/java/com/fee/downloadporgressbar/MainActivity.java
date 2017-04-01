package com.fee.downloadporgressbar;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.fee.progressbar.DownLoadProgressBar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private DownLoadProgressBar mPbMp;
    private Button mBtnReset;
    private Button mBtnStart;
    private Button mBtnPause;
    private int mCurrentProgress;
    private boolean isDownloading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mPbMp = (DownLoadProgressBar) findViewById(R.id.pb_mp);
        mBtnReset = (Button) findViewById(R.id.btn_reset);

        mBtnReset.setOnClickListener(this);
        mBtnStart = (Button) findViewById(R.id.btn_start);
        mBtnStart.setOnClickListener(this);
        mBtnPause = (Button) findViewById(R.id.btn_pause);
        mBtnPause.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_reset:
                mPbMp.reset();
                isDownloading = false;
                mCurrentProgress = 0;
                break;
            case R.id.btn_start:
                start();
                break;
            case R.id.btn_pause:
                isDownloading = false;
                break;
        }
    }

    private Handler mHandler  = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 > 100) {
                isDownloading = false;
                return;
            }
            mPbMp.setProgress(msg.arg1);
        }
    };

    private void start() {
        isDownloading = true;
        new Thread(){
            @Override
            public void run() {
                super.run();
                while (isDownloading) {
                    Message message =mHandler.obtainMessage();
                    message.arg1 = mCurrentProgress++;
                    mHandler.sendMessage(message);
                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

}
