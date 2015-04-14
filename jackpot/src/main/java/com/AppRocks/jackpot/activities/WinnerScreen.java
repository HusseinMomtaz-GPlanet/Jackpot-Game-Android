package com.AppRocks.jackpot.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.AppRocks.jackpot.JackpotApplication;
import com.AppRocks.jackpot.R;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.HashMap;

public class WinnerScreen extends Activity {

    TextView txtWinnerName;
    TextView txtCompanyName;
    TextView txtBrandName;
    ImageView imgLogo;
    ImageView jackpotLogo;

    ImageLoader imageLoader;
    DisplayImageOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.winner_screen);
        initUI();
        initUILConfig();
        setWinnerInfo();
    }

    @SuppressWarnings("unchecked")
    private void setWinnerInfo() {
        // TODO Auto-generated method stub
        HashMap<String, String> winnerInfo = (HashMap<String, String>) getIntent().getSerializableExtra("winner");
        txtCompanyName.setText(winnerInfo.get(JackpotApplication.TAG_COMPANY));
        txtBrandName.setText(winnerInfo.get(JackpotApplication.TAG_BRAND));

        //show user name is existed or nick name if not
        String username=winnerInfo.get(JackpotApplication.TAG_WON_USER_FIRST_NAME) + " " + winnerInfo.get(JackpotApplication.TAG_WON_USER_LAST_NAME);
        if(username.trim().isEmpty()){
            username=winnerInfo.get(JackpotApplication.TAG_WON_USER_Nick_NAME);
        }
        txtWinnerName.setText(username);


        String imgFly = winnerInfo.get(JackpotApplication.TAG_FLY);
        String imgURL = JackpotApplication.BASE_URL
                + imgFly.substring(imgFly.indexOf("uploads"));
        imageLoader.displayImage(imgURL,
                imgLogo, options,
                new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        // spinner.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view,
                                                FailReason failReason) {
                        String message = null;
                        switch (failReason.getType()) {
                            case IO_ERROR:
                                message = "Input/Output error";
                                break;
                            case DECODING_ERROR:
                                message = "Image can't be decoded";
                                break;
                            case NETWORK_DENIED:
                                message = "Downloads are denied";
                                break;
                            case OUT_OF_MEMORY:
                                message = "Out Of Memory error";
                                break;
                            case UNKNOWN:
                                message = "Unknown error";
                                break;
                        }
                        Toast.makeText(
                                getApplicationContext(),
                                message, Toast.LENGTH_SHORT).show();

                        // spinner.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri,
                                                  View view, Bitmap loadedImage) {
                        // spinner.setVisibility(View.GONE);
                    }
                });

        //Loading Big Logo in center
        String logoURL = winnerInfo.get(JackpotApplication.TAG_IMAGE);
        String jackpotLogoURL = JackpotApplication.BASE_URL
                + logoURL.substring(logoURL.indexOf("uploads"));
        imageLoader.displayImage(jackpotLogoURL,
                jackpotLogo, options,
                new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        // spinner.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view,
                                                FailReason failReason) {
                        String message = null;
                        switch (failReason.getType()) {
                            case IO_ERROR:
                                message = "Input/Output error";
                                break;
                            case DECODING_ERROR:
                                message = "Image can't be decoded";
                                break;
                            case NETWORK_DENIED:
                                message = "Downloads are denied";
                                break;
                            case OUT_OF_MEMORY:
                                message = "Out Of Memory error";
                                break;
                            case UNKNOWN:
                                message = "Unknown error";
                                break;
                        }
                        Toast.makeText(
                                getApplicationContext(),
                                message, Toast.LENGTH_SHORT).show();

                        // spinner.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri,
                                                  View view, Bitmap loadedImage) {
                        // spinner.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        Intent i = new Intent(this, Main_.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    private void initUI() {
        // TODO Auto-generated method stub
        txtWinnerName = (TextView) findViewById(R.id.txtWinner);
        txtCompanyName = (TextView) findViewById(R.id.txtCompanyName);
        txtBrandName = (TextView) findViewById(R.id.txtBrandName);
        imgLogo = (ImageView) findViewById(R.id.imgLogo);
        jackpotLogo = (ImageView) findViewById(R.id.jackpotLogo);

    }

    private void initUILConfig() {
        // This configuration tuning is custom. You can tune every option, you
        // may tune some of them,
        // or you can create default configuration by
        // ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                WinnerScreen.this).threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024)
                        // 50 Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);

        options = new DisplayImageOptions.Builder()
                //.showImageForEmptyUri(R.drawable.ic_empty)
                //.showImageOnFail(R.drawable.ic_error)
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                        //.imageScaleType(ImageScaleType.EXACTLY)
                        //.bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();

        imageLoader = ImageLoader.getInstance();
    }


}
