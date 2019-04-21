package com.maxeye.einksdk1;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.maxeye.einksdk.Bluetooth.EventUserMessage;
import com.maxeye.einksdk.EinkClient.EinkClient;
import com.maxeye.einksdk.EinkClient.EinkClientView;
import com.polidea.rxandroidble.RxBleDevice;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/4/8 0008.
 */

public class BtActivity extends Activity {
    private static final String TAG = "wlDebug";
    private LayoutInflater mInflater;
    public EinkClient einkClient;
    public TextView notifyText;
    public ListView btList;

    private List<RxBleDevice> DevList = new ArrayList<RxBleDevice>();
    private RxBleDevice currDev = null;

    private BaseAdapter myAdapter = null;

    private EinkClient.EinkClienteCallbacks callbacks = null;
    private EinkClientView einkView = null;
    private BluetoothAdapter btAdapter;

    private int MSG_SCAN_BLUE = 1;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    einkClient.StartScanBluetooth();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bt_activity_layout);
        notifyText = findViewById(R.id.UserNotifyTextView);
        btList = findViewById(R.id.BluetoothListView);
        TextView tipsTv = findViewById(R.id.tips);
        Typeface typeFace = Typeface.createFromAsset(getAssets(), "DroidSansFallback.ttf");
        tipsTv.setTypeface(typeFace);
        notifyText.setTypeface(typeFace);
        MyApplication application = (MyApplication) getApplication();
        // application.setEinkClient(new EinkClient(this, "/sdcard"));
        einkClient = application.getEinkClient();

        // 环境条件检查 蓝牙是否打开
        btAdapter = getDefaultAdapter(this);
        if (btAdapter == null) {
            Log.i(TAG, "当前设备不支持蓝牙！");
        } else if (!btAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBtIntent);
            return;
        }

        //环境条件检查 权限申请
        /*
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    ) {
                ActivityCompat.requestPermissions(this
                        , new String[]{Manifest.permission.BLUETOOTH
                                , Manifest.permission.BLUETOOTH_ADMIN
                                , Manifest.permission.WRITE_EXTERNAL_STORAGE
                                , Manifest.permission.ACCESS_COARSE_LOCATION}, 222);
                return;
            }
        }
        */

        callbacks = new EinkClient.EinkClienteCallbacks() {
            @Override
            public void BluetootScanResult(RxBleDevice device) {
                // TODO: 2018/4/21 0021  处理 蓝牙搜索 的结果
                Log.i(TAG, "BluetootScanResult: " + device.toString());
                if (DevList.indexOf(device) == -1) {
                    // for (int i = 0; i < 10; i++) {
                    DevList.add(device);
                    // }
                    myAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void EventBTConnectState(String message) {
                // TODO: 2018/4/21 0021 处理 蓝牙连接 状态
                Log.i(TAG, "EventBTConnectState: " + message);
                // notifyText.setText(notifyText.getText() + "\r\n" + message);
                if (message.equals("RxBleConnectionState{CONNECTED}")) {
                    Log.d("wlDebug", "CONNECTED");
                    // go实时模式;
                    // startActivity(new Intent(getApplicationContext(), OnlineActivity2.class));
                }
                notifyText.setText(einkClient.IsBluetoothConnected()?R.string.bluetooch_connected:R.string.bluetooch_disconnect);
            }

            @Override
            public void UserMessage(EventUserMessage message) {
                // TODO: 2018/4/21 0021 处理 SDK 发布的其他信息，详情请参考附录 消息列表 
                // notifyText.setText(notifyText.getText() + "\r\n" + message.getMessage());
                // notifyText.setText(message.getMessage());
                notifyText.setText(einkClient.IsBluetoothConnected()?R.string.bluetooch_connected:R.string.bluetooch_disconnect);
            }
        };


        //清除log
        findViewById(R.id.clearLog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (einkClient.IsBluetoothConnected()) {
                    notifyText.setText("User Notify:\r\n" + "蓝牙已连接！");
                } else {
                    notifyText.setText("User Notify:\r\n" + "蓝牙未连接！");
                }
            }
        });

        //开始搜索
        findViewById(R.id.startScan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                einkClient.StartScanBluetooth();
            }
        });


        //开始连接
        btList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currDev = DevList.get(position);
                einkClient.StartConnectDevice(currDev, false);
                //einkClient.StartConnectDevice("D9:AD:F0:53:0F:9D");
            }
        });

        //断开连接
        findViewById(R.id.DisConnect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                einkClient.StopConnectDevice();
                DevList.clear();
                myAdapter.notifyDataSetChanged();
            }
        });


        //离线模式
        findViewById(R.id.OffLieneMod).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //einkClient.SetWorkMod(einkClient.MODE_OFFLINE);
                startActivity(new Intent(getApplicationContext(), OffLineActivity.class));
            }
        });

        //在线模式
        findViewById(R.id.OnLineMod2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), OnlineActivity2.class));
            }
        });

        //在线模式2
        findViewById(R.id.OnLineMod).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), OnLineActivity.class));
            }
        });


        //单页数据
        findViewById(R.id.PageShow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PageShowActivity.class));
            }
        });

        //蓝牙改名
        findViewById(R.id.reNameBT).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currDev != null)
                    einkClient.ReNameBlutthootDevice(currDev.getBluetoothDevice().getName() + "1");
            }
        });

        //升级固件
        findViewById(R.id.update_hw).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                einkClient.StartHandWritingUpdate("/sdcard/1.bin##1.bin", false);
            }
        });

        //取固件版本
        findViewById(R.id.hw_version).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                einkClient.GetHandWritingVersion();
            }
        });

        //电量
        findViewById(R.id.getPower).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //handler.postDelayed(runnable, 3000);//每两秒执行一次runnable.
                einkClient.GetHandWritingPower();
            }
        });

        //SDK 版本
        findViewById(R.id.SdkVersion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyText.setText(einkClient.GetSDKVersion());
            }
        });


        //蓝牙列表
        myAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return DevList.size();
            }

            @Override
            public Object getItem(int position) {
                return DevList.indexOf(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                mInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                TextView textView = (TextView) mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
                textView.setTextColor(Color.BLACK);
                textView.setText(DevList.get(position).getName());
                return textView;
            }
        };
        btList.setAdapter(myAdapter);

    }

    public static BluetoothAdapter getDefaultAdapter(Context context) {
        BluetoothAdapter adapter = null;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            adapter = BluetoothAdapter.getDefaultAdapter();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            adapter = bluetoothManager.getAdapter();
        }
        return adapter;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 222) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED
                    && grantResults[3] == PackageManager.PERMISSION_GRANTED) {
                recreate();
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        einkClient.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //完成客户端初始化
        einkClient.Init(callbacks, null, EinkClient.MODE_OFFLINE);
        android.util.Log.d("wlDebug", "onResume StartScanBluetooth.");
        handler.sendEmptyMessage(MSG_SCAN_BLUE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        einkClient.StopScanElueTooth();
    }
}
