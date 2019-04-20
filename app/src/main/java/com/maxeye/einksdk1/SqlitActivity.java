package com.maxeye.einksdk1;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.maxeye.einksdk.DBdata.PageBean;
import com.maxeye.einksdk.DBdata.PointBean;
import com.maxeye.einksdk.EinkClient.EinkClient;
import com.maxeye.einksdk.EinkClient.EinkClientView;

import java.util.ArrayList;
import java.util.List;

import static com.maxeye.einksdk.EinkClient.EinkClient.ERASER;
import static com.maxeye.einksdk.EinkClient.EinkClient.PRESSURE_PEN;

/**
 * Created by Administrator on 2018/4/17 0017.
 */

public class SqlitActivity extends Activity {
    private EinkClient einkClient;
    private EinkClient.EinkClienteCallbacks callbacks = null;
    private EinkClientView einkView = null;

    private Button sqlit1Bt = null;
    private Button sqlit1Bt2 = null;
    private Button sqlit1Bt3 = null;
    private Button sqlitSaveBt = null;

    private SeekBar seekBar;
    private int oldprogress=-1;

    SeekBar.OnSeekBarChangeListener seekType1ChangeListener=null;
    SeekBar.OnSeekBarChangeListener seekType1ChangeListener2=null;
    SeekBar.OnSeekBarChangeListener seekType1ChangeListener3=null;


    private int totalLine = 0;
    private List<List<PointBean>> LineList = new ArrayList<>();
    private int pageBeanId;

    PageBean currPage = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sqlit_activity_layout);


        sqlit1Bt = findViewById(R.id.sqlit_type1);
        sqlit1Bt2 = findViewById(R.id.sqlit_type2);
        sqlit1Bt3 = findViewById(R.id.sqlit_type3);

        seekBar = findViewById(R.id.seekBar);
        einkView = findViewById(R.id.sqlit_panintview);
        sqlitSaveBt = findViewById(R.id.sqlit_save);
        einkClient = ((MyApplication) getApplication()).getEinkClient();


        //需要加载的 pageBeanId
        pageBeanId = getIntent().getIntExtra("PageBeanId", -1);
        currPage = einkClient.GetPageInfo(pageBeanId);
        totalLine = currPage.getLineCount();

        for (int i = 0; i < totalLine; i++) {
            LineList.add(einkClient.GetPageBeanOneLine(pageBeanId, i, i));
        }

        seekBar.setMax(totalLine - 1);
        seekBar.setProgress(totalLine - 1);

        sqlitSaveBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                einkClient.SqlitOnePage(pageBeanId, oldprogress);
                finish();
            }
        });

        sqlit1Bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBar.setOnSeekBarChangeListener(seekType1ChangeListener);
            }
        });

        sqlit1Bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBar.setOnSeekBarChangeListener(seekType1ChangeListener2);
            }
        });

        sqlit1Bt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBar.setOnSeekBarChangeListener(seekType1ChangeListener3);
            }
        });


        //样式1
        seekType1ChangeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (!fromUser)
                    return;

                if (oldprogress == -1) {
                    oldprogress = progress;
                } else if (oldprogress == progress) {
                    return;
                }

                if (oldprogress > progress) {
                    //回退
                    einkClient.GetEinkView().setPenColor(Color.BLACK);
                    einkClient.GetEinkView().setPenType(ERASER);
                    for (; oldprogress > progress; oldprogress--) {
                        //einkClient.DrawPointToEinkViewForSqlit(LineList.get(oldprogress));
                    }
                    einkClient.GetEinkView().setPenType(PRESSURE_PEN);

                } else {
                    //前进
                    for (; oldprogress <= progress; oldprogress++) {
                        //einkClient.DrawPointToEinkViewForSqlit(LineList.get(oldprogress));
                    }
                }
                oldprogress = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };

        //样式2
        seekType1ChangeListener2 = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (!fromUser)
                    return;

                if (oldprogress == -1) {
                    oldprogress = seekBar.getMax();
                } else if (oldprogress == progress) {
                    return;
                }

                if (oldprogress > progress) {
                    //回退
                    einkClient.GetEinkView().setPenColor(0xFF00FF00);
                    for (; oldprogress > progress; oldprogress--) {
                        //einkClient.DrawPointToEinkViewForSqlit(LineList.get(oldprogress));
                    }
                    einkClient.GetEinkView().setPenColor(Color.BLACK);

                } else {
                    //前进
                    for (; oldprogress <= progress; oldprogress++) {
                        //einkClient.DrawPointToEinkViewForSqlit(LineList.get(oldprogress));
                    }
                }

                oldprogress = progress;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };

        seekType1ChangeListener3 = new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {

                if (!fromUser)
                    return;

                if (oldprogress == -1){
                    oldprogress = seekBar.getMax();
                }

                else if (oldprogress == progress){
                    return;
                }

                einkClient.GetEinkView().setPenColor(Color.BLACK);
                //einkClient.DrawPointToEinkViewForSqlit(LineList.get(oldprogress));

                einkClient.GetEinkView().setPenColor(Color.RED);
                //einkClient.DrawPointToEinkViewForSqlit(LineList.get(progress));



                oldprogress = progress;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };

    }

    @Override
    protected void onResume() {
        super.onResume();
        einkClient.Init(null, einkView, EinkClient.MODE_OFFLINE);
        seekBar.setOnSeekBarChangeListener(seekType1ChangeListener);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus){
            einkClient.DrawPointToEinkView(einkClient.GetPagePoinDate(pageBeanId));
        }
    }
}
