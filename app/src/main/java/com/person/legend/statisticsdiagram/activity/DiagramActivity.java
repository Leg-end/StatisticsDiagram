package com.person.legend.statisticsdiagram.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.person.legend.statisticsdiagram.R;
import com.person.legend.statisticsdiagram.bean.BusinessFlow;
import com.person.legend.statisticsdiagram.bean.BusinessTag;
import com.person.legend.statisticsdiagram.bean.DateState;
import com.person.legend.statisticsdiagram.view.HistogramView;

import java.util.ArrayList;
import java.util.List;

public class DiagramActivity extends BaseActivity implements View.OnClickListener {
    private List<BusinessFlow> inFlows, outFlows;
    private Button btn_y, btn_m, btn_d;
    private TextView tv_compare;
    private HistogramView mHView;
    private DateState state;

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
        mHView.setFlows(null,state,null,false);
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
                mHView.setFlows(null, state,null,false);
                break;
            case R.id.diagram_tv_compare:
                tv_compare.setTextColor(getResources().getColor(R.color.colorLightBlue));
                mHView.setFlows(null,state,null,true);
                break;

        }
    }
}
