package cn.finalteam.rxgalleryfinal.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import cn.finalteam.rxgalleryfinal.imageloader.rotate.RotateTransformation;
import cn.finalteam.rxgalleryfinal.ui.widget.FixImageView;

/**
 * Created by pengjianbo  Dujinyang on 2016/8/13 0013.
 */
public class GlideImageLoader implements AbsImageLoader {

    @Override
    public void displayImage(Context context, String path, FixImageView imageView, Drawable defaultDrawable, Bitmap.Config config, boolean resize, boolean isGif, int width, int height, int rotate) {
//        DrawableRequestBuilder builder;
//        if (path != null) {
//            builder = Glide.with(context)
//                    .load(new File(path)).
//                    .placeholder(defaultDrawable);
//
//        } else {
//            builder = Glide.with(context)
//                    .load(new File("/sdcard"))
//                    .placeholder(defaultDrawable);
//        }
//        if (resize) {
//            builder = builder.override(width, height);
//        }
//        builder
//                .crossFade()
//                .transform(new RotateTransformation(context, rotate))
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .into(imageView);

        if (isGif) {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(defaultDrawable);
            requestOptions.error(defaultDrawable);
            requestOptions.override(width, height);
            requestOptions.transform(new RotateTransformation(context, rotate));
            requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
            Glide
                    .with(context)
                    .load(path)
                    .transition( DrawableTransitionOptions
                            .withCrossFade())
                    .apply(requestOptions)
                    .into(imageView);
        } else {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(defaultDrawable);
            requestOptions.error(defaultDrawable);
            requestOptions.override(width, height);
            requestOptions.transform(new RotateTransformation(context, rotate));
            requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
            Glide
                    .with(context)
                    .asBitmap()
                    .load(path)
                    .apply(requestOptions)
                    .into(imageView);
        }
    }
}
