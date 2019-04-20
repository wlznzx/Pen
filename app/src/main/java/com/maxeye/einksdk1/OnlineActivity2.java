package com.maxeye.einksdk1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.maxeye.einksdk.Bluetooth.EventUserMessage;
import com.maxeye.einksdk.EinkClient.EinkClient;
import com.maxeye.einksdk.EinkClient.EinkClientView;
import com.polidea.rxandroidble.RxBleDevice;

/**
 * Created by Administrator on 2018/4/9 0009.
 */

public class OnlineActivity2 extends Activity {
    private EinkClient einkClient;
    private EinkClient.EinkClienteCallbacks callbacks = null;
    private TextView notifyText = null;

    int pageBeanId = -1;
    private SeekBar seekBar;
    private EinkClientView einkPaintViewLayout = null;

    private Button penBtn,pencilBtn,writingBrushBtn,erasserOnlyBtn;

    private Button color1Btn,color2Btn,color3Btn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.online_hover_activity_layout);
        seekBar = findViewById(R.id.seekBar);
        notifyText = findViewById(R.id.notifyTextView);
        einkPaintViewLayout = findViewById(R.id.hover_EinkPaintViewLayout);
        einkClient = ((MyApplication) getApplication()).getEinkClient();


        //需要加载的 pageBeanId
        pageBeanId = getIntent().getIntExtra("PageBeanId", -1);

        //接收消息
        callbacks = new EinkClient.EinkClienteCallbacks() {
            @Override
            public void BluetootScanResult(RxBleDevice dev) {

            }

            @Override
            public void EventBTConnectState(String state) {

            }

            @Override
            public void UserMessage(EventUserMessage message) {
                notifyText.setText(message.getMessage());
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

        penBtn = findViewById(R.id.pen1);
        pencilBtn = findViewById(R.id.pen2);
        writingBrushBtn = findViewById(R.id.pen3);
        erasserOnlyBtn = findViewById(R.id.erase);
        penBtn.setOnClickListener(penListener);
        pencilBtn.setOnClickListener(penListener);
        writingBrushBtn.setOnClickListener(penListener);
        erasserOnlyBtn.setOnClickListener(penListener);
        penBtn.setSelected(true);
//
//        findViewById(R.id.pen1).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                einkClient.SetPenType(EinkClient.PRESSURE_PEN);
//            }
//        });
//        findViewById(R.id.pen2).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                einkClient.SetPenType(EinkClient.PRESSURE_PENCIL);
//            }
//        });
//
//
//        findViewById(R.id.pen3).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                einkClient.SetPenType(EinkClient.PRESSURE_PEN_INK);
//            }
//        });

        findViewById(R.id.pen4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                einkClient.SetPenType(EinkClient.MARK);
            }
        });

        findViewById(R.id.undo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                einkClient.Undo();
            }
        });

        findViewById(R.id.redo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                einkClient.Redo();
            }
        });

        findViewById(R.id.color0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                einkClient.SetPenColor(getResources().getColor(android.R.color.black));
            }
        });

        findViewById(R.id.color1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                einkClient.SetPenColor(0x3F51B5);
            }
        });

        findViewById(R.id.color2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                einkClient.SetPenColor(0xFF4081);
            }
        });

        findViewById(R.id.color3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                einkClient.SetPenColor(0xFFFF81);
            }
        });

        findViewById(R.id.save_online2_pic).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final EditText inputServer = new EditText(OnlineActivity2.this);
                AlertDialog.Builder builder = new AlertDialog.Builder(OnlineActivity2.this);
                builder.setTitle(R.string.type_file_name).setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
                        .setNegativeButton(android.R.string.cancel, null);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        String str = inputServer.getText().toString();
                        Log.d("wlDebug", "str = " + str);
                        if (einkClient.SavePictrueWithBackground("/sdcard/" + str + ".png", einkPaintViewLayout))
                            Toast.makeText(getApplicationContext(), "/sdcard/" + str + ".png", Toast.LENGTH_LONG).show();
                    }
                });
                builder.show();
            }
        });


        seekBar.setMax(20);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                einkClient.SetPenSize(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    View.OnClickListener penListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            penBtn.setSelected(false);
            pencilBtn.setSelected(false);
            writingBrushBtn.setSelected(false);
            erasserOnlyBtn.setSelected(false);
            switch (view.getId()){
                case R.id.erase:
                    einkClient.SetPenType(EinkClient.ERASER);
                    erasserOnlyBtn.setSelected(true);
                    break;
                case R.id.pen1:
                    einkClient.SetPenType(EinkClient.PRESSURE_PEN);
                    penBtn.setSelected(true);
                    break;
                case R.id.pen2:
                    einkClient.SetPenType(EinkClient.PRESSURE_PENCIL);
                    pencilBtn.setSelected(true);
                    break;
                case R.id.pen3:
                    einkClient.SetPenType(EinkClient.PRESSURE_PEN_INK);
                    writingBrushBtn.setSelected(true);
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        //完成客户端初始化
        einkClient.Init(callbacks, einkPaintViewLayout, EinkClient.MODE_ONLINE);
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
