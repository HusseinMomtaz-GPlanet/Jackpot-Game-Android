/*
 * Copyright 2012 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.AppRocks.jackpot.activities.youtube;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.AppRocks.jackpot.R;
import com.AppRocks.jackpot.services.ShowCompanyLogoService;

/**
 * Sample activity showing how to properly enable custom fullscreen behavior.
 * <p/>
 * This is the preferred way of handling fullscreen because the default
 * fullscreen implementation will cause re-buffering of the video.
 */
public class FullscreenDemoActivity extends Activity {

    private final BroadcastReceiver abcd = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullscreen_demo);
        registerReceiver(abcd, new IntentFilter("xyz"));
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(abcd);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        //startService(new Intent(FullscreenDemoActivity.this,
                //ShowCompanyLogoService.class));
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        //stopService(new Intent(FullscreenDemoActivity.this,
                //ShowCompanyLogoService.class));
    }

}
