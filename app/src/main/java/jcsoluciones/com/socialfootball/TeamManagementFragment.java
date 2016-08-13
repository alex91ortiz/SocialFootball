package jcsoluciones.com.socialfootball;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.loopj.android.image.SmartImageView;
import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.App42CacheManager;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Query;
import com.shephertz.app42.paas.sdk.android.storage.QueryBuilder;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import com.shephertz.app42.paas.sdk.android.storage.StorageService;

import java.util.ArrayList;

import jcsoluciones.com.socialfootball.utils.SessionManager;

/**
 * Created by Admin on 31/07/2016.
 */

public class TeamManagementFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,AsyncApp42ServiceApi.App42StorageServiceListener {
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
    private Storage response;
    private SessionManager sessionManager;
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

        asyncService = AsyncApp42ServiceApi.instance(getContext());

        View view = inflater.inflate(R.layout.fragment_teamsmanagement, container, false);
        listView = (ListView)view.findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getContext(), TeamsMgtActivity.class);
                intent.putExtra("object",adapter.getItem(position).getJsonDoc());
                intent.putExtra("IdTeams",adapter.getItem(position).getDocId());
                intent.putExtra("createOrupdate",false);
                startActivity(intent);
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                onRefresh();
            }
        });
        sessionManager = new SessionManager(getContext());
        fabEditTeamMgt = (FloatingActionButton) view.findViewById(R.id.fabEdit);
        fabEditTeamMgt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!sessionManager.isLoggedIn()) {
                    Intent intent = new Intent(getContext(), SignInActivity.class);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(getContext(), TeamsMgtActivity.class);
                    startActivity(intent);
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onRefresh() {
        //asyncService.findAllDocs(Constants.App42DBName, "Teams", this);
        Query q1 = QueryBuilder.build("active",true, QueryBuilder.Operator.EQUALS);
        Query q2 = QueryBuilder.build("email",sessionManager.getUserDetails().get(sessionManager.KEY_EMAIL), QueryBuilder.Operator.EQUALS);
        Query q3 = QueryBuilder.compoundOperator(q1, QueryBuilder.Operator.AND,q2);// Build query q1 for key1 equal to name and value1 equal to Nick
        asyncService.findDocByQuery(Constants.App42DBName,"Teams",q3,this);
    }


    @Override
    public void onDocumentInserted(Storage response) {

    }

    @Override
    public void onUpdateDocSuccess(Storage response) {

    }

    @Override
    public void onFindDocSuccess(Storage response) {
        listTeamJson = response.getJsonDocList();
        if(listTeamJson.size()>0) {
            adapter = new TeamMgtAdapter(getActivity(), listTeamJson);
            listView.setAdapter(adapter);
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onDeleteDocSuccess() {

    }

    @Override
    public void onInsertionFailed(App42Exception ex) {

    }

    @Override
    public void onFindDocFailed(App42Exception ex) {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onUpdateDocFailed(App42Exception ex) {

    }

    @Override
    public void onDeleteDocFailed(App42Exception ex) {

    }

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
