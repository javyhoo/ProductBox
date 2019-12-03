package com.jv.productbox;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

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
                account = etAccount.getText().toString().trim();
                password = etPassword.getText().toString().trim();

                if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "账号或密码为空，请检查！", Toast.LENGTH_SHORT).show();
                } else {
                    if ("a".equals(account) && "1".equals(password)) {
                        App.user.setAccount("a");
                        App.user.setRoid(1);

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        LoginActivity.this.startActivity(intent);

                        LoginActivity.this.finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "账号或密码不正确，请检查！", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

}
