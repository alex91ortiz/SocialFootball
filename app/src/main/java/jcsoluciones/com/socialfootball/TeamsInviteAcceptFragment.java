package jcsoluciones.com.socialfootball;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
            swipeRefreshLayout.setRefreshing(false);
            createAlertDialog("fallo la busqueda: "+ ex.getMessage());
        }

        @Override
        public void onUpdateDocFailed(App42Exception ex) {

        }

        @Override
        public void onDeleteDocFailed(App42Exception ex) {

        }

        @Override
        public void onRefresh() {
            Query q1 = QueryBuilder.build("Teams.active", true, QueryBuilder.Operator.EQUALS);
            Query q2 = QueryBuilder.build("Teams.email","alexortizcortes@gmail.com", QueryBuilder.Operator.EQUALS);
            Query q3 = QueryBuilder.compoundOperator(q1, QueryBuilder.Operator.AND, q2);
            Query q4 = QueryBuilder.build("email", "alexortizcortes@gmail.com", QueryBuilder.Operator.EQUALS);
            Query q5 = QueryBuilder.build("Teams.active", true, QueryBuilder.Operator.EQUALS);
            Query q6 = QueryBuilder.build("Teams.Accept_invite", true, QueryBuilder.Operator.EQUALS);
            Query q7 = QueryBuilder.compoundOperator(q4, QueryBuilder.Operator.AND, q5);
            Query q8 = QueryBuilder.compoundOperator(q7, QueryBuilder.Operator.AND, q6);
            Query q9 = QueryBuilder.compoundOperator(q3,QueryBuilder.Operator.OR,q8);
            asyncService.findDocByQuery(Constants.App42DBName,"Invites",q9,this);
        }

        /**
         * Creates the alert dialog.
         *
         * @param msg the msg
         */
        public void createAlertDialog(String msg) {
            AlertDialog.Builder alertbox = new AlertDialog.Builder(getActivity());
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