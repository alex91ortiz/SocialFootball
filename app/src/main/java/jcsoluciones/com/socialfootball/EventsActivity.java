package jcsoluciones.com.socialfootball;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;
import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import jcsoluciones.com.socialfootball.models.RequestInviteBody;

import jcsoluciones.com.socialfootball.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EventsActivity extends AppCompatActivity  implements TimePickerDialog.OnTimeSetListener,DatePickerDialog.OnDateSetListener {
    private RelativeLayout teamOne,teamTwo;
    private ImageView vsplay;
    private TextView txvnameOne;
    private TextView txvnameTwo;
    private JSONObject jsonObjectTeamOne;
    private JSONObject jsonObjectTeamTwo;
    private JSONObject jsonObjectinvite;
    private BootstrapCircleThumbnail mImgTeamOne,mImgTeamTwo;
    private AwesomeTextView infoDate,infoTime;
    private BootstrapButton mangerDate,managerTime;
    private  int dayOfMonth,monthOfYear,year,hourDay,minute;
    private SessionManager sessionManager;
    private int flagAccept=1;
    private DatePickerDialog dpg;
    private TimePickerDialog tpg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        teamOne = (RelativeLayout) findViewById(R.id.TeamOne);
        teamTwo = (RelativeLayout) findViewById(R.id.TeamTwo);
        vsplay = (ImageView) findViewById(R.id.Vsplay);
        mImgTeamOne = (BootstrapCircleThumbnail) findViewById(R.id.ImageTeams);
        mImgTeamTwo = (BootstrapCircleThumbnail) findViewById(R.id.ImageTeams_inv);
        txvnameOne = (TextView) findViewById(R.id.title_team);
        txvnameTwo = (TextView) findViewById(R.id.title_team_inv);
        infoDate  = (AwesomeTextView) findViewById(R.id.info_date_text);
        infoTime  = (AwesomeTextView) findViewById(R.id.info_time_text);
        mangerDate = (BootstrapButton) findViewById(R.id.info_date);
        managerTime = (BootstrapButton) findViewById(R.id.info_time);

        sessionManager = new SessionManager(this);

        mangerDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                dpg = DatePickerDialog.newInstance(
                        EventsActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpg.show(getFragmentManager(), "Datepickerdialog");
            }
        });
        managerTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                tpg = TimePickerDialog.newInstance(
                        EventsActivity.this,
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        true
                );
                tpg.show(getFragmentManager(), "TimePickerDialog");
            }
        });
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
        Bundle bundle =getIntent().getExtras();
        if(bundle!=null){
            try {
                flagAccept = bundle.getInt("flagAccept");
                jsonObjectTeamOne = new JSONObject(bundle.getString("team",""));
                jsonObjectTeamTwo = new JSONObject(bundle.getString("friend",""));

                txvnameOne.setText(jsonObjectTeamOne.getString("name"));
                txvnameTwo.setText(jsonObjectTeamTwo.getString("name"));
                String selectedImage1 = Constants.HostServer+"img/"+jsonObjectTeamOne.getString("_id")+"/profile.jpg";
                String selectedImage2 = Constants.HostServer+"img/"+jsonObjectTeamTwo.getString("_id")+"/profile.jpg";

                Picasso.with(this).load(selectedImage1).into(mImgTeamOne);
                Picasso.with(this).load(selectedImage2).into(mImgTeamTwo);
                jsonObjectinvite = new JSONObject(bundle.getString("invite", ""));
                if(!bundle.getString("invite","").isEmpty()) {

                    if (jsonObjectinvite != null) {
                        dayOfMonth = jsonObjectinvite.getInt("datedayOfMonth");
                        monthOfYear = jsonObjectinvite.getInt("datemonthOfYear");
                        year = jsonObjectinvite.getInt("dateyear");
                        hourDay = jsonObjectinvite.getInt("timehour");
                        minute = jsonObjectinvite.getInt("timeminute");
                        SimpleDateFormat format = new SimpleDateFormat("EEE d, MM", Locale.getDefault());
                        Calendar dat = Calendar.getInstance();
                        dat.set(year, monthOfYear, dayOfMonth);
                        infoDate.setText(format.format(dat.getTime()));
                        infoTime.setText(hourDay +" : "+ minute);
                    }
                }
                teamOne.startAnimation(slideLeft);
                teamTwo.startAnimation(slideRigth);
                vsplay.startAnimation(zoomin);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tmgt, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_edit) {
            if (flagAccept == 2) {
                RequestInviteBody requestInviteBody = new RequestInviteBody();
                try {

                    requestInviteBody.setCreator(jsonObjectTeamOne.getString("_id").toString());
                    requestInviteBody.setAcceptinvite(true);
                    requestInviteBody.setMessage(jsonObjectTeamOne.getString("name").toString() + " VS " + jsonObjectTeamTwo.getString("name").toString());
                    requestInviteBody.setDatedayOfMonth(dayOfMonth);
                    requestInviteBody.setDatemonthOfYear(monthOfYear);
                    requestInviteBody.setDateyear(year);
                    requestInviteBody.setTimehour(hourDay);
                    requestInviteBody.setTimeminute(minute);
                    requestInviteBody.setStatus(false);
                    requestInviteBody.setId(jsonObjectinvite.getString("_id").toString());
                    requestInviteBody.setFriends(jsonObjectTeamTwo.getString("_id").toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(Constants.HostServer)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                RequestInterface request = retrofit.create(RequestInterface.class);
                Call<RequestInviteBody> call = request.updateInvite(requestInviteBody);
                call.enqueue(new Callback<RequestInviteBody>() {
                    @Override
                    public void onResponse(Call<RequestInviteBody> call, Response<RequestInviteBody> response) {
                        RequestInviteBody responseBody = response.body();
                        Toast.makeText(getApplicationContext(), "successfully registered.", Toast.LENGTH_SHORT).show();
                        finish();
                        //progressDialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<RequestInviteBody> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                        //progressDialog.dismiss();
                    }
                });
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        if(flagAccept==2) {
            this.dayOfMonth = dayOfMonth;
            this.monthOfYear = monthOfYear;
            this.year = year;
            SimpleDateFormat format = new SimpleDateFormat("EEE d, MM", Locale.getDefault());
            Calendar dat = Calendar.getInstance();
            dat.set(year, monthOfYear, dayOfMonth);
            infoDate.setText(format.format(dat.getTime()));
        }
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        this.hourDay = hourOfDay;
        this.minute = minute;
        infoTime.setText(hourOfDay +" : "+ minute);
    }
}
