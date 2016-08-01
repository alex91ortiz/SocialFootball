package jcsoluciones.com.socialfootball;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.App42CacheManager;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import com.shephertz.app42.paas.sdk.android.storage.StorageService;

import java.util.ArrayList;

/**
 * Created by Admin on 31/07/2016.
 */

public class TeamManagementFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    /**
     *  Button for add team
     */
    private FloatingActionButton fabEditTeamMgt;
    /**
     * The async service.
     */
    private AsyncApp42ServiceApi asyncService;
    /**
     * The progress dialog.
     */
    private ProgressDialog progressDialog;

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private Context context;
    /**
     *  adapter for teams management
     */
    private TeamMgtAdapter adapter;

    /**
     * List of your Teams setting
     */

    private ArrayList<Storage.JSONDocument> listTeamJson = new ArrayList<Storage.JSONDocument>();

    /** The storage service. */
    private StorageService storageService;

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
        App42API.initialize(getActivity(), Constants.App42ApiKey, Constants.App42ApiSecret);
        App42CacheManager.setPolicy(App42CacheManager.Policy.CACHE_FIRST);
        storageService=App42API.buildStorageService();

        View view = inflater.inflate(R.layout.fragment_teamsmanagement, container, false);
        listView = (ListView)view.findViewById(R.id.listView);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        adapter = new TeamMgtAdapter(getActivity(), listTeamJson);
        listView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(this);

        context=getContext();

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                fetchTeams();
            }
        });

        fabEditTeamMgt = (FloatingActionButton) view.findViewById(R.id.fabEdit);
        fabEditTeamMgt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),TeamMgtActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public  void fetchTeams() {
        final Handler callerThreadHandler = new Handler();
        new Thread() {
            @Override
            public void run() {
                try {
                    final Storage response = storageService.findAllDocuments(Constants.App42DBName, "Teams");
                    callerThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listTeamJson = response.getJsonDocList();
                        }
                    });
                } catch (final App42Exception ex) {
                    callerThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });
                }
            }
        }.start();
    }


    @Override
    public void onRefresh() {
        fetchTeams();
    }
}
