package com.sikece.bekasi;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.onesignal.OneSignal;
import com.sikece.bekasi.ads.AdMobUtility;
import com.sikece.bekasi.fcm.OneSignalNotificationOpenedHandler;
import com.sikece.bekasi.utility.Preferences;

import org.alfonz.utility.Logcat;

public class WebViewAppApplication extends Application {

	private static WebViewAppApplication a;

	public WebViewAppApplication() {
		WebViewAppApplication.a = this;
	}

	public static Context getContext() {
		return (Context)WebViewAppApplication.a;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		// init logcat
		Logcat.init(WebViewAppConfig.LOGS, "WEBVIEWAPP");

		// init analytics
		FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(!WebViewAppConfig.DEV_ENVIRONMENT);

		// init AdMob
		MobileAds.initialize(this);
		MobileAds.setRequestConfiguration(AdMobUtility.createRequestConfiguration());

		// init OneSignal
		initOneSignal(getString(R.string.onesignal_app_id));
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}

	private void initOneSignal(String oneSignalAppId) {
		if (!oneSignalAppId.equals("")) {
			OneSignal.initWithContext(this);
			OneSignal.setAppId(oneSignalAppId);
			OneSignal.setNotificationOpenedHandler(new OneSignalNotificationOpenedHandler());
			OneSignal.addSubscriptionObserver(stateChanges -> {
				if (stateChanges.getTo().isSubscribed()) {
					String userId = stateChanges.getTo().getUserId();
					saveOneSignalUserId(userId);
				}
			});
			saveOneSignalUserId(OneSignal.getDeviceState().getUserId());
		}
	}

	private void saveOneSignalUserId(String userId) {
		if (userId != null) {
			Logcat.d("userId = " + userId);
			Preferences preferences = new Preferences();
			preferences.setOneSignalUserId(userId);
		}
	}
}
