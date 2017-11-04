package zp.com.zpstepdemo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.ArrayList;

import zp.com.zpstepdemo.view.ZpStepView;

/**
 * Created by Administrator on 2017/11/4 0004.
 */

public class ZpStepShuActivity extends Activity {

    private ZpStepView stepView;
    private ArrayList<CustomerOrderNode> list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zp_step);

        initData();
        initView();
    }

    private void initData() {
        list = new ArrayList<>();
        CustomerOrderNode node = new CustomerOrderNode();
        node.setNodeName("接单");
        node.setNodeDesc("接单啦");
        node.setLight(true);

        CustomerOrderNode node1 = new CustomerOrderNode();
        node1.setNodeName("订单");
        node1.setNodeDesc("订单啦");
        node1.setLight(true);

        CustomerOrderNode node3 = new CustomerOrderNode();
        node3.setNodeName("运输");
        node3.setNodeDesc("运输啦");
        node3.setLight(false);

        CustomerOrderNode node2 = new CustomerOrderNode();
        node2.setNodeName("结束");
        node2.setNodeDesc("结束啦");
        node2.setLight(false);

        list.add(node);
        list.add(node1);
        list.add(node3);
        list.add(node2);
    }

    private void initView() {
        stepView = (ZpStepView) findViewById(R.id.zp_step);

        stepView.setTextSize(14)
                .setStepViewTexts(list)
                //设置StepsViewIndicator完成线的颜色
                .setStepsViewIndicatorCompletedLineColor(Color.parseColor("#f70101"))
                //设置StepsViewIndicator未完成线的颜色
                .setStepsViewIndicatorUnCompletedLineColor(Color.parseColor("#0712e4"))
                //设置StepsView text完成的颜色
                .setStepViewComplectedTextColor(Color.parseColor("#fc03eb"))
                //设置StepsView text未完成的颜色
                .setStepViewUnComplectedTextColor(Color.parseColor("#0414f4"))
                //设置StepsViewIndicator CompleteIcon
                .setStepsViewIndicatorCompleteIcon(this.getResources().getDrawable(R.drawable.bg_shape_completed))
                //设置StepsViewIndicator DefaultIcon
                .setStepsViewIndicatorDefaultIcon(this.getResources().getDrawable(R.drawable.bg_shape_uncompleted))
                //设置StepsViewIndicator AttentionIcon
                .setStepsViewIndicatorAttentionIcon(this.getResources().getDrawable(R.drawable.bg_shape_uncompleted));

    }
}
