package com.jv.productbox;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.jv.productbox.model.callback.AddProduct;
import com.jv.productbox.model.callback.ImageUrl;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.broadcast.BroadcastAction;
import com.luck.picture.lib.broadcast.BroadcastManager;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.decoration.GridSpacingItemNotBothDecoration;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.permissions.PermissionChecker;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.luck.picture.lib.tools.ScreenUtils;
import com.luck.picture.lib.tools.ToastUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okgo.request.base.Request;
import com.skateboard.zxinglib.CaptureActivity;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
    @BindView(R.id.recycler_images)
    RecyclerView listImages;
    @BindView(R.id.btn_add)
    Button btnAdd;

    private List<LocalMedia> selectList = new ArrayList<>();
    private GridImageAdapter adapter;
    private int maxSelectNum = 5;
    private RxPermissions rxPermissions;
    private List<String> imageUrl = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        getSupportActionBar().setTitle("新增产品");
        ButterKnife.bind(this);

        rxPermissions = new RxPermissions(this);

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

        FullyGridLayoutManager manager = new FullyGridLayoutManager(this,
                4, GridLayoutManager.VERTICAL, false);
        listImages.setLayoutManager(manager);
        listImages.addItemDecoration(new GridSpacingItemNotBothDecoration(4,
                ScreenUtils.dip2px(this, 8), true, false));
        adapter = new GridImageAdapter(this, onAddPicClickListener);
        adapter.setList(selectList);
        adapter.setSelectMax(maxSelectNum);
        listImages.setAdapter(adapter);

        adapter.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
//                PictureSelector.create(AddActivity.this)
//                        //.themeStyle(themeId) // xml设置主题
////                        .setPictureStyle(mPictureParameterStyle)// 动态自定义相册主题
//                        //.setPictureWindowAnimationStyle(animationStyle)// 自定义页面启动动画
////                        .isNotPreviewDownload(true)// 预览图片长按是否可以下载
//                        .loadImageEngine(GlideEngine.createGlideEngine())// 外部传入图片加载引擎，必传项
//                        .openExternalPreview(position, selectList);

            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etName.getText().toString())) {
                    Toast.makeText(AddActivity.this, "请输入产品名称！", Toast.LENGTH_SHORT).show();
                } else {
//                    saveProductToServer();
                    uploadFile();
                }
            }
        });

        // 清空图片缓存，包括裁剪、压缩后的图片 注意:必须要在上传完成后调用 必须要获取权限
        if (PermissionChecker.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            PictureFileUtils.deleteAllCacheDirFile(this);
        } else {
            PermissionChecker.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PictureConfig.APPLY_STORAGE_PERMISSIONS_CODE);
        }

        // 注册外部预览图片删除按钮回调
        BroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                BroadcastAction.ACTION_DELETE_PREVIEW_POSITION);

    }

//    private void saveProductToServer() {
//        Product product = new Product();
//        product.setProductname(etName.getText().toString());
//        product.setBarcode(tvBarcode.getText().toString());
//        product.setDescription(etDes.getText().toString());
//
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        product.setDate(df.format(new Date()));
//
//        product.setUser(App.user.getAccount());
//
//        List<String> uris = new ArrayList<>();
//        for (int i = 0; i < selectList.size(); i++) {
//            LocalMedia media = selectList.get(i);
//            if (media.isCompressed()) {
//                uris.add(media.getCompressPath());
//            } else {
//                uris.add(media.getPath());
//            }
//        }
//        product.setImags(uris);
//
//        App.product = product;
//    }

    private void addProduct() {
        GetRequest<String> request = OkGo.<String>get(Constant.API_ADD_PRODUCT)
                .tag("addProduct")
                .params("token", App.user.getToken())
                .params("productname", etName.getText().toString())
                .params("description", etDes.getText().toString())
                .params("barcode", tvBarcode.getText().toString());

        for (int i = 0; i < imageUrl.size(); i++) {
            request.params("imags", imageUrl.get(i));
        }

        try {
            request.execute(new StringCallback() {
                @Override
                public void onSuccess(Response<String> response) {
                    Gson gson = new Gson();
                    AddProduct product = gson.fromJson(response.body(), AddProduct.class);

                    if (null != product) {
                        String statusMsg;
                        switch (product.getStatus()) {
                            case "0":
                                statusMsg = "新增成功";
                                break;
                            case "1":
                                statusMsg = "参数缺失";
                                break;
                            case "2":
                                statusMsg = "该操作需要登陆 ";
                                break;
                            case "3":
                                statusMsg = "该用户角色无权进行该操作";
                                break;
                            default:
                                statusMsg = "新增产品失败，请重试!";
                                break;
                        }

                        Log.d(AddActivity.this.getClass().getSimpleName(), statusMsg);

                        Toast.makeText(AddActivity.this, statusMsg, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AddActivity.this, "新增产品失败，请重试！", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFinish() {
                    super.onFinish();

                    // todo hide dialog: 正在提交数据

                    setResult(1002);
                    AddActivity.this.finish();
                }

                @Override
                public void onError(Response<String> response) {
                    super.onError(response);

                    Toast.makeText(AddActivity.this, "新增产品失败，请重试！", Toast.LENGTH_SHORT).show();
                }

            });
        } catch (Exception e) {
            Toast.makeText(this, "网络异常，请稍后重试！", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void uploadFile() {

        if (imageUrl.size() > 0) {
            imageUrl.clear();
        }

        for (int i = 0; i < selectList.size(); i++) {
            LocalMedia media = selectList.get(i);
            if (media == null) {
                Log.d(this.getClass().getSimpleName(), "图片不存在！");
            } else {
                String path = media.isCompressed() ? media.getCompressPath() : media.getPath();
                File file = new File(path);
                try {
                    OkGo.<String>post(Constant.API_FILE_UPLOAD)
                            .tag("uploadFile")
                            .params("file", file)
                            .execute(new StringCallback() {
                                @Override
                                public void onStart(Request<String, ? extends Request> request) {
                                    super.onStart(request);

                                    // todo show dialog: 正在提交数据
                                }

                                @Override
                                public void onSuccess(Response<String> response) {

                                    Gson gson = new Gson();
                                    ImageUrl image = gson.fromJson(response.body(), ImageUrl.class);

                                    if (null != image && "true".equals(image.getStatus())) {
                                        String url = image.getImgurl();
                                        if (!TextUtils.isEmpty(url)) {
                                            imageUrl.add(url);
                                        }
                                        Log.d(AddActivity.this.getClass().getSimpleName(), url);

                                        if (selectList.size() == imageUrl.size()) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    addProduct();
                                                }
                                            });
                                        } else {
                                            Toast.makeText(AddActivity.this, "图片上传失败，请重试！", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(AddActivity.this, "图片上传失败，请重试！", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void uploadProgress(Progress progress) {
                                    super.uploadProgress(progress);
                                }

                                @Override
                                public void onError(Response<String> response) {
                                    super.onError(response);

                                    Toast.makeText(AddActivity.this, "图片上传失败，请重试！", Toast.LENGTH_SHORT).show();
                                }

                            });
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "网络异常，请稍后重试！", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick() {
            rxPermissions
                    .request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA)
                    .subscribe(granted -> {
                        if (granted) { // Always true pre-M
                            // 进入相册 以下是例子：用不到的api可以不写
                            PictureSelector.create(AddActivity.this)
                                    .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
//                                        .theme()//主题样式(不设置为默认样式) 也可参考demo values/styles下 例如：R.style.picture.white.style
                                    .loadImageEngine(GlideEngine.createGlideEngine())// 外部传入图片加载引擎，必传项   参考Demo MainActivity中代码
                                    .maxSelectNum(5)// 最大图片选择数量 int
                                    .minSelectNum(1)// 最小选择数量 int
                                    .imageSpanCount(3)// 每行显示个数 int
                                    .isNotPreviewDownload(false)// 预览图片长按是否可以下载
//                                        .cameraFileName("test.png") // 重命名拍照文件名、注意这个只在使用相机时可以使用
//                                        .renameCompressFile("test.png")// 重命名压缩文件名、 注意这个不要重复，只适用于单张图压缩使用
//                                        .renameCropFileName("test.png")// 重命名裁剪文件名、 注意这个不要重复，只适用于单张图裁剪使用
//                                        .isSingleDirectReturn(false)// 单选模式下是否直接返回，PictureConfig.SINGLE模式下有效
                                    .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                                    .previewImage(true)// 是否可预览图片 true or false
                                    .isCamera(true)// 是否显示拍照按钮 true or false
                                    .imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
                                    .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                                    .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                                    .setOutputCameraPath("/CustomPath")// 自定义拍照保存路径,可不填
                                    .enableCrop(false)// 是否裁剪 true or false
                                    .compress(true)// 是否压缩 true or false
//                                        .glideOverride()// int glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
//                                        .withAspectRatio()// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                                    .hideBottomControls(true)// 是否显示uCrop工具栏，默认不显示 true or false
                                    .isGif(false)// 是否显示gif图片 true or false
//                                        .compressSavePath(getPath())//压缩图片保存地址
//                                        .freeStyleCropEnabled(true)// 裁剪框是否可拖拽 true or false
//                                        .circleDimmedLayer(false)// 是否圆形裁剪 true or false
//                                        .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
//                                        .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
//                                        .openClickSound(false)// 是否开启点击声音 true or false
                                    .selectionMedia(selectList)// 是否传入已选图片 List<LocalMedia> list
                                    .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
                                    .cutOutQuality(90)// 裁剪输出质量 默认100
                                    .minimumCompressSize(100)// 小于100kb的图片不压缩
                                    .synOrAsy(true)//同步true或异步false 压缩 默认同步
//                                        .cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效 int
//                                        .rotateEnabled() // 裁剪是否可旋转图片 true or false
//                                        .scaleEnabled()// 裁剪是否可放大缩小图片 true or false
//                                        .isDragFrame(false)// 是否可拖动裁剪框(固定)
                                    .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
                        } else {
                            Toast.makeText(AddActivity.this, "请开启权限！", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1001) {
                String result = data.getStringExtra(CaptureActivity.KEY_DATA);
                tvBarcode.setText(result);
            } else if (requestCode == PictureConfig.CHOOSE_REQUEST) {// 图片选择结果回调
                selectList = PictureSelector.obtainMultipleResult(data);

                // 例如 LocalMedia 里面返回三种path
                // 1.media.getPath(); 为原图path
                // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的

                adapter.setList(selectList);
                adapter.notifyDataSetChanged();
            }
        }

    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Bundle extras;
            if (BroadcastAction.ACTION_DELETE_PREVIEW_POSITION.equals(action)) {
                // 外部预览删除按钮回调
                extras = intent.getExtras();
                int position = extras.getInt(PictureConfig.EXTRA_PREVIEW_DELETE_POSITION);
                ToastUtils.s(context, "delete image index:" + position);
                if (position < adapter.getItemCount()) {
                    selectList.remove(position);
                    adapter.notifyItemRemoved(position);
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            BroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver,
                    BroadcastAction.ACTION_DELETE_PREVIEW_POSITION);
        }

        OkGo.getInstance().cancelTag("addProduct");
        OkGo.getInstance().cancelTag("uploadFile");

    }
}
