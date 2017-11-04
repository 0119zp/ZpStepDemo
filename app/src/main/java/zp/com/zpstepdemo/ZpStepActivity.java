package zp.com.zpstepdemo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import zp.com.zpstepdemo.view.StepLineaLayout;

/**
 * Created by Administrator on 2017/11/4 0004.
 */

public class ZpStepActivity extends Activity {

    private StepLineaLayout stepLineaLayout;
    private ArrayList<CustomerOrderNode> list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_1);

        initData();
        initView();
    }

    private void initView() {
        stepLineaLayout = (StepLineaLayout) findViewById(R.id.underline_layout);

        stepLineaLayout.removeAllViews();
        for (int i = 0 ; i < list.size() ; i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.item_step, stepLineaLayout, false);
            TextView title = (TextView) view.findViewById(R.id.tx_action);
            TextView content = (TextView) view.findViewById(R.id.tx_action_time);

            title.setText(list.get(i).getNodeName());
            content.setText(list.get(i).getNodeDesc());
            stepLineaLayout.addCompleted(view);
        }
    }

    private void initData() {
        list = new ArrayList<>();
        CustomerOrderNode node = new CustomerOrderNode();
        node.setNodeName("接单");
        node.setNodeDesc("接单啦接单啦接单啦接单啦接单啦接单啦接单啦接单啦接单啦");

        CustomerOrderNode node1 = new CustomerOrderNode();
        node1.setNodeName("订单");
        node1.setNodeDesc("订单啦订单啦订单啦订单啦订单啦订单啦订单啦订单啦订单啦");

        CustomerOrderNode node3 = new CustomerOrderNode();
        node3.setNodeName("运输");
        node3.setNodeDesc("运输啦运输啦运输啦运输啦运输啦运输啦运输啦运输啦运输啦");

        CustomerOrderNode node2 = new CustomerOrderNode();
        node2.setNodeName("结束");
        node2.setNodeDesc("结束啦结束啦结束啦结束啦结束啦结束啦结束啦结束啦结束啦");

        list.add(node);
        list.add(node1);
        list.add(node3);
        list.add(node2);
    }

}
