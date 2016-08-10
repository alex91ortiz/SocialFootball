package jcsoluciones.com.socialfootball;

import android.app.AlertDialog;
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

import jcsoluciones.com.socialfootball.utils.ImageLoader;

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
    private BootstrapCircleThumbnail mImg;
    private BootstrapButton Sendnvite;
    private  String IdTeams;

    /**
     * The async service.
     */
    private AsyncApp42ServiceApi asyncService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_play);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        asyncService = AsyncApp42ServiceApi.instance(this);

        mImg = (BootstrapCircleThumbnail) findViewById(R.id.ImageTeams);
        txvname = (TextView) findViewById(R.id.input_layout_name);
        txvphone = (TextView) findViewById(R.id.input_layout_phone);
        txvdescrip = (TextView) findViewById(R.id.input_layout_desc);
        txvcity = (TextView) findViewById(R.id.input_layout_city);
        txvemail = (TextView) findViewById(R.id.input_layout_email);
        Sendnvite = (BootstrapButton) findViewById(R.id.button_event);


        Bundle bundle =getIntent().getExtras();
        if(bundle!=null){
            try {
                jsonObject = new JSONObject(bundle.getString("object",""));
                txvname.setText(jsonObject.getString("name"));
                txvphone.setText(jsonObject.getString("phone"));
                txvdescrip.setText(jsonObject.getString("desc"));
                txvcity.setText(jsonObject.getString("city"));
                txvemail.setText(jsonObject.getString("email"));
                IdTeams = bundle.getString("IdTeams", "");
                new ImageLoader(mImg).execute(jsonObject.getString("ImageUrl"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void SendInvite(View view){
        JSONObject jsonObjectInvite = new JSONObject();
        try {
            jsonObjectInvite.put("email",txvemail);
            jsonObject.put("Accept_invite",false);
            jsonObjectInvite.put("Teams",jsonObject);
            asyncService.insertJSONDoc(Constants.App42DBName,"Invites",jsonObjectInvite,this);
        } catch (JSONException e) {
            e.printStackTrace();
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
        asyncService.onSendPushMessageUser("invite","Tienes una invitacion",this);
    }


    @Override
    public void onSendPushMessageFailed(App42Exception exception) {

    }

    @Override
    public void onSendPushMessageUserSuccess(PushNotification response) {

    }

    @Override
    public void onSendPushMessageUserFailed(App42Exception exception) {

    }

    @Override
    public void onDocumentInserted(Storage response) {

    }

    @Override
    public void onUpdateDocSuccess(Storage response) {

    }

    @Override
    public void onFindDocSuccess(Storage response) {

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

}
