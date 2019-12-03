package com.jv.productbox;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.recycler_product)
    XRecyclerView listProduct;

    private ProductListAdapter mAdapter;
    private List<Product> products = new ArrayList<>();
    private int times = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        getSupportActionBar().setTitle("产品列表");

        initListView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu);

        MenuItem productAdd = menu.findItem(R.id.app_bar_product_add);
        MenuItem userAdd = menu.findItem(R.id.app_bar_admin_add);

        if (App.user.getRoid() == 1) {
            productAdd.setVisible(true);
            userAdd.setVisible(true);
        } else {
            productAdd.setVisible(false);
            userAdd.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.app_bar_search:
                Intent intent = new Intent(this, SearchActivity.class);
                this.startActivity(intent);
                break;

            case R.id.app_bar_product_add:
                Intent intent1 = new Intent(this, AddActivity.class);
                this.startActivityForResult(intent1, 1002);
                break;

            case R.id.app_bar_admin_add:
                Intent intent2 = new Intent(this, RegisterActivity.class);
                this.startActivity(intent2);
                break;

            default:
                break;
        }
        return true;

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
            Product product = App.product;
//            product.setUser("李三" + i);
//            product.setProductname("红牛红牛" + i);
//            product.setDate("2019-11-30");
            data.add(product);
        }

        return data;
    }

    long lastTime = System.currentTimeMillis();

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTime < 1000) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
        }

        lastTime = currentTime;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 1002) {
            listProduct.refresh();
        }
    }
}
