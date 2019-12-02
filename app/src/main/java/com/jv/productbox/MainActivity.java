package com.jv.productbox;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.recycler_product)
    XRecyclerView listProduct;
    @BindView(R.id.fab_new)
    FloatingActionButton fabNew;

    private ProductListAdapter mAdapter;
    private List<Product> products = new ArrayList<>();
    private int times = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        getSupportActionBar().setTitle("产品列表");

        fabNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        initListView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, SearchActivity.class);
        this.startActivity(intent);

        return super.onOptionsItemSelected(item);
    }

    private void initListView() {
        listProduct.setPullRefreshEnabled(true);
        listProduct.setLoadingMoreEnabled(true);
        listProduct.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        listProduct.setLoadingMoreProgressStyle(ProgressStyle.BallClipRotate);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listProduct.setLayoutManager(layoutManager);

        mAdapter = new ProductListAdapter(this, products);
        listProduct.setAdapter(mAdapter);

        listProduct.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                times = 0;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        products.clear(); //先要清掉数据

                        List<Product> list = genFakeDate();
                        products.addAll(list); //再将数据插入到前面

                        mAdapter.notifyDataSetChanged();

                        listProduct.refreshComplete(); //下拉刷新完成
                        Toast.makeText(MainActivity.this, "刷新完成，新加" + list.size() + "件产品", Toast.LENGTH_SHORT).show();
                    }
                }, 1000);
            }

            @Override
            public void onLoadMore() {
                if (times < 20) {//加载20次后，就不再加载更多
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            List<Product> list = genFakeDate();
                            products.addAll(list); //直接将数据追加到后面
                            Toast.makeText(MainActivity.this, "加载完成，新加" + list.size() + "件产品", Toast.LENGTH_SHORT).show();

                            listProduct.loadMoreComplete();
                            mAdapter.notifyDataSetChanged();
                        }
                    }, 1000);
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            List<Product> list = genFakeDate();
                            products.addAll(list); //将数据追加到后面

                            mAdapter.notifyDataSetChanged();
                            listProduct.setNoMore(true);
                        }
                    }, 1000);
                }
                times++;
            }
        });

        listProduct.refresh();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (listProduct != null) {
            listProduct.destroy();
            listProduct = null;
        }
    }

    private List<Product> genFakeDate() {
        ArrayList<Product> data = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Product product = new Product();
            product.setUser("李三" + i);
            product.setProductname("红牛红牛" + i);
            product.setDate("2019-11-30");
            data.add(product);
        }

        return data;
    }
}
