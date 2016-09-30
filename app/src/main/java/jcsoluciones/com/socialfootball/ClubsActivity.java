package jcsoluciones.com.socialfootball;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import jcsoluciones.com.socialfootball.models.JSONConverterFactory;
import jcsoluciones.com.socialfootball.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ClubsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,SearchView.OnQueryTextListener {

    private String  AVATAR ="http://192.168.0.13:3000/img/57c4bc8c37cee530271588a3/profile.jpg";
    private SearchClubsAdapter adapter;
    private SearchView mSearchView;
    private MenuItem searchMenuItem;
    private GridView searchList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clubs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        searchList =(GridView) findViewById(R.id.list_clubs);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        searchMenuItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) searchMenuItem.getActionView();
        mSearchView.setOnQueryTextListener(this);
        return true;
    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        if(newText.length()>0) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.HostServer)
                    .addConverterFactory(JSONConverterFactory.create())
                    .build();

            RequestInterface request = retrofit.create(RequestInterface.class);
            //Call<JSONArray> call = request.searchTeams(newText, newText,sessionManager.getUserDetails().get(sessionManager.KEY_EMAIL));
            Call<JSONArray> call = request.searchTeams(newText, newText,"alexortizcortes@gmail.com");
            call.enqueue(new Callback<JSONArray>() {
                             @Override
                             public void onResponse(Call<JSONArray> call, Response<JSONArray> response) {
                                 JSONArray teambody = response.body();
                                 if (teambody != null && teambody.length() > 0) {
                                    adapter = new SearchClubsAdapter(getApplicationContext(), teambody);
                                    searchList.setAdapter(adapter);
                                 }
                             }

                             @Override
                             public void onFailure(Call<JSONArray> call, Throwable t) {

                             }
                         }
            );
        }
        return false;
    }

    public class SearchClubsAdapter extends BaseAdapter {
        private Context context;
        private LayoutInflater inflater;
        private JSONArray teambody;

        /**
         * The async service.
         */

        public SearchClubsAdapter(Context context, JSONArray teambody){
            this.teambody = teambody;
            this.context = context;

        }

        @Override
        public int getCount() {
            return teambody.length();
        }

        @Override
        public Object getItem(int position) {
            try {
                return teambody.get(position);
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
            ViewHolder viewHolder;
            if (inflater==null)
                inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if(convertView==null) {
                convertView = inflater.inflate(R.layout.list_grid_clubs, null);
                viewHolder = new ViewHolder();
                viewHolder.text1 = (TextView) convertView.findViewById(R.id.name_club);
                viewHolder.mImg =(ImageView) convertView.findViewById(R.id.imagen_club);

                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            try {
                if(teambody.getJSONObject(position)!=null) {
                    final JSONObject jsonteambody = teambody.getJSONObject(position);
                    viewHolder.text1.setText(jsonteambody.getString("name").toString());
                    //Picasso.with(context).load(AVATAR).into(viewHolder.mImg);
                    //Picasso.with(context).load(AVATAR).resizeDimen(R.dimen.width,R.dimen.height).centerCrop().into(viewHolder.mImg);
                    /*final JSONObject jsonteambody = teambody.getJSONObject(position);
                    viewHolder.text1.setText(jsonteambody.getString("name").toString());
                    String selectedImage = Constants.HostServer + "/img/" + jsonteambody.getString("_id") + "/profile.jpg";
                    Picasso.with(activity).load(selectedImage).into(viewHolder.mImg);
                    viewHolder.text1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(activity, InvitePlayActivity.class);
                            intent.putExtra("team", jsonteambody.toString());
                            intent.putExtra("invite", "");
                            intent.putExtra("flagAccept", 3);
                            activity.startActivity(intent);
                        }
                    });*/

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return convertView;
        }
        private  class ViewHolder {
            public TextView text1;
            public ImageView mImg;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
