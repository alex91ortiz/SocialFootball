package jcsoluciones.com.socialfootball;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import jcsoluciones.com.socialfootball.models.JSONConverterFactory;
import jcsoluciones.com.socialfootball.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
public class MainActivity extends AppCompatActivity{

    /**
     *  adapter for teams management
     */

    private TabLayout tabLayout;
    private ViewPager viewPager;


    private static final String TAG = "MainActivity";
    public static final String REGISTRATION_PROCESS = "registration";
    public static final String MESSAGE_RECEIVED = "message_received";
    private  Activity activity;
    private SessionManager sessionManager;
    private boolean isReceiverRegistered;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        registerReceiver();

        Intent intent = getIntent();
        if(intent != null){
            if(intent.getAction() != null) {
                if (intent.getAction().equals(MESSAGE_RECEIVED)) {
                    String message = intent.getStringExtra("message");
                    showAlertDialog(message);
                }
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


}
