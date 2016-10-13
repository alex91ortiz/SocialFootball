package jcsoluciones.com.socialfootball;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.shephertz.app42.paas.sdk.android.game.Game;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import jcsoluciones.com.socialfootball.plugin.RegistrationIntentService;
import jcsoluciones.com.socialfootball.provider.JSONConverterFactory;
import jcsoluciones.com.socialfootball.provider.RequestInterface;
import jcsoluciones.com.socialfootball.util.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends FragmentActivity {

    private static final String TAG = "MainActivity";
    public static final String REGISTRATION_PROCESS = "registration";
    public static final String MESSAGE_RECEIVED = "message_received";
    private  Activity activity;
    private static SessionManager sessionManager;
    private boolean isReceiverRegistered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Intent intent = getIntent();
        if(intent != null){
            if(intent.getAction() != null) {
                if (intent.getAction().equals(MESSAGE_RECEIVED)) {
                    String message = intent.getStringExtra("message");
                    showAlertDialog(message);
                }
            }
        }
        if (getSupportFragmentManager().findFragmentByTag(TAG) == null) {
            if(sessionManager.isLoggedIn()) {
                final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.add(android.R.id.content, new MenuFragment(), TAG);
                ft.commit();
            }else {
                final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.add(android.R.id.content, new SingFragment(), TAG);
                ft.commit();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
    }

    private void showAlertDialog(String message){
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("GCM Message Received !");
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals(REGISTRATION_PROCESS)){

                String result  = intent.getStringExtra("result");
                String message = intent.getStringExtra("message");
                Log.d(TAG, "onReceive: " + result + message);
                showAlertDialog(message);

            } else if (intent.getAction().equals(MESSAGE_RECEIVED)){

                String message = intent.getStringExtra("message");
                showAlertDialog(message);
            }
        }
    };

    private void registerReceiver(){

        LocalBroadcastManager bManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(REGISTRATION_PROCESS);
        intentFilter.addAction(MESSAGE_RECEIVED);
        bManager.registerReceiver(broadcastReceiver, intentFilter);

    }

    public static class MenuFragment extends Fragment{
        private ImageButton btnMatch,btnClubs;
        public MenuFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View view = inflater.inflate(R.layout.activity_main, container, false);
            btnClubs = (ImageButton) view.findViewById(R.id.btn_clubs);
            btnClubs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ClubsActivity.class);
                    startActivity(intent);
                }
            });
            return view;
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
        }
    }

    public static class SingFragment extends Fragment{
        private LoginButton loginbutton;
        private CallbackManager callbackManager;
        public SingFragment() {

        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
            callbackManager = CallbackManager.Factory.create();
            try {
                PackageInfo info = getActivity().getPackageManager().getPackageInfo("jcsoluciones.com.socialfootball.MainActivity", PackageManager.GET_SIGNATURES);
                for (Signature signature : info.signatures) {
                    MessageDigest md = MessageDigest.getInstance("SHA");
                    md.update(signature.toByteArray());
                    Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                }
            } catch (PackageManager.NameNotFoundException e) {

            } catch (NoSuchAlgorithmException e) {

            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            // Inflate the layout for this fragment

            View view = inflater.inflate(R.layout.activity_sign_in, container, false);
            loginbutton = (LoginButton) view.findViewById(R.id.login_button);
            loginbutton.setFragment(this);
            loginbutton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {

                    //sessionManager.createLoginSession("","","","");
                    onRefresh(loginResult.getAccessToken().getToken());
                    Toast toast = Toast.makeText(getActivity(),"inicio",Toast.LENGTH_SHORT);
                    toast.show();

                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onError(FacebookException error) {

                }
            });
            return view;
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            callbackManager.onActivityResult(requestCode, resultCode, data);

        }

        public void onRefresh(final String key){
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.HostServer)
                    .addConverterFactory(JSONConverterFactory.create())
                    .build();
            final RequestInterface request = retrofit.create(RequestInterface.class);
            Call<JSONObject> call = request.getTeams(key, "1234rGSS34567AWS");
            call.enqueue(new Callback<JSONObject>() {
                @Override
                public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                    JSONObject jsonObject = response.body();
                    if(response.body()!=null) {
                        try {

                            Intent intents = new Intent(getActivity(), RegistrationIntentService.class);
                            intents.putExtra("TEAM_ID", jsonObject.getString("_id"));
                            intents.putExtra("TEAM_NAME", jsonObject.getString("name"));
                            intents.putExtra("TEAM_PHOTO", jsonObject.getString("phone"));
                            intents.putExtra("TEAM_CITY", jsonObject.getString("city"));
                            intents.putExtra("TEAM_DESC", jsonObject.getString("desc"));
                            intents.putExtra("TEAM_EMAIL", jsonObject.getString("email"));
                            intents.putExtra("HOST", Constants.HostServer);
                            intents.putExtra("CREATEORUPDATE_TEAM", false);
                            getActivity().startService(intents);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else{
                        Toast toast = Toast.makeText(getActivity(),"no existe",Toast.LENGTH_SHORT);
                        toast.show();

                        Intent intents = new Intent(getActivity(), RegistrationIntentService.class);

                        intents.putExtra("TEAM_NAME", "");
                        intents.putExtra("TEAM_PHOTO", "");
                        intents.putExtra("TEAM_CITY", "");
                        intents.putExtra("TEAM_DESC", "");
                        intents.putExtra("TEAM_EMAIL",key );
                        intents.putExtra("HOST", Constants.HostServer);
                        intents.putExtra("CREATEORUPDATE_TEAM", true);
                        getActivity().startService(intents);
                    }
                }

                @Override
                public void onFailure(Call<JSONObject> call, Throwable t) {


                }
            });
        }
    }
}
