package com.hnhy.framework.system;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hnhy.framework.R;
import com.hnhy.framework.frame.BaseSystem;

/**
 * Author: hardcattle
 * Time: 2018/3/26 上午9:57
 * Description:
 */

public class SystemImageLoader extends BaseSystem {
    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void destroy() {
        super.destroy();
    }

    public void displayImage(Activity activity, ImageView imageView, String url) {
        Glide.with(activity).load(url).into(imageView);
    }

    public void displayImage(Context context, ImageView imageView, String url) {
        Glide.with(context).load(url).into(imageView);
    }

    public void displayImageWithPlaceholder(Activity activity, ImageView imageView, String url, @DrawableRes int placeHolderId, @DrawableRes int errorId) {
        RequestOptions options = new RequestOptions().placeholder(placeHolderId).error(errorId);
        Glide.with(activity).load(url)
                .apply(options)
                .into(imageView);
    }

    public void displayImageWithPlaceholder(Context context, ImageView imageView, String url, @DrawableRes int placeHolderId, @DrawableRes int errorId) {
        RequestOptions options = new RequestOptions().placeholder(placeHolderId).error(errorId);
        Glide.with(context).load(url)
                .apply(options)
                .into(imageView);
    }

    public void displayImageWithDefaultPlaceholder(Context context, ImageView imageView, String url) {
        RequestOptions options = new RequestOptions().placeholder(R.drawable.ic_place_pic).error(R.drawable.ic_place_pic);
        Glide.with(context).load(url)
                .apply(options)
                .into(imageView);
    }


}
