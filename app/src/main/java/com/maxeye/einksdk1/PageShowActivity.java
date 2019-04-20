package com.maxeye.einksdk1;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.chrisbanes.photoview.PhotoView;
import com.maxeye.einksdk.Bluetooth.EventUserMessage;
import com.maxeye.einksdk.DBdata.PageBean;
import com.maxeye.einksdk.EinkClient.EinkClient;
import com.maxeye.einksdk.EinkClient.EinkClientView;
import com.maxeye.einksdk.wkpaintview.utils.BitMapUtils;
import com.polidea.rxandroidble.RxBleDevice;

import java.io.File;
import java.util.List;


/**
 * Created by Administrator on 2018/4/19 0019.
 */

public class PageShowActivity extends Activity {
    private String TAG = "PictureInfoActivity";

    private EinkClient einkClient;
    private EinkClient.EinkClienteCallbacks callbacks;
    private EinkClientView einkPaintView = null;

    private List<PageBean> pagelist;

    private ViewPager viewPager = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.icon_activity_layout);

        viewPager = findViewById(R.id.pictureinfoactivity_layout_viewPager);
        einkPaintView = findViewById(R.id.PictureInfo_panintview);

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
                if (message.getId() == EventUserMessage.DBDATA_DELPAGE
                        || message.getId() == EventUserMessage.OFFLINEDATA_SYN_SUCCESS
                        || message.getId() == EventUserMessage.DBDATA_SQLITPAGE) {
                    pagelist = einkClient.GetAllPageInfo();
                    if (pagelist.size() <= 0) {
                        finish();
                    }
                    UpdataAllPageIcon();
                    viewPager.getAdapter().notifyDataSetChanged();
                }
            }
        };

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            pagelist = einkClient.GetAllPageInfo();

            if (pagelist == null)
                return;

            UpdataAllPageIcon();

            InitViewPage();
        }
    }

    private void UpdataAllPageIcon() {

        int wallpaperId = 0;
        String path = null;
        if (pagelist == null) {
            return;
        }

        //更新所有图片
        for (int i = 0; i < pagelist.size(); ) {
            PageBean temp = pagelist.get(i);

            if (temp.getPointCount() <= 0 || temp.getLineCount() <= 0) {
                pagelist.remove(i);
                einkClient.DelOnePage(temp);
                continue;
            }

            path = temp.getPageIconPath();
            //wallpaperId = PreferencesUtils.getInt(getApplicationContext(), "WALLPAPER_SELECT_ID", Constant.WALLPAPER_ID);
            wallpaperId = 0;

            if (path == null || !new File(path).exists()) {
                //绘图
                if (wallpaperId != 0)
                    temp.setExtendString("id:" + wallpaperId);
                einkClient.DrawPointToEinkView(einkClient.GetPagePoinDate(temp.getId()));

                //保存图片
                path = EinkClient.DataPath + File.separator + temp.getId() + ".png";
                if (einkClient.SavePicture(path)) {
                    //if (einkClient.SavePictrueWithBackground(path,einkClient.GetEinkView())){
                    //更新数据库
                    temp.setPageIconPath(path);
                    einkClient.UpdateOnePageInfo(temp);
                }
                einkClient.CleanOnLineDataOnePage();
            }
            i++;
        }
    }

    private void InitViewPage() {
        viewPager.setAdapter(new PagerAdapter() {

            @Override
            public int getCount() {
                return pagelist.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                PageBean pictureBean = pagelist.get(position);

                PhotoView convertView = (PhotoView) LayoutInflater.from(getApplicationContext())
                        .inflate(R.layout.page_baen_view, null, false);

                Bitmap bageBeanBitMap = BitMapUtils.loadFromSdCard(pictureBean.getPageIconPath());
                String bg = pictureBean.getExtendString();
                if (bg != null && bg.startsWith("id:")) {
                    bg = bg.substring("id:".length());
                    bageBeanBitMap = mergeBitmap(((BitmapDrawable) getResources().getDrawable(Integer.valueOf(bg))).getBitmap(), bageBeanBitMap);
                }

                convertView.setImageBitmap(bageBeanBitMap);
                container.addView(convertView);

                return convertView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }

            @Override
            public int getItemPosition(Object object) {
                // 最简单解决 notifyDataSetChanged() 页面不刷新问题的方法
                return POSITION_NONE;
            }

        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }


    private Bitmap mergeBitmap(Bitmap firstBitmap, Bitmap secondBitmap) {

        float wscale = firstBitmap.getWidth() / secondBitmap.getWidth();
        float hscale = firstBitmap.getHeight() / secondBitmap.getHeight();

        Bitmap bitmap = Bitmap.createBitmap(firstBitmap.getWidth(), firstBitmap.getHeight(), firstBitmap.getConfig());
        Canvas canvas = new Canvas(bitmap);

        Matrix martix = new Matrix();
        canvas.drawBitmap(firstBitmap, martix, null);
        martix.setScale(wscale, hscale);
        canvas.drawBitmap(secondBitmap, martix, null);
        return bitmap;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        einkClient.Init(callbacks, einkPaintView, EinkClient.MODE_OFFLINE);
    }
}
