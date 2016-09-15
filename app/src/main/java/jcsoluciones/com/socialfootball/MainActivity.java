package jcsoluciones.com.socialfootball;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
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
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import jcsoluciones.com.socialfootball.models.JSONConverterFactory;
import jcsoluciones.com.socialfootball.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

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
    private  Activity activity;
    private SessionManager sessionManager;
    private boolean isReceiverRegistered;
    private ViewPagerAdapter adapterViewpager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        activity=this;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sessionManager  = new SessionManager(this);
        sessionManager.getUserDetails().get(sessionManager.KEY_EMAIL);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        searchList = (ListView) findViewById(R.id.listsearch);

        tabLayout = (TabLayout) findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(viewPager);

        setupTabIcons();
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                /*if(searchMenuItem!=null)
                    searchMenuItem.setVisible(tab.getPosition() == 0);*/
                viewPager.setVisibility(View.VISIBLE);
                searchList.setVisibility(View.INVISIBLE);
                /*switch (tab.getPosition()){
                    case 0:
                        tabLayout.getTabAt(tab.getPosition()).setIcon(R.drawable.ic_web_yellow_24px);
                        break;
                    case 1:
                        tabLayout.getTabAt(tab.getPosition()).setIcon(R.drawable.ic_public_yellow_24px);
                        break;
                    case 2:
                        tabLayout.getTabAt(tab.getPosition()).setIcon(R.drawable.ic_group_work_yellow_24px);
                        break;
                }*/

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                /*switch (tab.getPosition()){
                    case 0:
                        tabLayout.getTabAt(tab.getPosition()).setIcon(R.drawable.ic_web_green_24px);
                        break;
                    case 1:
                        tabLayout.getTabAt(tab.getPosition()).setIcon(R.drawable.ic_public_green_24px);
                        break;
                    case 2:
                        tabLayout.getTabAt(tab.getPosition()).setIcon(R.drawable.ic_group_work_green_24px);
                        break;
                }*/
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

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

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_web_black_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_public_black_24dp);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_group_work_black_24dp);
        //tabLayout.getTabAt(3).setIcon(R.drawable.ic_web_green_24px);
    }

    private void setupViewPager(ViewPager viewPager) {
        adapterViewpager = new ViewPagerAdapter(getSupportFragmentManager());
        adapterViewpager.addFragment(new TeamsEventsFragment(), "ONE");
        adapterViewpager.addFragment(new TeamsInviteAcceptFragment(), "TWO");
        adapterViewpager.addFragment(new TeamManagementFragment(), "THREE");
        //adapterViewpager.addFragment(new TournamentsFragment(), "FOUR");

        viewPager.setAdapter(adapterViewpager);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        searchMenuItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        mSearchView.setOnQueryTextListener(this);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, MainActivity.class)));
        mSearchView.setIconifiedByDefault(false);
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
        if(newText.length()>0) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.HostServer)
                    .addConverterFactory(JSONConverterFactory.create())
                    .build();

            RequestInterface request = retrofit.create(RequestInterface.class);
            Call<JSONArray> call = request.searchTeams(newText, newText,sessionManager.getUserDetails().get(sessionManager.KEY_EMAIL));
            call.enqueue(new Callback<JSONArray>() {
                    @Override
                    public void onResponse(Call<JSONArray> call, Response<JSONArray> response) {
                        JSONArray teambody = response.body();
                        if (teambody != null && teambody.length() > 0) {
                            adapter = new SearchTeamsAdapter(activity, teambody);
                            searchList.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onFailure(Call<JSONArray> call, Throwable t) {

                    }
                }
            );
        }
        return false;
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
            //return mFragmentTitleList.get(position);
            return null;
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

                viewPager.setCurrentItem(2);
                FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
                fragTransaction.detach(adapterViewpager.getItem(2));
                fragTransaction.attach(adapterViewpager.getItem(2));
                fragTransaction.commit();

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

    public class SearchTeamsAdapter extends BaseAdapter {
        private Activity activity;
        private LayoutInflater inflater;
        private  JSONArray teambody;
        private BootstrapCircleThumbnail mImg;
        private SessionManager sessionManager;
        private int position;
        /**
         * The async service.
         */

        public SearchTeamsAdapter(Activity activity, JSONArray teambody){
            this.teambody = teambody;
            this.activity = activity;
            sessionManager = new SessionManager(activity.getApplicationContext());
        }

        @Override
        public int getCount() {
            return teambody.length();
        }

        @Override
        public Object getItem(int position) {
            try {
                return teambody.get(position);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (inflater==null)
                inflater= (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if(convertView==null) {
                convertView = inflater.inflate(R.layout.list_row_events, null);
                viewHolder = new ViewHolder();
                viewHolder.text1 = (TextView) convertView.findViewById(R.id.title);
                viewHolder.text2 = (TextView) convertView.findViewById(R.id.message);
                viewHolder.mImg =(BootstrapCircleThumbnail) convertView.findViewById(R.id.ImageTeams);
                viewHolder.divider = (View) convertView.findViewById(R.id.divider);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            try {
                if(teambody.getJSONObject(position)!=null) {

                    viewHolder.text1 = (TextView) convertView.findViewById(R.id.title);
                    viewHolder.text2 = (TextView) convertView.findViewById(R.id.message);
                    viewHolder.mImg=(BootstrapCircleThumbnail) convertView.findViewById(R.id.ImageTeams);

                    BootstrapButton makeInvite = (BootstrapButton) convertView.findViewById(R.id.button_invite);
                    final JSONObject jsonteambody = teambody.getJSONObject(position);
                    viewHolder.text1.setText(jsonteambody.getString("name").toString());
                    viewHolder.text2.setText(jsonteambody.getString("desc").toString());
                    String selectedImage = Constants.HostServer + "/img/" + jsonteambody.getString("_id") + "/profile.jpg";
                    Picasso.with(activity).load(selectedImage).into(viewHolder.mImg);
                    makeInvite.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!sessionManager.isLoggedIn()) {
                                Intent intent = new Intent(activity, SignInActivity.class);
                                activity.startActivity(intent);
                            } else {
                                Intent intent = new Intent(activity, InvitePlayActivity.class);
                                intent.putExtra("team", jsonteambody.toString());
                                intent.putExtra("invite", "");
                                intent.putExtra("flagAccept", false);
                                activity.startActivity(intent);
                            }
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return convertView;
        }
        private  class ViewHolder {
            public TextView text1;
            public TextView text2;
            public BootstrapCircleThumbnail mImg;
            public View divider;
        }
    }
}
