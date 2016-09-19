package jcsoluciones.com.socialfootball;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.beardedhen.androidbootstrap.font.FontAwesome;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import jcsoluciones.com.socialfootball.models.JSONConverterFactory;
import jcsoluciones.com.socialfootball.utils.ImageLoader;
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
                        //Log.d("TeamsInviteAcceptFragment", "Failed to ", t);
                    }
            });
        }


        public class TeamsInviteAcceptAdapter extends BaseAdapter {

            private Activity activity;
            private LayoutInflater inflater;
            private JSONArray inviteBody;

            public TeamsInviteAcceptAdapter(Activity activity, JSONArray jsonList){
                this.inviteBody = jsonList;
                this.activity = activity;
            }

            @Override
            public int getCount() {
                return inviteBody.length();
            }

            @Override
            public Object getItem(int position) {

                try {
                    return inviteBody.get(position);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                final ViewHolder viewHolder;
                if (inflater==null)
                    inflater= (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                if(convertView==null) {
                    convertView = inflater.inflate(R.layout.list_row_inviteaccept, null);
                    viewHolder = new ViewHolder();
                    viewHolder.text1 = (TextView) convertView.findViewById(R.id.title);
                    viewHolder.text2 = (TextView) convertView.findViewById(R.id.message);
                    viewHolder.mImg =(BootstrapCircleThumbnail) convertView.findViewById(R.id.ImageTeams);
                    viewHolder.divider = (View) convertView.findViewById(R.id.divider);
                    viewHolder.makeInvite = (BootstrapButton) convertView.findViewById(R.id.button_invite);
                    convertView.setTag(viewHolder);
                }else{
                    viewHolder = (ViewHolder) convertView.getTag();
                }

                try {
                    if(inviteBody.getJSONObject(position)!=null) {
                        final JSONObject jsonInvites = inviteBody.getJSONObject(position);
                        final JSONObject jsonCreate = new JSONObject(jsonInvites.getString("creator"));
                        final JSONObject jsonFriends = new JSONObject(jsonInvites.getString("friends"));
                        if (jsonCreate.getString("email").equals(sessionManager.getUserDetails().get(sessionManager.KEY_EMAIL))) {

                            viewHolder.text1.setText(jsonFriends.getString("name"));

                            viewHolder.text2.setText(jsonFriends.getString("desc"));
                            String selectedImage = Constants.HostServer + "/img/" + jsonFriends.getString("_id") + "/profile.jpg";

                            //new ImageLoader(mImg).execute(selectedImage);
                            Picasso.with(activity).load(selectedImage).into(viewHolder.mImg);
                            if (jsonInvites.getBoolean("Acceptinvite")) {
                                viewHolder.makeInvite.setShowOutline(false);
                                viewHolder.makeInvite.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
                                viewHolder.makeInvite.setFontAwesomeIcon(FontAwesome.FA_CALENDAR);
                                viewHolder.makeInvite.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        viewHolder.makeInvite.setShowOutline(false);
                                        Intent intent = new Intent(activity, AcceptInviteActivity.class);
                                        intent.putExtra("invite", jsonInvites.toString());
                                        intent.putExtra("team", jsonCreate.toString());
                                        activity.startActivity(intent);
                                    }
                                });
                                viewHolder.text1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(activity, InvitePlayActivity.class);
                                        intent.putExtra("team", jsonFriends.toString());
                                        activity.startActivity(intent);
                                    }
                                });
                            } else {
                                viewHolder.makeInvite.setBootstrapBrand(DefaultBootstrapBrand.WARNING);
                                viewHolder.makeInvite.setFontAwesomeIcon(FontAwesome.FA_CLOCK_O);
                                viewHolder.text1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(activity, InvitePlayActivity.class);
                                        intent.putExtra("team", jsonFriends.toString());
                                        intent.putExtra("flagAccept", 1);
                                        activity.startActivity(intent);
                                    }
                                });
                            }
                        }

                        if (jsonFriends.getString("email").equals(sessionManager.getUserDetails().get(sessionManager.KEY_EMAIL))) {

                            viewHolder.text1.setText(jsonCreate.getString("name"));
                            viewHolder.text2.setText(jsonCreate.getString("desc"));

                            String selectedImage = Constants.HostServer + "/img/" + jsonCreate.getString("_id") + "/profile.jpg";

                            Picasso.with(activity).load(selectedImage).into(viewHolder.mImg);
                            if (jsonInvites.getBoolean("Acceptinvite")) {
                                viewHolder.makeInvite.setShowOutline(false);
                                viewHolder.makeInvite.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
                                viewHolder.makeInvite.setFontAwesomeIcon(FontAwesome.FA_CALENDAR);
                            } else {
                                viewHolder.makeInvite.setBootstrapBrand(DefaultBootstrapBrand.INFO);
                                viewHolder.makeInvite.setFontAwesomeIcon(FontAwesome.FA_CHECK_CIRCLE);
                                viewHolder.makeInvite.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        viewHolder.makeInvite.setShowOutline(false);
                                        Intent intent = new Intent(activity, InvitePlayActivity.class);
                                        intent.putExtra("invite", jsonInvites.toString());
                                        intent.putExtra("team", jsonCreate.toString());
                                        intent.putExtra("flagAccept", 2);
                                        activity.startActivity(intent);
                                    }
                                });
                            }

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return convertView;
            }
            private  class ViewHolder {
                public TextView text1;
                public TextView text2;
                public BootstrapCircleThumbnail mImg;
                public RelativeLayout relativeLayout;
                public View divider;
                public BootstrapButton makeInvite;
            }
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