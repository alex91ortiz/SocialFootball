package jcsoluciones.com.socialfootball;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


/**
 * Created by ADMIN on 24/09/2016.
 */
public class AdapterFlagViewColors extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private int[] flagViewColorsList;

    public AdapterFlagViewColors(Activity activity, int[] flagViewColorsList) {
        this.activity = activity;
        this.flagViewColorsList = flagViewColorsList;
    }

    @Override
    public int getCount() {
        return flagViewColorsList.length;
    }

    @Override
    public Object getItem(int position) {
        return flagViewColorsList[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (inflater==null)
            inflater= (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView==null) {
            convertView = inflater.inflate(R.layout.list_flag_colors, null);
            viewHolder = new ViewHolder();
            viewHolder.view1 =  convertView.findViewById(R.id.view_flag_1);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.view1.setBackgroundResource(flagViewColorsList[position]);
        return convertView;
    }

    private  class ViewHolder {
        public View view1;
    }
}
