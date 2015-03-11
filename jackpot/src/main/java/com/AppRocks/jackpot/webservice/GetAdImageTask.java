package com.AppRocks.jackpot.webservice;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;

import com.AppRocks.jackpot.JackpotApplication;
import com.AppRocks.jackpot.activities.Question;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONException;
import org.json.JSONObject;

public class GetAdImageTask extends AsyncTask<Void, Void, Boolean> {

    JSONObject adImgJSON;
    Question questionActivity;
    private String img;
    private ImageLoaderConfiguration config;
    private DisplayImageOptions options;
    private ImageLoader imageLoader;

    public GetAdImageTask(Question q) {
        questionActivity = q;
    }

    @Override
    protected void onPreExecute() {
        questionActivity.adProgressBar.setVisibility(View.VISIBLE);
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Void... arg0) {
        adImgJSON = JackpotServicesClient.getInstance().getAdImg();
        if (adImgJSON != null) {
            try {
                if (!adImgJSON.isNull(JackpotApplication.TAG_AD_IMAGE)) {
                    //FIXME img = JackpotApplication.BASE_URL + adImgJSON.getString(JackpotApplication.TAG_AD_IMAGE);
                    img = "http://game.gplanet-tech.com/" + adImgJSON.getString(JackpotApplication.TAG_AD_IMAGE);
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            //img = JackpotApplication.BASE_URL + "uploads/HUIintumyPVB.png";
            initUILConfig();
            imageLoader.displayImage(img, questionActivity.adImgBtn, options, new ImageLoadingListener() {

                @Override
                public void onLoadingStarted(String imageUri, View view) {
                }

                @Override
                public void onLoadingFailed(String imageUri, View view,
                                            FailReason failReason) {
                    questionActivity.adProgressBar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    questionActivity.adProgressBar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                }
            });
            //questionActivity.adImgBtn.setBackgroundResource(R.drawable.traffic_bar_yellow);
            //setImageResource(R.drawable.traffic_bar_yellow);


        }
        super.onPostExecute(result);
    }

    private void initUILConfig() {
        // This configuration tuning is custom. You can tune every option, you
        // may tune some of them,
        // or you can create default configuration by
        // ImageLoaderConfiguration.createDefault(this);
        // method.

        imageLoader = ImageLoader.getInstance();
        if (imageLoader != null) {
            return;
        }
        config = new ImageLoaderConfiguration.Builder(
                questionActivity).threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024) // 50 Mb
                .threadPoolSize(1)
                .memoryCache(new WeakMemoryCache())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);

        options = new DisplayImageOptions.Builder()
                // .showImageForEmptyUri(R.drawable.ic_empty)
                // .showImageOnFail(R.drawable.ic_error)
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300)).build();
    }

}
