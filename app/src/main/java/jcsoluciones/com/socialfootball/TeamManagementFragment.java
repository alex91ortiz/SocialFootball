package jcsoluciones.com.socialfootball;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jcsoluciones.com.socialfootball.models.JSONConverterFactory;
import jcsoluciones.com.socialfootball.plugin.RegistrationIntentService;
import jcsoluciones.com.socialfootball.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

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
    private JSONObject jsonObject = null;
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
        sessionManager = new SessionManager(getContext());
        View view = inflater.inflate(R.layout.fragment_teamsmanagement, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        validateUser();
        ListView lv = getListView();
        //ColorDrawable white = new ColorDrawable(this.getResources().getColor(R.color.md_white_1000,null));
        //lv.setDivider(white);
        lv.setDividerHeight(0);
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
                if(response.body()!=null) {
                    try {

                        Intent intents = new Intent(getActivity(), RegistrationIntentService.class);
                        intents.putExtra("TEAM_ID", jsonObject.getString("_id"));
                        intents.putExtra("TEAM_NAME", jsonObject.getString("name"));
                        intents.putExtra("TEAM_PHOTO", jsonObject.getString("phone"));
                        intents.putExtra("TEAM_CITY", jsonObject.getString("city"));
                        intents.putExtra("TEAM_DESC", jsonObject.getString("desc"));
                        intents.putExtra("TEAM_EMAIL", jsonObject.getString("email"));
                        intents.putExtra("HOST", Constants.HostServer);
                        intents.putExtra("CREATEORUPDATE_TEAM", false);
                        getActivity().startService(intents);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                Log.v("Upload", "success");
            }
        });
    }

    public  void createList(String name,String title,String UrlImg){
        ArrayList<TeamMgtMenu> teamMgtMenus = new ArrayList<TeamMgtMenu>();
        teamMgtMenus.add(new TeamMgtMenu(name,title,UrlImg,1));
        teamMgtMenus.add(new TeamMgtMenu("Eventos","","",2));
        teamMgtMenus.add(new TeamMgtMenu("Canchas","", "", 3));
        teamMgtMenus.add(new TeamMgtMenu("Contactenos","" ,"", 4));
        setListAdapter(new TeamMgtAdapter(getActivity(), teamMgtMenus));
    }

    public void validateUser(){

        if(sessionManager.getUserDetails().get(sessionManager.KEY_EMAIL).isEmpty()){

            createList("Crear Perfil","","");
        }else {
            if(sessionManager.getUserDetails().get(sessionManager.CONTENT).isEmpty()) {
                onRefresh();
            }else{

                try {
                    /*jsonObject = new JSONObject(sessionManager.getUserDetails().get(sessionManager.CONTENT));
                    name.setText(jsonObject.getString("name"));
                    desc.setText(jsonObject.getString("desc"));
                    String selectedImage = Constants.HostServer + "/img/" + jsonObject.getString("_id") + "/profile.jpg";
                    new ImageLoader(mImg).execute(selectedImage);
                    fabEditTeamMgt.setVisibility(View.INVISIBLE);*/
                    jsonObject = new JSONObject(sessionManager.getUserDetails().get(sessionManager.CONTENT));
                    String selectedImage = Constants.HostServer + "/img/" + jsonObject.getString("_id") + "/profile.jpg";
                    createList(jsonObject.getString("name"),"Ver tu perfil", selectedImage);
                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch(position){
                case 0:
                    if(sessionManager.getUserDetails().get(sessionManager.KEY_EMAIL).isEmpty()){
                        Intent intent = new Intent(getContext(), SignInActivity.class);
                        intent.putExtra("createOrupdate",true);
                        startActivity(intent);
                    }else if(sessionManager.getUserDetails().get(sessionManager.CONTENT).isEmpty()) {
                        Intent intent = new Intent(getActivity(), TeamsMgtActivity.class);
                        startActivity(intent);
                    }else{
                        Intent intent = new Intent(getActivity(), InvitePlayActivity.class);
                        intent.putExtra("team", jsonObject.toString());
                        intent.putExtra("flagAccept", 1);

                        startActivity(intent);
                    }
                    /*Intent intent = new Intent(getActivity(), InvitePlayActivity.class);
                    startActivity(intent);*/
                    break;
                case 1:break;
                case 2:break;
                case 3:break;
            }
    }

    private class TeamMgtMenu{
        private String title;
        private String desc;
        private String imgurl;
        private int tipo;

        public TeamMgtMenu(String title, String desc,String imgurl, int tipo) {
            this.title = title;
            this.desc = desc;
            this.tipo = tipo;
            this.imgurl = imgurl;
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

        public String getImgurl() {
            return imgurl;
        }

        public void setImgurl(String imgurl) {
            this.imgurl = imgurl;
        }
    }

    private class TeamMgtAdapter extends BaseAdapter  {
        private Activity activity;
        private LayoutInflater inflater;
        private ArrayList<TeamMgtMenu> jsonList;


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
            ViewHolder viewHolder;
            if (inflater==null)
                inflater=(LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if(convertView==null) {
                convertView = inflater.inflate(R.layout.list_group_teamsmanagement, null);
                viewHolder = new ViewHolder();
                viewHolder.text1 = (TextView) convertView.findViewById(R.id.title);
                viewHolder.text2 = (TextView) convertView.findViewById(R.id.message);
                viewHolder.mImg =(BootstrapCircleThumbnail) convertView.findViewById(R.id.ImageTeams);
                viewHolder.divider = (View) convertView.findViewById(R.id.divider);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }

            TeamMgtMenu teamMgtMenu = jsonList.get(position);
            if(teamMgtMenu !=null) {
                viewHolder.text1.setText(teamMgtMenu.getTitle());
                viewHolder.text2.setText(teamMgtMenu.getDesc());

                switch (teamMgtMenu.getTipo()) {
                    case 1:
                        if (!teamMgtMenu.getImgurl().isEmpty()) {
                            //new ImageLoader(viewHolder.mImg).execute(jsonList.get(position).getImgurl());
                            Picasso.with(activity).load(teamMgtMenu.getImgurl()).into(viewHolder.mImg);
                            viewHolder.divider.setVisibility(View.VISIBLE);
                        }
                        break;
                    case 2:

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            viewHolder.mImg.setImageDrawable( getResources().getDrawable(R.drawable.events,activity.getTheme()));
                        }else{
                            viewHolder.mImg.setImageDrawable(getResources().getDrawable(R.drawable.events));
                        }
                        break;
                }
            }
            return convertView;
        }

        private  class ViewHolder {
            public TextView text1;
            public TextView text2;
            public BootstrapCircleThumbnail mImg;
            public View divider;
        }

    }
}
