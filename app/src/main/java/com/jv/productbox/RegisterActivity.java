package com.jv.productbox;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        getSupportActionBar().setTitle("注册账号");


        tvRole.setText(App.user.getRoid() == 1 ? "管理员" : "查询人员");

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = etAccount.getText().toString().trim();
                String password = etPsw.getText().toString().trim();

                if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password)) {
                    Toast.makeText(RegisterActivity.this, "账号或密码为空，请检查！", Toast.LENGTH_SHORT).show();
                } else {
                    saveToServer(account, password);

                    finish();
                }
            }
        });
    }

    private void saveToServer(String account, String psw) {
        //todo 2019-11-27 16:27:41
        Toast.makeText(this, "存到服务端", Toast.LENGTH_SHORT).show();
    }
}