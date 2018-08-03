package com.person.legend.statisticsdiagram.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.person.legend.statisticsdiagram.R;
import com.person.legend.statisticsdiagram.bean.BusinessFlow;
import com.person.legend.statisticsdiagram.bean.BusinessTag;
import com.person.legend.statisticsdiagram.bean.DateState;
import com.person.legend.statisticsdiagram.util.CommonUtil;
import com.person.legend.statisticsdiagram.util.DateUtil;
import com.person.legend.statisticsdiagram.util.DrawUtil;
import com.person.legend.statisticsdiagram.util.FakeUtil;
import com.person.legend.statisticsdiagram.util.LogUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HistogramView extends View {
    private final String TAG = "Diagram";
    private Resources mResources;
    private Paint xLinePaint;// 坐标轴 轴线 画笔：
    private Paint hLinePaint;// 坐标轴水平内部 虚线画笔
    private Paint titlePaint;// 绘制文本的画笔
    private Paint flowPaint;// 绘制流的画笔
    private Paint markPaint;// 绘制侧标识的画笔
    private Paint clickPaint;// 点击时绘制标识的画笔
    private float timeInterval,numInterval;//x,y 轴基本比例
    private int mWidth,mHeight;
    private float rHeight,rWidth;//坐标轴内真实的测量尺度
    private float marginT,marginB,marginL,marginR;
    private float columnOffset = 3f;//柱状图时所绘矩形的宽度的一半
    private int yStep = 4,xStep = 4;//按间隔步伐计算，不是按坐标标记数,所以坐标标记数要在此基础上加一
    private List<BusinessFlow> inFlows,compareFlows;
    private DateState state = DateState.DAY;
    private boolean isCompare = false;//是否对比数据
    private List<PointF> inPoints,comparePoints,crossPoints;
    private Rect yTextBound,xTextBound,markTextBound;

    // 坐标轴左侧的数标
    private String[] ySteps;
    // 坐标轴底部的数标
    private String[] xSteps;
    // 右侧侧标识的文字说明
    private String[] marks;
    // 现在的年-月-日数值
    private int[] ymd;
    // 现在的日期
    private String now;
    // 标题
    private StringBuilder title;
    //日数据的更新频率 60/tateFre
    private int rateMeasure = 6;//default update/10 mins
    private boolean showValue = false;
    private PointF clickPoint;

    public HistogramView(Context context) {
        super(context);
        mResources = context.getResources();
    }

    public HistogramView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mResources = context.getResources();
    }

    public void setFlows(List<BusinessFlow> inFlows, @NonNull DateState state
            ,List<BusinessFlow> compareFlows,boolean isCompare) {
        this.state = state;
        //this.inFlows = inFlows;
        //this.compareFlows = compareFlows;
        this.isCompare = isCompare;
        init();
        invalidate();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        mHeight = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(mWidth, mHeight);
        init();
    }

    private void addFakeData() {
        inFlows = new ArrayList<>();
        compareFlows = new ArrayList<>();
        switch (state) {
            case YEAR:
                FakeUtil.generateYearData(isCompare,inFlows,compareFlows,90);
                break;
            case MONTH:
                FakeUtil.generateMonthData(isCompare,inFlows,compareFlows,60);
                break;
            case DAY:
                FakeUtil.generateDayData(isCompare,inFlows,compareFlows,30,rateMeasure);
                break;
        }
    }

    private void initCoordinateMark() {
        /*-------init x--------*/
        switch (state) {
            case DAY:
                xSteps = new String[] { "8:00", "12:00", "16:00", "20:00", "24:00"};
                break;
            case MONTH:
                xSteps = DateUtil.generateMonthDays(ymd[0],ymd[1]);
                break;
            case YEAR:
                xSteps = new String[]{"一","二","三","四","五","六","七","八","九","十","十","十"};
                break;
        }
        xStep = xSteps.length-1;
        xTextBound = getTextBounds(xSteps[xSteps.length-1],titlePaint);
        /*-------init x--------*/
        /*-------init y--------*/
        Log.d(TAG,"width:"+mWidth+",height:"+mHeight);
        int max = 1;
        for(BusinessFlow flow:inFlows) {
            if(flow.getNum() >= max)
                max = flow.getNum();
        }
        if(isCompare) {
            for(BusinessFlow flow:compareFlows) {
                if(flow.getNum() >= max)
                    max = flow.getNum();
            }
        }
        if(max%100 > 0)//向百位数取整,方便以n.mK的形式输出
            max += (100-max%100);
        Log.d(TAG,"max:"+max);
        marginL = dp2px(44) + xTextBound.width() / 2;
        marginB = dp2px(40);
        marginR = dp2px(60);
        marginT = dp2px(42);
        rHeight = mHeight-marginT-marginB;
        rWidth = mWidth-marginL-marginR;
        numInterval = rHeight/max;//基本比例尺
        Log.d(TAG,"xStep:"+xStep);
        switch (state) {
            case DAY:
                timeInterval = rWidth/(16*rateMeasure);
                break;
            case MONTH:
                timeInterval = rWidth/xStep;
                columnOffset = timeInterval/8;
                break;
            case YEAR:
                timeInterval = rWidth/xStep;
                break;
        }//基本比例尺
        Log.d(TAG,"yInterval:"+numInterval+",xInterval:"+timeInterval);
        int diff = max/yStep;
        ySteps = new String[yStep+1];
        for(int i = 0;i <= yStep;i++) {
            int num = i*diff;
            if(num >= 1000) {
                ySteps[i] = num/1000+"."+(num/100)%10+"K";
            } else {
                ySteps[i] = String.valueOf(num);
            }
        }
        yTextBound = getTextBounds("800",titlePaint);
        /*-------init y--------*/
    }

    /**
     * 对对比模式时的日期的前后判断放到点击事件里去处理
     */
    private void initSideMark() {
        int firstIndex = now.indexOf("/");
        int secondIndex = now.lastIndexOf("/");
        switch (state) {
            case DAY:
                title = new StringBuilder(now);
                if(isCompare) {
                    int preDay = ymd[2]-1;
                    marks = new String[]{ymd[1]+"/"+ymd[2], ymd[1]+"/"+preDay};
                    title.append(" vs ");
                    title.append(now.subSequence(0,secondIndex+1));
                    title.append(preDay);
                } else {
                    marks = new String[]{"总人数"};
                    title.append(" 客流统计图");
                }
                break;
            case MONTH:
                title = new StringBuilder(now.substring(0,secondIndex));
                if(isCompare) {
                    int preMonth = ymd[1]-1;
                    marks = new String[]{ymd[1]+"月",preMonth+"月"};
                    title.append(" vs ");
                    title.append(now.substring(0,firstIndex+1));
                    title.append(preMonth);
                } else {
                    marks = new String[]{"今天"};
                    title.append(" 客流统计图");
                }
                //columnOffset = dp2px(3);
                break;
            case YEAR:
                title = new StringBuilder(String.valueOf(ymd[0]));
                if(isCompare) {
                    columnOffset = dp2px(6);
                    int preYear = ymd[0]-1;
                    marks = new String[]{String.valueOf(ymd[0]),String.valueOf(preYear)};
                    title.append(" vs ");
                    title.append(preYear);
                } else {
                    columnOffset = dp2px(4);
                    marks = new String[]{"今年"};
                    title.append("年客流统计图");
                }
                break;
        }
        markTextBound = getTextBounds(marks[marks.length-1],titlePaint);
    }

    private void init() {
        showValue = false;
        addFakeData();
        initPaint();
        now = inFlows.get(0).getDate();
        String[] strs = now.split("/");
        ymd = new int[strs.length];
        for(int i = 0;i < strs.length;i++) {
            ymd[i] = Integer.parseInt(strs[i]);
        }
        Log.d(TAG,"init...");
        Log.d(TAG,"width:"+mWidth+",height:"+mHeight);
        initSideMark();
        initCoordinateMark();
        generateCurvePoint();
    }

    private void initPaint() {
        xLinePaint = new Paint();
        hLinePaint = new Paint();
        titlePaint = new Paint();
        flowPaint = new Paint();
        markPaint = new Paint();
        clickPaint = new Paint();

        // 给画笔设置颜色
        xLinePaint.setColor(Color.BLACK);
        xLinePaint.setStrokeWidth(dp2px(1));
        setPaintColor(R.color.colorSoftLimeGray,hLinePaint);
        titlePaint.setColor(Color.BLACK);
        titlePaint.setTextSize(sp2px(12));
        titlePaint.setTextAlign(Align.CENTER);
        titlePaint.setAntiAlias(true);
        titlePaint.setStyle(Paint.Style.FILL);
        flowPaint.setStrokeWidth(dp2px(2));
        markPaint.setAntiAlias(true);
        setPaintColor(R.color.colorLightGreen, clickPaint);
        clickPaint.setStyle(Paint.Style.FILL);
        clickPaint.setStrokeWidth(dp2px(3));
    }

    /*public void start(int flag) {
        this.flag = flag;
        this.startAnimation(ani);
    }*/

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(this.inFlows == null)
            throw new NullPointerException("the data of business flow cannot be null");
        /*---绘制侧标识---*/
        drawSideMark(canvas);
        /*---绘制坐标---*/
        drawCoordinate(canvas);
        /*---根据给定数据绘制曲线or柱状图---*/
        drawData(canvas);
        if(showValue)
            drawValue(canvas,clickPoint);

    }

    private void drawValue(Canvas canvas, PointF pointF) {
        String msg = String.valueOf(inFlows.get(inPoints.indexOf(pointF)).getNum());
        canvas.drawText(msg,pointF.x,pointF.y
                -getTextBounds(msg,titlePaint).height()-dp2px(2),titlePaint);
        switch (state) {
            case DAY:
                canvas.drawCircle(pointF.x, pointF.y, dp2px(6), clickPaint);
                canvas.drawLine(pointF.x, pointF.y
                        , pointF.x, rHeight+marginT, clickPaint);
                break;
            case MONTH:
                break;
            case YEAR:
                break;
        }

    }

    private void drawData(Canvas canvas) {
        float bottomLine = mHeight-marginB-dp2px(1);
        if(state == DateState.DAY) {
            Path path = DrawUtil.drawCurvesFromPoints(canvas,inPoints,0.2
                    ,getColorByResId(R.color.colorLightBlue),flowPaint);
            drawGradientShader(true,canvas,path
                    ,inPoints.get(0),inPoints.get(inPoints.size()-1),bottomLine);

            if (isCompare) {
                flowPaint.setPathEffect(new DashPathEffect(new float[]{5,5},0));
                Path comparePath = DrawUtil.drawCurvesFromPoints(canvas,comparePoints,0.2
                        ,getColorByResId(R.color.colorOrangeRed),flowPaint);
                drawGradientShader(false,canvas,comparePath
                        ,comparePoints.get(0),comparePoints.get(comparePoints.size()-1),bottomLine);
                flowPaint.setPathEffect(null);
            }
            drawPoint(canvas);
        } else {
            drawColumns(canvas);
        }
    }

    /**
     * 关键在于如何截取曲线路径上的一段。。这个点一直没搞定,暂时现放在这里把
     */
    private void drawCompareGradientShader(Canvas canvas
            ,Path comparePath,Path inPath,float bottomLine) {
        PathMeasure pathMeasure = new PathMeasure(comparePath,false);
        Path clip;
        for(int i = 0;i < crossPoints.size()-1;i++) {
            PointF pHead = crossPoints.get(i);
            PointF pNext = crossPoints.get(i+1);
            int middleIndex = (comparePoints.indexOf(pHead)
                    +comparePoints.indexOf(pNext))/2;
            //if(inPoints.get(middleIndex).y > comparePoints.get(middleIndex).y)
                /*drawGradientShader(true,canvas
                        ,pathMeasure.getSegment()
                        ,pHead,pNext,bottomLine);*/
            //else
                //drawGradientShader(true,canvas,comparePath,);
        }
    }



    /**
     *
     * @param inOrCompare 标识绘制的是当前曲线的阴影还是对比的曲线的
     * @param canvas 画布
     * @param curvePath 曲线的路径
     */
    private void drawGradientShader(boolean inOrCompare, Canvas canvas
            ,Path curvePath,PointF pStart,PointF pEnd,float bottomLine) {
        Path path = new Path();
        path.addPath(curvePath);
        path.lineTo(pStart.x,bottomLine);
        path.lineTo(pEnd.x,pEnd.y);
        path.lineTo(pEnd.x,bottomLine);
        path.close();

        //默认开启硬件加速的情况下 clipPath 需要SDK >18
        //如果关闭硬件加速，则不需要SDK要求
        if(clipPathSupported()) {
            int save = canvas.save();
            //将画布切割成path的形状
            canvas.clipPath(path);

            Drawable drawable = getFillDrawable(inOrCompare);
            drawable.setBounds((int) pStart.x, (int) pEnd.y
                    , (int)pEnd.x, (int)bottomLine);
            drawable.draw(canvas);
            canvas.restoreToCount(save);
        }
    }

    /**
     * Clip path with hardware acceleration only working properly on API level 18 and above.
     */
    private boolean clipPathSupported() {
        return CommonUtil.getSDKInt() >= 18;
    }

    private Drawable getFillDrawable(boolean inOrCompare){
        if(inOrCompare)
            return getResources().getDrawable(R.drawable.path_fill_grdient_blue);
        else
            return getResources().getDrawable(R.drawable.path_fill_grdient_red);
    }



    /**
     * 绘制点
     */
    private void drawPoint(Canvas canvas){
        flowPaint.setStyle(Paint.Style.FILL);
        setPaintColor(R.color.colorLightBlue,flowPaint);
        for(int i = 0;i < inFlows.size();i++) {
            Log.d(TAG,"x:"+inPoints.get(i).x+",y:"+inPoints.get(i).y);
            if(inFlows.get(i).getTime()%(4*rateMeasure) == 0&&i != 0) {
                canvas.drawCircle(inPoints.get(i).x,inPoints.get(i).y,dp2px(4),flowPaint);
            }
        }
        if(isCompare) {
            setPaintColor(R.color.colorOrangeRed,flowPaint);
            int len = dp2px(5);
            for(int i = 0;i < inFlows.size();i++) {
                if(inFlows.get(i).getTime()%(4*rateMeasure) == 0&&i != 0) {
                    PointF pointF = comparePoints.get(i);
                    canvas.drawBitmap(BitmapFactory.decodeResource(
                            mResources,R.mipmap.ic_shape_triangle_red)
                            ,pointF.x-len,pointF.y-len,markPaint);
                    //canvas.drawCircle(comparePoints.get(i).x,comparePoints.get(i).y,dp2px(3),flowPaint);
                }
            }
        }
    }

    private void setPaintColor(int resId,Paint paint) {
        paint.setColor(mResources.getColor(resId));
    }

    private int getColorByResId(int resId) {
        return mResources.getColor(resId);
    }

    private void drawColumns(Canvas canvas) {
        int otherColor = 0, currentMarkColor = 0;
        switch (state) {
            case MONTH:
                setPaintColor(R.color.colorSoftLimeBlue,flowPaint);
                otherColor = getColorByResId(R.color.colorOrangeRed);
                currentMarkColor = getColorByResId(R.color.colorLightBlue);
                //flowPaint.setShader(new LinearGradient());
                break;
            case YEAR:
                setPaintColor(R.color.colorLightBlue,flowPaint);
                otherColor = getColorByResId(R.color.colorLimeBlue);
                currentMarkColor = getColorByResId(R.color.colorPrimary);
                break;
        }
        if(isCompare) {
            for(PointF pointF:inPoints) {
                canvas.drawRect(new RectF(pointF.x-columnOffset
                        ,pointF.y,pointF.x,mHeight-marginB),flowPaint);
            }
            flowPaint.setColor(otherColor);
            for(PointF pointF:comparePoints) {
                canvas.drawRect(new RectF(pointF.x
                        ,pointF.y,pointF.x+columnOffset,mHeight-marginB),flowPaint);
            }
        } else {
            for(int i = 0;i < inFlows.size()-1;i++) {
                PointF pointF = inPoints.get(i);
                canvas.drawRect(new RectF(pointF.x-columnOffset
                        ,pointF.y,pointF.x+columnOffset,mHeight-marginB),flowPaint);
            }
            PointF pointF = inPoints.get(inFlows.size()-1);
            if(pointF.y == mHeight-marginB)
                pointF = inPoints.get(inFlows.size()-2);
            Log.d(TAG,"current num:"+pointF.y);
            flowPaint.setColor(currentMarkColor);
            canvas.drawRect(new RectF(pointF.x-columnOffset
                    ,pointF.y,pointF.x+columnOffset,mHeight-marginB),flowPaint);
        }
    }

    private void drawCoordinate(Canvas canvas) {
        titlePaint.setColor(Color.BLACK);
        float height = mHeight - marginB;
        float yShift = height-yTextBound.height()/2-dp2px(2);// 左侧外周的 需要划分的高度：
        float xShift = dp2px(11)+yTextBound.width()/2;
        // 绘制底部的线条
        canvas.drawLine(xShift, height,
                mWidth - dp2px(41), height, xLinePaint);
        //分成四部分
        float yInterval = rHeight/yStep;
        // 绘制 Y 轴坐标

        // 设置左部的数字
        for (int i = 0; i < ySteps.length; i++) {
            canvas.drawText(ySteps[i], xShift,yShift-i*yInterval, titlePaint);
        }
        hLinePaint.setTextAlign(Align.CENTER);
        // 设置四条虚线
        yShift = height-dp2px(5)-yTextBound.height()/2;
        for (int i = 1; i < 5; i++) {
            canvas.drawLine(marginL,yShift-i*yInterval,mWidth- marginR
                    ,yShift-i*yInterval, hLinePaint);
        }
        // 绘制 X 轴坐标
        float xInterval = rWidth/xStep;
        // 设置底部的数字
        switch (state) {
            case DAY:
                for (int i = 0; i < xSteps.length; i++) {
                    Log.d(TAG,"xSteps["+i+"]:"+xSteps[i]);
                    canvas.drawText(xSteps[i], marginL+xInterval*i
                            , height + dp2px(8)+xTextBound.height()/2, titlePaint);
                    }
                break;
            case MONTH:
                int index = 0;
                titlePaint.setTextSize(sp2px(8));
                for (; index < xSteps.length; index++) {
                    Log.d(TAG,"xSteps["+index+"]:"+xSteps[index]);
                    canvas.drawText(xSteps[index], marginL+xInterval*index
                            , height + dp2px(10)+xTextBound.height()/2, titlePaint);
                }
                titlePaint.setTextSize(sp2px(12));
                break;
            case YEAR:
                for (int i = 0; i < xSteps.length; i++) {
                    Log.d(TAG,"xSteps["+i+"]:"+xSteps[i]);
                    canvas.drawText(xSteps[i], marginL+xInterval*i
                            , height + dp2px(8)+xTextBound.height()/2, titlePaint);
                    if(i >= 10 ) {
                        canvas.drawText(xSteps[i%10], marginL+xInterval*i
                                , height + dp2px(8)+xTextBound.height()*1.5f, titlePaint);
                    }
                }
                break;
        }
    }

    private void drawSideMark(Canvas canvas) {
        setPaintColor(R.color.colorGray,titlePaint);
        float yShift = marginT+dp2px(6),xShift = mWidth-marginR+dp2px(8);
        Bitmap bitmap;
        switch (state) {
            case DAY:
                canvas.drawBitmap(BitmapFactory.decodeResource(
                        mResources, R.mipmap.statistics_day_numberofpeople)
                        ,xShift,yShift,markPaint);
                yShift += dp2px(6);
                canvas.drawText(marks[0],xShift+markTextBound.width()/2
                        , yShift += (dp2px(9)+markTextBound.height()/2), titlePaint);
                if(isCompare) {
                    canvas.drawBitmap(BitmapFactory.decodeResource(
                            mResources, R.mipmap.other_days_line)
                            , xShift, yShift += (markTextBound.height()/2+dp2px(15)), markPaint);
                    canvas.drawText(marks[1],xShift+markTextBound.width()/2
                            , yShift +dp2px(13)+markTextBound.height()/2,titlePaint);
                }
                break;
            case MONTH:
                if(isCompare) {
                    xShift += dp2px(15);
                    bitmap = BitmapFactory.decodeResource(mResources
                            ,R.mipmap.ar_graph_of_othermonth);
                    canvas.drawBitmap(bitmap,xShift,yShift,markPaint);
                    yShift += (bitmap.getHeight()+dp2px(6)+markTextBound.height()/2);
                    canvas.drawText(marks[0],xShift+markTextBound.width()/2,yShift,titlePaint);

                    yShift += (markTextBound.height()/2+dp2px(12));
                    /*bitmap = BitmapFactory.decodeResource(mContext.getResources()
                            ,R.mipmap.statistics_month_numberofpeople);*/
                    canvas.drawBitmap(bitmap,xShift,yShift,markPaint);
                    yShift += (bitmap.getHeight()+dp2px(6)+markTextBound.height()/2);
                    canvas.drawText(marks[1],xShift+markTextBound.width()/2,yShift,titlePaint);
                } else {
                    bitmap = BitmapFactory.decodeResource(mResources
                            ,R.mipmap.statistics_month_numberofpeople);
                    canvas.drawBitmap(bitmap,xShift,yShift,markPaint);
                    xShift += (bitmap.getWidth()+dp2px(3)+markTextBound.width()/2);
                    canvas.drawText(marks[0],xShift,bitmap.getHeight()/2+yShift
                            +markTextBound.height()/2,titlePaint);
                }
                break;
            case YEAR:
                if(isCompare) {
                    bitmap = BitmapFactory.decodeResource(mResources
                            ,R.mipmap.bar_graph_of_theyear);
                    canvas.drawBitmap(bitmap,xShift,yShift,markPaint);
                    yShift += (bitmap.getHeight()+dp2px(6)+markTextBound.height()/2);
                    canvas.drawText(marks[0],xShift+markTextBound.width()/2,yShift,titlePaint);

                    yShift += (markTextBound.height()/2+dp2px(12));
                    bitmap = BitmapFactory.decodeResource(mResources
                            ,R.mipmap.ar_graph_of_otheryear);
                    canvas.drawBitmap(bitmap,xShift,yShift,markPaint);
                    yShift += (bitmap.getHeight()+dp2px(6)+markTextBound.height()/2);
                    canvas.drawText(marks[1],xShift+markTextBound.width()/2,yShift,titlePaint);
                } else {
                    bitmap = BitmapFactory.decodeResource(mResources
                            ,R.mipmap.statistics_year_numberofpeople);
                    canvas.drawBitmap(bitmap,xShift,yShift,markPaint);
                    xShift += (bitmap.getWidth()+dp2px(3)+markTextBound.width()/2);
                    canvas.drawText(marks[0],xShift,yShift+bitmap.getHeight()/2+markTextBound.height()/2,titlePaint);
                }
                break;
        }
        setPaintColor(R.color.colorLimeGray,titlePaint);
        Rect titleRect = getTextBounds(title.toString(),titlePaint);
        canvas.drawText(title.toString(),mWidth/2,titleRect.height(),titlePaint);
    }


    private void generateCurvePoint() {
        inPoints = new ArrayList<>();
        for(int i = 0;i < inFlows.size();i++) {
            BusinessFlow in = inFlows.get(i);
            //Log.d(TAG,"in time:"+in.getTime()+",in num:"+in.getNum());
            inPoints.add(new PointF(in.getTime()*timeInterval+marginL
                    ,mHeight-(in.getNum()*numInterval+marginB)));
        }
        if(isCompare) {
            crossPoints = new ArrayList<>();
            comparePoints = new ArrayList<>();
            for(int i = 0;i < inFlows.size();i++) {
                BusinessFlow compare = compareFlows.get(i);
                //Log.d(TAG,"compare time:"+compare.getTime()+",compare num:"+compare.getNum());
                PointF temp = new PointF(compare.getTime()*timeInterval+marginL
                        ,mHeight-(compare.getNum()*numInterval+marginB));
                comparePoints.add(temp);
                if(compare.getNum() == inFlows.get(i).getNum())
                    crossPoints.add(temp);
            }
        }
    }

    private int dp2px(int value) {
        float v = getContext().getResources().getDisplayMetrics().density;
        return (int) (v * value + 0.5f);
    }

    private int sp2px(int value) {
        float v = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (v * value + 0.5f);
    }

    /**
     * 设置点击事件，是否显示数字
     **/

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        LogUtil.d("click point is ....................x:"+x+",y:"+y);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                for (PointF pointF : inPoints) {
                    LogUtil.d("point (x,y)->("+pointF.x+","+pointF.y+")coming through");
                    if (Math.abs(pointF.x - x) < timeInterval / 2) {
                        LogUtil.d("in loop");
                        clickPoint = pointF;
                        LogUtil.d("find point:(x,y)->("+pointF.x+","+pointF.y+")");
                        showValue = true;
                        if (Looper.getMainLooper() == Looper.myLooper()) {
                            LogUtil.d("redraw in UI thread");
                            invalidate();
                        } else {
                            LogUtil.d("redraw by post in UI thread");
                            postInvalidate();
                        }
                        break;
                    }
                }
                break;
        }
        /*int step = (getWidth() - dp2px(30)) / 8;
        int x = (int) event.getX();
        for (int i = 0; i < 7; i++) {
            if (x > (dp2px(15) + step * (i + 1) - dp2px(15))
                    && x < (dp2px(15) + step * (i + 1) + dp2px(15))) {
                text[i] = 1;
                for (int j = 0; j < 7; j++) {
                    if (i != j) {
                        text[j] = 0;
                    }
                }
                if (Looper.getMainLooper() == Looper.myLooper()) {
                    invalidate();
                } else {
                    postInvalidate();
                }
            }
        }*/
        return super.onTouchEvent(event);
    }

    /**
     * 获取丈量文本的矩形
     *
     * @param text contain content
     * @param paint the paint draw the text
     * @return the rect wrap the text
     */
    private Rect getTextBounds(String text, Paint paint) {
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect;
    }



}
