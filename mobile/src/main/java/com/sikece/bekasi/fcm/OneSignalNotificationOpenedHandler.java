package com.sikece.bekasi.fcm;

import android.content.Context;
import android.content.Intent;

import com.onesignal.OSNotification;
import com.onesignal.OSNotificationOpenedResult;
import com.onesignal.OneSignal;
import com.sikece.bekasi.WebViewAppApplication;
import com.sikece.bekasi.activity.MainActivity;

import org.alfonz.utility.Logcat;
import org.json.JSONException;
import org.json.JSONObject;

public class OneSignalNotificationOpenedHandler implements OneSignal.OSNotificationOpenedHandler {
	@Override
	public void notificationOpened(OSNotificationOpenedResult result) {
		try {
			Context context = WebViewAppApplication.getContext();
			String url = null;

			// notification data
			OSNotification notification = result.getNotification();
			String launchURL = notification.getLaunchURL();
			JSONObject additionalData = notification.getAdditionalData();

			// get launch url
			if (launchURL != null) {
				Logcat.d("launchURL = " + launchURL);
				url = launchURL;
			}

			// get url from additional data
			if (additionalData != null) {
				Logcat.d("additionalData = " + additionalData.toString());

				if (additionalData.has("url")) url = additionalData.getString("url");
				else if (additionalData.has("URL")) url = additionalData.getString("URL");
				else if (additionalData.has("launchURL")) url = additionalData.getString("launchURL");
			}

			// start activity
			Intent intent;
			if (url == null) intent = MainActivity.newIntent(context);
			else intent = MainActivity.newIntent(context, url);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
