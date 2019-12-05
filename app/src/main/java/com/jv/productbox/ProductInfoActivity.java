package com.jv.productbox;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.jv.productbox.model.callback.Product;
import com.tbruyelle.rxpermissions2.RxPermissions;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductInfoActivity extends AppCompatActivity {

    public static final String TAG_PRODUCT = "tag_product";
    private Product product;

    @BindView(R.id.tv_name)
    TextView tvProductName;
    @BindView(R.id.tv_barcode)
    TextView tvBarcodee;
    @BindView(R.id.tv_description)
    TextView tvDes;
    @BindView(R.id.recycler_images)
    XRecyclerView listImags;
    @BindView(R.id.tv_user)
    TextView tvUserName;
    @BindView(R.id.tv_date)
    TextView tvDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        ButterKnife.bind(this);

        getSupportActionBar().setTitle("产品详情");

        product = (Product) getIntent().getSerializableExtra(TAG_PRODUCT);

        if (null != product) {
            showInfo();

            RxPermissions permissions = new RxPermissions(this);
            permissions.request(Manifest.permission.INTERNET)
                    .subscribe(granted -> {
                        if (granted) {
                            initImageList();
                        } else {
                            Toast.makeText(this, "加载图片需要网络权限，请检查！", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "数据异常，请返回！", Toast.LENGTH_SHORT).show();
        }
    }

    private void initImageList() {
        listImags.setLoadingMoreEnabled(false);
        listImags.setPullRefreshEnabled(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listImags.setLayoutManager(layoutManager);
        ImageListAdapter mAdapter = new ImageListAdapter(this, product.getImags());
        listImags.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        listImags.setVisibility(View.VISIBLE);
    }

    private void showInfo() {
        tvProductName.setText(product.getName());
        tvBarcodee.setText(product.getBarcode());
        tvDes.setText(product.getDescription());
        tvUserName.setText(product.getUserid());
        tvDate.setText(product.getCreatedate());

        listImags.setVisibility(View.GONE);
    }
}
