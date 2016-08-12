package jcsoluciones.com.socialfootball;

import android.app.AlertDialog;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Query;
import com.shephertz.app42.paas.sdk.android.storage.QueryBuilder;
import com.shephertz.app42.paas.sdk.android.storage.Storage;

import java.util.ArrayList;
import java.util.List;

import jcsoluciones.com.socialfootball.plugin.App42GCMController;
import jcsoluciones.com.socialfootball.plugin.App42GCMService;
import jcsoluciones.com.socialfootball.utils.SessionManager;

public class MainActivity extends AppCompatActivity implements
        SearchView.OnQueryTextListener,
        AsyncApp42ServiceApi.App42StorageServiceListener
        ,App42GCMController.App42GCMListener
        ,GoogleApiClient.OnConnectionFailedListener{

    /**
     * The async service.
     */
    private AsyncApp42ServiceApi asyncService;
    /**
     * List of your Teams setting
     */
    private ArrayList<Storage.JSONDocument> listTeamJson = new ArrayList<Storage.JSONDocument>();
    /**
     *  adapter for teams management
     */
    private SearchTeamsAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SearchView mSearchView;
    private MenuItem searchMenuItem;
    private ListView searchList;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";
    private GoogleApiClient mGoogleApiClient;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        asyncService = AsyncApp42ServiceApi.instance(this);
        sessionManager  = new SessionManager(this);
        asyncService.setLoggedInUser(sessionManager.getUserDetails().get(sessionManager.KEY_NAME));
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        searchList = (ListView) findViewById(R.id.listsearch);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                searchMenuItem.setVisible(tab.getPosition() == 0);
                viewPager.setVisibility(View.VISIBLE);
                searchList.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        App42API.buildLogService().setEvent("Message", "Opened", new App42CallBack() {
            public void onSuccess(Object arg0) {
                // TODO Auto-generated method stub
                Log.i("MainActivity-BroadcastReceiver", "Message Recieved " + " : "
                        + arg0.toString());
            }

            public void onException(Exception arg0) {
                // TODO Auto-generated method stub
            }
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


    }
    @Override
    public void onStart() {
        super.onStart();
        if (App42GCMController.isPlayServiceAvailable(this)) {
            App42GCMController.getRegistrationId(MainActivity.this,Constants.GoogleProjectNo, this);
        } else {
            Log.i("App42PushNotification", "No valid Google Play Services APK found.");
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        //unregisterReceiver(mBroadcastReceiver);
    }

    private void onMessageOpen(View view){
        createAlertDialog("registro");
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new TeamsEventsFragment(), "ONE");
        adapter.addFragment(new TeamsInviteAcceptFragment(), "TWO");
        adapter.addFragment(new TeamManagementFragment(), "THREE");
        adapter.addFragment(new TournamentsFragment(), "FOUR");
        viewPager.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        searchMenuItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        mSearchView.setOnQueryTextListener(this);

        return true;
    }
    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        searchList.setVisibility(View.VISIBLE);
        viewPager.setVisibility(View.INVISIBLE);
        Query q1 = QueryBuilder.build("active", true, QueryBuilder.Operator.EQUALS);
        Query q2 = QueryBuilder.build("name",newText , QueryBuilder.Operator.LIKE);
        Query q3 = QueryBuilder.compoundOperator(q1, QueryBuilder.Operator.AND, q2);
        Query q4 = QueryBuilder.build("city",newText, QueryBuilder.Operator.EQUALS);
        Query q5 = QueryBuilder.compoundOperator(q3, QueryBuilder.Operator.OR, q4);
        asyncService.findDocByQuery(Constants.App42DBName, "Teams", q5, this);
        return false;
    }

    @Override
    public void onDocumentInserted(Storage response) {

    }

    @Override
    public void onUpdateDocSuccess(Storage response) {

    }

    @Override
    public void onFindDocSuccess(Storage response) {
        listTeamJson = response.getJsonDocList();
        if(listTeamJson.size()>0) {
            adapter = new SearchTeamsAdapter(this, listTeamJson);
            searchList.setAdapter(adapter);
        }
    }

    @Override
    public void onDeleteDocSuccess() {

    }

    @Override
    public void onInsertionFailed(App42Exception ex) {

    }

    @Override
    public void onFindDocFailed(App42Exception ex) {

    }

    @Override
    public void onUpdateDocFailed(App42Exception ex) {

    }

    @Override
    public void onDeleteDocFailed(App42Exception ex) {

    }

    final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra(App42GCMService.ExtraMessage);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.common_ic_googleplayservices)
                    .setContentTitle("prueba")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setContentText(message) //.setWhen(when).setNumber(++msgCount)
                    .setLights(Color.YELLOW, 1, 2).setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setDefaults(Notification.DEFAULT_VIBRATE);
            Log.i("MainActivity-BroadcastReceiver", "Message Recieved " + " : " + message);


        }
    };

    @Override
    public void onError(String errorMsg) {

    }

    @Override
    public void onGCMRegistrationId(String gcmRegId) {
        //responseTv.setText("Registration Id on GCM--" + gcmRegId);
        App42GCMController.storeRegistrationId(this, gcmRegId);
        if(!App42GCMController.isApp42Registerd(MainActivity.this))
            App42GCMController.registerOnApp42(asyncService.getLoggedInUser(), gcmRegId, this);
    }

    @Override
    public void onApp42Response(String responseMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    @Override
    public void onRegisterApp42(String responseMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                App42GCMController.storeApp42Success(MainActivity.this);
            }
        });
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);

        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
    /**
     * Creates the alert dialog.
     *
     * @param msg the msg
     */
    public void createAlertDialog(String msg) {
        AlertDialog.Builder alertbox = new AlertDialog.Builder(MainActivity.this);
        alertbox.setTitle("Response Message");
        alertbox.setMessage(msg);
        alertbox.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            // do something when the button is clicked
            public void onClick(DialogInterface arg0, int arg1) {

            }
        });
        alertbox.show();
    }
}
