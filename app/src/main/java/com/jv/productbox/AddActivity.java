package com.jv.productbox;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.skateboard.zxinglib.CaptureActivity;
import com.tbruyelle.rxpermissions2.RxPermissions;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddActivity extends AppCompatActivity {

    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.tv_barcode)
    TextView tvBarcode;
    @BindView(R.id.ll_barcode)
    LinearLayout llBarcode;
    @BindView(R.id.et_description)
    EditText etDes;
    @BindView(R.id.ll_images)
    LinearLayout llImages;
    @BindView(R.id.btn_add)
    Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        getSupportActionBar().setTitle("新增产品");

        ButterKnife.bind(this);

        final RxPermissions rxPermissions = new RxPermissions(this);

        llBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rxPermissions
                        .request(Manifest.permission.CAMERA)
                        .subscribe(granted -> {
                            if (granted) { // Always true pre-M
                                Intent intent = new Intent(AddActivity.this, CaptureActivity.class);
                                startActivityForResult(intent, 1001);
                            } else {
                                Toast.makeText(AddActivity.this, "扫描条形码需要相机权限，请开启权限！", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        llImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProductToServer();
                AddActivity.this.finish();
            }
        });

    }

    private void saveProductToServer() {
        //todo
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            String result = data.getStringExtra(CaptureActivity.KEY_DATA);
            Toast.makeText(this, "扫描成功： " + result, Toast.LENGTH_SHORT).show();

            tvBarcode.setText(result);
        }
    }
}
