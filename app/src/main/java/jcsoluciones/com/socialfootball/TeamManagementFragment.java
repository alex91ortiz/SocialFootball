package jcsoluciones.com.socialfootball;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Query;
import com.shephertz.app42.paas.sdk.android.storage.QueryBuilder;
import com.shephertz.app42.paas.sdk.android.storage.Storage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import jcsoluciones.com.socialfootball.models.JSONConverterFactory;
import jcsoluciones.com.socialfootball.models.RequestTeamBody;
import jcsoluciones.com.socialfootball.plugin.RegistrationIntentService;
import jcsoluciones.com.socialfootball.utils.ImageLoader;
import jcsoluciones.com.socialfootball.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Admin on 31/07/2016.
 */

public class TeamManagementFragment extends ListFragment implements AdapterView.OnItemClickListener {
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

       /* View view;
        view = inflater.inflate(R.layout.fragment_teamsmanagement, container, false);
        btnedit = (BootstrapButton) view.findViewById(R.id.button_event);
        btnedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), TeamsMgtActivity.class);
                intent.putExtra("object", sessionManager.getUserDetails().get(sessionManager.CONTENT));
                intent.putExtra("IdTeams", sessionManager.getUserDetails().get(sessionManager.ID_CONTENT));
                intent.putExtra("createOrupdate", false);
                startActivity(intent);
            }
        });


        name = (TextView) view.findViewById(R.id.layout_name);
        desc = (TextView) view.findViewById(R.id.layout_desc);
        mImg = (BootstrapCircleThumbnail) view.findViewById(R.id.ImageTeams);

        sessionManager = new SessionManager(getContext());
        fabEditTeamMgt = (FloatingActionButton) view.findViewById(R.id.fabEdit);
        fabEditTeamMgt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!sessionManager.isLoggedIn()) {
                    Intent intent = new Intent(getContext(), SignInActivity.class);
                    intent.putExtra("createOrupdate", true);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getContext(), TeamsMgtActivity.class);
                    intent.putExtra("createOrupdate", true);
                    startActivity(intent);
                }
            }
        });
        validateUser();*/
        View view = inflater.inflate(R.layout.fragment_teamsmanagement, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //InfoViewAdapter adapter = InfoViewAdapter();
        //setListAdapter(adapter);
        getListView().setOnItemClickListener(this);
    }

    public void onRefresh(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.HostServer)
                .addConverterFactory(JSONConverterFactory.create())
                .build();
        final RequestInterface request = retrofit.create(RequestInterface.class);
        Call<JSONObject> call = request.getTeams(sessionManager.getUserDetails().get(sessionManager.KEY_EMAIL), "1234rGSS34567AWS");
        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                JSONObject jsonObject = response.body();
                try {
                    Intent intents = new Intent(getActivity(), RegistrationIntentService.class);
                    intents.putExtra("TEAM_ID", jsonObject.getString("_id"));
                    intents.putExtra("TEAM_NAME", jsonObject.getString("name"));
                    intents.putExtra("TEAM_PHOTO", jsonObject.getString("phone"));
                    intents.putExtra("TEAM_CITY", jsonObject.getString("city"));
                    intents.putExtra("TEAM_DESC", jsonObject.getString("desc"));
                    intents.putExtra("TEAM_EMAIL", jsonObject.getString("email"));
                    intents.putExtra("HOST",Constants.HostServer);
                    intents.putExtra("CREATEORUPDATE_TEAM", false);
                    getActivity().startService(intents);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                Log.v("Upload", "success");
            }
        });
    }

    public void validateUser(){

        if(sessionManager.getUserDetails().get(sessionManager.KEY_EMAIL).isEmpty()){
            Intent intent = new Intent(getContext(), SignInActivity.class);
            startActivity(intent);
        }else {
            if(sessionManager.getUserDetails().get(sessionManager.CONTENT).isEmpty()) {
                onRefresh();
            }else{
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(sessionManager.getUserDetails().get(sessionManager.CONTENT));
                    name.setText(jsonObject.getString("name"));
                    desc.setText(jsonObject.getString("desc"));
                    String selectedImage = Constants.HostServer + "/img/" + jsonObject.getString("_id") + "/profile.jpg";
                    new ImageLoader(mImg).execute(selectedImage);
                    fabEditTeamMgt.setVisibility(View.INVISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    public class InfoViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }
    }
}
