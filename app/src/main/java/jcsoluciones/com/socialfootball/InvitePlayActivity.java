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
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;
import jcsoluciones.com.socialfootball.provider.JSONConverterFactory;
import jcsoluciones.com.socialfootball.provider.RequestInviteBody;
import jcsoluciones.com.socialfootball.provider.RequestInterface;
import jcsoluciones.com.socialfootball.util.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InvitePlayActivity extends AppCompatActivity{
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

    private TextView txvdesc;
    private  String IdTeams;
    private  String IdInvite;

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
                jsonObjectinvite = new JSONObject(bundle.getString("invite", ""));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //ActionEvent();
        /*if(flagAccept)
            Sendnvite.setText("Accept");*/
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

}
