package jcsoluciones.com.socialfootball;

import android.annotation.TargetApi;

import android.app.AlertDialog;
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
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;

import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import jcsoluciones.com.socialfootball.models.JSONConverterFactory;
import jcsoluciones.com.socialfootball.plugin.RegistrationIntentService;
import jcsoluciones.com.socialfootball.utils.ImageLoader;
import jcsoluciones.com.socialfootball.utils.RealPathUtil;
import jcsoluciones.com.socialfootball.utils.SessionManager;
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
    /**
     * The name
     */
    private BootstrapEditText edtname;
    /**
     * The phone
     */
    private BootstrapEditText edtphone;
    /**
     * The description
     */
    private BootstrapEditText edtdescrip;
    /**
     * The city
     */
    private Spinner spncity;
    /**
     * The cumplimiento
     */

    private RatingBar cumplimiento;
    /**
     * The email
     */

    private TextView email;

    /**
     * The status Image of Gallery
     */
    private  int SELECT_FILE = 1;

    /**
     * The Button for open image
     */

    private FloatingActionButton floatingActionButton;
    private String selectedImage;
    private static final String TAG = "MainActivity";
    private String IdTeams;
    private BootstrapCircleThumbnail mImg;
    private RelativeLayout mRlView;
    private static String APP_DIRECTORY = "MyPictureApp/";
    private static String MEDIA_DIRECTORY = APP_DIRECTORY + "PictureApp";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private final int MY_PERMISSIONS = 100;
    private final int PHOTO_CODE = 200;
    private final int SELECT_PICTURE = 300;
    private JSONObject jsonObject;
    private SwitchCompat switchCancel;
    private SessionManager sessionManager;
    private  File file;
    /**
     * The Flag for create/update
     */
    private boolean createOrupdate=true;
    private boolean resultImageOnSelected=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teams_mgt);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        edtname = (BootstrapEditText) findViewById(R.id.input_layout_name);
        edtphone = (BootstrapEditText) findViewById(R.id.input_layout_phone);
        edtdescrip = (BootstrapEditText) findViewById(R.id.input_layout_desc);
        email = (TextView) findViewById(R.id.input_layout_email);
        mImg = (BootstrapCircleThumbnail) findViewById(R.id.ImageTeams);
        mRlView = (RelativeLayout) findViewById(R.id.layout_main);
        cumplimiento =(RatingBar) findViewById(R.id.rtbCumplimiento);
        sessionManager = new SessionManager(this);

        switchCancel = (SwitchCompat) findViewById(R.id.switchCancel);
        switchCancel.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    confirmDeleteTeam();
                }
            }
        });

        spncity = (Spinner) findViewById(R.id.input_layout_city);
        ArrayAdapter<CharSequence> adapterspinner = ArrayAdapter.createFromResource(this,R.array.city, android.R.layout.simple_spinner_dropdown_item);
        spncity.setAdapter(adapterspinner);
        Bundle bundle =getIntent().getExtras();
        if(bundle!=null){
            try {
                jsonObject = new JSONObject(bundle.getString("object",""));
                edtname.setText(jsonObject.getString("name"));
                edtphone.setText(jsonObject.getString("phone"));
                edtdescrip.setText(jsonObject.getString("desc"));
                email.setText(jsonObject.getString("email"));
                createOrupdate = bundle.getBoolean("createOrupdate", true);

                IdTeams = bundle.getString("IdTeams", "");
                selectedImage =jsonObject.getString("ImageUrl");
                new ImageLoader(mImg).execute(selectedImage);
                spncity.setSelection(adapterspinner.getPosition(jsonObject.getString("city")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(createOrupdate) {
            cumplimiento.setVisibility(View.INVISIBLE);
            switchCancel.setVisibility(View.INVISIBLE);

        }else {
            cumplimiento.setVisibility(View.VISIBLE);
            switchCancel.setVisibility(View.VISIBLE);
        }
        //;
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
            String srtname  = edtname.getText().toString();
            String srtphone = edtphone.getText().toString();
            String srtdesc  = edtdescrip.getText().toString();

            if(createOrupdate) {
                if(selectedImage!=null) {
                    if(checkPlayServices()){
                        Intent intents = new Intent(TeamsMgtActivity.this, RegistrationIntentService.class);
                        intents.putExtra("TEAM_NAME", srtname);
                        intents.putExtra("TEAM_PHOTO", srtphone);
                        intents.putExtra("TEAM_CITY", spncity.getSelectedItem().toString());
                        intents.putExtra("TEAM_DESC", (srtdesc.length() > 0) ? srtdesc : " ");
                        intents.putExtra("TEAM_EMAIL", sessionManager.getUserDetails().get(sessionManager.KEY_EMAIL));
                        intents.putExtra("HOST",Constants.HostServer);
                        intents.putExtra("URL_PHOTO", selectedImage);
                        startService(intents);
                        //uploadFile();
                        finish();
                    }
                }else{
                    createAlertDialog("Input Picture");
                }
            }else{
                /*jsonObject.put("name", srtname);
                jsonObject.put("phone", srtphone);
                jsonObject.put("city", spncity.getSelectedItem());
                jsonObject.put("desc", (srtdesc.length() > 0) ? srtdesc : " ");*/
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Creates the alert dialog.
     *
     * @param msg the msg
     */
    public void createAlertDialog(String msg) {
        AlertDialog.Builder alertbox = new AlertDialog.Builder(TeamsMgtActivity.this);
        alertbox.setTitle("Response Message");
        alertbox.setMessage(msg);
        alertbox.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            // do something when the button is clicked
            public void onClick(DialogInterface arg0, int arg1) {

            }
        });
        alertbox.show();
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

    private void confirmDeleteTeam() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TeamsMgtActivity.this);
        builder.setTitle("Delete Teams");
        builder.setMessage("Desea elminar el equipo");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

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
            resultImageOnSelected=true;
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
    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
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
        String descriptionString = sessionManager.getUserDetails().get(SessionManager.ID_CONTENT);
        RequestBody description = RequestBody.create(MediaType.parse("multipart/form-data"), descriptionString);
        // finally, execute the request
        Call<JSONObject> call = service.upload(description,body);
        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                Log.v("Upload", "success");
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
            }
        });
    }
}
