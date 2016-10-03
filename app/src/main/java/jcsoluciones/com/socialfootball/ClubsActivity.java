package jcsoluciones.com.socialfootball;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import jcsoluciones.com.socialfootball.models.JSONConverterFactory;
import jcsoluciones.com.socialfootball.util.ImageCache;
import jcsoluciones.com.socialfootball.util.ImageFetcher;
import jcsoluciones.com.socialfootball.utils.FileCache;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ClubsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,SearchView.OnQueryTextListener {

    private String  AVATAR =Constants.HostServer+"/img/57c4bc8c37cee530271588a3/profile.jpg";
    private SearchClubsAdapter adapter;
    private SearchView mSearchView;
    private MenuItem searchMenuItem;
    private GridView searchList;
    private Retrofit retrofit;
    private RequestInterface request;
    private FileCache<String> fileCache;
    private FileCache<Bitmap> fileCacheImg;
    private static final String IMAGE_CACHE_DIR = "thumbs";
    private int mImageThumbSize;
    private int mImageThumbSpacing;
    private ImageFetcher mImageFetcher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clubs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        searchList =(GridView) findViewById(R.id.list_clubs);
        searchList.setOnItemClickListener(this);


        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
        mImageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);

        ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(this, IMAGE_CACHE_DIR);

        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(this, mImageThumbSize);
        mImageFetcher.setLoadingImage(R.drawable.champions);
        mImageFetcher.addImageCache(this.getSupportFragmentManager(), cacheParams);
        setupHttp();
    }

    public void setupHttp(){

        fileCache = new FileCache<>(ClubsActivity.this,"listSearccTeam.json");
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.HostServer)
                .addConverterFactory(JSONConverterFactory.create())
                .build();
        request = retrofit.create(RequestInterface.class);
        if(!fileCache.hasCache()) {
            Log.d("MainActivity", "desde http");
            Call<JSONArray> call = request.searchTeams(" ", " ", "alexortizcortes@gmail.com", 1);
            call.enqueue(new retrofit2.Callback<JSONArray>() {
                             @Override
                             public void onResponse(Call<JSONArray> call, Response<JSONArray> response) {
                                 JSONArray teambody = response.body();
                                 if (teambody != null && teambody.length() > 0) {

                                     try {
                                         fileCache.writeCache(teambody.toString());
                                     } catch (IOException e) {
                                         e.printStackTrace();
                                     }
                                     adapter = new SearchClubsAdapter(getApplicationContext(), teambody);
                                     searchList.setAdapter(adapter);
                                 }
                             }

                             @Override
                             public void onFailure(Call<JSONArray> call, Throwable t) {

                             }
                         }
            );
        }else{
            Log.d("MainActivity", "desde cache");
            JSONArray teambody = null;
            try {
                teambody = new JSONArray(fileCache.readCache());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            adapter = new SearchClubsAdapter(getApplicationContext(), teambody);
            searchList.setAdapter(adapter);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        mImageFetcher.setExitTasksEarly(false);
        adapter.notifyDataSetChanged();
    }
    @Override
    public void onPause() {
        super.onPause();
        mImageFetcher.setPauseWork(false);
        mImageFetcher.setExitTasksEarly(true);
        mImageFetcher.flushCache();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mImageFetcher.closeCache();
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
            searchList.setAdapter(null);

            //Call<JSONArray> call = request.searchTeams(newText, newText,sessionManager.getUserDetails().get(sessionManager.KEY_EMAIL));
            Call<JSONArray> call = request.searchTeams(newText, newText,"alexortizcortes@gmail.com",2);
            call.enqueue(new retrofit2.Callback<JSONArray>() {
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
    /** SearchClubsAdapter */
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
        public JSONObject getItem(int position) {
            try {
                return teambody.getJSONObject(position);
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
                inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if(convertView==null) {
                convertView = inflater.inflate(R.layout.list_grid_clubs, null);
                viewHolder = new ViewHolder();
                viewHolder.text1 = (TextView) convertView.findViewById(R.id.name_club);
                viewHolder.mImg =(ImageView) convertView.findViewById(R.id.imagen_club);
                viewHolder.mImg.setScaleType(ImageView.ScaleType.CENTER_CROP);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            String idImg="";
            try {

                if(teambody.getJSONObject(position)!=null) {
                    final JSONObject jsonteambody = teambody.getJSONObject(position);
                    viewHolder.text1.setText(jsonteambody.getString("name").toString());
                    idImg = jsonteambody.getString("_id");
                    AVATAR = Constants.HostServer + "/img/" + jsonteambody.getString("_id") + "/profile.jpg";
                    fileCacheImg = new FileCache<Bitmap>(ClubsActivity.this, idImg);
                    Picasso.with(context).load(AVATAR).resizeDimen(R.dimen.width, R.dimen.height).centerCrop().fetch();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mImageFetcher.loadImage(AVATAR,viewHolder.mImg);
            return convertView;
        }



        private  class ViewHolder {
            public TextView text1;
            public ImageView mImg;
            public Picasso mPicasso;
        }

    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

         if(adapter.getItem(position)!=null) {
            Intent intent = new Intent(this, MyclubActivity.class);
            intent.putExtra("team", adapter.getItem(position).toString());
            /*intent.putExtra("invite", "");
            intent.putExtra("flagAccept", 3);*/
            startActivity(intent);
        }
    }
}
