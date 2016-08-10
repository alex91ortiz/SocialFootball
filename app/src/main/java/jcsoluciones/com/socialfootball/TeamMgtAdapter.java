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
    private ArrayList<Storage.JSONDocument> jsonList;
    private BootstrapCircleThumbnail mImg;
    private int position;

    private AsyncApp42ServiceApi asyncService;
    public  TeamMgtAdapter (Activity activity,ArrayList<Storage.JSONDocument> jsonList){
        this.jsonList = jsonList;
        this.activity = activity;
        asyncService = AsyncApp42ServiceApi.instance(activity);
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater==null)
            inflater= (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView==null)
            convertView = inflater.inflate(R.layout.list_group_teamsmanagement,null);

        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView message = (TextView) convertView.findViewById(R.id.message);

        try {
            JSONObject jsonObject = new JSONObject(jsonList.get(position).getJsonDoc());
            mImg=(BootstrapCircleThumbnail) convertView.findViewById(R.id.ImageTeams);

            if(mImg!=null) {
                new ImageLoader(mImg).execute(jsonObject.getString("ImageUrl"));
            }
            title.setText(jsonObject.getString("name"));
            message.setText(jsonObject.getString("desc"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return convertView;
    }

}
