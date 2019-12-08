package com.jv.productbox;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.jv.productbox.model.callback.Register;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.et_account)
    EditText etAccount;
    @BindView(R.id.et_psw)
    EditText etPsw;
    @BindView(R.id.tv_role)
    TextView tvRole;
    @BindView(R.id.btn_save)
    Button btnSave;

    private int roleid = 2;
    private SweetAlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        getSupportActionBar().setTitle("注册账号");

        if (App.user != null) {
            if (App.user.getRoleid() == 1) {
                roleid = 1;
            } else {
                roleid = 2;
            }
        }

        tvRole.setText(roleid == 1 ? "管理员" : "查询人员");

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = etAccount.getText().toString().trim();
                String password = etPsw.getText().toString().trim();

                if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password)) {
                    Toast.makeText(RegisterActivity.this, "账号或密码为空，请检查！", Toast.LENGTH_SHORT).show();
                } else {
                    saveToServer(account, password);
                }
            }
        });

        dialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        dialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        dialog.setTitleText("用户注册中...");
        dialog.setCancelable(true);
    }

    private void saveToServer(String account, String password) {
        try {
            OkGo.<String>get(Constant.API_REGISTER)
                    .tag(this)
                    .params("account", account)
                    .params("psw", password)
                    .params("roid", roleid)
                    .execute(new StringCallback() {

                        @Override
                        public void onStart(Request<String, ? extends Request> request) {
                            super.onStart(request);
                            dialog.show();
                        }

                        @Override
                        public void onFinish() {
                            super.onFinish();
                            dialog.cancel();
                        }

                        @Override
                        public void onSuccess(Response<String> response) {
                            Register register = null;
                            try {
                                Gson gson = new Gson();
                                register = gson.fromJson(response.body(), Register.class);
                            } catch (JsonSyntaxException e) {
                                e.printStackTrace();
                            }

                            if (null == register) {
                                Toast.makeText(RegisterActivity.this, "数据异常，请联系管理员！", Toast.LENGTH_SHORT).show();
                            } else if ("true".equals(register.getResult())) {
                                Toast.makeText(RegisterActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
                                RegisterActivity.this.finish();
                            } else {
                                Toast.makeText(RegisterActivity.this, register.getMsg(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(Response<String> response) {
                            super.onError(response);

                            Toast.makeText(RegisterActivity.this, "网络异常，请稍后重试！", Toast.LENGTH_SHORT).show();
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
        if (dialog != null) {
            dialog = null;
        }
    }
}