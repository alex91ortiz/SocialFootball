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
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.beardedhen.androidbootstrap.font.FontAwesome;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import jcsoluciones.com.socialfootball.models.RequestInviteBody;
import jcsoluciones.com.socialfootball.utils.ImageLoader;
import jcsoluciones.com.socialfootball.utils.SessionManager;

/**
 * Created by ADMIN on 10/08/2016.
 */
public class TeamsInviteAcceptAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private JSONArray inviteBody;
    private BootstrapCircleThumbnail mImg;
    private TextView title;
    private TextView message;
    private BootstrapButton makeInvite;

    private SessionManager sessionManager;



    private int position;

    public TeamsInviteAcceptAdapter(Activity activity, JSONArray jsonList){
        this.inviteBody = jsonList;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return inviteBody.length();
    }

    @Override
    public Object getItem(int position) {

        try {
            return inviteBody.get(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
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
            final JSONObject jsonInvites = inviteBody.getJSONObject(position);
            final JSONObject jsonCreate = new JSONObject(jsonInvites.getString("create"));
            JSONObject jsonFriends = new JSONObject(jsonInvites.getString("friends"));
            if(jsonCreate.getString("email").equals(sessionManager.getUserDetails().get(sessionManager.KEY_EMAIL))){

                title.setText(jsonFriends.getString("name"));
                message.setText(jsonFriends.getString("desc"));

                if(jsonInvites.getBoolean("Acceptinvite")) {
                    makeInvite.setShowOutline(false);
                    makeInvite.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
                    makeInvite.setFontAwesomeIcon(FontAwesome.FA_CALENDAR);
                }else{
                    makeInvite.setBootstrapBrand(DefaultBootstrapBrand.WARNING );
                    makeInvite.setFontAwesomeIcon(FontAwesome.FA_CLOCK_O);
                }
            }

            if(jsonFriends.getString("email").equals(sessionManager.getUserDetails().get(sessionManager.KEY_EMAIL))){

                title.setText(jsonCreate.getString("name"));
                message.setText(jsonCreate.getString("desc"));

                if (jsonInvites.getBoolean("Acceptinvite")) {
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
                            intent.putExtra("team",jsonCreate.toString());
                            intent.putExtra("invite", jsonInvites.toString());
                            intent.putExtra("flagAccept", true);
                            activity.startActivity(intent);
                        }
                    });
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }
}
