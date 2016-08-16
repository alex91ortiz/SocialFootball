/**
 * -----------------------------------------------------------------------
 *     Copyright 2015 ShepHertz Technologies Pvt Ltd. All rights reserved.
 * -----------------------------------------------------------------------
 */
package jcsoluciones.com.socialfootball.plugin;

/**
 * @author Vishnu Garg
 */
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.App42Response;

import jcsoluciones.com.socialfootball.R;

/**
 * @author Vishnu Garg
 *
 */
public class App42GCMController {
	private static final int PlayServiceResolutionRequest = 9000;
	private static final String Tag = "App42PushNotification";
	public static final String KeyRegId = "registration_id";
	private static final String KeyAppVersion = "appVersion";
	private static final String PrefKey = "App42PushSample";
	private static final String KeyRegisteredOnApp42 = "app42_register";
    
	/**
	 * This function checks for GooglePlay Service availability
	 * @param activity
	 * @return
	 */
	public static boolean isPlayServiceAvailable(Activity activity) {
		GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
		int resultCode = apiAvailability.isGooglePlayServicesAvailable(activity);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (apiAvailability.isUserResolvableError(resultCode)) {
				apiAvailability.getErrorDialog(activity, resultCode, PlayServiceResolutionRequest).show();
			} else {
				Log.i(activity.getPackageName(), "This device is not supported.");
			}
			return false;
		}
		return true;
	}


	/**
	 * @param context
	 * @return
	 */
	private static SharedPreferences getGCMPreferences(Context context) {
		return context.getSharedPreferences(PrefKey, Context.MODE_PRIVATE);
	}

	/**
	 * Get AppVersion
	 * @param context
	 * @return
	 */
	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	/**
	 * Store registartion Id from GCM in preferences
	 * @param context
	 * @param regId
	 */
	public static void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getGCMPreferences(context);
		int appVersion = getAppVersion(context);
		Log.i(Tag, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(KeyRegId, regId);
		editor.putInt(KeyAppVersion, appVersion);
		editor.commit();
	}

	/**
	 * Validate if registered
	 * @param context
	 * @return
	 */
	public static boolean isApp42Registerd(Context context){
		return getGCMPreferences(context).getBoolean(KeyRegisteredOnApp42, false);
	}
	/**
	 * @param context
	 */
	public static void storeApp42Success(Context context) {
		final SharedPreferences prefs = getGCMPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(KeyRegisteredOnApp42, true);
		editor.commit();
	}


	/**
	 * CallBack Listener
	 *
	 */
	public interface App42GCMListener {

		public void onRegisterApp42(String responseMessage);

	}



}
