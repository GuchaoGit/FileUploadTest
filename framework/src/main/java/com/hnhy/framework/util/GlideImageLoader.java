package com.hnhy.framework.util;

import android.app.Activity;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.hnhy.framework.R;
import com.lzy.imagepicker.loader.ImageLoader;

import java.io.File;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧 Github地址：https://github.com/jeasonlzy0216
 * 版    本：1.0
 * 创建日期：2016/5/19
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class GlideImageLoader implements ImageLoader {

    @Override
    public void displayImage(Activity activity, String path, ImageView imageView, int width, int height) {
        RequestOptions options = new RequestOptions();
        options.placeholder(R.drawable.ic_place_pic);
        options.error(R.drawable.ic_place_pic);
        options.diskCacheStrategy(DiskCacheStrategy.ALL);//缓存全尺寸
        Glide.with(activity)                             //配置上下文
                .load(Uri.fromFile(new File(path)))//设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                /*    .error(R.drawable.ic_default_image)           //设置错误图片
                    .placeholder(R.drawable.ic_default_image)     //设置占位图片
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存全尺寸*/
                .apply(options)
                .into(imageView);
    }

    @Override
    public void displayImagePreview(Activity activity, String path, ImageView imageView, int width, int height) {
        if (path.startsWith("/storage/")) {
            RequestOptions options = new RequestOptions();
            options.placeholder(R.drawable.ic_place_pic);
            options.error(R.drawable.ic_place_pic);
            options.diskCacheStrategy(DiskCacheStrategy.ALL);//缓存全尺寸
            Glide.with(activity)                             //配置上下文
                    .load(Uri.fromFile(new File(path)))      //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                    .apply(options)
                    .into(imageView);
        } else {
            RequestOptions options = new RequestOptions();
            options.placeholder(R.drawable.ic_place_pic);
            options.error(R.drawable.ic_place_pic);
            Glide.with(activity).load(path).apply(options)
                    .into(imageView);
        }
    }

    @Override
    public void clearMemoryCache() {

    }
}
