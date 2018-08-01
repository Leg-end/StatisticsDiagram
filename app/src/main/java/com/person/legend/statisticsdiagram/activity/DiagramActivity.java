package com.person.legend.statisticsdiagram.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.person.legend.statisticsdiagram.R;
import com.person.legend.statisticsdiagram.bean.BusinessFlow;
import com.person.legend.statisticsdiagram.bean.BusinessTag;
import com.person.legend.statisticsdiagram.bean.DateState;
import com.person.legend.statisticsdiagram.util.DateUtil;
import com.person.legend.statisticsdiagram.view.HistogramView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DiagramActivity extends BaseActivity implements View.OnClickListener {
    private List<BusinessFlow> inFlows, outFlows;
    private Button btn_y, btn_m, btn_d;
    private TextView tv_compare,tv_date;
    private HistogramView mHView;
    private DateState state;
    private String content;
    private int y,m,d;

    @Override
    public void onCreate(Bundle onSavedInstance) {
        super.onCreate(onSavedInstance);
        setContentView(R.layout.activity_diagram);
        initView();
    }

    private void initView() {
        state = DateState.DAY;
        btn_d = findViewById(R.id.diagram_btn_day);
        btn_m = findViewById(R.id.diagram_btn_month);
        btn_y = findViewById(R.id.diagram_btn_year);
        tv_compare = findViewById(R.id.diagram_tv_compare);
        mHView = findViewById(R.id.diagram_hgv);
        tv_date = findViewById(R.id.diagram_tv_date);
        ImageView left = findViewById(R.id.diagram_iv_left);
        ImageView right = findViewById(R.id.diagram_iv_right);
        y = DateUtil.getCurrentTimeFiled(Calendar.YEAR);
        m = DateUtil.getCurrentTimeFiled(Calendar.MONTH)+1;
        d = DateUtil.getCurrentTimeFiled(Calendar.DAY_OF_MONTH);
        content = d+"号";
        tv_date.setText(content);
        mHView.setFlows(null,state,null,false);
        left.setOnClickListener(this);
        right.setOnClickListener(this);
        btn_d.setOnClickListener(this);
        btn_m.setOnClickListener(this);
        btn_y.setOnClickListener(this);
        tv_compare.setOnClickListener(this);
        //Log.d("Diagram","->width:"+histogramView.getWidth()+",->height:"+histogramView.getHeight());
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.diagram_btn_day:
                btn_d.setBackgroundResource(R.drawable.btn_on_bg);
                btn_d.setTextColor(getResources().getColor(R.color.colorWhite));
                btn_y.setBackgroundResource(R.drawable.btn_out_bg);
                btn_y.setTextColor(getResources().getColor(R.color.colorLightGray));
                btn_m.setBackgroundResource(R.drawable.btn_out_bg);
                btn_m.setTextColor(getResources().getColor(R.color.colorLightGray));
                tv_compare.setTextColor(getResources().getColor(R.color.colorLightGray));
                state = DateState.DAY;
                content = d+"号";
                tv_date.setText(content);
                mHView.setFlows(null, state,null,false);
                break;
            case R.id.diagram_btn_month:
                btn_d.setBackgroundResource(R.drawable.btn_out_bg);
                btn_d.setTextColor(getResources().getColor(R.color.colorLightGray));
                btn_y.setBackgroundResource(R.drawable.btn_out_bg);
                btn_y.setTextColor(getResources().getColor(R.color.colorLightGray));
                btn_m.setBackgroundResource(R.drawable.btn_on_bg);
                btn_m.setTextColor(getResources().getColor(R.color.colorWhite));
                tv_compare.setTextColor(getResources().getColor(R.color.colorLightGray));
                state = DateState.MONTH;
                content = m+"月";
                tv_date.setText(content);
                mHView.setFlows(null, state,null,false);
                break;
            case R.id.diagram_btn_year:
                btn_d.setBackgroundResource(R.drawable.btn_out_bg);
                btn_d.setTextColor(getResources().getColor(R.color.colorLightGray));
                btn_y.setBackgroundResource(R.drawable.btn_on_bg);
                btn_y.setTextColor(getResources().getColor(R.color.colorWhite));
                btn_m.setBackgroundResource(R.drawable.btn_out_bg);
                btn_m.setTextColor(getResources().getColor(R.color.colorLightGray));
                tv_compare.setTextColor(getResources().getColor(R.color.colorLightGray));
                state = DateState.YEAR;
                content = String.valueOf(y);
                tv_date.setText(content);
                mHView.setFlows(null, state,null,false);
                break;
            case R.id.diagram_tv_compare:
                tv_compare.setTextColor(getResources().getColor(R.color.colorLightBlue));
                mHView.setFlows(null,state,null,true);
                break;
            case R.id.diagram_iv_left:
                switch (state) {
                    case DAY:
                        int preDay = --d;
                        if(preDay <= 0) {
                            Toast.makeText(this, "已到月初!", Toast.LENGTH_SHORT).show();
                            d++;
                        } else {
                            content = preDay + "号";
                            tv_date.setText(content);
                            mHView.setFlows(null,state,null,false);
                        }
                        break;
                    case MONTH:
                        int preMonth = --m;
                        if(preMonth <= 0) {
                            Toast.makeText(this, "已到年初!", Toast.LENGTH_SHORT).show();
                            m++;
                        } else {
                            content = preMonth + "月";
                            tv_date.setText(content);
                            mHView.setFlows(null,state,null,false);
                        }
                        break;
                    case YEAR:
                        int preYear = --y;
                        content = String.valueOf(preYear);
                        tv_date.setText(content);
                        mHView.setFlows(null,state,null,false);
                        break;
                }
                break;
            case R.id.diagram_iv_right:
                switch (state) {
                    case DAY:
                        int nextDay = ++d;
                        if(nextDay > DateUtil.getCurrentTimeFiled(Calendar.DAY_OF_MONTH)) {
                            Toast.makeText(this, "无法前往未来", Toast.LENGTH_SHORT).show();
                            d--;
                        }
                        else if(nextDay > DateUtil.getDayNum(DateUtil.getCurrentTimeFiled(Calendar.YEAR)
                                ,DateUtil.getCurrentTimeFiled(Calendar.MONTH)+1)) {
                            Toast.makeText(this, "已到月末!", Toast.LENGTH_SHORT).show();
                            d--;
                        } else {
                            content = nextDay + "号";
                            tv_date.setText(content);
                            mHView.setFlows(null,state,null,false);
                        }
                        break;
                    case MONTH:
                        int nextMonth = ++m;
                        if(nextMonth > (DateUtil.getCurrentTimeFiled(Calendar.MONTH)+1)) {
                            Toast.makeText(this, "无法前往未来", Toast.LENGTH_SHORT).show();
                            m--;
                        }
                        else if(nextMonth <= 0) {
                            Toast.makeText(this, "已到年末!", Toast.LENGTH_SHORT).show();
                            m--;
                        } else {
                            content = nextMonth + "月";
                            tv_date.setText(content);
                            mHView.setFlows(null,state,null,false);
                        }
                        break;
                    case YEAR:
                        int nextYear = ++y;
                        if(nextYear > (DateUtil.getCurrentTimeFiled(Calendar.YEAR))) {
                            Toast.makeText(this, "无法前往未来", Toast.LENGTH_SHORT).show();
                            y--;
                        } else {
                            content = String.valueOf(nextYear);
                            tv_date.setText(content);
                            mHView.setFlows(null, state, null, false);
                        }
                        break;
                }
                break;
        }
    }
}
