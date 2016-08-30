package jcsoluciones.com.socialfootball;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Query;
import com.shephertz.app42.paas.sdk.android.storage.QueryBuilder;
import com.shephertz.app42.paas.sdk.android.storage.Storage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jcsoluciones.com.socialfootball.utils.ImageLoader;
import jcsoluciones.com.socialfootball.utils.SessionManager;

/**
 * Created by Admin on 31/07/2016.
 */

public class TeamManagementFragment extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener {
    /**
     *  Button for add team
     */
    private FloatingActionButton fabEditTeamMgt;

    /**
     * List of your Teams setting
     */
    private ArrayList<Storage.JSONDocument> listTeamJson = new ArrayList<Storage.JSONDocument>();
    /** The storage service. */

    private SessionManager sessionManager;
    private TextView name;
    private TextView desc;
    private BootstrapButton btnedit;
    private BootstrapCircleThumbnail mImg;
    public TeamManagementFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_teamsmanagement, container, false);
        btnedit =(BootstrapButton) view.findViewById(R.id.button_event);
        btnedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), TeamsMgtActivity.class);
                intent.putExtra("object", sessionManager.getUserDetails().get(sessionManager.CONTENT));
                intent.putExtra("IdTeams",sessionManager.getUserDetails().get(sessionManager.ID_CONTENT));
                intent.putExtra("createOrupdate",false);
                startActivity(intent);
            }
        });


        name =(TextView) view.findViewById(R.id.layout_name);
        desc =(TextView) view.findViewById(R.id.layout_desc);
        mImg = (BootstrapCircleThumbnail) view.findViewById(R.id.ImageTeams);

        sessionManager = new SessionManager(getContext());
        fabEditTeamMgt = (FloatingActionButton) view.findViewById(R.id.fabEdit);
        fabEditTeamMgt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!sessionManager.isLoggedIn()) {
                    Intent intent = new Intent(getContext(), SignInActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getContext(), TeamsMgtActivity.class);
                    startActivity(intent);
                }
            }
        });

        JSONObject jsonObject = null;
        try {
            if(sessionManager.getUserDetails().get(sessionManager.CONTENT).isEmpty()){
                onRefresh();
            }else{
                jsonObject = new JSONObject(sessionManager.getUserDetails().get(sessionManager.CONTENT));
                name.setText(jsonObject.getString("name"));
                desc.setText(jsonObject.getString("desc"));
                String selectedImage =Constants.HostServer+"/img/"+jsonObject.getString("_id")+"/profile.jpg";
                new ImageLoader(mImg).execute(selectedImage);
                fabEditTeamMgt.setVisibility(View.INVISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void onRefresh(){

    }

    /*@Override
    public void onFindDocSuccess(Storage response) {

        final ArrayList<Storage.JSONDocument> jsonDocList = response.getJsonDocList();
        if(jsonDocList.size()>0) {

            fabEditTeamMgt.setVisibility(View.INVISIBLE);
            try {
                JSONObject jsonObject = new JSONObject(jsonDocList.get(0).getJsonDoc());
                name.setText(jsonObject.getString("name"));
                desc.setText(jsonObject.getString("desc"));
                String selectedImage = jsonObject.getString("ImageUrl");
                new ImageLoader(mImg).execute(selectedImage);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            sessionManager.createContentSession(jsonDocList.get(0).getDocId(),jsonDocList.get(0).getJsonDoc());
        }
    }*/

    /**
     * Creates the alert dialog.
     *
     * @param msg the msg
     */
    public void createAlertDialog(String msg) {
        AlertDialog.Builder alertbox = new AlertDialog.Builder(getContext());
        alertbox.setTitle("Response Message");
        alertbox.setMessage(msg);
        alertbox.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            // do something when the button is clicked
            public void onClick(DialogInterface arg0, int arg1) {

            }
        });
        alertbox.show();
    }
}
