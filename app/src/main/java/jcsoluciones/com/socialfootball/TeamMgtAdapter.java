package jcsoluciones.com.socialfootball;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import com.shephertz.app42.paas.sdk.android.upload.Upload;
import com.shephertz.app42.paas.sdk.android.upload.UploadFileType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.InputStream;
import java.util.ArrayList;

import jcsoluciones.com.socialfootball.R;

/**
 * Created by ADMIN on 01/08/2016.
 */

public class TeamMgtAdapter extends BaseAdapter implements AsyncApp42ServiceApi.App42UploadServiceListener {
    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<Storage.JSONDocument> jsonList;
    private ImageView mImg;
    /**
     * The async service.
     */
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

        TextView title = (TextView) convertView.findViewById(R.id.title);

        try {
            JSONObject jsonObject = new JSONObject(jsonList.get(position).getJsonDoc());
            mImg=(ImageView) convertView.findViewById(R.id.ImageTeams);
            asyncService.getImage(jsonObject.getString("name"),this);
            title.setText(jsonObject.getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return convertView;
    }

    @Override
    public void onUploadImageSuccess(Upload response) {

    }

    @Override
    public void onUploadImageFailed(App42Exception ex) {

    }

    @Override
    public void onGetImageSuccess(Upload response) {
        Upload upload = (Upload)response;
        ArrayList<Upload.File> fileList = upload.getFileList();
        for(int i = 0; i < fileList.size();i++ )
        {
            new DownloadImageTask(mImg).execute(fileList.get(i).getUrl());
        }
    }

    @Override
    public void onGetImageFailed(App42Exception ex) {

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
