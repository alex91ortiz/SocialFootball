package jcsoluciones.com.socialfootball;

import android.app.Activity;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;
import com.loopj.android.image.SmartImageView;
import com.shephertz.app42.paas.sdk.android.storage.Storage;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

import jcsoluciones.com.socialfootball.utils.ImageLoader;


/**
 * Created by ADMIN on 01/08/2016.
 */

public class TeamMgtAdapter extends BaseAdapter  {
    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<String> jsonList;
    private BootstrapCircleThumbnail mImg;
    private String id;
    private String name;


    private AsyncApp42ServiceApi asyncService;
    public  TeamMgtAdapter (Activity activity,ArrayList<String> jsonList,String id,String name){
        this.jsonList = jsonList;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return jsonList.size();
    }

    @Override
    public String getItem(int position) {
        return jsonList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater==null)
            inflater= (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView==null)
            convertView = inflater.inflate(R.layout.list_group_teamsmanagement,null);

        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView message = (TextView) convertView.findViewById(R.id.message);

        title.setText(jsonList.get(position).toString());

        mImg=(BootstrapCircleThumbnail) convertView.findViewById(R.id.ImageTeams);


        return convertView;
    }

}
