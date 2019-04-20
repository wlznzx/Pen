package com.maxeye.einksdk1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.maxeye.einksdk.Bluetooth.EventUserMessage;
import com.maxeye.einksdk.DBdata.PageBean;
import com.maxeye.einksdk.DBdata.PointBean;
import com.maxeye.einksdk.EinkClient.EinkClient;
import com.maxeye.einksdk.EinkClient.EinkClientView;
import com.polidea.rxandroidble.RxBleDevice;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/4/9 0009.
 */

public class OffLineActivity extends Activity {
    private EinkClient einkClient;
    private ListView pageView;
    private TextView pageInfoText;
    private BaseAdapter pageAdapter = null;
    private PageBean selPageBean = null;

    private LayoutInflater mInflater;

    private List<PageBean> pageInfoList = new ArrayList<>();
    private EinkClient.EinkClienteCallbacks callbacks = null;
    private EinkClientView einkView = null;

    private Button pageAddBt;
    private Button pagDelBt;
    private Button pagEditBt;
    private Button apgeSqlitBt;
    private Button pageReNameBt;
    private Button pageCatBt;
    private Button pageCateStrBt;


    private EditText reNameET;

    //for test
    long time = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.his_activity_layout);

        pageView = findViewById(R.id.listPage);
        pageInfoText = findViewById(R.id.pageInfoText);
        einkView = findViewById(R.id.his_panintview);
        pageAddBt = findViewById(R.id.page_add);
        pagDelBt = findViewById(R.id.page_del);
        pagEditBt = findViewById(R.id.page_edit);
        apgeSqlitBt = findViewById(R.id.page_sqlit);
        pageReNameBt = findViewById(R.id.page_rename);
        pageCatBt = findViewById(R.id.page_categroy);
        pageCateStrBt = findViewById(R.id.page_categroy_string);

        reNameET = findViewById(R.id.editNameText);

        einkClient = ((MyApplication) getApplication()).getEinkClient();

        callbacks = new EinkClient.EinkClienteCallbacks() {
            @Override
            public void BluetootScanResult(RxBleDevice dev) {

            }

            @Override
            public void EventBTConnectState(String state) {

            }

            @Override
            public void UserMessage(EventUserMessage message) {

                if (message.getId() == EventUserMessage.OFFLINEDATA_SYN_SUCCESS
                        || message.getId() == EventUserMessage.DBDATA_DELPAGE
                        || message.getId() == EventUserMessage.DBDATA_PAGEINFOUPDATE
                        || message.getId() == EventUserMessage.DBDATA_SQLITPAGE
                        || message.getId() == EventUserMessage.OFFLINEDATA_GETED_DATA
                        ) {
                    pageInfoList = einkClient.GetAllPageInfo();
                    pageAdapter.notifyDataSetChanged();
                }
                pageInfoText.setText(message.getMessage());

            }
        };

        pageAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return ((pageInfoList == null) ? 0 : pageInfoList.size());
            }

            @Override
            public Object getItem(int position) {
                return pageInfoList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return pageInfoList.get(position).getId();
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                mInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                TextView textView = (TextView) mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
                textView.setTextColor(Color.RED);

                PageBean currPage = pageInfoList.get(position);
                textView.setText("" + currPage.getPageTitle());
                return textView;
            }
        };

        //新增
        pageAddBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), OnLineActivity.class));
            }
        });

        //删除
        pagDelBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selPageBean == null) {
                    pageInfoText.setText("请选择数据！");
                    return;
                }

                einkClient.DelOnePage(selPageBean);
                einkClient.CleanOnLineDataOnePage();
            }
        });


        //编辑
        pagEditBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selPageBean == null) {
                    pageInfoText.setText("请选择数据！");
                    return;
                }

                Intent intent = new Intent(getApplicationContext(), OnLineActivity.class);
                intent.putExtra("PageBeanId", selPageBean.getId());

                startActivity(intent);
            }
        });


        //重命名编辑框框
        reNameET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    reNameET.setVisibility(View.GONE);
                    hideKeyboard(v.getContext());
                    einkClient.SetPageDataTitle(selPageBean.getId(), v.getText().toString());
                    return true;
                }
                return false;
            }
        });

        //重命名
        pageReNameBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reNameET.setVisibility(View.VISIBLE);
            }
        });

        //拆分
        apgeSqlitBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selPageBean == null) {
                    pageInfoText.setText("请选择数据！");
                    return;
                }

                Intent intent = new Intent(getApplicationContext(), SqlitActivity.class);
                intent.putExtra("PageBeanId", selPageBean.getId());

                startActivity(intent);
            }
        });

        pageView.setAdapter(pageAdapter);
        pageView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selPageBean = einkClient.GetPageInfo((int) id);
                pageInfoText.setText(selPageBean.toString());

                einkClient.CleanOnLineDataOnePage();

                time = System.currentTimeMillis();
                List<PointBean> tempList = einkClient.GetPagePoinDate((int) id);
                Log.e("TimeTest", "TimeTest1: " + (System.currentTimeMillis() - time));

                time = System.currentTimeMillis();
                einkClient.DrawPointToEinkView(tempList);
                Log.e("TimeTest", "TimeTest2: " + (System.currentTimeMillis() - time));

            }
        });

        //分类
        pageCatBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selPageBean == null) {
                    Toast.makeText(OffLineActivity.this
                            , "选择一个记录", Toast.LENGTH_SHORT).show();

                }

                einkClient.SetPageDataCategory(selPageBean.getId(), "22", false);
            }
        });

        //查找
        findViewById(R.id.page_find).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageInfoList = einkClient.GetPageDateByCategory("22");
                pageAdapter.notifyDataSetChanged();

            }
        });

        //查询共多少类别
        pageCateStrBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> data = einkClient.GetCategoryString();
                if (data != null)
                    Toast.makeText(OffLineActivity.this
                            , "共" + data.size() + "类：" + data.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        //
        findViewById(R.id.one_page_categroy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<String> data = einkClient.GetCategoryByPageId(4);
                ;
                Toast.makeText(OffLineActivity.this
                        , "属于" + data.size() + "类：" + data.toString(), Toast.LENGTH_SHORT).show();

            }
        });

        findViewById(R.id.get_offline_data).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                einkClient.GetOnePageOfflineData();
            }
        });
    }

    public static void hideKeyboard(Context ctx) {
        if (ctx != null) {
            View view = ((Activity) ctx).getCurrentFocus();
            if (view != null) {
                InputMethodManager inputManager = (InputMethodManager) ctx
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //完成客户端初始化
        einkClient.Init(callbacks, einkView, EinkClient.MODE_OFFLINE);
        pageInfoList = einkClient.GetAllPageInfo();
        pageAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
