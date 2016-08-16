package jcsoluciones.com.socialfootball.plugin;

/**
 * Created by ADMIN on 16/08/2016.
 */

import android.app.IntentService;
import android.content.Intent;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.App42Response;


import java.io.IOException;

import jcsoluciones.com.socialfootball.R;


public class RegistrationIntentService extends IntentService{
    private static final int PlayServiceResolutionRequest = 9000;

    public RegistrationIntentService() {
        super("RegistrationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String deviceId = intent.getStringExtra("DEVICE_ID");
        String deviceName = intent.getStringExtra("DEVICE_NAME");

        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String registrationId = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            registerDeviceProcess(deviceName,deviceId,registrationId);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void registerDeviceProcess(String deviceName, String deviceId, String registrationId){
        App42API.buildPushNotificationService().storeDeviceToken(deviceName, registrationId, new App42CallBack() {
            @Override
            public void onSuccess(Object arg0) {
                App42Response response=(App42Response) arg0;
            }

            @Override
            public void onException(Exception arg0) {
                // TODO Auto-generated method stub
            }
        });
    }
}
