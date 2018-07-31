package com.person.legend.statisticsdiagram.util;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;

import java.util.List;

public final class DrawUtil {

    private DrawUtil() {}
        private static Path path = new Path();



    public static Path preparePoints(List<PointF> pointFList) {
        path = new Path();
        path.moveTo(pointFList.get(0).x, pointFList.get(0).y);

        for (int i = 1; i < pointFList.size() - 1; i++) {
            PointF ctrlPointA = new PointF();
            PointF ctrlPointB = new PointF();
            getCtrlPoint(pointFList, i, ctrlPointA, ctrlPointB);
            path.cubicTo(ctrlPointA.x, ctrlPointA.y, ctrlPointB.x, ctrlPointB.y,
                    pointFList.get(i + 1).x, pointFList.get(i + 1).y);
        }
        return path;
    }

    public static void drawPoints(Canvas canvas, int color,Paint paint) {
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(4);
        canvas.drawPath(path, paint);
    }



    private static final float CTRL_VALUE_A = 0.2f;
    private static final float CTRL_VALUE_B = 0.2f;

    /**
     * 根据已知点获取第i个控制点的坐标
     * @param pointFList
     * @param currentIndex
     * @param ctrlPointA
     * @param ctrlPointB
     */
    private static void getCtrlPoint(List<PointF> pointFList, int currentIndex,
                              PointF ctrlPointA, PointF ctrlPointB) {
        if(currentIndex + 1 >= pointFList.size())
            currentIndex -= 1;
        ctrlPointA.x = pointFList.get(currentIndex).x +
                (pointFList.get(currentIndex + 1).x - pointFList.get(currentIndex - 1).x) * CTRL_VALUE_A;
        ctrlPointA.y = pointFList.get(currentIndex).y +
                (pointFList.get(currentIndex + 1).y - pointFList.get(currentIndex - 1).y) * CTRL_VALUE_A;
        if(currentIndex +2 >= pointFList.size())
            currentIndex -= 2;
        ctrlPointB.x = pointFList.get(currentIndex + 1).x -
                (pointFList.get(currentIndex + 2).x - pointFList.get(currentIndex).x) * CTRL_VALUE_B;
        ctrlPointB.y = pointFList.get(currentIndex + 1).y -
                (pointFList.get(currentIndex + 2).y - pointFList.get(currentIndex).y) * CTRL_VALUE_B;
    }

    /**
     * 绘制穿过多边形顶点的平滑曲线
     * 用三阶贝塞尔曲线实现
     * @param canvas 画布
     * @param points 多边形的顶点
     * @param k 控制点系数，系数越小，曲线越锐利
     * @param color 线条颜色
     */
    public static void drawCurvesFromPoints(Canvas canvas, List<Point> points, double k, int color, Paint paint) {
        int size = points.size();

        // 计算中点
        Point[] midPoints = new Point[size];
        for (int i = 0; i < size; i++) {
            Point p1 = points.get(i);
            Point p2 = points.get((i + 1) % size);
            midPoints[i] = new Point((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
        }

        // 计算比例点
        Point[] ratioPoints = new Point[size];
        for (int i = 0; i < size; i++) {
            Point p1 = points.get(i);
            Point p2 = points.get((i + 1) % size);
            Point p3 = points.get((i + 2) % size);
            double l1 = distance(p1, p2);
            double l2 = distance(p2, p3);
            double ratio = l1 / (l1 + l2);
            Point mp1 = midPoints[i];
            Point mp2 = midPoints[(i + 1) % size];
            ratioPoints[i] = ratioPointConvert(mp2, mp1, ratio);
        }

        // 移动线段，计算控制点
        Point[] controlPoints = new Point[size * 2];
        for (int i = 0, j = 0; i < size; i++) {
            Point ratioPoint = ratioPoints[i];
            Point verPoint = points.get((i + 1) % size);
            int dx = ratioPoint.x - verPoint.x;
            int dy = ratioPoint.y - verPoint.y;
            Point controlPoint1 = new Point(midPoints[i].x - dx, midPoints[i].y - dy);
            Point controlPoint2 = new Point(midPoints[(i + 1) % size].x - dx, midPoints[(i + 1) % size].y - dy);
            controlPoints[j++] = ratioPointConvert(controlPoint1, verPoint, k);
            controlPoints[j++] = ratioPointConvert(controlPoint2, verPoint, k);
        }

        // 用三阶贝塞尔曲线连接顶点
        Path path = new Path();
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        for (int i = 0; i < size; i++) {
            Point startPoint = points.get(i);
            Point endPoint = points.get((i + 1) % size);
            Point controlPoint1 = controlPoints[(i * 2 + controlPoints.length - 1) % controlPoints.length];
            Point controlPoint2 = controlPoints[(i * 2) % controlPoints.length];
            //path.reset();
            path.moveTo(startPoint.x, startPoint.y);
            path.cubicTo(controlPoint1.x, controlPoint1.y, controlPoint2.x, controlPoint2.y, endPoint.x, endPoint.y);
            canvas.drawPath(path, paint);
        }
    }

    /**
     * 计算两点之间的距离
     */
    private static double distance(Point p1, Point p2) {
        return Math.sqrt(((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y)));
    }

    /**
     * 比例点转换
     */
    private static Point ratioPointConvert(Point p1, Point p2, double ratio) {
        Point p = new Point();
        p.x = (int) (ratio * (p1.x - p2.x) + p2.x);
        p.y = (int) (ratio * (p1.y - p2.y) + p2.y);
        return p;
    }
}
