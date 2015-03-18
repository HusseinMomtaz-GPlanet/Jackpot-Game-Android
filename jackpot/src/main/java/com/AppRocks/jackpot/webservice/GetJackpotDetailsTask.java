package com.AppRocks.jackpot.webservice;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

import com.AppRocks.jackpot.JackpotApplication;
import com.AppRocks.jackpot.R;
import com.AppRocks.jackpot.activities.Jackpot;
import com.AppRocks.jackpot.activities.WinnerScreen;
import com.AppRocks.jackpot.activities.youtube.FullscreenDemoActivity2;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class GetJackpotDetailsTask extends AsyncTask<Object, Void, Void> {

    HashMap<String, String> jackpotDetails;
    Jackpot callerActivity;
    private JSONObject jackpotDetailsJSON;
    private String imgFly;
    private String img;
    private String videoUrl;
    private HashMap<String, String> winnerDetails;

    public GetJackpotDetailsTask(Jackpot a) {
        callerActivity = a;
    }

    //"won" is null or has winner info
    @Override
    protected Void doInBackground(Object... params) {
        jackpotDetailsJSON = JackpotServicesClient.getInstance()
                .getJackpotDetails(JackpotApplication.JACKPOT_ID, JackpotApplication.TOKEN_ID);

        if (jackpotDetailsJSON != null) {
            try {

                if (!jackpotDetailsJSON.isNull(JackpotApplication.TAG_WON)) {
                    //get winner info
                    JackpotApplication.isWined = true;
                    JSONObject wonJson = jackpotDetailsJSON.getJSONObject(JackpotApplication.TAG_WON);
                    winnerDetails = new HashMap<String, String>();
                    winnerDetails.put(JackpotApplication.TAG_COMPANY,
                            jackpotDetailsJSON
                                    .getString(JackpotApplication.TAG_COMPANY));
                    winnerDetails.put(JackpotApplication.TAG_BRAND,
                            jackpotDetailsJSON
                                    .getString(JackpotApplication.TAG_BRAND));
                    winnerDetails.put(JackpotApplication.TAG_FLY,
                            jackpotDetailsJSON
                                    .getString(JackpotApplication.TAG_FLY));
                    winnerDetails.put(JackpotApplication.TAG_IMAGE,
                            jackpotDetailsJSON
                                    .getString(JackpotApplication.TAG_IMAGE));

                    JSONObject userWonJson = wonJson.getJSONObject(JackpotApplication.TAG_WON_USER);
                    winnerDetails.put(JackpotApplication.TAG_WON_USER_FIRST_NAME, userWonJson.getString(JackpotApplication.TAG_WON_USER_FIRST_NAME));
                    winnerDetails.put(JackpotApplication.TAG_WON_USER_LAST_NAME, userWonJson.getString(JackpotApplication.TAG_WON_USER_LAST_NAME));

                } else if (!jackpotDetailsJSON.isNull(JackpotApplication.TAG_COMPANY)) {
                    //get jackpot details
                    JackpotApplication.isWined = false;
                    jackpotDetails = new HashMap<String, String>();
                    jackpotDetails.put(JackpotApplication.TAG_COMPANY,
                            jackpotDetailsJSON
                                    .getString(JackpotApplication.TAG_COMPANY));
                    jackpotDetails.put(JackpotApplication.TAG_BRAND,
                            jackpotDetailsJSON
                                    .getString(JackpotApplication.TAG_BRAND));
                    jackpotDetails.put(JackpotApplication.TAG_DESCRIPTION,
                            jackpotDetailsJSON
                                    .getString(JackpotApplication.TAG_DESCRIPTION));
                    jackpotDetails.put(JackpotApplication.TAG_YOUR_VIEWS,
                            jackpotDetailsJSON
                                    .getString(JackpotApplication.TAG_YOUR_VIEWS));
                    jackpotDetails.put(JackpotApplication.TAG_WORLD_VIEWS,
                            jackpotDetailsJSON
                                    .getString(JackpotApplication.TAG_WORLD_VIEWS));

                    //get video link from videos array
                    JSONArray videosJsonArray = jackpotDetailsJSON.getJSONArray(JackpotApplication.TAG_VIDEOS_ARRAY);
                    JSONObject videoJsonObject = videosJsonArray.getJSONObject(0);
                    //jackpotDetails.put(JackpotApplication.TAG_VIDEOS_LINK, videoJsonObject.getString(JackpotApplication.TAG_VIDEOS_LINK));
                    videoUrl = videoJsonObject.getString(JackpotApplication.TAG_VIDEOS_LINK);

					/*jackpotDetails.put(JackpotApplication.TAG_FLY,
                            jackpotDetailsJSON
									.getString(JackpotApplication.TAG_FLY));*/
                    imgFly = jackpotDetailsJSON
                            .getString(JackpotApplication.TAG_FLY);
                    img = jackpotDetailsJSON
                            .getString(JackpotApplication.TAG_IMAGE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        if (jackpotDetailsJSON != null) {
            if (JackpotApplication.isWined) {
                // start activity winner
                Intent i = new Intent(callerActivity.getApplicationContext(), WinnerScreen.class);

                i.putExtra("winner", winnerDetails);
                callerActivity.startActivity(i);
            } else if (!jackpotDetailsJSON.isNull(JackpotApplication.TAG_COMPANY)) {
                callerActivity.onJackpotDataRetrieved(jackpotDetailsJSON);
                // else not wined and has jackpot details then show it
                //Log.d("ELSAWAF", jackpotDetails.toString());
                callerActivity.txtCompanyName.setText(jackpotDetails
                        .get(JackpotApplication.TAG_COMPANY));
                callerActivity.txtBrandName.setText(jackpotDetails
                        .get(JackpotApplication.TAG_BRAND));
                callerActivity.desc = jackpotDetails.get(JackpotApplication.TAG_DESCRIPTION);
                callerActivity.txtDesc.setText(callerActivity.desc);
                callerActivity.btnDescComplete.setVisibility(View.VISIBLE);
                int youPercentage = (int) Double.parseDouble(jackpotDetails
                        .get(JackpotApplication.TAG_YOUR_VIEWS));
                callerActivity.animateThermometerYou(youPercentage);

                // show world thermometer if he watched the video
                if (youPercentage >= 100) {
                    callerActivity.imgProgrWorld.setVisibility(View.VISIBLE);
                    callerActivity.viewBGWorld.setBackgroundColor(callerActivity.getResources().getColor(R.color.Blue));
                    callerActivity.animateThermometerWorld((int) Double.parseDouble(jackpotDetails
                            .get(JackpotApplication.TAG_WORLD_VIEWS)));
                }

                int startIndex = videoUrl.indexOf("v=") + 2;
                int endIndex = videoUrl.indexOf("&");
                if (endIndex == -1) {
                    FullscreenDemoActivity2.videoID = videoUrl.substring(startIndex);
                } else {
                    FullscreenDemoActivity2.videoID = videoUrl.substring(startIndex, endIndex);
                }
                //Log.d(GetJackpotDetailsTask.this.getClass().getName(), FullscreenDemoActivity2.videoID);

                String imgURL = JackpotApplication.BASE_URL
                        + imgFly.substring(imgFly.indexOf("uploads"));
                JackpotApplication.logoURL = imgURL;
                // FIXME crash when u in Q Activity and press home then reopen app from recent app list
                callerActivity.imageLoader.displayImage(imgURL,
                        callerActivity.imgLogo, callerActivity.options,
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
                                        callerActivity.getApplicationContext(),
                                        message, Toast.LENGTH_SHORT).show();

                                // spinner.setVisibility(View.GONE);
                            }

                            @Override
                            public void onLoadingComplete(String imageUri,
                                                          View view, Bitmap loadedImage) {
                                // spinner.setVisibility(View.GONE);
                            }
                        });

                imgURL = JackpotApplication.BASE_URL
                        + img.substring(img.indexOf("uploads"));
                callerActivity.imageLoader.displayImage(imgURL,
                        callerActivity.imgJAckBot, callerActivity.options,
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
                                        callerActivity.getApplicationContext(),
                                        message, Toast.LENGTH_SHORT).show();

                                callerActivity.spinner.setVisibility(View.GONE);
                                callerActivity.btnSeeTheVideo.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onLoadingComplete(String imageUri,
                                                          View view, Bitmap loadedImage) {
                                callerActivity.spinner.setVisibility(View.GONE);
                                callerActivity.btnSeeTheVideo.setVisibility(View.VISIBLE);
                            }
                        });
            } else {
                // response doesn't has data
                callerActivity.spinner.setVisibility(View.GONE);
                callerActivity.txtDesc.setText("Data isn't available now");
            }
        } else {
            // json response is null
            Toast.makeText(callerActivity.getApplicationContext(), "No Data: Please, try again.", Toast.LENGTH_LONG).show();
        }
        super.onPostExecute(result);
    }

}
