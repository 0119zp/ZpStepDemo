package zp.com.zpstepdemo.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import zp.com.zpstepdemo.CustomerOrderNode;
import zp.com.zpstepdemo.R;

/**
 * Created by Administrator on 2017/11/4 0004.
 */

public class ZpStepView extends LinearLayout implements ZpStepViewIndicator.OnDrawIndicatorListener {

    private RelativeLayout mTextContainer;
    private ZpStepViewIndicator mStepsViewIndicator;
    private List<CustomerOrderNode> nodeList;
    private int mComplectingPosition;
    private int mUnComplectedTextColor = Color.parseColor("#878787"); //定义默认未完成文字的颜色;
    private int mComplectedTextColor = Color.parseColor("#878787");  //定义默认完成状态文字的颜色;
    private int mTextSize = 16;     //default textSize
    private int mTextSize2 = 12;        //default textSize
    private TextView mTextView;
    private TextView mTextView2;
    private RelativeLayout mLayout1;
    private RelativeLayout mLayout2;

    public ZpStepView(Context context) {
        this(context, null);
    }

    public ZpStepView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZpStepView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.widget_horizontal_stepsvie, this);
        mStepsViewIndicator = (ZpStepViewIndicator) rootView.findViewById(R.id.steps_indicator);
        mStepsViewIndicator.setOnDrawListener(this);
        mTextContainer = (RelativeLayout) rootView.findViewById(R.id.rl_text_container);
        mLayout1 = ((RelativeLayout) rootView.findViewById(R.id.layout1));
        mLayout2 = ((RelativeLayout) rootView.findViewById(R.id.layout2));
    }

    /**
     * 设置显示的文字
     *
     * @return
     */
    public ZpStepView setStepViewTexts(List<CustomerOrderNode> nodeList) {
        this.nodeList = nodeList;
        if (nodeList == null || nodeList.size() == 0) {
            return null;
        }
        for (int i = 0; i < nodeList.size(); i++) {
            CustomerOrderNode customerOrderNode = nodeList.get(i);
            if (customerOrderNode.isLight()) {
                mComplectingPosition = i;
            }
        }
        mStepsViewIndicator.setStepNum(nodeList);
        return this;
    }

    /**
     * 设置未完成文字的颜色
     *
     * @param unComplectedTextColor
     * @return
     */
    public ZpStepView setStepViewUnComplectedTextColor(int unComplectedTextColor) {
        mUnComplectedTextColor = unComplectedTextColor;
        return this;
    }

    /**
     * 设置完成文字的颜色
     *
     * @param complectedTextColor
     * @return
     */
    public ZpStepView setStepViewComplectedTextColor(int complectedTextColor) {
        this.mComplectedTextColor = complectedTextColor;
        return this;
    }

    /**
     * 设置StepsViewIndicator未完成线的颜色
     *
     * @param unCompletedLineColor
     * @return
     */
    public ZpStepView setStepsViewIndicatorUnCompletedLineColor(int unCompletedLineColor) {
        mStepsViewIndicator.setUnCompletedLineColor(unCompletedLineColor);
        return this;
    }

    /**
     * 设置StepsViewIndicator完成线的颜色
     *
     * @param completedLineColor
     * @return
     */
    public ZpStepView setStepsViewIndicatorCompletedLineColor(int completedLineColor) {
        mStepsViewIndicator.setCompletedLineColor(completedLineColor);
        return this;
    }

    /**
     * 设置StepsViewIndicator默认图片
     *
     * @param defaultIcon
     */
    public ZpStepView setStepsViewIndicatorDefaultIcon(Drawable defaultIcon) {
        mStepsViewIndicator.setDefaultIcon(defaultIcon);
        return this;
    }

    /**
     * 设置StepsViewIndicator已完成图片
     *
     * @param completeIcon
     */
    public ZpStepView setStepsViewIndicatorCompleteIcon(Drawable completeIcon) {
        mStepsViewIndicator.setCompleteIcon(completeIcon);
        return this;
    }

    /**
     * 设置StepsViewIndicator正在进行中的图片
     *
     * @param attentionIcon
     */
    public ZpStepView setStepsViewIndicatorAttentionIcon(Drawable attentionIcon) {
        mStepsViewIndicator.setAttentionIcon(attentionIcon);
        return this;
    }

    /**
     * set textSize
     *
     * @param textSize
     * @return
     */
    public ZpStepView setTextSize(int textSize) {
        if (textSize > 0) {
            mTextSize = textSize;
        }
        return this;
    }

    @Override
    public void ondrawIndicator() {
        if (mTextContainer != null) {
            mLayout1.removeAllViews();
            mLayout2.removeAllViews();
            List<Float> complectedXPosition = mStepsViewIndicator.getCircleCenterPointPositionList();
            if (nodeList != null && complectedXPosition != null && complectedXPosition.size() > 0) {
                for (int i = 0; i < nodeList.size(); i++) {
                    mTextView = new TextView(getContext());
                    mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize);
                    mTextView.setText(nodeList.get(i).getNodeName());
                    int spec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
                    mTextView.measure(spec, spec);

                    int measuredWidth = mTextView.getMeasuredWidth();
                    mTextView.setX(complectedXPosition.get(i) - measuredWidth / 2);
                    if (i <= mComplectingPosition) {
                        mTextView.setTextColor(mComplectedTextColor);
                    } else {
                        mTextView.setTextColor(mUnComplectedTextColor);
                    }


                    mLayout1.addView(mTextView);

                    mTextView2 = new TextView(getContext());
                    mTextView2.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize2);
                    mTextView2.setText(nodeList.get(i).getNodeDesc());
                    int spec2 = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
                    mTextView2.measure(spec2, spec2);
                    // getMeasuredWidth
                    int measuredWidth2 = mTextView2.getMeasuredWidth();
                    mTextView2.setX(complectedXPosition.get(i) - measuredWidth2 / 2);

                    if (i <= mComplectingPosition) {
                        mTextView2.setTextColor(mComplectedTextColor);
                    } else {
                        mTextView2.setTextColor(mUnComplectedTextColor);
                    }
                    mLayout2.addView(mTextView2);
                }
            }
        }
    }
}
