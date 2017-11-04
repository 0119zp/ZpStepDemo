package zp.com.zpstepdemo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import zp.com.zpstepdemo.CustomerOrderNode;
import zp.com.zpstepdemo.R;

/**
 * Created by Administrator on 2017/11/4 0004.
 */

public class ZpStepViewIndicator extends View {

    //定义默认的高度
    private int defaultStepIndicatorNum = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());

    private float mCompletedLineHeight;//完成线的高度
    private float mCircleRadius;//圆的半径
    private float mUnCompletedCircleRadius;//圆的半径

    private Drawable mCompleteIcon;//完成的默认图片
    private Drawable mAttentionIcon;//正在进行的默认图片
    private Drawable mDefaultIcon;//默认的背景图
    private float mCenterY;//该view的Y轴中间位置
    private float mLeftY;//左上方的Y位置
    private float mRightY;//右下方的位置

    private List<CustomerOrderNode> nodeList;//当前有几部流程
    private int mStepNum = 0;
    private int mCompleteNum = 0;
    private int mUnCompletedNum = 0;
    private float mLinePadding;//两条连线之间的间距

    private List<Float> mCircleCenterPointPositionList;//定义所有圆的圆心点位置的集合
    private Paint mUnCompletedPaint;//未完成Paint
    private Paint mCompletedPaint;//完成paint
    private int mUnCompletedLineColor = Color.parseColor("#f90606");  //定义默认未完成线的颜色
    private int mCompletedLineColor = Color.parseColor("#f90606");   //定义默认完成线的颜色
    private PathEffect mEffects;
    private int mComplectingPosition;//正在进行position


    private Path mPath;

    private OnDrawIndicatorListener mOnDrawListener;
    private int screenWidth;//this screen width

    public ZpStepViewIndicator(Context context) {
        this(context, null);
    }

    public ZpStepViewIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public ZpStepViewIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * 设置监听
     *
     * @param onDrawListener
     */
    public void setOnDrawListener(OnDrawIndicatorListener onDrawListener) {
        mOnDrawListener = onDrawListener;
    }

    /**
     * get圆的半径  get circle radius
     *
     * @return
     */
    public float getCircleRadius() {
        return mCircleRadius;
    }

    /**
     * init
     */
    private void init() {
        nodeList = new ArrayList<>();
        mPath = new Path();
        mEffects = new DashPathEffect(new float[] {8, 8, 8, 8}, 1);

        mCircleCenterPointPositionList = new ArrayList<>();//初始化

        mUnCompletedPaint = new Paint();
        mCompletedPaint = new Paint();
        mUnCompletedPaint.setAntiAlias(true);
        mUnCompletedPaint.setColor(mUnCompletedLineColor);
        mUnCompletedPaint.setStyle(Paint.Style.STROKE);
        mUnCompletedPaint.setStrokeWidth(2);

        mCompletedPaint.setAntiAlias(true);
        mCompletedPaint.setColor(mCompletedLineColor);
        mCompletedPaint.setStyle(Paint.Style.STROKE);
        mCompletedPaint.setStrokeWidth(2);

        //虚线
        mUnCompletedPaint.setPathEffect(mEffects);
        mCompletedPaint.setStyle(Paint.Style.FILL);
//        mUnCompletedPaint.setStyle(Paint.Style.FILL);

        //已经完成线的宽高 set mCompletedLineHeight
        mCompletedLineHeight = 0.025f * defaultStepIndicatorNum;
        //圆的半径  set mCircleRadius
        mCircleRadius = 0.225f * defaultStepIndicatorNum;
        mUnCompletedCircleRadius = 0.125f * defaultStepIndicatorNum;

        //线与线之间的间距    set mLinePadding
        mLinePadding = 1f * defaultStepIndicatorNum * 2;

        mCompleteIcon = ContextCompat.getDrawable(getContext(), R.drawable.bg_shape_circle_completed);//已经完成的icon
        mAttentionIcon = ContextCompat.getDrawable(getContext(), R.drawable.bg_shape_circle_uncompleted);//正在进行的icon
        mDefaultIcon = ContextCompat.getDrawable(getContext(), R.drawable.bg_shape_circle_uncompleted);//未完成的icon
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = defaultStepIndicatorNum * 2;
        if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(widthMeasureSpec)) {
            screenWidth = MeasureSpec.getSize(widthMeasureSpec);
        }
        int height = defaultStepIndicatorNum;
        if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(heightMeasureSpec)) {
            height = Math.min(height, MeasureSpec.getSize(heightMeasureSpec));
        }
//        width = (int) (mStepNum * mCircleRadius * 2 - (mStepNum - 1) * mLinePadding);
        width = (int) (mCompleteNum * mCircleRadius * 2 + mUnCompletedNum * mUnCompletedCircleRadius * 2 - (mStepNum - 1) * mLinePadding);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //获取中间的高度,目的是为了让该view绘制的线和圆在该view垂直居中   get view centerY，keep current stepview center vertical
        mCenterY = 0.5f * getHeight();
        //获取左上方Y的位置，获取该点的意义是为了方便画矩形左上的Y位置
        mLeftY = mCenterY - (mCompletedLineHeight / 2);
        //获取右下方Y的位置，获取该点的意义是为了方便画矩形右下的Y位置
        mRightY = mCenterY + mCompletedLineHeight / 2;

        mCircleCenterPointPositionList.clear();
        //先计算全部最左边的padding值（getWidth()-（圆形直径+两圆之间距离）*2）
        float paddingLeft = (screenWidth - (mCompleteNum * mCircleRadius * 2 + mUnCompletedNum * mUnCompletedCircleRadius * 2) - (mStepNum - 1) * mLinePadding) / 2;
        float eachPaddingLeft = paddingLeft;
        float addPaddingLeft = 0;
        for (int i = 0; i < mStepNum; i++) {
            //add to list
            CustomerOrderNode customerOrderNode = nodeList.get(i);
            if (customerOrderNode != null) {
                if (customerOrderNode.isLight()) {
                    if (i == 0) {
                        addPaddingLeft = mCircleRadius;
                    } else {
                        addPaddingLeft = mCircleRadius * 2 + mLinePadding;
                    }
                } else {
                    if (i == 0) {
                        addPaddingLeft = mUnCompletedCircleRadius;
                    } else {
                        addPaddingLeft = mUnCompletedCircleRadius * 2 + mLinePadding;
                    }
                }
                eachPaddingLeft += addPaddingLeft;
                mCircleCenterPointPositionList.add(eachPaddingLeft);
            }
        }

        if (mOnDrawListener != null) {
            mOnDrawListener.ondrawIndicator();
        }
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mOnDrawListener != null) {
            mOnDrawListener.ondrawIndicator();
        }
        if (mCircleCenterPointPositionList == null || mCircleCenterPointPositionList.size() == 0) {
            return;
        }
        mUnCompletedPaint.setColor(mUnCompletedLineColor);
        mCompletedPaint.setColor(mCompletedLineColor);
//        canvas.drawRect(mCircleCenterPointPositionList.get(0), mLeftY, mCircleCenterPointPositionList.get(mCircleCenterPointPositionList.size() - 1), mRightY, mCompletedPaint);

        Log.e("zpan", "size = " + mCircleCenterPointPositionList.size());
        for (int i = 0; i < mCircleCenterPointPositionList.size() - 1; i++) {
            //前一个ComplectedXPosition
            final float preComplectedXPosition = mCircleCenterPointPositionList.get(i);
            //后一个ComplectedXPosition
            final float afterComplectedXPosition = mCircleCenterPointPositionList.get(i + 1);

            CustomerOrderNode customerOrderNode = nodeList.get(1);
            //判断在完成之前的所有点
            if (customerOrderNode.isLight()) {
                canvas.drawRect(preComplectedXPosition + mCircleRadius - 10, mLeftY, afterComplectedXPosition - mCircleRadius + 10, mRightY, mCompletedPaint);
            } else {
                mPath.moveTo(preComplectedXPosition + mCircleRadius, mCenterY);
                mPath.lineTo(afterComplectedXPosition - mCircleRadius, mCenterY);
                canvas.drawPath(mPath, mUnCompletedPaint);
            }
        }

        //-----------------------画线-------draw line-----------------------------------------------


        //-----------------------画图标-----draw icon-----------------------------------------------
        for (int i = 0; i < mCircleCenterPointPositionList.size(); i++) {
            final float currentComplectedXPosition = mCircleCenterPointPositionList.get(i);

            Rect rect;
            //判断在完成之前的所有点
            if (i <= mComplectingPosition) {
                rect = new Rect((int) (currentComplectedXPosition - mCircleRadius), (int) (mCenterY - mCircleRadius), (int) (currentComplectedXPosition + mCircleRadius), (int) (mCenterY + mCircleRadius));
                mCompleteIcon.setBounds(rect);
                mCompleteIcon.draw(canvas);
            } else {
                rect = new Rect((int) (currentComplectedXPosition - mUnCompletedCircleRadius), (int) (mCenterY - mUnCompletedCircleRadius), (int) (currentComplectedXPosition + mUnCompletedCircleRadius), (int) (mCenterY + mUnCompletedCircleRadius));
                mDefaultIcon.setBounds(rect);
                mDefaultIcon.draw(canvas);
            }
        }
    }

    /**
     * 得到所有圆点所在的位置
     *
     * @return
     */
    public List<Float> getCircleCenterPointPositionList() {
        return mCircleCenterPointPositionList;
    }

    /**
     * 设置流程步数
     */
    public void setStepNum(List<CustomerOrderNode> nodeList) {
        this.nodeList = nodeList;
        mStepNum = nodeList.size();

        if (nodeList != null && nodeList.size() > 0) {
            for (int i = 0; i < mStepNum; i++) {
                CustomerOrderNode customerOrderNode = nodeList.get(i);
                if (customerOrderNode.isLight()) {
                    mComplectingPosition = i;
                    mCompleteNum++;
                } else {
                    mUnCompletedNum++;
                }
            }
        }

        requestLayout();
    }

    /**
     * 设置未完成线的颜色
     *
     * @param unCompletedLineColor
     */
    public void setUnCompletedLineColor(int unCompletedLineColor) {
        this.mUnCompletedLineColor = unCompletedLineColor;
    }

    /**
     * 设置已完成线的颜色
     *
     * @param completedLineColor
     */
    public void setCompletedLineColor(int completedLineColor) {
        this.mCompletedLineColor = completedLineColor;
    }

    /**
     * 设置默认图片
     *
     * @param defaultIcon
     */
    public void setDefaultIcon(Drawable defaultIcon) {
        this.mDefaultIcon = defaultIcon;
    }

    /**
     * 设置已完成图片
     *
     * @param completeIcon
     */
    public void setCompleteIcon(Drawable completeIcon) {
        this.mCompleteIcon = completeIcon;
    }

    /**
     * 设置正在进行中的图片
     *
     * @param attentionIcon
     */
    public void setAttentionIcon(Drawable attentionIcon) {
        this.mAttentionIcon = attentionIcon;
    }

    /**
     * 设置对view监听
     */
    public interface OnDrawIndicatorListener {
        void ondrawIndicator();
    }
}
