package jcsoluciones.com.socialfootball;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;
import com.beardedhen.androidbootstrap.api.attributes.BootstrapBrand;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.beardedhen.androidbootstrap.font.FontAwesome;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

import jcsoluciones.com.socialfootball.utils.ImageLoader;
import jcsoluciones.com.socialfootball.utils.SessionManager;

/**
 * Created by ADMIN on 10/08/2016.
 */
public class TeamsInviteAcceptAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<Storage.JSONDocument> jsonList;
    private BootstrapCircleThumbnail mImg;
    private TextView title;
    private TextView message;
    private BootstrapButton makeInvite;
    private JSONObject InvitejsonObject;
    private String IdInvite;
    private SessionManager sessionManager;



    private int position;

    public TeamsInviteAcceptAdapter(Activity activity, ArrayList<Storage.JSONDocument> jsonList){
        this.jsonList = jsonList;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return jsonList.size();
    }

    @Override
    public Storage.JSONDocument getItem(int position) {
        return jsonList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (inflater==null)
            inflater= (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView==null)
            convertView = inflater.inflate(R.layout.list_row_inviteaccept,null);

        sessionManager = new SessionManager(activity);
        title = (TextView) convertView.findViewById(R.id.title);
        message = (TextView) convertView.findViewById(R.id.message);
        mImg=(BootstrapCircleThumbnail) convertView.findViewById(R.id.ImageTeams);
        makeInvite = (BootstrapButton) convertView.findViewById(R.id.button_invite);
        try {
            final JSONObject jsonObject = new JSONObject(jsonList.get(position).getJsonDoc());
            final JSONObject jsonObjectteamsacept = new JSONObject(jsonObject.getString("Teams_accept"));
            final JSONObject jsonObjectteamsinvite = new JSONObject(jsonObject.getString("Teams_invite"));
            if(jsonObjectteamsacept.getString("email").equals(sessionManager.getUserDetails().get(sessionManager.KEY_EMAIL))){
                if(mImg!=null) {
                    new ImageLoader(mImg).execute(jsonObjectteamsinvite.getString("ImageUrl"));
                }
                title.setText(jsonObjectteamsinvite.getString("name"));
                message.setText(jsonObjectteamsinvite.getString("desc"));

                //Accept_invite
                if(jsonObject.getBoolean("Accept_invite")) {
                    makeInvite.setShowOutline(false);
                    makeInvite.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
                    makeInvite.setFontAwesomeIcon(FontAwesome.FA_CALENDAR);
                }else{
                    makeInvite.setBootstrapBrand(DefaultBootstrapBrand.WARNING );
                    makeInvite.setFontAwesomeIcon(FontAwesome.FA_CLOCK_O);
                }
            }

            if(jsonObjectteamsinvite.getString("email").equals(sessionManager.getUserDetails().get(sessionManager.KEY_EMAIL))){
                if(mImg!=null) {
                    new ImageLoader(mImg).execute(jsonObjectteamsacept.getString("ImageUrl"));
                }
                title.setText(jsonObjectteamsacept.getString("name"));
                message.setText(jsonObjectteamsacept.getString("desc"));
                if(jsonObject.getBoolean("Accept_invite")) {
                    makeInvite.setShowOutline(false);
                    makeInvite.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
                    makeInvite.setFontAwesomeIcon(FontAwesome.FA_CALENDAR);
                }else{
                    makeInvite.setBootstrapBrand(DefaultBootstrapBrand.INFO);
                    makeInvite.setFontAwesomeIcon(FontAwesome.FA_CHECK_CIRCLE);
                    makeInvite.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            makeInvite.setShowOutline(false);
                            Intent intent = new Intent(activity, InvitePlayActivity.class);
                            intent.putExtra("object", jsonObjectteamsacept.toString());
                            intent.putExtra("object2", jsonList.get(position).getJsonDoc());
                            intent.putExtra("IdTeams", "");
                            intent.putExtra("IdInvite", jsonList.get(position).getDocId());

                            intent.putExtra("flagAccept", true);
                            activity.startActivity(intent);

                        }
                    });
                }



            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        /*

           if(InvitejsonObject.getBoolean("Accept_invite")){


            }else{

            }
        */
        return convertView;
    }
}
