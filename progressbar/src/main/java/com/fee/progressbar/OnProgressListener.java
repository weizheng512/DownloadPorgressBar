package com.fee.progressbar;

/**
 * =========================
 * <br/>Created by weizheng on  2017/3/24
 * <br/>email: weiz@mobilereality.org
 * <br/>Version：1.0
 * <br/>description:
 * <br/>
 * =========================
 */

public interface OnProgressListener {

    void onStart();

    void onPause();

    void onComplete();

    void onProgressChange(int max, int current);

    void onInstallComplete(String installResult);
}
