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
public class TeamsInviteAcceptAdapter extends BaseAdapter implements AsyncApp42ServiceApi.App42StorageServiceListener{

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

    /**
     * The async service.
     */
    private AsyncApp42ServiceApi asyncService;

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
        asyncService = AsyncApp42ServiceApi.instance(activity);
        makeInvite = (BootstrapButton) convertView.findViewById(R.id.button_invite);
        try {
            InvitejsonObject = new JSONObject(jsonList.get(position).getJsonDoc());
            IdInvite =jsonList.get(position).getDocId();
            if(InvitejsonObject.getString("id_Teams_invite").equals(sessionManager.getUserDetails().get(sessionManager.ID_CONTENT)))
                asyncService.findDocByDocId(Constants.App42DBName,"Teams",InvitejsonObject.getString("id_Teams_accept"),this);
            else
                asyncService.findDocByDocId(Constants.App42DBName,"Teams",InvitejsonObject.getString("id_Teams_invite"),this);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }

    @Override
    public void onDocumentInserted(Storage response) {

    }

    @Override
    public void onUpdateDocSuccess(Storage response) {

    }

    @Override
    public void onFindDocSuccess(Storage response) {
        final ArrayList<Storage.JSONDocument> jsonDocList = response.getJsonDocList();
        try {
            JSONObject jsonObject = new JSONObject(jsonDocList.get(0).getJsonDoc());
            if(mImg!=null) {
                new ImageLoader(mImg).execute(jsonObject.getString("ImageUrl"));
            }
            title.setText(jsonObject.getString("name"));
            message.setText(jsonObject.getString("desc"));
            if(InvitejsonObject.getBoolean("Accept_invite")){
                makeInvite.setShowOutline(false);
                makeInvite.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
                makeInvite.setFontAwesomeIcon(FontAwesome.FA_CHECK_CIRCLE);

            }else{
                makeInvite.setBootstrapBrand(DefaultBootstrapBrand.WARNING );
                makeInvite.setFontAwesomeIcon(FontAwesome.FA_CLOCK_O);
                makeInvite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(activity, InvitePlayActivity.class);
                        intent.putExtra("object", jsonDocList.get(0).getJsonDoc());
                        intent.putExtra("object2", InvitejsonObject.toString());
                        intent.putExtra("IdTeams", jsonDocList.get(0).getDocId());
                        intent.putExtra("IdInvite", IdInvite);

                        intent.putExtra("flagAccept", true);
                        activity.startActivity(intent);

                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
