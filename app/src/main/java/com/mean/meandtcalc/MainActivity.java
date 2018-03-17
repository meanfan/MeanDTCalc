package com.mean.meandtcalc;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,View.OnClickListener{
    private String[] sizeArray = {"B","KB","MB","GB","TB","PB"};
    private int[] sizeArray2Lower = {1,1024,1024,1024,1024,1024};
    private String[] speedArray = {"B/s","KB/s","MB/s","GB/s"};
    private int[] speedArray2Lower = {1,1024,1024,1024};
    private String[] timeArray = {"秒","分钟","小时","天","月","年"};
    private int[] timeArray2Lower = {1,60,60,24,30,365};
    private Spinner sp_size,sp_speed,sp_time;
    private EditText et_size,et_speed,et_time;
    private Button btn_calc;
    private double rst_second;
    private SharedPreferences sps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("还要下多久?!");
        sps = getSharedPreferences("default", Context.MODE_PRIVATE);
        sp_size = findViewById(R.id.sp_size);
        sp_speed = findViewById(R.id.sp_speed);
        sp_time = findViewById(R.id.sp_time);
        et_size = findViewById(R.id.et_size);
        et_speed = findViewById(R.id.et_speed);
        et_time = findViewById(R.id.et_time);
        btn_calc = findViewById(R.id.btn_calc);
        btn_calc.setOnClickListener(this);
        ArrayAdapter<String> sizeAdapter = new ArrayAdapter<>(this,R.layout.spinner__default_selected,sizeArray);
        sizeAdapter.setDropDownViewResource(R.layout.spinner_default_dropdown);
        sp_size.setAdapter(sizeAdapter);
        sp_size.setSelection(sps.getInt("size",2));
        sp_size.setOnItemSelectedListener(this);

        ArrayAdapter<String> speedAdapter = new ArrayAdapter<>(this,R.layout.spinner__default_selected,speedArray);
        sp_speed.setAdapter(speedAdapter);
        sp_speed.setSelection(sps.getInt("speed",1));
        sp_speed.setOnItemSelectedListener(this);

        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(this,R.layout.spinner__default_selected,timeArray);
        sp_time.setAdapter(timeAdapter);
        sp_time.setSelection(sps.getInt("time",1));
        sp_time.setOnItemSelectedListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_calc){
            if(TextUtils.isEmpty(et_size.getText())){
                Toast.makeText(this,"请输入大小",Toast.LENGTH_SHORT).show();
                return;
            }
            if(TextUtils.isEmpty(et_speed.getText())){
                Toast.makeText(this,"请输入速度",Toast.LENGTH_SHORT).show();
                return;
            }
            double size = Double.valueOf(et_size.getText().toString());
            double speed = Double.valueOf(et_speed.getText().toString());
            int sizePos = sp_size.getSelectedItemPosition();
            int speedPos = sp_speed.getSelectedItemPosition();
            //int timePos = sp_time.getSelectedItemPosition();
            rst_second = calc(size,sizePos,speed,speedPos);
            double tmp_second = rst_second;
            int i=1;
            for(;i<timeArray2Lower.length;i++){
                if(tmp_second<timeArray2Lower[i])
                    break;
                tmp_second/=timeArray2Lower[i];
            }
            sp_time.setSelection(i-1);
            double rst = convertTime(rst_second,i-1);
            et_time.setText(String.format("%.3f",rst));
        }
    }
    private double calc(double size,int sizePos,double speed,int speedPos){
        for(int i=0;i<=sizePos;i++)
            size*=sizeArray2Lower[i];
        for(int i=0;i<=speedPos;i++)
            speed*=speedArray2Lower[i];
        return size/speed;
    }
    private double convertTime(double time,int timePos){
        for(int i=0;i<=timePos;i++)
            time /=timeArray2Lower[i];
        return time;
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int resId = parent.getId();
        SharedPreferences.Editor spsEditor = sps.edit();
        if(resId == R.id.sp_size){
            spsEditor.putInt("size",position);
        }else if(resId  == R.id.sp_speed){
            spsEditor.putInt("speed",position);
        }else if(resId  == R.id.sp_time) {
            spsEditor.putInt("time",position);
            if (!TextUtils.isEmpty(et_time.getText())) {
                double rst = convertTime(rst_second, position);
                et_time.setText(String.format("%.3f", rst));
            }
        }
        spsEditor.apply();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
