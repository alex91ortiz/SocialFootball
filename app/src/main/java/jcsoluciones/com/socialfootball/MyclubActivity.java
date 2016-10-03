package jcsoluciones.com.socialfootball;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyclubActivity extends AppCompatActivity {
    /** Informacion */
    private static String  AVATAR ="http://147.120.0.123:3000/img/57c4bc8c37cee530271588a3/profile.jpg";
    private static String  PHONE;
    private static String  CITY;
    private static String  NAME;
    private String  NIVEL;
    /** objetos */
    private ViewPagerAdapter adapterViewpager;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ImageView imageview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myclub);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        imageview = (ImageView) findViewById(R.id.profile_img);
        setupInformation();
    }

    private void setupInformation(){
        Bundle bundle =getIntent().getExtras();
        if(bundle!=null){
            try {
                JSONObject jsonTeam = new JSONObject(bundle.getString("team",""));
                NAME = jsonTeam.getString("name");
                PHONE = jsonTeam.getString("phone");
                CITY = jsonTeam.getString("city");
                AVATAR = Constants.HostServer+"/img/"+jsonTeam.getString("_id")+"/profile.jpg";
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Picasso.with(this).load(AVATAR).resizeDimen(R.dimen.width,R.dimen.height).centerCrop().into(imageview);
    }

    private void setupViewPager(ViewPager viewPager) {
        adapterViewpager = new ViewPagerAdapter(getSupportFragmentManager());
        adapterViewpager.addFragment(new TeamMatchFragment(), "MATCHES");
        adapterViewpager.addFragment(new TeamMatchFragment(), "INFORMATIONS");
        adapterViewpager.addFragment(new TeamMatchFragment(), "PHOTOS");
        viewPager.setAdapter(adapterViewpager);
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
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
            //return null;
        }
    }

    public static class TeamMatchFragment extends Fragment {
        public TeamMatchFragment() {
            // Required empty public constructor
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View view = inflater.inflate(R.layout.fragment_myclub, container, false);

            TextView txtphone = (TextView) view.findViewById(R.id.txt_phone);
            TextView txtcity = (TextView) view.findViewById(R.id.txt_city);
            TextView lblnivel = (TextView) view.findViewById(R.id.lbl_nivel);
            TextView txtpj = (TextView) view.findViewById(R.id.txt_pj);
            TextView txtpg = (TextView) view.findViewById(R.id.txt_pg);
            TextView txtpe = (TextView) view.findViewById(R.id.txt_pe);
            TextView txtpp = (TextView) view.findViewById(R.id.txt_pp);
            ProgressBar pgbrnivel = (ProgressBar) view.findViewById(R.id.pgbr_nivel);
            txtphone.setText(PHONE);
            txtcity.setText(CITY);

            return view;
        }


    }
}
