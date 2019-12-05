package com.jv.productbox;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.jv.productbox.model.callback.ListName;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity {

    private String fakeProdut = "{\"namelist\":[\"我是测试\",\"我是测试10\",\"我是测试11\",\"我是测试12\",\"我是测试2\",\"我是测试3\",\"我是测试4\",\"我是测试5\",\"我是测试6\",\"我是测试7\",\"我是测试8\",\"我是测试9\"]}";
    private String fakeUser = "{\"namelist\":[\"18613157068\",\"chenchang\"]}";

    @BindView(R.id.spinner_product)
    Spinner sProduct;
    @BindView(R.id.spinner_user)
    Spinner sUser;
    @BindView(R.id.tv_begin_date)
    TextView tvBeginDate;
    @BindView(R.id.tv_end_date)
    TextView tvEndDate;
    @BindView(R.id.btn_search)
    Button btnSearch;

    public static final String TAG_SEARCH_RESULT = "tag_search_result";

    private RxPermissions permissions;
    private List<String> productList = new ArrayList<>();
    private List<String> userList = new ArrayList<>();
    private int mYear, mMonth, mDay;
    private String selectProduct, selectUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        getSupportActionBar().setTitle("产品搜索");
        permissions = new RxPermissions(this);

        loadData();
        initSpinner();

        sProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectProduct = productList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sUser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectUser = userList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        tvBeginDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker(tvBeginDate);
            }
        });

        tvEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker(tvEndDate);
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String beginDate = tvBeginDate.getText().toString();
                String endDate = tvEndDate.getText().toString();

                String result = "productname:" + selectProduct + ";userid:" + selectUser
                        + ";begindate:" + beginDate + ";enddate:" + endDate;

                Intent intent = new Intent();
                intent.putExtra(TAG_SEARCH_RESULT, result);
                setResult(RESULT_OK, intent);

                SearchActivity.this.finish();
            }
        });
    }

    private void loadData() {
        productList.clear();
        userList.clear();

        permissions.request(Manifest.permission.INTERNET)
                .subscribe(granted -> {
                    if (granted) {
                        try {
                            OkGo.<String>get(Constant.API_GET_PRODUCT_NAME)
                                    .tag("productName")
                                    .execute(new StringCallback() {
                                        @Override
                                        public void onSuccess(Response<String> response) {
                                            Gson gson = new Gson();
                                            ListName names = gson.fromJson(response.body(), ListName.class);

                                            if (null != names) {
                                                productList.addAll(names.getNamelist());
                                            } else {
                                                Toast.makeText(SearchActivity.this, "数据异常，请稍后重试！", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onError(Response<String> response) {
                                            super.onError(response);

                                            Toast.makeText(SearchActivity.this, "数据加载失败，请稍后重试！", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } catch (Exception e) {
                            Toast.makeText(SearchActivity.this, "网络异常，请稍后重试！", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                        try {
                            OkGo.<String>get(Constant.API_GET_PRODUCT_USER)
                                    .tag("productUser")
                                    .execute(new StringCallback() {
                                        @Override
                                        public void onSuccess(Response<String> response) {
                                            Gson gson = new Gson();
                                            ListName names = gson.fromJson(response.body(), ListName.class);

                                            if (null != names) {
                                                userList.addAll(names.getNamelist());
                                            } else {
                                                Toast.makeText(SearchActivity.this, "数据异常，请稍后重试！", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onError(Response<String> response) {
                                            super.onError(response);

                                            Toast.makeText(SearchActivity.this, "数据加载失败，请稍后重试！", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } catch (Exception e) {
                            Toast.makeText(SearchActivity.this, "网络异常，请稍后重试！", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                    } else {
                        Toast.makeText(SearchActivity.this, "请开启网络权限！", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void initSpinner() {
        ArrayAdapter<String> productAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, productList);
        sProduct.setAdapter(productAdapter);

        ArrayAdapter<String> userAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, userList);
        sUser.setAdapter(userAdapter);
    }

    private void datePicker(TextView textView) {
        Calendar ca = Calendar.getInstance();
        mYear = ca.get(Calendar.YEAR);
        mMonth = ca.get(Calendar.MONTH);
        mDay = ca.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(SearchActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String days;
                if (month + 1 < 10) {
                    if (dayOfMonth < 10) {
                        days = new StringBuffer().append(year).append("-").append("0").
                                append(month + 1).append("-").append("0").append(dayOfMonth).toString();
                    } else {
                        days = new StringBuffer().append(year).append("-").append("0").
                                append(month + 1).append("-").append(dayOfMonth).toString();
                    }
                } else {
                    if (dayOfMonth < 10) {
                        days = new StringBuffer().append(year).append("-").
                                append(month + 1).append("-").append("0").append(dayOfMonth).toString();
                    } else {
                        days = new StringBuffer().append(year).append("-").
                                append(month + 1).append("-").append(dayOfMonth).toString();
                    }
                }
                textView.setText(days);
            }
        }, mYear, mMonth, mDay).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        OkGo.getInstance().cancelTag("productName");
        OkGo.getInstance().cancelTag("productUser");
    }
}
