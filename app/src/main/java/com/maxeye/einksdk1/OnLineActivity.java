package com.maxeye.einksdk1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.maxeye.einksdk.EinkClient.EinkClient;
import com.maxeye.einksdk.Bluetooth.EventUserMessage;
import com.maxeye.einksdk.EinkClient.EinkClientView;
import com.polidea.rxandroidble.RxBleDevice;

/**
 * Created by Administrator on 2018/4/9 0009.
 */

public class OnLineActivity extends Activity {
    private EinkClient einkClient;
    private EinkClient.EinkClienteCallbacks callbacks = null;
    private TextView notifyText = null;

    int pageBeanId = -1;
    private EinkClientView einkView = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.online_activity_layout);
        notifyText = findViewById(R.id.notifyTextView);
        einkView = findViewById(R.id.online_panintview);
        einkClient = ((MyApplication) getApplication()).getEinkClient();

        //notifyText.setText(einkClient.);
        notifyText.setText(einkClient.IsBluetoothConnected()?R.string.bluetooch_connected:R.string.bluetooch_disconnect);
        //需要加载的 pageBeanId
        pageBeanId = getIntent().getIntExtra("PageBeanId", -1);

        //接收消息
        callbacks = new EinkClient.EinkClienteCallbacks() {
            @Override
            public void BluetootScanResult(RxBleDevice dev) {

            }

            @Override
            public void EventBTConnectState(String state) {
                notifyText.setText(einkClient.IsBluetoothConnected()?R.string.bluetooch_connected:R.string.bluetooch_disconnect);
            }

            @Override
            public void UserMessage(EventUserMessage message) {
                // notifyText.setText(message.getMessage());
                notifyText.setText(einkClient.IsBluetoothConnected()?R.string.bluetooch_connected:R.string.bluetooch_disconnect);
            }
        };

        //保存
        findViewById(R.id.save_page).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pageBeanId != -1) {
                    einkClient.UpdateOnLineDataOnePage(pageBeanId);
                } else {
                    einkClient.SaveOnLineDataOnePage();
                }

            }
        });

        //清除
        findViewById(R.id.clean_page).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                einkClient.CleanOnLineDataOnePage();
            }
        });

        //笔记优化开
        findViewById(R.id.optimize_on).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                einkClient.SetEinkViewOptimize(true);
                notifyText.setText("笔记优化已经打开");
            }
        });

        //笔记优化关
        findViewById(R.id.optimize_off).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                einkClient.SetEinkViewOptimize(false);
                notifyText.setText("笔记优化已经关闭");
            }
        });

        //导出图片
        findViewById(R.id.save_pic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (einkClient.SavePictrueWithBackground("/sdcard/aaa.png", einkView))
//                    Toast.makeText(getApplicationContext(), "保存成功： /sdcard/aaa.png", Toast.LENGTH_LONG).show();

                final EditText inputServer = new EditText(OnLineActivity.this);
                AlertDialog.Builder builder = new AlertDialog.Builder(OnLineActivity.this);
                builder.setTitle(R.string.type_file_name).setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
                        .setNegativeButton(android.R.string.cancel, null);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        String str = inputServer.getText().toString();
                        Log.d("wlDebug", "str = " + str);
                        if (einkClient.SavePictrueWithBackground("/sdcard/" + str + ".png", einkView))
                            Toast.makeText(getApplicationContext(), "/sdcard/" + str + ".png", Toast.LENGTH_LONG).show();
                    }
                });
                builder.show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        //完成客户端初始化
        einkClient.Init(callbacks, einkView, EinkClient.MODE_ONLINE);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            if (pageBeanId != -1)
                einkClient.DrawPointToEinkView(einkClient.GetPagePoinDate(pageBeanId));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
