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

import java.util.ArrayList;
import java.util.List;

public class MyclubActivity extends AppCompatActivity {
    /** Informacion */
    private String  AVATAR ="http://147.120.0.123:3000/img/57c4bc8c37cee530271588a3/profile.jpg";
    private String  PHONE;
    private String  CITY;

    private ViewPagerAdapter adapterViewpager;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ImageView imageview;
    private TextView  txtphone,txtcity,lblnivel,txtpj,txtpg,txtpe,txtpp;
    private ProgressBar pgbrnivel;

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
        imageview = (ImageView) findViewById(R.id.profile_img);
        txtphone = (TextView) findViewById(R.id.txt_phone);
        txtcity = (TextView) findViewById(R.id.txt_city);
        lblnivel = (TextView) findViewById(R.id.lbl_nivel);
        txtpj = (TextView) findViewById(R.id.txt_pj);
        txtpg = (TextView) findViewById(R.id.txt_pg);
        txtpe = (TextView) findViewById(R.id.txt_pe);
        txtpp = (TextView) findViewById(R.id.txt_pp);
        pgbrnivel = (ProgressBar) findViewById(R.id.pgbr_nivel);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        Picasso.with(this).load(AVATAR).into(imageview);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }
    private void setupViewPager(ViewPager viewPager) {
        adapterViewpager = new ViewPagerAdapter(getSupportFragmentManager());
        adapterViewpager.addFragment(new TeamMatchFragment(), "MATCHES");
        adapterViewpager.addFragment(new TeamMatchFragment(), "INFORMATIONS");
        adapterViewpager.addFragment(new TeamMatchFragment(), "PHOTOS");
        //adapterViewpager.addFragment(new TournamentsFragment(), "FOUR");

        viewPager.setAdapter(adapterViewpager);

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
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.fragment_myclub, container, false);
        }
    }
}
