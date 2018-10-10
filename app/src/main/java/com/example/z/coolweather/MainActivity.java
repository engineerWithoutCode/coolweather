package com.example.z.coolweather;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.bumptech.glide.Glide;
import com.example.z.coolweather.Adapter.MyFragmentPagerAdapter;
import com.example.z.coolweather.Fragment.CommonFragment;
import com.facebook.stetho.Stetho;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import Network.HttpUtil;
import db.Customer;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private ImageView manageCity;
    private ImageView delFragment;
    private ImageView bingPic;
    private MyFragmentPagerAdapter myFragmentPagerAdapter;
    private List<CommonFragment> fragmentList = new ArrayList<>();

    private PointTabView pointTabView;
    private Date date;
    private SimpleDateFormat simpleDateFormat;
    private String currentDate;
    private int size;

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);
        LitePal.getDatabase();
        if (Build.VERSION.SDK_INT >= 21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_main);
        bingPic = (ImageView)findViewById(R.id.back_ground);
        viewPager = (ViewPager)findViewById(R.id.view_pager);
        manageCity = (ImageView)findViewById(R.id.manage_city);
        delFragment = (ImageView)findViewById(R.id.delete_city);
        pointTabView = (PointTabView)findViewById(R.id.point_tab);
        //对比日期
        simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        date = new Date(System.currentTimeMillis());
        currentDate = simpleDateFormat.format(date);
        sp = getSharedPreferences("date", Context.MODE_PRIVATE);
        editor = sp.edit();
        String pic = sp.getString("bing_pic",null);
        String day = sp.getString("day","dd");
        if(pic != null && day.equals(currentDate)){
            Glide.with(MainActivity.this).load(pic).into(bingPic);
        }else{
            loadPic();
        }
        loadPager();
        myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(),fragmentList);
        viewPager.setAdapter(myFragmentPagerAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pointTabView.setSelectNum(position+1);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        manageCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,SearchActivity.class);
                startActivityForResult(intent,1);
            }
        });
        delFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("提示");
                builder.setMessage("确定删除当前城市天气预报吗？");
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        delPager();
                    }
                });
                builder.setCancelable(false);
                builder.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                if (resultCode == RESULT_OK){
                    String weatherId = data.getStringExtra("weatherId");
                    addPage(weatherId);
                }
                break;
        }
    }

    private void loadPager(){
        List<Customer> customerList = DataSupport.findAll(Customer.class);
        size = customerList.size();
        if(size > 0){
            for (Customer customer : customerList){
                CommonFragment commonFragment = new CommonFragment();
                Bundle bundle = new Bundle();
                bundle.putString("weatherId",customer.getWeatherId());
                commonFragment.setArguments(bundle);
                fragmentList.add(commonFragment);
            }
        }
        pointTabView.setNum(size);
    }

    public void addPage(String weatherId){
        CommonFragment commonFragment = new CommonFragment();
        Bundle bundle = new Bundle();
        bundle.putString("weatherId",weatherId);
        commonFragment.setArguments(bundle);
        fragmentList.add(commonFragment);
        size++;
        pointTabView.setNum(size);
        myFragmentPagerAdapter.notifyDataSetChanged();//通知UI更新
        viewPager.setCurrentItem(size-1);
        Customer customer = new Customer();
        customer.setWeatherId(weatherId);
        customer.save();
    }

    public void delPager(){
        int position = viewPager.getCurrentItem();
        CommonFragment commonFragment = fragmentList.get(position);
        String weatherId = commonFragment.getWeatherId();
        DataSupport.deleteAll(Customer.class,"weatherId = ?",weatherId);
        fragmentList.remove(position);
        myFragmentPagerAdapter.notifyDataSetChanged();
        size--;
        pointTabView.setNum(size);
    }

    private void loadPic(){
        String address = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String data = response.body().string();
                editor.putString("bing_pic",data);
                editor.putString("day",currentDate);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(MainActivity.this).load(data).into(bingPic);
                    }
                });
            }
        });
    }
}
