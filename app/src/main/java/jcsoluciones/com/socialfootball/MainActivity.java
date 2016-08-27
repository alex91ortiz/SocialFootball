package jcsoluciones.com.socialfootball;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
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

import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Query;
import com.shephertz.app42.paas.sdk.android.storage.QueryBuilder;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import java.util.ArrayList;
import java.util.List;

import jcsoluciones.com.socialfootball.utils.SessionManager;

public class MainActivity extends AppCompatActivity implements
        SearchView.OnQueryTextListener,
        AsyncApp42ServiceApi.App42StorageServiceListener {

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
    private static final String TAG = "MainActivity";
    public static final String REGISTRATION_PROCESS = "registration";
    public static final String MESSAGE_RECEIVED = "message_received";

    private SessionManager sessionManager;
    private boolean isReceiverRegistered;


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
        sessionManager.getUserDetails().get(sessionManager.KEY_EMAIL);

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

        registerReceiver();

        Intent intent = getIntent();
        if(intent != null){
            if(intent.getAction().equals(MESSAGE_RECEIVED)){
                String message = intent.getStringExtra("message");
                showAlertDialog(message);
            }
        }

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
