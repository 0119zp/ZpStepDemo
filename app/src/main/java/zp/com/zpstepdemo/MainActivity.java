package zp.com.zpstepdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void setHeng(View view){
        Intent intent = new Intent(MainActivity.this, ZpStepActivity.class);
        startActivity(intent);
    }

    public void setShu(View view){
        Intent intent = new Intent(MainActivity.this, ZpStepShuActivity.class);
        startActivity(intent);
    }


}
