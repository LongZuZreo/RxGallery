package cn.finalteam.rxgalleryfinal.sample;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yalantis.ucrop.model.AspectRatio;

import java.io.File;
import java.util.List;

import cn.finalteam.rxgalleryfinal.RxGalleryFinal;
import cn.finalteam.rxgalleryfinal.RxGalleryFinalApi;
import cn.finalteam.rxgalleryfinal.bean.MediaBean;
import cn.finalteam.rxgalleryfinal.imageloader.GlideImageLoader;
import cn.finalteam.rxgalleryfinal.imageloader.ImageLoaderType;
import cn.finalteam.rxgalleryfinal.rxbus.RxBusResultDisposable;
import cn.finalteam.rxgalleryfinal.rxbus.event.ImageCropResultEvent;
import cn.finalteam.rxgalleryfinal.rxbus.event.ImageMultipleResultEvent;
import cn.finalteam.rxgalleryfinal.rxbus.event.ImageRadioResultEvent;
import cn.finalteam.rxgalleryfinal.sample.imageloader.ImageLoaderActivity;
import cn.finalteam.rxgalleryfinal.ui.RxGalleryListener;
import cn.finalteam.rxgalleryfinal.ui.activity.MediaActivity;
import cn.finalteam.rxgalleryfinal.utils.Logger;
import cn.finalteam.rxgalleryfinal.utils.PermissionCheckUtils;
import io.reactivex.functions.Consumer;

/**
 * 示例
 *
 * @author KARL-dujinyang
 *         <p>
 *         openGallery 返回 void,如果想使用RxGalleryFinal对象，请在 openGallery() 之前返回 RxGalleryFinal 对象
 *         <p>
 *         <p>
 *         RxGalleryFinal radio = RxGalleryFinal
 *         with(MainActivity.this)
 *         image()
 *         radio();
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener , PermissionUtil.RequestPermission {

    RadioButton mRbRadioIMG, mRbMutiIMG, mRbOpenC, mRbRadioVD, mRbMutiVD, mRbCropZD, mRbCropZVD;
    private ImageView ivPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_image_loader).setOnClickListener(this);
        findViewById(R.id.btn_open_def_radio).setOnClickListener(this);
        findViewById(R.id.btn_open_def_multi).setOnClickListener(this);
        findViewById(R.id.btn_open_img).setOnClickListener(this);
        findViewById(R.id.btn_open_vd).setOnClickListener(this);
        findViewById(R.id.btn_open_crop).setOnClickListener(this);
        findViewById(R.id.btn_open_set_path).setOnClickListener(this);
        ivPicture = (ImageView) findViewById(R.id.iv_picture);
        mRbRadioIMG = (RadioButton) findViewById(R.id.rb_radio_img);
        mRbMutiIMG = (RadioButton) findViewById(R.id.rb_muti_img);
        mRbRadioVD = (RadioButton) findViewById(R.id.rb_radio_vd);
        mRbMutiVD = (RadioButton) findViewById(R.id.rb_muti_vd);
        mRbOpenC = (RadioButton) findViewById(R.id.rb_openC);
        mRbCropZD = (RadioButton) findViewById(R.id.rb_radio_crop_z);
        mRbCropZVD = (RadioButton) findViewById(R.id.rb_radio_crop_vz);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_image_loader:
                Intent intent = new Intent(v.getContext(), ImageLoaderActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.btn_open_def_radio:
                openRadio();
                break;
            case R.id.btn_open_def_multi:
                openMulti();
                break;
            case R.id.btn_open_img:
                openImageSelect();
                break;
            case R.id.btn_open_vd:
                openVideoSelect();
                break;
            case R.id.btn_open_crop:
                openCrop();
                break;
            case R.id.btn_open_set_path:
                setPath();
                break;
        }

    }

    /**
     * 设置 照片路径 和 裁剪路径
     */
    private void setPath() {
        RxGalleryFinalApi.setImgSaveRxSDCard("dujinyang");
        RxGalleryFinalApi.setImgSaveRxCropSDCard("dujinyang/crop");//裁剪会自动生成路径；也可以手动设置裁剪的路径；
    }

    /**
     * 直接裁剪  or  拍照并裁剪( 查看 onActivityResult())
     */
    private void openCrop() {
        if (mRbCropZD.isChecked()) {
            //直接裁剪
            String inputImg = "";
            Toast.makeText(MainActivity.this, "没有图片演示，请选择‘拍照裁剪’功能", Toast.LENGTH_SHORT).show();
            //  RxGalleryFinalApi.cropScannerForResult(MainActivity.this, RxGalleryFinalApi.getModelPath(), inputImg);//调用裁剪.RxGalleryFinalApi.getModelPath()为模拟的输出路径
        } else {
            //            RxGalleryFinalApi.openZKCamera(MainActivity.this);

            PermissionUtil
                    .requestPermission(this,new RxPermissions(this),Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    /**
     * 视频
     * 单选 多选
     */
    private void openVideoSelect() {
        if (mRbRadioVD.isChecked()) {
            openVideoSelectRadioMethod();
        } else if (mRbMutiVD.isChecked()) {
            openVideoSelectMultiMethod(0);
        }
    }

    /**
     * 图片
     * 单选，多选，  直接打开相机
     */
    private void openImageSelect() {
        if (mRbRadioIMG.isChecked()) {
            openImageSelectRadioMethod(3);
        } else if (mRbMutiIMG.isChecked()) {
            openImageSelectMultiMethod(1);
        } else {
            if (PermissionCheckUtils.checkCameraPermission(this, "", MediaActivity.REQUEST_CAMERA_ACCESS_PERMISSION)) {
                RxGalleryFinalApi.openZKCamera(MainActivity.this);
            }
        }
    }

    private List<MediaBean> list = null;

    /**
     * 自定义多选
     */
    private void openMulti() {
//        RxGalleryFinal.with(this).hidePreview();
        RxGalleryFinal rxGalleryFinal = RxGalleryFinal
                .with(MainActivity.this)
                .image()
                .multiple();
        if (list != null && !list.isEmpty()) {
            rxGalleryFinal
                    .selected(list);
        }
        rxGalleryFinal.maxSize(8)
                .imageLoader(ImageLoaderType.GLIDE)
                .subscribeGalleryListener(new Consumer<ImageMultipleResultEvent>() {


                    @Override
                    public void accept(ImageMultipleResultEvent imageMultipleResultEvent) throws Exception {

                        list = imageMultipleResultEvent.getResult();
                        Toast.makeText(getBaseContext(), "已选择" + imageMultipleResultEvent.getResult().size() + "张图片", Toast.LENGTH_SHORT).show();
                        Toast.makeText(getBaseContext(), "OVER", Toast.LENGTH_SHORT).show();

                    }
                })
                .openGallery();
    }

    /**
     * 自定义单选
     */
    private void openRadio() {
        RxGalleryFinal
                .with(MainActivity.this)
                .image()
                .radio()
                .cropAspectRatioOptions(0, new AspectRatio("裁切头像", 1, 1))
                .crop()
                .imageLoader(ImageLoaderType.GLIDE)
                .subscribeCropListener(new Consumer<ImageCropResultEvent>() {
                    @Override
                    public void accept(ImageCropResultEvent imageCropResultEvent) throws Exception {
                        Glide.with(getApplicationContext()).load(new File(imageCropResultEvent.getCropPath())).into(ivPicture);

                    }
                })
                .subscribeGalleryListener(new Consumer<ImageRadioResultEvent>() {
                    @Override
                    public void accept(ImageRadioResultEvent imageRadioResultEvent) throws Exception {
                        Toast.makeText(getBaseContext(), "选中了图片路径：" + imageRadioResultEvent.getResult().getOriginalPath(), Toast.LENGTH_SHORT).show();
                    }

                })
                .openGallery();
    }

    /**
     * 视频多选回调
     */
    private void openVideoSelectMultiMethod(int type) {
        switch (type) {
            case 0:

                //使用默认的参数
                RxGalleryFinalApi
                        .getInstance(this)
                        .setVDMultipleResultEvent(
                                new Consumer<ImageMultipleResultEvent>() {
                                    @Override
                                    public void accept(ImageMultipleResultEvent imageMultipleResultEvent) throws Exception {
                                        Logger.i("多选视频的回调");
                                    }

                                }).open();

                break;
            case 1:

                //使用自定义的参数
                RxGalleryFinalApi
                        .getInstance(this)
                        .setType(RxGalleryFinalApi.SelectRXType.TYPE_VIDEO, RxGalleryFinalApi.SelectRXType.TYPE_SELECT_MULTI)
                        .setVDMultipleResultEvent(
                                new Consumer<ImageMultipleResultEvent>() {
                                    @Override
                                    public void accept(ImageMultipleResultEvent imageMultipleResultEvent) throws Exception {
                                        Logger.i("多选视频的回调");

                                    }
                                }).open();

                break;
            case 2:

                //直接打开
                RxGalleryFinalApi
                        .openMultiSelectVD(this, new Consumer<ImageMultipleResultEvent>() {
                            @Override
                            public void accept(ImageMultipleResultEvent imageMultipleResultEvent) throws Exception {
                                Logger.i("多选视频的回调");

                            }
                        });

                break;
        }
    }

    /**
     * 视频单选回调
     */
    private void openVideoSelectRadioMethod() {
        RxGalleryFinalApi
                .getInstance(MainActivity.this)
                .setType(RxGalleryFinalApi.SelectRXType.TYPE_VIDEO, RxGalleryFinalApi.SelectRXType.TYPE_SELECT_RADIO)
                .setVDRadioResultEvent(new Consumer<ImageRadioResultEvent>() {
                    @Override
                    public void accept(ImageRadioResultEvent imageRadioResultEvent) throws Exception {
                        Toast.makeText(getApplicationContext(), imageRadioResultEvent.getResult().getOriginalPath(), Toast.LENGTH_SHORT).show();
                    }
                })
                .open();
    }

    /**
     * OPEN 图片多选实现方法
     * <p>
     * 默认使用 第三个 ，如果运行sample,可自行改变Type，运行Demo查看效果
     */
    private void openImageSelectMultiMethod(int type) {
        switch (type) {
            case 0:

                //使用默认的参数
                RxGalleryFinalApi
                        .getInstance(MainActivity.this)
                        .setImageMultipleResultEvent(
                                new Consumer<ImageMultipleResultEvent>() {
                                    @Override
                                    public void accept(ImageMultipleResultEvent imageMultipleResultEvent) throws Exception {
                                        Logger.i("多选图片的回调");

                                    }

                                }).open();

                break;
            case 1:

                //使用自定义的参数
                RxGalleryFinalApi
                        .getInstance(MainActivity.this)
                        .setType(RxGalleryFinalApi.SelectRXType.TYPE_IMAGE, RxGalleryFinalApi.SelectRXType.TYPE_SELECT_MULTI)
                        .setImageMultipleResultEvent(new Consumer<ImageMultipleResultEvent>() {
                            @Override
                            public void accept(ImageMultipleResultEvent imageMultipleResultEvent) throws Exception {
                                Logger.i("多选图片的回调");

                            }

                        }).open();

                break;
            case 2:

                //直接打开
                RxGalleryFinalApi.openMultiSelectImage(this, new Consumer<ImageMultipleResultEvent>() {
                    @Override
                    public void accept(ImageMultipleResultEvent imageMultipleResultEvent) throws Exception {
                        Logger.i("多选图片的回调");
                    }


                });

                break;
        }

    }

    /**
     * OPEN 图片单选实现方法
     * <p>
     * 默认使用 第三个 ，如果运行sample,可自行改变Type，运行Demo查看效果
     */
    private void openImageSelectRadioMethod(int type) {
        RxGalleryFinalApi instance = RxGalleryFinalApi.getInstance(MainActivity.this);
        switch (type) {
            case 0:

                //打开单选图片，默认参数
                instance
                        .setImageRadioResultEvent(new Consumer<ImageRadioResultEvent>() {
                            @Override
                            public void accept(ImageRadioResultEvent imageRadioResultEvent) throws Exception {
                                Logger.i("单选图片的回调");

                            }

                        }).open();

                break;
            case 1:

                //设置自定义的参数
                instance
                        .setType(RxGalleryFinalApi.SelectRXType.TYPE_IMAGE, RxGalleryFinalApi.SelectRXType.TYPE_SELECT_RADIO)
                        .setImageRadioResultEvent(new Consumer<ImageRadioResultEvent>() {
                            @Override
                            public void accept(ImageRadioResultEvent imageRadioResultEvent) throws Exception {
                                Logger.i("单选图片的回调");

                            }


                        }).open();

                break;
            case 2:

                //快速打开单选图片,flag使用true不裁剪
                RxGalleryFinalApi
                        .openRadioSelectImage(MainActivity.this, new Consumer<ImageRadioResultEvent>() {
                            @Override
                            public void accept(ImageRadioResultEvent imageRadioResultEvent) throws Exception {
                                Logger.i("单选图片的回调");
                            }
                        }, true);

                break;
            case 3:

                //单选，使用RxGalleryFinal默认设置，并且带有裁剪
                instance
                        .openGalleryRadioImgDefault(
                                new Consumer<ImageRadioResultEvent>() {
                                    @Override
                                    public void accept(ImageRadioResultEvent imageRadioResultEvent) throws Exception {
                                        Logger.i("只要选择图片就会触发");
                                    }
                                })
                        .onCropImageResult(
                                new Consumer<ImageCropResultEvent>() {
                                    @Override
                                    public void accept(ImageCropResultEvent imageCropResultEvent) throws Exception {
                                        Toast.makeText(getBaseContext(), "选中了图片路径：" + imageCropResultEvent.getCropPath(), Toast.LENGTH_SHORT).show();

                                    }
                                });

                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SimpleRxGalleryFinal.get().onActivityResult(requestCode, resultCode, data);
//        if (requestCode == RxGalleryFinalApi.TAKE_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
//            Logger.i("拍照OK，图片路径:" + RxGalleryFinalApi.fileImagePath.getPath());
//            //刷新相册数据库
//            RxGalleryFinalApi.openZKCameraForResult(MainActivity.this, new MediaScanner.ScanCallback() {
//                @Override
//                public void onScanCompleted(String[] strings) {
//                    Logger.i(String.format("拍照成功,图片存储路径:%s", strings[0]));
//                    Logger.d("演示拍照后进行图片裁剪，根据实际开发需求可去掉上面的判断");
//                    RxGalleryFinalApi.cropScannerForResult(MainActivity.this, RxGalleryFinalApi.getModelPath(), strings[0]);//调用裁剪.RxGalleryFinalApi.getModelPath()为默认的输出路径
//                }
//            });
//        } else {
//            Logger.i("失敗");
//        }
    }

    @Override
    public void onRequestPermissionSuccess() {
        SimpleRxGalleryFinal.get().init(
                new SimpleRxGalleryFinal.RxGalleryFinalCropListener() {
                    @NonNull
                    @Override
                    public Activity getSimpleActivity() {
                        return MainActivity.this;
                    }

                    @Override
                    public void onCropCancel() {
                        Toast.makeText(getSimpleActivity(), "裁剪被取消", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCropSuccess(@Nullable Uri uri) {
                        Toast.makeText(getSimpleActivity(), "裁剪成功：" + uri, Toast.LENGTH_SHORT).show();
                        Glide.with(MainActivity.this)
                                .load(uri)
                                .into(ivPicture);

                    }

                    @Override
                    public void onCropError(@NonNull String errorMessage) {
                        Toast.makeText(getSimpleActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
        ).openCamera();
    }

    @Override
    public void onRequestPermissionFailure(List<String> permissions) {

    }

    @Override
    public void onRequestPermissionFailureWithAskNeverAgain(List<String> permissions) {

    }
}