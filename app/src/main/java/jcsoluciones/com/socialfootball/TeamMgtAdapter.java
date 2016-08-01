package jcsoluciones.com.socialfootball;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.shephertz.app42.paas.sdk.android.storage.Storage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;

import jcsoluciones.com.socialfootball.R;

/**
 * Created by ADMIN on 01/08/2016.
 */
public class TeamMgtAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<Storage.JSONDocument> jsonList;

    public  TeamMgtAdapter (Activity activity,ArrayList<Storage.JSONDocument> jsonList){
        this.jsonList = jsonList;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return jsonList.size();
    }

    @Override
    public Object getItem(int position) {
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
            convertView = inflater.inflate(R.layout.list_row_teamsmanagement,null);
        TextView serial = (TextView) convertView.findViewById(R.id.serial);
        TextView title = (TextView) convertView.findViewById(R.id.title);

        try {
            JSONObject jsonObject = new JSONObject(jsonList.get(position).getJsonDoc());
            serial.setText(String.valueOf(jsonObject.getString("name")));
            title.setText(jsonObject.getString("descripcion"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return null;
    }
}
