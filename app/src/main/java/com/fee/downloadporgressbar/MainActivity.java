package com.fee.downloadporgressbar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.fee.progressbar.DownLoadProgressBar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private DownLoadProgressBar mPbWc;
    private DownLoadProgressBar mPbMp;
    private Button mBtnReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mPbWc = (DownLoadProgressBar) findViewById(R.id.pb_wc);
        mPbMp = (DownLoadProgressBar) findViewById(R.id.pb_mp);
        mBtnReset = (Button) findViewById(R.id.btn_reset);

        mBtnReset.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_reset:
                mPbWc.reset();
                mPbMp.reset();
                break;
        }
    }
}
