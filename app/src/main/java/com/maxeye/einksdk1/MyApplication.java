package com.maxeye.einksdk1;

import android.app.Application;

import com.maxeye.einksdk.EinkClient.EinkClient;

/**
 * Created by Administrator on 2018/4/2 0002.
 */

public class MyApplication extends Application {

    public EinkClient einkClient = null;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public EinkClient getEinkClient() {
        if (einkClient == null) {
            einkClient = new EinkClient(this, "/sdcard");
        }
        return einkClient;
    }

}
