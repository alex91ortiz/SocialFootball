package jcsoluciones.com.socialfootball;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.loopj.android.image.SmartImageView;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import com.shephertz.app42.paas.sdk.android.upload.Upload;
import com.shephertz.app42.paas.sdk.android.upload.UploadFileType;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by ADMIN on 01/08/2016.
 */

public class TeamMgtActivity extends AppCompatActivity implements AsyncApp42ServiceApi.App42StorageServiceListener ,
        AsyncApp42ServiceApi.App42UploadServiceListener {
    /**
     * The async service.
     */
    private AsyncApp42ServiceApi asyncService;
    /**
     * The progress dialog.
     */
    private ProgressDialog progressDialog;

    /**
     * The name
     */
    private TextInputLayout edtname;
    /**
     * The phone
     */
    private TextInputLayout edtphone;
    /**
     * The description
     */
    private TextInputLayout edtdescrip;
    /**
     * The city
     */
    private Spinner spncity;
    /**
     * The city
     */
    private RatingBar cumplimiento;
    /**
     * The status Image of Gallery
     */
    private  int SELECT_FILE = 1;

    /**
     * The Button for open image
     */

    private FloatingActionButton floatingActionButton;
    private String selectedImage;
    private SmartImageView mImg;
    private LinearLayout mRlView;
    private static String APP_DIRECTORY = "MyPictureApp/";
    private static String MEDIA_DIRECTORY = APP_DIRECTORY + "PictureApp";

    private final int MY_PERMISSIONS = 100;
    private final int PHOTO_CODE = 200;
    private final int SELECT_PICTURE = 300;
    private JSONObject jsonObject;
    /**
     * The Flag for create/update
     */
    private boolean createOrupdate=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teamsmanagement);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        asyncService = AsyncApp42ServiceApi.instance(this);

        edtname = (TextInputLayout) findViewById(R.id.input_layout_name);
        edtphone = (TextInputLayout) findViewById(R.id.input_layout_phone);
        edtdescrip = (TextInputLayout) findViewById(R.id.input_layout_desc);

        mImg= (SmartImageView) findViewById(R.id.ImageTeams);

        mRlView = (LinearLayout) findViewById(R.id.layout_main);
        cumplimiento =(RatingBar) findViewById(R.id.rtbCumplimiento);

        spncity = (Spinner) findViewById(R.id.input_layout_city);
        ArrayAdapter<CharSequence> adapterspinner = ArrayAdapter.createFromResource(this,R.array.city, android.R.layout.simple_spinner_dropdown_item);
        spncity.setAdapter(adapterspinner);

        floatingActionButton  = (FloatingActionButton) findViewById(R.id.fabImage);
        if(mayRequestStoragePermission())
            floatingActionButton.setEnabled(true);
        else
            floatingActionButton.setEnabled(false);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptions();
            }
        });

        Bundle bundle =getIntent().getExtras();
        if(bundle!=null){
            try {
                jsonObject = new JSONObject(bundle.getString("object",""));
                edtname.getEditText().setText(jsonObject.getString("name"));
                edtphone.getEditText().setText(jsonObject.getString("phone"));
                edtdescrip.getEditText().setText(jsonObject.getString("desc"));
                createOrupdate = bundle.getBoolean("createOrupdate",true);
                JSONObject jsonObjectFile = new JSONObject(jsonObject.getString("_files"));
                mImg.setImageUrl(jsonObjectFile.getString("url"));
                selectedImage = jsonObjectFile.getString("url");
                spncity.setSelection(adapterspinner.getPosition(jsonObject.getString("city")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(createOrupdate)
            cumplimiento.setVisibility(View.INVISIBLE);
        else
            cumplimiento.setVisibility(View.VISIBLE);
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
            String srtname  = edtname.getEditText().getText().toString();
            String srtphone = edtphone.getEditText().getText().toString();
            String srtdesc  = edtdescrip.getEditText().getText().toString();


            try {
                if(selectedImage!=null) {
                    progressDialog = ProgressDialog.show(this, "", "Saving..");
                    progressDialog.setCancelable(true);
                    if(createOrupdate) {
                        jsonObject = new JSONObject();
                        jsonObject.put("name",srtname);
                        jsonObject.put("phone",srtphone);
                        jsonObject.put("city",spncity.getSelectedItem());
                        jsonObject.put("desc", (srtdesc.length() > 0) ? srtdesc : " ");
                        asyncService.insertJSONDocFile(Constants.App42DBName, "Teams", jsonObject, selectedImage,srtname, this);
                    }else{
                        jsonObject.put("name", srtname);
                        jsonObject.put("phone", srtphone);
                        jsonObject.put("city",spncity.getSelectedItem());
                        jsonObject.put("desc", (srtdesc.length() > 0) ? srtdesc : " ");
                        JSONObject jsonObjectFile = new JSONObject(jsonObject.getString("_files"));

                        asyncService.updateDocByKeyValue(Constants.App42DBName, "Teams","name",srtname, jsonObject, this);
                        asyncService.uploadImage(jsonObjectFile.getString("name"), selectedImage, UploadFileType.IMAGE, srtdesc, this);
                    }

                }else{
                    createAlertDialog("Input Picture");
                }
            } catch (JSONException e) {
                createAlertDialog("Exception Occurred : " + e.getMessage());
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDocumentInserted(Storage response) {
        progressDialog.dismiss();
        finish();
    }

    @Override
    public void onUpdateDocSuccess(Storage response) {
        progressDialog.dismiss();
        finish();
    }

    @Override
    public void onFindDocSuccess(Storage response) {

    }

    @Override
    public void onInsertionFailed(App42Exception ex) {
        createAlertDialog("Error: "+ ex.getMessage());
    }

    @Override
    public void onFindDocFailed(App42Exception ex) {
        createAlertDialog("Error: "+ ex.getMessage());
    }

    @Override
    public void onUpdateDocFailed(App42Exception ex) {
        createAlertDialog("Error: " + ex.getMessage());
    }

    @Override
    public void onUploadImageSuccess(Upload response) {

    }

    @Override
    public void onUploadImageFailed(App42Exception ex) {

    }

    @Override
    public void onGetImageSuccess(Upload response) {


    }

    @Override
    public void onGetImageFailed(App42Exception ex) {

    }


    /**
     * Creates the alert dialog.
     *
     * @param msg the msg
     */
    public void createAlertDialog(String msg) {
        AlertDialog.Builder alertbox = new AlertDialog.Builder(TeamMgtActivity.this);
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


    private void showOptions() {
        final CharSequence[] option = {"Tomar foto", "Elegir de galeria", "Cancelar"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(TeamMgtActivity.this);
        builder.setTitle("Eleige una opción");
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(option[which] == "Tomar foto"){
                    openCamera();
                }else if(option[which] == "Elegir de galeria"){
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent.createChooser(intent, "Selecciona app de imagen"), SELECT_PICTURE);
                }else {
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
            switch (requestCode){
                case PHOTO_CODE:
                    MediaScannerConnection.scanFile(this,
                            new String[]{selectedImage}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.i("ExternalStorage", "Scanned " + path + ":");
                                    Log.i("ExternalStorage", "-> Uri = " + uri);
                                    selectedImage = RealPathUtil.getRealPathFromURI_API19(getBaseContext(),uri);
                                }
                            });


                    Bitmap bitmap = BitmapFactory.decodeFile(selectedImage);

                    mImg.setImageBitmap(bitmap);
                    break;
                case SELECT_PICTURE:
                    Uri path = data.getData();
                    selectedImage = RealPathUtil.getRealPathFromURI_API19(this, path);
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
                Toast.makeText(TeamMgtActivity.this, "Permisos aceptados", Toast.LENGTH_SHORT).show();
                floatingActionButton.setEnabled(true);
            }
        }else{
            showExplanation();
        }
    }

    private void showExplanation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TeamMgtActivity.this);
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
}
