package com.jv.productbox;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.ButterKnife;

public class ProductInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        ButterKnife.bind(this);

        getSupportActionBar().setTitle("产品详情");
    }
}
