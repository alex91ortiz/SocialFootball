package jcsoluciones.com.socialfootball;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.loopj.android.image.SmartImageView;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.push.PushNotification;
import com.shephertz.app42.paas.sdk.android.storage.Storage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jcsoluciones.com.socialfootball.utils.ImageLoader;
import jcsoluciones.com.socialfootball.utils.SessionManager;

public class InvitePlayActivity extends AppCompatActivity implements AsyncApp42ServiceApi.App42PushNotificationServiceListener,AsyncApp42ServiceApi.App42StorageServiceListener {
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
    private  String IdTeams;
    private  String IdInvite;
    private  String emailSend;

    /**
     * The async service.
     */
    private AsyncApp42ServiceApi asyncService;
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
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        asyncService = AsyncApp42ServiceApi.instance(this);

        mImg = (BootstrapCircleThumbnail) findViewById(R.id.ImageTeams);
        txvname = (TextView) findViewById(R.id.input_layout_name);
        txvphone = (TextView) findViewById(R.id.input_layout_phone);
        txvdescrip = (TextView) findViewById(R.id.input_layout_desc);
        txvcity = (TextView) findViewById(R.id.input_layout_city);
        txvemail = (TextView) findViewById(R.id.input_layout_email);
        Sendnvite = (BootstrapButton) findViewById(R.id.button_event);

        sessionManager = new SessionManager(this);
        Bundle bundle =getIntent().getExtras();
        if(bundle!=null){
            try {
                flagAccept = bundle.getBoolean("flagAccept", false);
                jsonObject = new JSONObject(bundle.getString("object",""));

                    txvname.setText(jsonObject.getString("name"));
                    txvphone.setText(jsonObject.getString("phone"));
                    txvdescrip.setText(jsonObject.getString("desc"));
                    txvcity.setText(jsonObject.getString("city"));
                    txvemail.setText(jsonObject.getString("email"));
                    IdInvite = bundle.getString("IdInvite", "");
                    IdTeams = bundle.getString("IdTeams", "");
                    jsonObjectinvite = new JSONObject(bundle.getString("object2",""));
                    new ImageLoader(mImg).execute(jsonObject.getString("ImageUrl"));

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
                asyncService.updateDocById(Constants.App42DBName, "Invites", IdInvite, jsonObjectinvite, this);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            JSONObject jsonObjectInvite = new JSONObject();

            try {
                jsonObjectInvite.put("id_Teams_invite", sessionManager.getUserDetails().get(sessionManager.ID_CONTENT));
                jsonObjectInvite.put("Accept_invite", false);
                jsonObjectInvite.put("id_Teams_accept", IdTeams);
                asyncService.insertJSONDoc(Constants.App42DBName, "Invites", jsonObjectInvite, this);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }


    @Override
    public void onCreationStoreDeviceTokenSuccess(PushNotification response) {

    }

    @Override
    public void onStoreDeviceTokenFailed(App42Exception exception) {

    }

    @Override
    public void onCreateChannelSuccess(PushNotification response) {

    }

    @Override
    public void onCreateChannelFailed(App42Exception exception) {

    }

    @Override
    public void onSubscribeToChannelSuccess(PushNotification response) {

    }

    @Override
    public void onSubscribeToChannelFailed(App42Exception exception) {

    }

    @Override
    public void onSendPushMessageSuccess(PushNotification response) {

    }


    @Override
    public void onSendPushMessageFailed(App42Exception exception) {

    }

    @Override
    public void onSendPushMessageUserSuccess(PushNotification response) {
        //progressDialog.dismiss();
        finish();
    }

    @Override
    public void onSendPushMessageUserFailed(App42Exception exception) {
        createAlertDialog("fallo envio push"+exception.getMessage());
    }

    @Override
    public void onDocumentInserted(Storage response) {
        asyncService.onSendPushMessageUser(txvemail.getText().toString(), "Tienes una invitacion",this);

    }

    @Override
    public void onUpdateDocSuccess(Storage response) {
        asyncService.onSendPushMessageUser(txvemail.getText().toString(), "Aceptaron tu invitacion",this);

    }

    @Override
    public void onFindDocSuccess(Storage response) {

    }

    @Override
    public void onDeleteDocSuccess() {

    }

    @Override
    public void onInsertionFailed(App42Exception ex) {
        createAlertDialog("fallo insercion"+ex.getMessage());
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
