package jcsoluciones.com.socialfootball;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;
import com.beardedhen.androidbootstrap.BootstrapLabel;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AcceptInviteActivity extends AppCompatActivity {
    private RelativeLayout teamOne,teamTwo;
    private ImageView vsplay;
    private TextView txvnameOne;
    private TextView txvnameTwo;
    private JSONObject jsonObjectTeamOne;
    private JSONObject jsonObjectTeamTwo;
    private JSONObject jsonObjectinvite;
    private BootstrapCircleThumbnail mImgTeamOne,mImgTeamTwo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_invite);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        teamOne = (RelativeLayout) findViewById(R.id.TeamOne);
        teamTwo = (RelativeLayout) findViewById(R.id.TeamTwo);
        vsplay = (ImageView) findViewById(R.id.Vsplay);

        //ObjectAnimator imageViewObjectAnimator = ObjectAnimator.ofFloat(teamOne , "rotation", 0f, 360f);
        Animation slideLeft = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_left);
        Animation slideRigth = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_rigth);
        final Animation zoomin = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoomin);
        final Animation zoomout = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoomout);
        zoomin.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                vsplay.startAnimation(zoomout);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        teamOne.startAnimation(slideLeft);
        teamTwo.startAnimation(slideRigth);
        vsplay.startAnimation(zoomin);

        mImgTeamOne = (BootstrapCircleThumbnail) findViewById(R.id.ImageTeams);
        mImgTeamTwo = (BootstrapCircleThumbnail) findViewById(R.id.ImageTeams_inv);
        txvnameOne = (TextView) findViewById(R.id.title_team);
        txvnameTwo = (TextView) findViewById(R.id.title_team_inv);

        Bundle bundle =getIntent().getExtras();
        if(bundle!=null){
            try {
                jsonObjectTeamOne = new JSONObject(bundle.getString("team",""));
                jsonObjectTeamTwo = new JSONObject(bundle.getString("friend",""));


                txvnameOne.setText(jsonObjectTeamOne.getString("name"));
                txvnameTwo.setText(jsonObjectTeamTwo.getString("name"));
                String selectedImage1 =Constants.HostServer+"/img/"+jsonObjectTeamOne.getString("_id")+"/profile.jpg";
                String selectedImage2 =Constants.HostServer+"/img/"+jsonObjectTeamTwo.getString("_id")+"/profile.jpg";

                Picasso.with(this).load(selectedImage1).into(mImgTeamOne);
                Picasso.with(this).load(selectedImage2).into(mImgTeamOne);
               /* jsonObjectinvite = new JSONObject(bundle.getString("invite", ""));
                if(!bundle.getString("invite","").isEmpty()) {

                    if (jsonObjectinvite != null) {
                        dayOfMonth = jsonObjectinvite.getInt("datedayOfMonth");
                        monthOfYear = jsonObjectinvite.getInt("datemonthOfYear");
                        year = jsonObjectinvite.getInt("dateyear");
                        SimpleDateFormat format = new SimpleDateFormat("EEE d, MMMM", Locale.getDefault());
                        Calendar dat = Calendar.getInstance();
                        dat.set(year, monthOfYear, dayOfMonth);
                        //mangerDate.setText(format.format(dat.getTime()));
                    }
                }*/

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


}
