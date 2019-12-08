package com.jv.productbox;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.jv.productbox.model.callback.ListProduct;
import com.jv.productbox.model.callback.Product;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.GetRequest;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.recycler_product)
    XRecyclerView listProduct;
    @BindView(R.id.ll_search_result)
    LinearLayout llSearResult;
    @BindView(R.id.tv_search_result)
    TextView tvSearchResult;

    private ProductListAdapter mAdapter;
    private List<Product> products = new ArrayList<>();

    private int searchPageNo = 1;
    private int searchTotal;
    private String searchProductName = null;
    private String searchUserName = null;
    private String searchBeginDate = null;
    private String searchEndDate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        getSupportActionBar().setTitle("产品列表");

        initListView();

        llSearResult.setVisibility(View.GONE);
        llSearResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //清除搜索结果
                searchProductName = "";
                searchUserName = "";
                searchBeginDate = "";
                searchEndDate = "";

                llSearResult.setVisibility(View.GONE);
                listProduct.refresh();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu);

        MenuItem productAdd = menu.findItem(R.id.app_bar_product_add);
        MenuItem userAdd = menu.findItem(R.id.app_bar_admin_add);

        if (1 == App.user.getRoleid()) {
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
                this.startActivityForResult(intent, 1003);
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
                searchPageNo = 1;//刷新将页码标为1

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        products.clear(); //先要清掉数据

                        getProductList(searchPageNo, searchProductName, searchUserName, searchBeginDate, searchEndDate);
                        mAdapter.notifyDataSetChanged();
                        listProduct.refreshComplete(); //下拉刷新完成
                    }
                }, 1000);
            }

            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (products.size() < searchTotal) {
                            searchPageNo++;
                            getProductList(searchPageNo, searchProductName, searchUserName, searchBeginDate, searchEndDate);

                            mAdapter.notifyDataSetChanged();
                            listProduct.setNoMore(true);
                        }
                    }
                }, 1000);
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

        if (App.user != null) {
            App.user = null;
        }

        OkGo.getInstance().cancelTag(this);
    }

    private void getProductList(int pageNo, String productName, String userName, String beginDate, String endDate) {
        try {
            GetRequest request = OkGo.<String>get(Constant.API_GET_PRODUCT)
                    .tag(this)
                    .params("pageno", pageNo);
            if (!TextUtils.isEmpty(productName)) {
                request.params("productname", productName);
            }
            if (!TextUtils.isEmpty(userName)) {
                request.params("userid", userName);
            }
            if (!TextUtils.isEmpty(beginDate)) {
                request.params("begindate", beginDate);
            }
            if (!TextUtils.isEmpty(endDate)) {
                request.params("enddate", endDate);
            }
            request.execute(new StringCallback() {
                @Override
                public void onSuccess(Response<String> response) {
                    ListProduct listProduct = null;
                    try {
                        Gson gson = new Gson();
                        listProduct = gson.fromJson(response.body(), ListProduct.class);
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                    }

                    if (listProduct != null) {
                        searchTotal = listProduct.getTotal();

                        products.addAll(listProduct.getList());
                        mAdapter.notifyDataSetChanged();

                        if (searchTotal == 0){
                            Toast.makeText(MainActivity.this, "产品列表为空，请检查！", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "获取产品列表失败，请稍后重试！", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(Response<String> response) {
                    super.onError(response);

                    Toast.makeText(MainActivity.this, "获取产品列表失败，请稍后重试！", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "网络异常，请稍后重试！", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

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
        } else if (resultCode == 1003) {
            String searchResult = data.getStringExtra(SearchActivity.TAG_SEARCH_RESULT);
            String[] search = searchResult.split(";");

            for (String result : search) {
                String[] condition = result.split(",");
                switch (condition[0]) {
                    case "productname":
                        searchProductName = condition.length == 1 ? "" : condition[1];
                        break;
                    case "userid":
                        searchUserName = condition.length == 1 ? "" : condition[1];
                        break;
                    case "begindate":
                        searchBeginDate = condition[1];
                        break;
                    case "enddate":
                        searchEndDate = condition[1];
                        break;
                    default:
                        break;
                }
            }

            String strName = TextUtils.isEmpty(searchProductName) ? "" : (" 产品名：" + searchProductName);
            String strUser = TextUtils.isEmpty(searchUserName) ? "" : (" 操作人：" + searchUserName);
            tvSearchResult.setText("搜索结果：" + strName + strUser + "从" + searchBeginDate + "到" + searchEndDate);
            llSearResult.setVisibility(View.VISIBLE);

            listProduct.refresh();
        }
    }
}
