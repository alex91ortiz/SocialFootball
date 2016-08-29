package jcsoluciones.com.socialfootball.plugin;

/**
 * Created by ADMIN on 16/08/2016.
 */

import android.app.IntentService;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import jcsoluciones.com.socialfootball.Constants;
import jcsoluciones.com.socialfootball.MainActivity;
import jcsoluciones.com.socialfootball.R;
import jcsoluciones.com.socialfootball.RequestInterface;
import jcsoluciones.com.socialfootball.models.JSONConverterFactory;
import jcsoluciones.com.socialfootball.models.RequestTeamBody;
import jcsoluciones.com.socialfootball.models.ResponseBody;
import jcsoluciones.com.socialfootball.utils.SessionManager;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RegistrationIntentService extends IntentService{
    private static final int PlayServiceResolutionRequest = 9000;
    /**
     * The progress dialog.
     */

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    private  String HOST;
    private  String URL_PHOTO;
    private  SessionManager sessionManager;
    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            // [START register_for_gcm]
            // Initially this call goes out to the network to retrieve the token, subsequent calls
            // are local.
            // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
            // See https://developers.google.com/cloud-messaging/android/start for details on this file.
            // [START get_token]
            String name = intent.getStringExtra("TEAM_NAME");
            String photo = intent.getStringExtra("TEAM_PHOTO");
            String city = intent.getStringExtra("TEAM_CITY");
            String desc = intent.getStringExtra("TEAM_DESC");
            String email = intent.getStringExtra("TEAM_EMAIL");
            HOST =intent.getStringExtra("HOST");
            URL_PHOTO =intent.getStringExtra("URL_PHOTO");
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            // [END get_token]
            Log.i(TAG, "GCM Registration Token: " + token);

            // TODO: Implement this method to send any registration to your app's servers.
            sendRegistrationToServer(token);

            // Subscribe to topic channels
            subscribeTopics(token);
            //if(!sharedPreferences.getBoolean(SENT_TOKEN_TO_SERVER,false))
            registerTeamProcess(name,photo,city,desc,email,token);
            // You should store a boolean that indicates whether the generated token has been
            // sent to your server. If the boolean is false, send the token to your server,
            // otherwise your server should have already received the token.
            sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, true).apply();
            // [END register_for_gcm]
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, false).apply();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    /**
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
    }

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }
    // [END subscribe_topics]

    private void registerTeamProcess(String name, String phone, String city, String desc,String email,String registrationId){
        RequestTeamBody requestBody = new RequestTeamBody();
        requestBody.setName(name);
        requestBody.setPhone(phone);
        requestBody.setCity(city);
        requestBody.setDesc(desc);
        requestBody.setEmail(email);
        requestBody.setRegistrationId(registrationId);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HOST)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        sessionManager = new SessionManager(this);
        RequestInterface request = retrofit.create(RequestInterface.class);
        Call<RequestTeamBody> call = request.registerTeam(requestBody);
        call.enqueue(new Callback<RequestTeamBody>() {
            @Override
            public void onResponse(Call<RequestTeamBody> call, Response<RequestTeamBody> response) {
                RequestTeamBody responseBody = response.body();
                sessionManager.createContentSession(responseBody.getId() , responseBody.toString());
                /*Intent intent = new Intent(MainActivity.REGISTRATION_PROCESS);
                intent.putExtra("result", responseBody.getString("_id"));
                intent.putExtra("message", responseBody.getString("_id"));
                LocalBroadcastManager.getInstance(RegistrationIntentService.this).sendBroadcast(intent);*/
                uploadFile();
                Toast.makeText(getApplicationContext(), "successfully registered.", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<RequestTeamBody> call, Throwable t) {

                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void uploadFile() {
        File file = new File(URL_PHOTO);
        // create upload service client
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HOST)
                .addConverterFactory(JSONConverterFactory.create())
                .build();
        RequestInterface service = retrofit.create(RequestInterface.class);
        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);
        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body = MultipartBody.Part.createFormData("picture", file.getName(), requestFile);
        // add another part within the multipart request
        String descriptionString = sessionManager.getUserDetails().get(SessionManager.ID_CONTENT);
        RequestBody description = RequestBody.create(MediaType.parse("multipart/form-data"), descriptionString);
        // finally, execute the request
        Call<JSONObject> call = service.upload(description, body);
        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                Log.v("Upload", "success");
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
            }
        });
    }
}
