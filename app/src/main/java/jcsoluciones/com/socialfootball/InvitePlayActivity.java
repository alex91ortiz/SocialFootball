package jcsoluciones.com.socialfootball;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.push.PushNotification;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import jcsoluciones.com.socialfootball.models.JSONConverterFactory;
import jcsoluciones.com.socialfootball.models.RequestInviteBody;
import jcsoluciones.com.socialfootball.models.ResponseBody;
import jcsoluciones.com.socialfootball.utils.ImageLoader;
import jcsoluciones.com.socialfootball.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class InvitePlayActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener,DatePickerDialog.OnDateSetListener {
    /**
     * The name
     */
    private TextView txvname;
    /**
     * The phone
     */
    private TextView txvphone;
    /**
     * The description
     */
    private TextView txvdescrip;
    /**
     * The email
     */
    private TextView txvemail;
    /**
     * The city
     */
    private TextView txvcity;
    private JSONObject jsonObject;
    private JSONObject jsonObjectinvite;
    private BootstrapCircleThumbnail mImg;
    private BootstrapButton Sendnvite;
    private BootstrapButton mangerDate;
    private  String IdTeams;
    private  String IdInvite;
    private  int dayOfMonth;
    private  int monthOfYear;
    private  int year;
    /**
     * The progress dialog.
     */
    private ProgressDialog progressDialog;
    /**
     * The progress dialog.
     */
    private Boolean flagAccept=false;
    private SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_play);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mImg = (BootstrapCircleThumbnail) findViewById(R.id.ImageTeams);
        txvname = (TextView) findViewById(R.id.input_layout_name);
        txvphone = (TextView) findViewById(R.id.input_layout_phone);
        txvdescrip = (TextView) findViewById(R.id.input_layout_desc);
        txvcity = (TextView) findViewById(R.id.input_layout_city);
        txvemail = (TextView) findViewById(R.id.input_layout_email);
        Sendnvite = (BootstrapButton) findViewById(R.id.button_event);
        mangerDate = (BootstrapButton) findViewById(R.id.button_date);

        mangerDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        InvitePlayActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });

        sessionManager = new SessionManager(this);
        Bundle bundle =getIntent().getExtras();
        if(bundle!=null){
            try {
                flagAccept = bundle.getBoolean("flagAccept", false);
                jsonObject = new JSONObject(bundle.getString("team",""));

                txvname.setText(jsonObject.getString("name"));
                txvphone.setText(jsonObject.getString("phone"));
                txvdescrip.setText(jsonObject.getString("desc"));
                txvcity.setText(jsonObject.getString("city"));
                txvemail.setText(jsonObject.getString("email"));
                IdInvite = bundle.getString("IdInvite", "");
                IdTeams = bundle.getString("IdTeams", "");


                jsonObjectinvite = new JSONObject(bundle.getString("invite",""));
                if(jsonObjectinvite!=null) {
                    dayOfMonth = jsonObjectinvite.getInt("date_dayOfMonth");
                    monthOfYear = jsonObjectinvite.getInt("date_monthOfYear");
                    year = jsonObjectinvite.getInt("date_year");
                    SimpleDateFormat format = new SimpleDateFormat("EEE d, MMMM", Locale.getDefault());
                    Calendar dat = Calendar.getInstance();
                    dat.set(year, monthOfYear, dayOfMonth);
                    mangerDate.setText(format.format(dat.getTime()));
                   // new ImageLoader(mImg).execute(jsonObject.getString("ImageUrl"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(flagAccept)
            Sendnvite.setText("Accept");
    }

    public void SendInvite(View view){
        progressDialog = ProgressDialog.show(this, "", "Invitando..");
        progressDialog.setCancelable(true);
        if(flagAccept){
            try {
                jsonObjectinvite.put("Accept_invite", true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {

            RequestInviteBody requestInviteBody = new RequestInviteBody();
            requestInviteBody.setCreator(sessionManager.getUserDetails().get(sessionManager.ID_CONTENT).toString());
            requestInviteBody.setAcceptinvite(false);
            requestInviteBody.setMessage("");
            requestInviteBody.setDatedayOfMonth(String.valueOf(dayOfMonth));
            requestInviteBody.setDatemonthOfYear(String.valueOf(monthOfYear));
            requestInviteBody.setDateyear(String.valueOf(year));
            try {
                requestInviteBody.setFriends(jsonObject.getString("_id").toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.HostServer)
                    .addConverterFactory(JSONConverterFactory.create())
                    .build();

            RequestInterface request = retrofit.create(RequestInterface.class);
            Call<JSONObject> call = request.registerInvite(requestInviteBody);
            call.enqueue(new Callback<JSONObject>() {
                @Override
                public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                    JSONObject responseBody = response.body();
                    Toast.makeText(getApplicationContext(), "successfully registered.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<JSONObject> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        }

    }


        //asyncService.onSendPushMessageUser(txvemail.getText().toString(), "Tienes una invitacion",this);





        //asyncService.onSendPushMessageUser(txvemail.getText().toString(), "Aceptaron tu invitacion",this);



    /**
     * Creates the alert dialog.
     *
     * @param msg the msg
     */
    public void createAlertDialog(String msg) {
        AlertDialog.Builder alertbox = new AlertDialog.Builder(InvitePlayActivity.this);
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
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        if(!flagAccept) {
            this.dayOfMonth = dayOfMonth;
            this.monthOfYear = monthOfYear;
            this.year = year;
            SimpleDateFormat format = new SimpleDateFormat("EEE d, MMMM", Locale.getDefault());
            Calendar dat = Calendar.getInstance();
            dat.set(year, monthOfYear, dayOfMonth);
            mangerDate.setText(format.format(dat.getTime()));
        }
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {

    }
}
