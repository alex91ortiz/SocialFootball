package jcsoluciones.com.socialfootball;

import android.annotation.TargetApi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.media.MediaScannerConnection;
import android.net.Uri;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONObject;

import java.io.File;

import jcsoluciones.com.socialfootball.provider.JSONConverterFactory;
import jcsoluciones.com.socialfootball.provider.RequestInterface;
import jcsoluciones.com.socialfootball.utils.RealPathUtil;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class TeamsMgtActivity extends AppCompatActivity {

    private FloatingActionButton floatingActionButton;
    private String selectedImage;
    private static final String TAG = "MainActivity";
    private String IdTeams;
    private String RegisterID;
    private BootstrapCircleThumbnail mImg;
    private RelativeLayout mRlView;
    private static String APP_DIRECTORY = "MyPictureApp/";
    private static String MEDIA_DIRECTORY = APP_DIRECTORY + "PictureApp";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private final int MY_PERMISSIONS = 100;
    private final int PHOTO_CODE = 200;
    private final int SELECT_PICTURE = 300;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teams_mgt);


        TeamsFlagFragment flagFragment= new TeamsFlagFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.layout_main,flagFragment);
        fragmentTransaction.commit();


        /*spncity = (Spinner) findViewById(R.id.input_layout_city);
        ArrayAdapter<CharSequence> adapterspinner = ArrayAdapter.createFromResource(this,R.array.city, android.R.layout.simple_spinner_dropdown_item);
        spncity.setAdapter(adapterspinner);*/

        Bundle bundle =getIntent().getExtras();
        if(bundle!=null){
            /*try {
                jsonObject = new JSONObject(bundle.getString("object",""));
                edtname.setText(jsonObject.getString("name"));
                edtphone.setText(jsonObject.getString("phone"));
                edtdescrip.setText(jsonObject.getString("desc"));
                email.setText(jsonObject.getString("email"));
                RegisterID = jsonObject.getString("registrationId");
                createOrupdate = getIntent().getBooleanExtra("createOrupdate", false);

                IdTeams =jsonObject.getString("_id");
                selectedImage =Constants.HostServer+"/img/"+jsonObject.getString("_id")+"/profile.jpg";
                Picasso.with(this).load(selectedImage).into(mImg);
                spncity.setSelection(adapterspinner.getPosition(jsonObject.getString("city")));
            } catch (JSONException e) {
                e.printStackTrace();
            }*/
        }

    }

    public void ImageChange(View view){
        if(mayRequestStoragePermission()){
            showOptions();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tmgt, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_edit) {
           /* String srtname  = edtname.getText().toString();
            String srtphone = edtphone.getText().toString();
            String srtdesc  = edtdescrip.getText().toString();

            if(createOrupdate) {
                if(checkPlayServices()){
                    Intent intents = new Intent(TeamsMgtActivity.this, RegistrationIntentService.class);
                    intents.putExtra("TEAM_NAME", srtname);
                    intents.putExtra("TEAM_PHOTO", srtphone);
                    intents.putExtra("TEAM_CITY", spncity.getSelectedItem().toString());
                    intents.putExtra("TEAM_DESC", (srtdesc.length() > 0) ? srtdesc : " ");
                    intents.putExtra("TEAM_EMAIL", sessionManager.getUserDetails().get(sessionManager.KEY_EMAIL));
                    intents.putExtra("HOST",Constants.HostServer);
                    intents.putExtra("URL_PHOTO", selectedImage);
                    intents.putExtra("CREATEORUPDATE_TEAM", true);
                    startService(intents);
                    finish();
                }
            }else{
                final RequestTeamBody requestBody = new RequestTeamBody();
                requestBody.setId(IdTeams);
                requestBody.setName(srtname);
                requestBody.setPhone(srtphone);
                requestBody.setCity( spncity.getSelectedItem().toString());
                requestBody.setDesc((srtdesc.length() > 0) ? srtdesc : " ");
                requestBody.setEmail(sessionManager.getUserDetails().get(sessionManager.KEY_EMAIL));
                requestBody.setRegistrationId(RegisterID);


                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(Constants.HostServer)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                sessionManager = new SessionManager(this);
                RequestInterface request = retrofit.create(RequestInterface.class);
                Call<RequestTeamBody> call = request.updateTeam(requestBody);
                call.enqueue(new Callback<RequestTeamBody>() {
                    @Override
                    public void onResponse(Call<RequestTeamBody> call, Response<RequestTeamBody> response) {
                        RequestTeamBody responseBody = response.body();
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("_id",responseBody.getId());
                            jsonObject.put("name",responseBody.getName());
                            jsonObject.put("phone",responseBody.getPhone());
                            jsonObject.put("email",responseBody.getEmail());
                            jsonObject.put("desc",responseBody.getDesc());
                            jsonObject.put("city",responseBody.getCity());
                            jsonObject.put("registrationId",responseBody.getRegistrationId());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        sessionManager.createContentSession(responseBody.getId(), jsonObject.toString());
                        uploadFile();

                    }

                    @Override
                    public void onFailure(Call<RequestTeamBody> call, Throwable t) {

                        Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }

            return true;*/
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean mayRequestStoragePermission() {

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true;

        if((checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                (checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED))
            return true;

        if((shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) || (shouldShowRequestPermissionRationale(CAMERA))){
            Snackbar.make(mRlView, "Los permisos son necesarios para poder usar la aplicación",
                    Snackbar.LENGTH_INDEFINITE).setAction(android.R.string.ok, new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {
                    requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, MY_PERMISSIONS);
                }
            });
        }else{
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, MY_PERMISSIONS);
        }

        return false;
    }

    private void showOptions() {
        final CharSequence[] option = {"Tomar foto", "Elegir de galeria", "Cancelar"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(TeamsMgtActivity.this);
        builder.setTitle("Eleige una opción");

        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (option[which] == "Tomar foto") {
                    openCamera();
                } else if (option[which] == "Elegir de galeria") {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent.createChooser(intent, "Selecciona app de imagen"), SELECT_PICTURE);
                } else {
                    dialog.dismiss();
                }
            }
        });

        builder.show();
    }

    private void openCamera() {
        File file = new File(Environment.getExternalStorageDirectory(), MEDIA_DIRECTORY);
        boolean isDirectoryCreated = file.exists();

        if(!isDirectoryCreated)
            isDirectoryCreated = file.mkdirs();

        if(isDirectoryCreated){
            Long timestamp = System.currentTimeMillis() / 1000;
            String imageName = timestamp.toString() + ".jpg";

            selectedImage = Environment.getExternalStorageDirectory() + File.separator + MEDIA_DIRECTORY
                    + File.separator + imageName;

            File newFile = new File(selectedImage);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newFile));
            startActivityForResult(intent, PHOTO_CODE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("file_path", selectedImage);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        selectedImage = savedInstanceState.getString("file_path");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            //resultImageOnSelected=true;
            switch (requestCode){
                case PHOTO_CODE:
                    MediaScannerConnection.scanFile(this,
                            new String[]{selectedImage}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.i("ExternalStorage", "Scanned " + path + ":");
                                    Log.i("ExternalStorage", "-> Uri = " + uri);
                                    selectedImage = RealPathUtil.getRealPathFromURI_API19(getBaseContext(), uri);
                                }
                            });


                    Bitmap bitmap = BitmapFactory.decodeFile(selectedImage);
                    file = new File(selectedImage);
                    mImg.setImageBitmap(bitmap);
                    break;
                case SELECT_PICTURE:
                    Uri path = data.getData();

                    selectedImage = RealPathUtil.getRealPathFromURI_API19(this, path);
                    file = new File(selectedImage);

                    mImg.setImageURI(path);
                    break;

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == MY_PERMISSIONS){
            if(grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(TeamsMgtActivity.this, "Permisos aceptados", Toast.LENGTH_SHORT).show();
                floatingActionButton.setEnabled(true);
            }
        }else{
            showExplanation();
        }
    }

    private void showExplanation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TeamsMgtActivity.this);
        builder.setTitle("Permisos denegados");
        builder.setMessage("Para usar las funciones de la app necesitas aceptar los permisos");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });

        builder.show();
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private void uploadFile() {
        // create upload service client
            if(file!=null) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(Constants.HostServer)
                        .addConverterFactory(JSONConverterFactory.create())
                        .build();
                RequestInterface service = retrofit.create(RequestInterface.class);
                // create RequestBody instance from file
                RequestBody requestFile =
                        RequestBody.create(MediaType.parse("multipart/form-data"), file);
                // MultipartBody.Part is used to send also the actual file name
                MultipartBody.Part body = MultipartBody.Part.createFormData("picture", file.getName(), requestFile);
                // add another part within the multipart request
                String descriptionString =""; //sessionManager.getUserDetails().get(SessionManager.ID_CONTENT);
                RequestBody description = RequestBody.create(MediaType.parse("multipart/form-data"), descriptionString);
                // finally, execute the request
                Call<JSONObject> call = service.upload(description, body);
                call.enqueue(new Callback<JSONObject>() {
                    @Override
                    public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                        Log.v("Upload", "success");
                        finish();
                    }

                    @Override
                    public void onFailure(Call<JSONObject> call, Throwable t) {
                        Log.e("Upload error:", t.getMessage());
                    }
                });
            }else{
                finish();
            }
    }

    public static class TeamsFlagFragment extends Fragment {
        public int[] ColorsFlag = new int[]{
                R.color.md_yellow_500,
                R.color.md_blue_800,
                R.color.md_red_500,
                R.color.md_white_1000,
                R.color.md_black_1000,
                R.color.md_blue_200,
                R.color.md_pink_500,
                R.color.md_green_500,
                R.color.md_orange_500
        };
        public View view_flag1,view_flag2,view_flag3;
        public TeamsFlagFragment() {
            super();
        }
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_teamflag, container, false);
            view_flag1 =  view.findViewById(R.id.view_flag_1);
            view_flag1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showFlagViewColors();
                }
            });

            return view;
        }

        public void  showFlagViewColors(){

            LayoutInflater inflater = getActivity().getLayoutInflater();
            View convertView =  inflater.inflate(R.layout.dialog_selectflag, null);
            GridView lv = (GridView) convertView.findViewById(R.id.list_item_colors);
            AdapterFlagViewColors adapterFlagViewColors = new AdapterFlagViewColors(getActivity(),ColorsFlag);
            lv.setAdapter(adapterFlagViewColors);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    view_flag1.setBackgroundResource(ColorsFlag[position]);
                }
            });
            Dialog dialog = new Dialog(getActivity());
            dialog.setContentView(convertView);
            dialog.show();
        }
    }

    public static class TeamsInfoComplementFragment extends Fragment {
        public TeamsInfoComplementFragment() {
            super();
        }
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_teaminfoadd, container, false);
            return view;
        }
    }
}
