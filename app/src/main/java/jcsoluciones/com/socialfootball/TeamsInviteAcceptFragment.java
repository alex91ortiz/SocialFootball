package jcsoluciones.com.socialfootball;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import org.json.JSONArray;
import jcsoluciones.com.socialfootball.models.JSONConverterFactory;
import jcsoluciones.com.socialfootball.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;



/**
 * Created by ADMIN on 10/08/2016.
 */
public class TeamsInviteAcceptFragment extends Fragment implements  SwipeRefreshLayout.OnRefreshListener{

        SwipeRefreshLayout swipeRefreshLayout;


        /**
        *  adapter for teams management
        */
        private ListView listView;
        /**
        *  adapter for teams management
        */
        private TeamsInviteAcceptAdapter adapter;
        private SessionManager sessionManager;
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
            listView = (ListView) view.findViewById(R.id.listView);
            sessionManager = new SessionManager(getActivity());
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


       /* @Override
        public void onFindDocSuccess(Storage response) {
            listTeamJson = response.getJsonDocList();
            if(listTeamJson.size()>0) {
                adapter = new TeamsInviteAcceptAdapter(getActivity(), listTeamJson);
                listView.setAdapter(adapter);
            }
            swipeRefreshLayout.setRefreshing(false);
        }



        @Override
        public void onFindDocFailed(App42Exception ex) {
            swipeRefreshLayout.setRefreshing(false);
            createAlertDialog("fallo la busqueda: "+ ex.getMessage());
        }*/



        @Override
        public void onRefresh() {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.HostServer)
                    .addConverterFactory(JSONConverterFactory.create())
                    .build();

            RequestInterface request = retrofit.create(RequestInterface.class);
            Call<JSONArray> call = request.invites(sessionManager.getUserDetails().get(sessionManager.ID_CONTENT));
            call.enqueue(new Callback<JSONArray>() {
                    @Override
                    public void onResponse(Call<JSONArray> call, Response<JSONArray> response) {

                        JSONArray invitebody = response.body();


                        if(invitebody !=null && invitebody.length()>0) {
                            adapter = new TeamsInviteAcceptAdapter(getActivity(), invitebody);
                            listView.setAdapter(adapter);
                        }
                        //Toast.makeText(getActivity(),invitebody.toString(), Toast.LENGTH_LONG).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(Call<JSONArray> call, Throwable t) {
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("TeamsInviteAcceptFragment", "Failed to ", t);


                    }
            });
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