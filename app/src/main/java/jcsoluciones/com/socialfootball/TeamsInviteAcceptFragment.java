package jcsoluciones.com.socialfootball;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Query;
import com.shephertz.app42.paas.sdk.android.storage.QueryBuilder;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import java.util.ArrayList;


/**
 * Created by ADMIN on 10/08/2016.
 */
public class TeamsInviteAcceptFragment extends Fragment implements  SwipeRefreshLayout.OnRefreshListener,AsyncApp42ServiceApi.App42StorageServiceListener{

        SwipeRefreshLayout swipeRefreshLayout;
        /**
        * The async service.
        */
        private AsyncApp42ServiceApi asyncService;
        /**
        * List of your Teams setting
        */
        private ArrayList<Storage.JSONDocument> listTeamJson = new ArrayList<Storage.JSONDocument>();
        /**
        *  adapter for teams management
        */
        private ListView listView;
        /**
        *  adapter for teams management
        */
        private TeamsInviteAcceptAdapter adapter;

        public TeamsInviteAcceptFragment() {
        // Required empty public constructor
        }


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View view = inflater.inflate(R.layout.fragment_teamsiviteaccept, container, false);
            asyncService = AsyncApp42ServiceApi.instance(getContext());
            listView = (ListView) view.findViewById(R.id.listView);
            swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
            swipeRefreshLayout.setOnRefreshListener(this);
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                    onRefresh();
                }
            });
            return view;
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
                adapter = new TeamsInviteAcceptAdapter(getActivity(), listTeamJson);
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

        }

        @Override
        public void onUpdateDocFailed(App42Exception ex) {

        }

        @Override
        public void onDeleteDocFailed(App42Exception ex) {

        }

        @Override
        public void onRefresh() {
            Query q1 = QueryBuilder.build("active", true, QueryBuilder.Operator.EQUALS); // Build query q1 for key1 equal to name and value1 equal to Nick
            asyncService.findDocByQuery(Constants.App42DBName,"Teams",q1,this);
        }
}