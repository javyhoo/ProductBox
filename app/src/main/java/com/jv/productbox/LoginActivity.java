package com.jv.productbox;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.gson.Gson;
import com.jv.productbox.model.User;
import com.jv.productbox.model.callback.Login;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.tbruyelle.rxpermissions2.RxPermissions;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends FragmentActivity {

    @BindView(R.id.et_acc)
    EditText etAccount;
    @BindView(R.id.et_psw)
    EditText etPassword;
    @BindView(R.id.tv_register)
    TextView tvRegister;
    @BindView(R.id.tv_forgetPsw)
    TextView tvForgetPsw;
    @BindView(R.id.btn_login)
    Button btLogin;

    String account, password;
    SweetAlertDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        RxPermissions permissions = new RxPermissions(this);

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(intent);
            }
        });
        tvForgetPsw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissions.request(Manifest.permission.INTERNET)
                        .subscribe(granted -> {
                            if (granted) {
                                account = etAccount.getText().toString().trim();
                                password = etPassword.getText().toString().trim();

                                if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password)) {
                                    Toast.makeText(LoginActivity.this, "账号或密码为空，请检查！", Toast.LENGTH_SHORT).show();
                                } else {
                                    login(account, password);
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "请开启网络权限！", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("登录中...");
        pDialog.setCancelable(true);
    }

    private void login(String account, String password) {
        try {
            OkGo.<String>get(Constant.API_LOGIN)
                    .tag(this)
                    .params("account", account)
                    .params("psw", password)
                    .execute(new StringCallback() {
                        @Override
                        public void onStart(Request<String, ? extends Request> request) {
                            super.onStart(request);
                            pDialog.show();
                        }

                        @Override
                        public void onFinish() {
                            super.onFinish();
                            pDialog.cancel();
                        }

                        @Override
                        public void onSuccess(Response<String> response) {
                            Gson gson = new Gson();
                            Login login = null;
                            try {
                                login = gson.fromJson(response.body(), Login.class);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(LoginActivity.this, "数据异常，请稍后重试！", Toast.LENGTH_SHORT).show();
                            }

                            if (null == login) {
                                Toast.makeText(LoginActivity.this, "数据异常，请联系管理员！", Toast.LENGTH_SHORT).show();
                            } else if ("true".equals(login.getStatus())) {
                                if (App.user == null) {
                                    App.user = new User();
                                }

                                App.user.setAccount(account);
                                App.user.setRoleid(login.getRoleid());
                                App.user.setToken(login.getToken());

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                LoginActivity.this.startActivity(intent);

                                LoginActivity.this.finish();
                            } else {
                                Toast.makeText(LoginActivity.this, login.getMsg(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(Response<String> response) {
                            super.onError(response);

                            Toast.makeText(LoginActivity.this, "网络异常，请稍后重试！", Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            Toast.makeText(this, "网络异常，请稍后重试！", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        OkGo.getInstance().cancelTag(this);
    }
}
