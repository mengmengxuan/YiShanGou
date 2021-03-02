package com.personal.baseutils.glide;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.personal.baseutils.R;
import com.personal.baseutils.utils.Utils;
import com.personal.baseutils.widget.CornerTransform;
import com.personal.baseutils.widget.GlideCircleTransform;
import com.personal.baseutils.widget.GlideRoundTransform;

/**
 * Created by KathLine on 2017/12/12.
 */

public class GlideLoader {

    private Object mImageUrlObj;
    private RequestOptions mRequestOptions;
    Activity activity;

    public static GlideLoader init(Activity activity) {
        return new GlideLoader(activity);
    }

    private GlideLoader(Activity activity) {
        this.activity = activity;
    }

    public GlideLoader load(@Nullable Object url) {
        mImageUrlObj = url;
        return this;
    }

    Bitmap errorDefaultCircle,errorDefaultRound, errorBgCircle, errorBgRound,errorFaceCircle,errorFaceRound;
    Bitmap placeDefaultCircle,placeDefaultRound, placeBgCircle, placeBgRound,placeFaceCircle,placeFaceRound;
    BitmapDrawable errorDefaultCircleDrawable, errorBgCircleDrawable, errorFaceCircleDrawable,placeBgCircleDrawable
    ,placeFaceCircleDrawable,placeDefaultCircleDrawable,placeBgRoundDrawable,placeFaceRoundDrawable
     ,placeDefaultRoundDrawable,errorDefaultRoundDrawable,errorBgRoundDrawable,errorFaceRoundDrawable;

    public GlideLoader applyDefault(int placeholderId, int errorId, String status) {
        mRequestOptions = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(false)
                .dontAnimate();

        if (status.equals("circle")) {
            mRequestOptions.transform(new GlideCircleTransform());
            if (placeholderId != 0) {
//                if(placeholderId == R.mipmap.icon_defalut_bg){
//                    if(placeBgCircleDrawable ==null){
//                        placeBgCircle = BitmapFactory.decodeResource(activity.getResources(), errorId);
//                        placeBgCircle = Utils.getOvalBitmap(placeBgCircle);
//                        placeBgCircleDrawable = new BitmapDrawable(activity.getResources(), placeBgCircle);
//                    }
//                    mRequestOptions.error(placeBgCircleDrawable);
//                }else if(placeholderId == R.mipmap.icon_defalut_face){
//                    if(placeFaceCircleDrawable ==null){
//                        placeFaceCircle = BitmapFactory.decodeResource(activity.getResources(), errorId);
//                        placeFaceCircle = Utils.getOvalBitmap(placeFaceCircle);
//                        placeFaceCircleDrawable = new BitmapDrawable(activity.getResources(), placeFaceCircle);
//                    }
//                    mRequestOptions.error(placeFaceCircleDrawable);
//                }
            } else {

                if(placeDefaultCircle ==null){
                    placeDefaultCircle = BitmapFactory.decodeResource(activity.getResources(), R.mipmap.icon_defult_pic);
                    placeDefaultCircle = Utils.getOvalBitmap(placeDefaultCircle);
                    placeDefaultCircleDrawable = new BitmapDrawable(activity.getResources(), placeDefaultCircle);
                }
                mRequestOptions.error(placeDefaultCircleDrawable);

            }

            if (errorId != 0) {
//                if(errorId == R.mipmap.icon_defalut_bg){
//                    if(errorBgCircleDrawable ==null){
//                        errorBgCircle = BitmapFactory.decodeResource(activity.getResources(), errorId);
//                        errorBgCircle = Utils.getOvalBitmap(errorBgCircle);
//                        errorBgCircleDrawable = new BitmapDrawable(activity.getResources(), errorBgCircle);
//                    }
//                    mRequestOptions.error(errorBgCircleDrawable);
//                }else if(errorId == R.mipmap.icon_defalut_face){
//                    if(errorFaceCircleDrawable ==null){
//                        errorFaceCircle = BitmapFactory.decodeResource(activity.getResources(), errorId);
//                        errorFaceCircle = Utils.getOvalBitmap(errorFaceCircle);
//                        errorFaceCircleDrawable = new BitmapDrawable(activity.getResources(), errorFaceCircle);
//                    }
//                    mRequestOptions.error(errorFaceCircleDrawable);
//                }
            } else {
              if(errorDefaultCircleDrawable ==null){
                  errorDefaultCircle = BitmapFactory.decodeResource(activity.getResources(), R.mipmap.icon_defult_pic);
                  errorDefaultCircle = Utils.getOvalBitmap(errorDefaultCircle);
                  errorDefaultCircleDrawable = new BitmapDrawable(activity.getResources(), errorDefaultCircle);
              }
              mRequestOptions.error(errorDefaultCircleDrawable);
            }

        } else if (status.equals("round")) {
            mRequestOptions.transform(new GlideRoundTransform(5));
            if (placeholderId != 0) {

//                if(placeholderId == R.mipmap.icon_defalut_bg){
//                    if(placeBgRoundDrawable ==null){
//                        placeBgRound = BitmapFactory.decodeResource(activity.getResources(), errorId);
//                        placeBgRound = Utils.roundCrop(5, placeBgRound);
//                        placeBgRoundDrawable = new BitmapDrawable(activity.getResources(), placeBgRound);
//                    }
//                    mRequestOptions.error(placeBgRoundDrawable);
//                }else if(placeholderId == R.mipmap.icon_defalut_face){
//                    if(placeFaceRoundDrawable ==null){
//                        placeFaceRound = BitmapFactory.decodeResource(activity.getResources(), errorId);
//                        placeFaceRound = Utils.roundCrop(5, placeFaceRound);
//                        placeFaceRoundDrawable = new BitmapDrawable(activity.getResources(), placeFaceRound);
//                    }
//                    mRequestOptions.error(placeFaceRoundDrawable);
//                }

            } else {
                if(placeDefaultRoundDrawable == null){
                    placeDefaultRound = BitmapFactory.decodeResource(activity.getResources(), R.mipmap.icon_defult_pic);
                    placeDefaultRound = Utils.roundCrop(5, placeDefaultRound);
                    placeDefaultRoundDrawable = new BitmapDrawable(activity.getResources(), placeDefaultRound);
                }
                mRequestOptions.placeholder(placeDefaultRoundDrawable);
            }

            if (errorId != 0) {

//                if(errorId == R.mipmap.icon_defalut_bg){
//                    if(errorBgRoundDrawable ==null){
//                        errorBgRound = BitmapFactory.decodeResource(activity.getResources(), errorId);
//                        errorBgRound = Utils.roundCrop(5, errorBgRound);
//                        errorBgRoundDrawable = new BitmapDrawable(activity.getResources(), errorBgRound);
//                    }
//                    mRequestOptions.error(errorBgRoundDrawable);
//                }else if(errorId == R.mipmap.icon_defalut_face){
//                    if(errorFaceRoundDrawable ==null){
//                        errorFaceRound = BitmapFactory.decodeResource(activity.getResources(), errorId);
//                        errorFaceRound = Utils.roundCrop(5, errorFaceRound);
//                        errorFaceRoundDrawable = new BitmapDrawable(activity.getResources(), errorFaceRound);
//                    }
//                    mRequestOptions.error(errorFaceRoundDrawable);
//                }
            } else {
                if(errorDefaultRoundDrawable == null){
                    errorDefaultRound = BitmapFactory.decodeResource(activity.getResources(), R.mipmap.icon_defult_pic);
                    errorDefaultRound = Utils.roundCrop(5, errorDefaultRound);
                    errorDefaultRoundDrawable = new BitmapDrawable(activity.getResources(), errorDefaultRound);
                }
                mRequestOptions.placeholder(errorDefaultRoundDrawable);
            }
        } else {
            if (placeholderId != 0) {
                mRequestOptions.placeholder(placeholderId);
            } else {
                mRequestOptions.placeholder(R.mipmap.icon_defult_pic);
            }

            if (errorId != 0) {
                mRequestOptions.error(errorId);
            } else {
                mRequestOptions.error(R.mipmap.icon_defult_pic);
            }
        }

        return this;
    }

    public GlideLoader applyDefault(int errorId) {
        return applyDefault(0, errorId, "");
    }

    public GlideLoader applyDefault(String status) {
        return applyDefault(0, 0, status);
    }

    public GlideLoader applyDefault() {
        return applyDefault(0, 0, "");
    }

    /**
     * 例子
     * <pre>
     * RequestOptions options = new RequestOptions()
     * .centerCrop()
     * .priority(Priority.HIGH)
     * .diskCacheStrategy(DiskCacheStrategy.ALL);
     * </pre>
     *
     * @param requestOptions
     * @return
     */
    public GlideLoader apply(@NonNull RequestOptions requestOptions) {
        mRequestOptions = requestOptions;
        return this;
    }

    public GlideLoader bitmapTransform(Transformation<Bitmap> transformation) {
        apply(RequestOptions.bitmapTransform(transformation));
        return this;
    }


    /**
     * 最后调用
     *
     * @param context
     * @param listener
     * @return
     */
    public RequestBuilder<Drawable> listener(Context context, RequestListener<Drawable> listener) {
        return Glide.with(context)
                .load(mImageUrlObj)
                .apply(mRequestOptions)
                .listener(listener);
    }

    /**
     * 最后调用
     *
     * @param imageView
     * @return
     */
    public Target<Drawable> into(ImageView imageView) {
        if (imageView == null) {
            return null;
        }
        if (activity != null) {
            return Glide.with(activity)
                    .load(mImageUrlObj)
                    .apply(mRequestOptions)
                    .into(imageView);
        } else {
            return null;
        }
    }

    public Target<Drawable> into(Context context, Target<Drawable> target) {
        return Glide.with(context)
                .load(mImageUrlObj)
                .apply(mRequestOptions)
                .into(target);
    }

}
