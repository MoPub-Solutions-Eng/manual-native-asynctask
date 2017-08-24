package vchau.mopub.com.manualnativewithasynctask;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.mopub.nativeads.AdapterHelper;
import com.mopub.nativeads.MoPubAdRenderer;
import com.mopub.nativeads.MoPubNative;
import com.mopub.nativeads.MoPubStaticNativeAdRenderer;
import com.mopub.nativeads.NativeAd;
import com.mopub.nativeads.NativeErrorCode;
import com.mopub.nativeads.ViewBinder;

public class MainActivity extends AppCompatActivity {

    /**
     * MoPub sample: 11a17b188668469fb0412708c3d16813
     * AdMob: 57fd53ccacbf4e49a91b4f2cde681923
     * Facebook: d766dac54f70456a8106644c2812924f (might need to use test mode if this results in no-fills)
     * Flurry: f067181d6f554495bdb4824e4001e750
     */

    private final String TAG = this.getClass().getName();
    private static final String AD_UNIT_ID = "11a17b188668469fb0412708c3d16813";

    private MoPubNative moPubNative;
    private MoPubNative.MoPubNativeNetworkListener moPubNativeNetworkListener;
    private RelativeLayout nativeAdView;
    private ViewBinder viewBinder;
    private RelativeLayout parentView;
    private AdapterHelper adapterHelper;
    private NativeAd.MoPubNativeEventListener moPubNativeEventListener;
    private MoPubAdRenderer moPubAdRenderer;
    private View v;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AdRequestTask task = new AdRequestTask();
        task.execute();

    }// onCreate()

    public class AdRequestTask extends AsyncTask<Void, Void, View> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Other UI preparation

            nativeAdView = (RelativeLayout) findViewById(R.id.nativeAdView);
            parentView = (RelativeLayout) findViewById(R.id.parentView);
            adapterHelper = new AdapterHelper(getApplicationContext(), 0, 3);
        }

        @Override
        protected View doInBackground(Void... params) {

            moPubNativeEventListener = new NativeAd.MoPubNativeEventListener() {

                @Override
                public void onImpression(View view) {
                    Log.d(TAG, "onImpression");
                }

                @Override
                public void onClick(View view) {
                    Log.d(TAG, "onClick");
                }

            };

            moPubNativeNetworkListener = new MoPubNative.MoPubNativeNetworkListener() {

                @Override
                public void onNativeLoad(final NativeAd nativeAd) {
                    Log.d(TAG, "onNativeLoad");

                    nativeAd.setMoPubNativeEventListener(moPubNativeEventListener);

                    v = adapterHelper.getAdView(null, nativeAdView, nativeAd, new ViewBinder.Builder(0).build());
                }

                @Override
                public void onNativeFail(NativeErrorCode errorCode) {
                    Log.d(TAG, "onNativeFail: " + errorCode.toString());
                }
            };

            viewBinder = new ViewBinder.Builder(R.layout.native_ad_layout)
                    .mainImageId(R.id.native_main_image)
                    .iconImageId(R.id.native_icon_image)
                    .titleId(R.id.native_title)
                    .textId(R.id.native_text)
                    .privacyInformationIconImageId(R.id.native_privacy_information_icon_image)
                    .build();

            moPubAdRenderer = new MoPubStaticNativeAdRenderer(viewBinder);

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    moPubNative = new MoPubNative(getApplicationContext(), AD_UNIT_ID, moPubNativeNetworkListener);
                    moPubNative.registerAdRenderer(moPubAdRenderer);
                    moPubNative.makeRequest();
                }
            });


            return v;
        }

        @Override
        protected void onPostExecute(View v) {
            super.onPostExecute(v);

            /**
             * UI Post-processing
             */

            v.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            parentView.addView(v);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        moPubNative.destroy();
        moPubNative = null;

        moPubNativeNetworkListener = null;
        moPubNativeEventListener = null;
    }
}
