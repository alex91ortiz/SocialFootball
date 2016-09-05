package jcsoluciones.com.socialfootball;

import android.app.Activity;
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
import java.util.zip.Inflater;

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
        ArrayList<TeamMgtMenu> teamMgtMenus = new ArrayList<TeamMgtMenu>();
        teamMgtMenus.add(new TeamMgtMenu("crear perfil","",1));
        teamMgtMenus.add(new TeamMgtMenu("crear perfil","",2));
        teamMgtMenus.add(new TeamMgtMenu("crear perfil","",3));
        teamMgtMenus.add(new TeamMgtMenu("crear perfil","",4));


        setListAdapter(new TeamMgtAdapter(getActivity(),teamMgtMenus));
        
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

    private class TeamMgtMenu{
        private String title;
        private String desc;
        private int tipo;

        public TeamMgtMenu(String title, String desc, int tipo) {
            this.title = title;
            this.desc = desc;
            this.tipo = tipo;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public int getTipo() {
            return tipo;
        }

        public void setTipo(int tipo) {
            this.tipo = tipo;
        }
    }

    private class TeamMgtAdapter extends BaseAdapter  {
        private Activity activity;
        private LayoutInflater inflater;
        private ArrayList<TeamMgtMenu> jsonList;
        private BootstrapCircleThumbnail mImg;

        public  TeamMgtAdapter (Activity activity,ArrayList<TeamMgtMenu> jsonList){
            this.jsonList = jsonList;
            this.activity = activity;
        }

        @Override
        public int getCount() {
            return jsonList.size();
        }

        @Override
        public TeamMgtMenu getItem(int position) {
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

            title.setText(jsonList.get(position).getTitle());

            mImg=(BootstrapCircleThumbnail) convertView.findViewById(R.id.ImageTeams);


            return convertView;
        }

    }
}
