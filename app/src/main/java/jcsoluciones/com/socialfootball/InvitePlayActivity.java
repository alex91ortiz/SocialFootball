package jcsoluciones.com.socialfootball;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;
import com.beardedhen.androidbootstrap.BootstrapLabel;
import com.beardedhen.androidbootstrap.BootstrapThumbnail;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.beardedhen.androidbootstrap.font.FontAwesome;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.push.PushNotification;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import com.squareup.picasso.Picasso;
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
import retrofit2.converter.gson.GsonConverterFactory;

public class InvitePlayActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener,DatePickerDialog.OnDateSetListener {
    /**
     * The name
     */
    private AwesomeTextView txvname;
    /**
     * The phone
     */
    private BootstrapLabel txvphone;
    /**
     * The description
     */
    private BootstrapLabel txvdescrip;
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
    private BootstrapButton sendnvite;
    private BootstrapButton mangerDate;
    private TextView txvdesc;
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
    private int flagAccept=1;
    private SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_play);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);


        mImg = (BootstrapCircleThumbnail) findViewById(R.id.ImageTeams);
        txvphone = (BootstrapLabel) findViewById(R.id.phone_label);
        txvcity = (BootstrapLabel) findViewById(R.id.city_label);
        sendnvite = (BootstrapButton) findViewById(R.id.button_event_edit_invite);
        txvdesc = (TextView) findViewById(R.id.layout_descripttion);
        txvname = (AwesomeTextView) findViewById(R.id.layout_name);

        //mangerDate = (BootstrapButton) findViewById(R.id.button_date);

/*        mangerDate.setOnClickListener(new View.OnClickListener() {
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
        });*/

        sessionManager = new SessionManager(this);
        Bundle bundle =getIntent().getExtras();
        if(bundle!=null){
            try {
                flagAccept = bundle.getInt("flagAccept");
                jsonObject = new JSONObject(bundle.getString("team",""));

                txvname.setText(jsonObject.getString("name"));
                txvdesc.setText(jsonObject.getString("desc"));
                txvcity.setText(jsonObject.getString("city"));
                txvphone.setText(jsonObject.getString("phone"));
                IdInvite = bundle.getString("IdInvite", "");
                IdTeams = bundle.getString("IdTeams", "");
                String selectedImage =Constants.HostServer+"/img/"+jsonObject.getString("_id")+"/profile.jpg";
                //new ImageLoader(mImg).execute(selectedImage);

                Picasso.with(this).load(selectedImage).into(mImg);
                if(!bundle.getString("invite","").isEmpty()) {
                    jsonObjectinvite = new JSONObject(bundle.getString("invite", ""));
                    if (jsonObjectinvite != null) {
                        dayOfMonth = jsonObjectinvite.getInt("datedayOfMonth");
                        monthOfYear = jsonObjectinvite.getInt("datemonthOfYear");
                        year = jsonObjectinvite.getInt("dateyear");
                        SimpleDateFormat format = new SimpleDateFormat("EEE d, MMMM", Locale.getDefault());
                        Calendar dat = Calendar.getInstance();
                        dat.set(year, monthOfYear, dayOfMonth);
                        //mangerDate.setText(format.format(dat.getTime()));
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        ActionEvent();
        /*if(flagAccept)
            Sendnvite.setText("Accept");*/
    }

    public void ActionEvent(){
        sendnvite.setBootstrapBrand(DefaultBootstrapBrand.PRIMARY);
        sendnvite.setText("Editar");
        if(flagAccept==1) {
            sendnvite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), TeamsMgtActivity.class);
                    intent.putExtra("object",jsonObject.toString());
                    startActivity(intent);
                }
            });


        }else if(flagAccept==2){
            sendnvite.setBootstrapBrand(DefaultBootstrapBrand.PRIMARY);
            sendnvite.setText("Aceptar");
            sendnvite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RequestInviteBody requestInviteBody = new RequestInviteBody();
                    requestInviteBody.setCreator(sessionManager.getUserDetails().get(sessionManager.ID_CONTENT).toString());
                    requestInviteBody.setAcceptinvite(true);
                    requestInviteBody.setMessage("");
                    requestInviteBody.setDatedayOfMonth(String.valueOf(dayOfMonth));
                    requestInviteBody.setDatemonthOfYear(String.valueOf(monthOfYear));
                    requestInviteBody.setDateyear(String.valueOf(year));
                    try {
                        requestInviteBody.setId(jsonObjectinvite.getString("_id").toString());
                        requestInviteBody.setFriends(jsonObject.getString("_id").toString());

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
                            //progressDialog.dismiss();
                            finish();
                        }

                        @Override
                        public void onFailure(Call<RequestInviteBody> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                            //progressDialog.dismiss();
                        }
                    });
                }
            });
        }else {
            sendnvite.setBootstrapBrand(DefaultBootstrapBrand.PRIMARY);
            sendnvite.setText("Invitar");
            sendnvite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    RequestInterface request = retrofit.create(RequestInterface.class);
                    Call<RequestInviteBody> call = request.registerInvite(requestInviteBody);
                    call.enqueue(new Callback<RequestInviteBody>() {
                        @Override
                        public void onResponse(Call<RequestInviteBody> call, Response<RequestInviteBody> response) {
                            RequestInviteBody responseBody = response.body();
                            Toast.makeText(getApplicationContext(), "successfully registered.", Toast.LENGTH_SHORT).show();
                            //finish();
                            //progressDialog.dismiss();
                        }

                        @Override
                        public void onFailure(Call<RequestInviteBody> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                            //progressDialog.dismiss();
                        }
                    });
                }
            });
        }

    }

    public void makeSendnvite(String message,String registrationId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("registrationId",registrationId);
            jsonObject.put("message",message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.HostServer)
                .addConverterFactory(JSONConverterFactory.create())
                .build();

        RequestInterface request = retrofit.create(RequestInterface.class);
        Call<JSONObject> call = request.send(jsonObject);
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
        if(flagAccept==2) {
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
