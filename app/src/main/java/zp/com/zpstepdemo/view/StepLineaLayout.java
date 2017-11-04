package zp.com.zpstepdemo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import zp.com.zpstepdemo.R;

/**
 * Created by Administrator on 2017/11/4 0004.
 */

public class StepLineaLayout extends LinearLayout {

    //line gravity常量定义
    public static final int GRAVITY_LEFT = 2;
    public static final int GRAVITY_RIGHT = 4;
    public static final int GRAVITY_MIDDLE = 0;
    public static final int GRAVITY_TOP = 1;
    public static final int GRAVITY_BOTTOM = 3;
    public static final int MODE_UNCOMPLETE = 0; //未完成
    public static final int MODE_COMPLETE = 1;//完成
    //点线的类型定
//    private DisplayMode displaymode;
    private List<DisplayMode> modeList = new ArrayList<>();
    //元素定义
    private Bitmap mIcon;
    private Drawable uncompleteicon;
    //line location
    private int lineMarginSide;
    private int lineDynamicDimen;
    //line property
    private int lineStrokeWidth;
    private int lineColor;
    private int completeLineColor;
    private int uncompleteLineColor;
    //point property
    private int pointSize;
    private int pointColor;
    private int completePointColor;
    private int uncompletePointColor;
    //paint
    private Paint linePaint;
    private Paint pointPaint;
    //其他辅助参数
    //第一个点的位置
    private int firstX;
    private int firstY;
    //最后一个图的位置
    private int lastX;
    private int lastY;
    //默认垂直
    private int curOrientation = VERTICAL;
    //line gravity(默认垂直的左边)
    private int lineGravity = GRAVITY_LEFT;
    private Context mContext;
    //开关
    private boolean drawLine = true;
    private int rootLeft;
    private int rootMiddle;
    private int rootRight;
    private int rootTop;
    private int rootBottom;
    //参照点
    private int sideRelative;

    public StepLineaLayout(Context context) {
        this(context, null);
    }

    public StepLineaLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StepLineaLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.VerticalStepView);
        lineMarginSide = attr.getDimensionPixelOffset(R.styleable.VerticalStepView_line_margin_side, 10);
        lineDynamicDimen = attr.getDimensionPixelOffset(R.styleable.VerticalStepView_line_dynamic_dimen, 0);
        lineStrokeWidth = attr.getDimensionPixelOffset(R.styleable.VerticalStepView_line_stroke_width, 2);

        pointSize = attr.getDimensionPixelSize(R.styleable.VerticalStepView_point_size, 8);
        completeLineColor = attr.getColor(R.styleable.VerticalStepView_complete_line_color, 0xff3dd1a5);
        uncompleteLineColor = attr.getColor(R.styleable.VerticalStepView_uncomplete_line_color, 0xff3dd1a5);
        uncompletePointColor = attr.getColor(R.styleable.VerticalStepView_uncomplete_point_color, 0xff3dd1a5);
        completePointColor = attr.getColor(R.styleable.VerticalStepView_complete_point_color, 0xff3dd1a5);

        lineGravity = attr.getInt(R.styleable.VerticalStepView_line_gravity, GRAVITY_LEFT);
        int iconRes = attr.getResourceId(R.styleable.VerticalStepView_icon_src, R.drawable.ic_launcher);

        BitmapDrawable temp = (BitmapDrawable) context.getResources().getDrawable(iconRes);
        if (temp != null) {
            mIcon = temp.getBitmap();
        }
        uncompleteicon = ContextCompat.getDrawable(getContext(), R.drawable.circle_cecece);//未完成的icon //
        curOrientation = getOrientation();
        attr.recycle();
        setWillNotDraw(false);
        initView(context);

    }

    private void initView(Context context) {
        this.mContext = context;
        lineColor = completeLineColor; //默認點線為完成顏色
        pointColor = completePointColor;

        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setDither(true);
        linePaint.setColor(lineColor);
        linePaint.setStrokeWidth(lineStrokeWidth);
        linePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        pointPaint = new Paint();
        pointPaint.setAntiAlias(true);
        pointPaint.setDither(true);
        pointPaint.setColor(pointColor);
        pointPaint.setStyle(Paint.Style.FILL);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        setLineMode();
        calculateSideRelative();
        if (drawLine) {
            drawTimeLine(canvas);
        }
    }

    private void calculateSideRelative() {
        rootLeft = getLeft();
        rootTop = getTop();
        rootRight = getRight();
        rootBottom = getBottom();
        if (curOrientation == VERTICAL) {
            rootMiddle = (rootLeft + rootRight) >> 1;
        }
        if (curOrientation == HORIZONTAL) {
            rootMiddle = (rootTop + rootBottom) >> 1;
        }

        boolean isCorrect = (lineGravity == GRAVITY_MIDDLE || (lineGravity + curOrientation) % 2 != 0);
        if (isCorrect) {
            switch (lineGravity) {
                case GRAVITY_TOP:
                    sideRelative = rootTop;
                    break;
                case GRAVITY_BOTTOM:
                    sideRelative = rootBottom;
                    break;
                case GRAVITY_LEFT:
                    sideRelative = rootLeft;
                    break;
                case GRAVITY_RIGHT:
                    sideRelative = rootRight;
                    break;
                case GRAVITY_MIDDLE:
                    sideRelative = rootMiddle;
                    break;
            }
        } else {
            sideRelative = 0;
        }
    }

    private void drawTimeLine(Canvas canvas) {
        int childCount = getChildCount();

        if (childCount > 0) {
            //大于1，证明至少有2个，也就是第一个和第二个之间连成线，第一个和最后一个分别有点/icon
            if (childCount > 1) {
                switch (curOrientation) {
                    case VERTICAL:
                        drawFirstChildViewVertical(canvas);
                        drawLastChildViewVertical(canvas);
                        drawBetweenLineVertical(canvas);
                        break;
                    case HORIZONTAL:
                        drawFirstChildViewHorizontal(canvas);
                        drawLastChildViewHorizontal(canvas);
                        drawBetweenLineHorizontal(canvas);
                        break;
                    default:
                        break;
                }
            } else if (childCount == 1) {
                switch (curOrientation) {
                    case VERTICAL:
                        drawFirstChildViewVertical(canvas);
                        break;
                    case HORIZONTAL:
                        drawFirstChildViewHorizontal(canvas);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    //===========================================================================判断item 的类型
    private int setModeColor(int position) {
        DisplayMode dis = modeList.get(position);
        switch (dis.displayMode) {
            case MODE_COMPLETE:
//                lineColor = getResources().getColor(R.color.main_blue_6281c2);
//                pointColor = getResources().getColor(R.color.main_blue_6281c2);

                lineColor = completeLineColor;
                pointColor = completePointColor;

                break;
            case MODE_UNCOMPLETE:
//                lineColor = getResources().getColor(R.color.gray_cecece);
//                pointColor = getResources().getColor(R.color.gray_cecece);

                lineColor = uncompleteLineColor;
                pointColor = uncompletePointColor;
                //pointPaint.setColor(pointColor);
                break;
            default:

//                lineColor = attr.getColor(R.styleable.VerticalStepView_line_color, 0xff3dd1a5);
//                pointColor = attr.getDimensionPixelOffset(R.styleable.VerticalStepView_point_color, 0xff3dd1a5);
                break;
        }

        linePaint.setColor(lineColor);
        pointPaint.setColor(pointColor);

//        Log.d("cqy", "画笔颜色改变===>>Color=" + lineColor + "==>>position=" + position);

        return dis.displayMode;
    }

    private void drawItemStepViewVertical(Canvas canvas) {
        if (getChildCount() == 1) {
            drawFirstChildViewVertical(canvas);
        }

        drawLastChildViewVertical(canvas);

    }

    //Vertical Draw

    private void drawFirstChildViewVertical(Canvas canvas) {
        if (getChildAt(0) != null) {
            int top = getChildAt(0).getTop();
            //记录值
            firstX = sideRelative >= rootMiddle ? (sideRelative - lineMarginSide) : (sideRelative + lineMarginSide);
            firstY = top + getChildAt(0).getPaddingTop() + lineDynamicDimen;

            //画前先判断类型
            int mod = setModeColor(0);
            switch (mod) {
                case MODE_COMPLETE: //完成
                    canvas.drawCircle(firstX, firstY, pointSize, pointPaint);
                    break;
                case MODE_UNCOMPLETE://未完成
                    uncompleteicon.setBounds(new Rect((firstX - pointSize), (firstY - pointSize), (firstX + pointSize), (firstY + pointSize)));
                    uncompleteicon.draw(canvas);
                    break;
                default:
                    //画一个圆
                    canvas.drawCircle(firstX, firstY, pointSize, pointPaint);
                    break;
            }

            //画一个圆
//            canvas.drawCircle(firstX, firstY, pointSize, pointPaint);

//            Log.d("cqyline==cf","x="+firstX+"^^^^y="+firstY);
        }
    }

    private void drawLastChildViewVertical(Canvas canvas) {
        if (getChildAt(getChildCount() - 1) != null) {
            int top = getChildAt(getChildCount() - 1).getTop();

            int linefirsy = getChildAt(getChildCount() - 2).getTop() + getChildAt(getChildCount() - 2).getPaddingTop() + lineDynamicDimen;

            //记录值
            lastX = (sideRelative >= rootMiddle ? (sideRelative - lineMarginSide) : (sideRelative + lineMarginSide)) - (mIcon
                    .getWidth() >> 1);
            lastY = top + getChildAt(getChildCount() - 1).getPaddingTop() + lineDynamicDimen;


            //画前先判断类型
            int mod = setModeColor(getChildCount() - 1);
            canvas.drawLine(firstX, linefirsy + pointSize * 2, firstX, lastY - pointSize, linePaint);//两点之间的线
            switch (mod) {
                case MODE_COMPLETE: //完成
                    //canvas.drawCircle(lastX+pointSize, lastY+pointSize,  pointSize, pointPaint);
                    canvas.drawCircle(firstX, lastY + pointSize, pointSize, pointPaint);
                    break;
                case MODE_UNCOMPLETE://未完成
                    uncompleteicon.setBounds(new Rect(lastX, lastY, (lastX + pointSize * 2), (lastY + pointSize * 2)));
                    uncompleteicon.draw(canvas);
                    break;
                default:
                    //画一个圆
                    canvas.drawCircle(lastX, lastY, pointSize, pointPaint);
                    break;
            }

//
//            //画前先判断类型
//            setModeColor(getChildCount() - 1);
//            canvas.drawLine(firstX, linefirsy + pointSize * 2, firstX, lastY - pointSize, linePaint);//两点之间的线
//            //画一个图
//            canvas.drawBitmap(mIcon, lastX, lastY, null);
//            Log.d("cqyline==cl","firstX="+firstX+"^^^^firstY="+firstY);
        }
    }

    private void drawBetweenLineVertical(Canvas canvas) {
        //画剩下的
        //  canvas.drawLine(firstX, firstY, firstX, lastY, linePaint);
//        canvas.drawLine(firstX, firstY+pointSize*2, firstX, lastY-pointSize, linePaint);//线与点之间的间隔点的半径
//        Log.d("cqyline==lm", "firstX=" + firstX + "^^^^firstY=" + firstY + "^^^^lastY=" + lastY);
        for (int i = 0; i < getChildCount() - 1; i++) {
            //画了线，就画圆
            if (getChildAt(i) != null && i != 0) {
                int linefirsy = getChildAt(i - 1).getTop() + getChildAt(i - 1).getPaddingTop() + lineDynamicDimen;
                int top = getChildAt(i).getTop();
                //记录值
                int Y = top + getChildAt(i).getPaddingTop() + lineDynamicDimen;


                //画前先判断类型
                int mod = setModeColor(i);
                canvas.drawLine(firstX, linefirsy + pointSize * 2, firstX, Y - pointSize * 2, linePaint);//两点之间的线
                switch (mod) {
                    case MODE_COMPLETE: //完成
                        canvas.drawCircle(firstX, Y, pointSize, pointPaint);
                        break;
                    case MODE_UNCOMPLETE://未完成
                        uncompleteicon.setBounds(new Rect((firstX - pointSize), (Y - pointSize), (firstX + pointSize), (Y + pointSize)));
                        uncompleteicon.draw(canvas);
                        break;
                    default:
                        //画一个圆
                        canvas.drawCircle(firstX, Y, pointSize, pointPaint);
                        break;
                }

//                //画前先判断类型
//                setModeColor(i);
//                canvas.drawLine(firstX, linefirsy + pointSize * 2, firstX, Y - pointSize * 2, linePaint);//两点之间的线
//                //画一个圆
//                canvas.drawCircle(firstX, Y, pointSize, pointPaint);
////                Log.d("cqyline==cl","firstX="+firstX+"^^^^Y="+Y);
            }
        }
    }

    //=============================================================Horizontal Draw
    private void drawFirstChildViewHorizontal(Canvas canvas) {
        if (getChildAt(0) != null) {
            int left = getChildAt(0).getLeft();
            //记录值
            firstX = left + getChildAt(0).getPaddingLeft() + lineDynamicDimen;
            firstY = sideRelative >= rootMiddle ? (sideRelative - lineMarginSide) : (sideRelative + lineMarginSide);

            //画一个内圆
            canvas.drawCircle(firstX, firstY, pointSize, pointPaint);
        }
    }

    private void drawLastChildViewHorizontal(Canvas canvas) {
        if (getChildAt(getChildCount() - 1) != null) {
            int left = getChildAt(getChildCount() - 1).getLeft();
            //记录值
            lastX = left + getChildAt(getChildCount() - 1).getPaddingLeft() + lineDynamicDimen;
            lastY = (sideRelative >= rootMiddle ? (sideRelative - lineMarginSide) : (sideRelative + lineMarginSide)) - (mIcon
                    .getWidth() >> 1);

            //画一个图
            canvas.drawBitmap(mIcon, lastX, lastY, null);
        }
    }

    private void drawBetweenLineHorizontal(Canvas canvas) {
        //画剩下的线
        canvas.drawLine(firstX, firstY, lastX, firstY, linePaint);
        for (int i = 0; i < getChildCount() - 1; i++) {
            //画了线，就画圆
            if (getChildAt(i) != null && i != 0) {
                int left = getChildAt(i).getLeft();
                //记录值
                int x = left + getChildAt(i).getPaddingLeft() + lineDynamicDimen;

                canvas.drawCircle(x, firstY, pointSize, pointPaint);
            }
        }
    }

    @Override
    public void setOrientation(int orientation) {
        super.setOrientation(orientation);
        this.curOrientation = orientation;
        invalidate();
    }

    //=============================================================Getter/Setter

    public int getLineStrokeWidth() {
        return lineStrokeWidth;
    }

    public void setLineStrokeWidth(int lineStrokeWidth) {
        this.lineStrokeWidth = lineStrokeWidth;
        invalidate();
    }

    public boolean isDrawLine() {
        return drawLine;
    }

    public void setDrawLine(boolean drawLine) {
        this.drawLine = drawLine;
        invalidate();
    }

    public Paint getLinePaint() {
        return linePaint;
    }

    public void setLinePaint(Paint linePaint) {
        this.linePaint = linePaint;
        invalidate();
    }

    public int getPointSize() {
        return pointSize;
    }

    public void setPointSize(int pointSize) {
        this.pointSize = pointSize;
        invalidate();
    }

    public int getPointColor() {
        return pointColor;
    }

    public void setPointColor(int pointColor) {
        this.pointColor = pointColor;
        invalidate();
    }

    public Paint getPointPaint() {
        return pointPaint;
    }

    public void setPointPaint(Paint pointPaint) {
        this.pointPaint = pointPaint;
        invalidate();
    }

    public int getLineColor() {
        return lineColor;
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
        invalidate();
    }

    public int getLineMarginSide() {
        return lineMarginSide;
    }

    public void setLineMarginSide(int lineMarginSide) {
        this.lineMarginSide = lineMarginSide;
        invalidate();
    }

    public int getLineDynamicDimen() {
        return lineDynamicDimen;
    }

    public void setLineDynamicDimen(int lineDynamicDimen) {
        this.lineDynamicDimen = lineDynamicDimen;
        invalidate();
    }

    public Bitmap getIcon() {
        return mIcon;
    }

    public void setIcon(Bitmap icon) {
        mIcon = icon;
    }

    public void setIcon(int resId) {
        if (resId == 0) {
            return;
        }
        BitmapDrawable temp = (BitmapDrawable) mContext.getResources().getDrawable(resId);
        if (temp != null) {
            mIcon = temp.getBitmap();
        }
        invalidate();
    }

    public int getLineGravity() {
        return lineGravity;
    }

    public void setLineGravity(int lineGravity) {
        this.lineGravity = lineGravity;
        invalidate();
    }

    public List<DisplayMode> getDisplayMode() {
        return modeList;
    }

    public StepLineaLayout setDisplayMode(int displaymodevue) {

        DisplayMode dismode = new DisplayMode();
        dismode.displayMode = displaymodevue;
        dismode.displayMode_position = getChildCount();
        modeList.add(dismode);
        this.modeList = modeList;
        return this;

    }

    public StepLineaLayout addCompleted(View v) {
        setDisplayMode(StepLineaLayout.MODE_COMPLETE);
        addView(v);
        return this;
    }

    public StepLineaLayout addUnCompleted(View v) {
        setDisplayMode(StepLineaLayout.MODE_UNCOMPLETE);
        addView(v);
        return this;
    }

    class DisplayMode {
        public int displayMode;
        public int displayMode_position;
    }

}
