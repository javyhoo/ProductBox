package com.jv.productbox;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.jv.productbox.model.callback.Register;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.et_account)
    EditText etAccount;
    @BindView(R.id.et_psw)
    EditText etPsw;
    @BindView(R.id.tv_role)
    TextView tvRole;
    @BindView(R.id.btn_save)
    Button btnSave;

    String roleid = "2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        getSupportActionBar().setTitle("注册账号");

        if (App.user != null && !TextUtils.isEmpty(App.user.getRoleid())) {
            roleid = App.user.getRoleid();
        }

        tvRole.setText("1".equals(roleid) ? "管理员" : "查询人员");

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
    }

    private void saveToServer(String account, String password) {

        OkGo.<String>get(Constant.API_REGISTER)
                .tag(this)
                .params("account", account)
                .params("psw", password)
                .params("roid", roleid)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Gson gson = new Gson();
                        Register register = gson.fromJson(response.body(), Register.class);

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

    }
}