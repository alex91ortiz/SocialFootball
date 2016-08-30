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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import jcsoluciones.com.socialfootball.models.RequestTeamBody;
import jcsoluciones.com.socialfootball.utils.ImageLoader;
import jcsoluciones.com.socialfootball.utils.SessionManager;

/**
 * Created by Admin on 07/08/2016.
 */
public class SearchTeamsAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private  JSONArray teambody;
    private BootstrapCircleThumbnail mImg;
    private SessionManager sessionManager;
    private int position;
    /**
     * The async service.
     */

    public SearchTeamsAdapter(Activity activity, JSONArray teambody){
        this.teambody = teambody;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return teambody.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return teambody.get(position);
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
            convertView = inflater.inflate(R.layout.list_row_events,null);

        sessionManager = new SessionManager(activity.getApplicationContext());
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView message = (TextView) convertView.findViewById(R.id.message);
        mImg=(BootstrapCircleThumbnail) convertView.findViewById(R.id.ImageTeams);
        BootstrapButton makeInvite = (BootstrapButton) convertView.findViewById(R.id.button_invite);

        try {
            final JSONObject jsonteambody = teambody.getJSONObject(position);
            title.setText(jsonteambody.getString("name").toString());
            message.setText(jsonteambody.getString("desc").toString());
            String selectedImage =Constants.HostServer+"/img/"+jsonteambody.getString("_id")+"/profile.jpg";
            //new ImageLoader(mImg).execute(selectedImage);
            Picasso.with(activity).load(selectedImage).into(mImg);
            makeInvite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!sessionManager.isLoggedIn()) {
                        Intent intent = new Intent(activity, SignInActivity.class);
                        activity.startActivity(intent);
                    } else {
                        Intent intent = new Intent(activity, InvitePlayActivity.class);
                        intent.putExtra("team", jsonteambody.toString());
                        intent.putExtra("invite","" );
                        intent.putExtra("flagAccept", false);
                        activity.startActivity(intent);
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }

}
