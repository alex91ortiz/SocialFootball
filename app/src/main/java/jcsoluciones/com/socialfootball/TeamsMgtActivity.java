package jcsoluciones.com.socialfootball;

import android.annotation.TargetApi;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;

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
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;

import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.loopj.android.image.SmartImageView;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import com.shephertz.app42.paas.sdk.android.upload.Upload;
import com.shephertz.app42.paas.sdk.android.upload.UploadFileType;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import java.util.ArrayList;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class TeamsMgtActivity extends AppCompatActivity implements AsyncApp42ServiceApi.App42StorageServiceListener , AsyncApp42ServiceApi.App42UploadServiceListener {
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
    private String IdTeams;
    private SmartImageView mImg;
    private RelativeLayout mRlView;
    private static String APP_DIRECTORY = "MyPictureApp/";
    private static String MEDIA_DIRECTORY = APP_DIRECTORY + "PictureApp";

    private final int MY_PERMISSIONS = 100;
    private final int PHOTO_CODE = 200;
    private final int SELECT_PICTURE = 300;
    private JSONObject jsonObject;
    private SwitchCompat switchCancel;
    /**
     * The Flag for create/update
     */
    private boolean createOrupdate=true;
    private boolean resultImageOnSelected=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teams_mgt);
       /* Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

        asyncService = AsyncApp42ServiceApi.instance(this);


        edtname = (BootstrapEditText) findViewById(R.id.input_layout_name);
        edtphone = (BootstrapEditText) findViewById(R.id.input_layout_phone);
        edtdescrip = (BootstrapEditText) findViewById(R.id.input_layout_desc);

        mImg= (SmartImageView) findViewById(R.id.ImageTeams);

        mRlView = (RelativeLayout) findViewById(R.id.layout_main);
        cumplimiento =(RatingBar) findViewById(R.id.rtbCumplimiento);

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
                edtname.setText(jsonObject.getString("name"));
                edtphone.setText(jsonObject.getString("phone"));
                edtdescrip.setText(jsonObject.getString("desc"));
                createOrupdate = bundle.getBoolean("createOrupdate", true);
                /*JSONObject jsonObjectFile = new JSONObject(jsonObject.getString("_files"));
                ;
                selectedImage = jsonObjectFile.getString("url");*/
                IdTeams = bundle.getString("IdTeams", "");
                selectedImage =jsonObject.getString("ImageUrl");
                mImg.setImageUrl(selectedImage);
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


            try {

                progressDialog = ProgressDialog.show(this, "", "Saving..");
                progressDialog.setCancelable(true);
                if(createOrupdate) {
                    if(selectedImage!=null) {
                        jsonObject = new JSONObject();
                        jsonObject.put("name",srtname);
                        jsonObject.put("countComply",0);
                        jsonObject.put("complyValue",0);
                        jsonObject.put("phone",srtphone);
                        jsonObject.put("city",spncity.getSelectedItem());
                        jsonObject.put("desc", (srtdesc.length() > 0) ? srtdesc : " ");
                        jsonObject.put("active",true);
                        asyncService.insertJSONDoc(Constants.App42DBName, "Teams", jsonObject, this);
                    }else{
                        createAlertDialog("Input Picture");
                    }
                }else{
                    jsonObject.put("name", srtname);
                    jsonObject.put("phone", srtphone);
                    jsonObject.put("city",spncity.getSelectedItem());
                    jsonObject.put("desc", (srtdesc.length() > 0) ? srtdesc : " ");
                    if(resultImageOnSelected)
                        asyncService.deleteImage(edtname.getText().toString().replaceAll("^\\s*", ""), this);
                    else
                        asyncService.updateDocByKeyValue(Constants.App42DBName, "Teams", "name", edtname.getText().toString(), jsonObject, this);

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

        asyncService.uploadImage( edtname.getText().toString().replaceAll("^\\s*",""), selectedImage, UploadFileType.IMAGE,
                edtname.getText().toString(), this);

    }

    @Override
    public void onUpdateDocSuccess(Storage response) {
        progressDialog.dismiss();
        finish();
    }

    @Override
    public void onFindDocSuccess(Storage response) {
        progressDialog.dismiss();
        finish();
    }

    @Override
    public void onDeleteDocSuccess() {

    }

    @Override
    public void onInsertionFailed(App42Exception ex) {
        progressDialog.dismiss();
        createAlertDialog("Error: " + ex.getMessage());
    }

    @Override
    public void onFindDocFailed(App42Exception ex) {
        progressDialog.dismiss();
        createAlertDialog("Error: " + ex.getMessage());
    }

    @Override
    public void onUpdateDocFailed(App42Exception ex) {
        progressDialog.dismiss();
        createAlertDialog("Error: " + ex.getMessage());
    }

    @Override
    public void onDeleteDocFailed(App42Exception ex) {
        progressDialog.dismiss();
        createAlertDialog("Error: " + ex.getMessage());
    }

    @Override
    public void onUploadImageSuccess(Upload response) {

        Upload upload = (Upload) response;
        ArrayList<Upload.File> fileList = upload.getFileList();
        if (fileList.size() > 0) {
            try {
                jsonObject.put("ImageUrl", fileList.get(0).getUrl());
                asyncService.updateDocByKeyValue(Constants.App42DBName, "Teams", "name", edtname.getText().toString(), jsonObject, this);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        progressDialog.dismiss();
        finish();
    }



    @Override
    public void onUploadImageFailed(App42Exception ex) {
        createAlertDialog("Error: " + ex.getMessage());
    }

    @Override
    public void onGetImageSuccess(Upload response) {
        finish();
    }

    @Override
    public void onGetImageFailed(App42Exception ex) {
        createAlertDialog("Error: " + ex.getMessage());
    }

    @Override
    public void onDeleteImageSuccess() {
        asyncService.uploadImage( edtname.getText().toString().replaceAll("^\\s*",""), selectedImage, UploadFileType.IMAGE,
                edtname.getText().toString(), this);
    }

    @Override
    public void onDeleteImageFailed(App42Exception ex) {
        createAlertDialog("Error: " + ex.getMessage());
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
                deleteTeam();
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

    private void deleteTeam(){
        try {
            progressDialog = ProgressDialog.show(this, "", "Deleting..");
            progressDialog.setCancelable(true);
            jsonObject.put("active",false);
            asyncService.updateDocByKeyValue(Constants.App42DBName, "Teams", "name", edtname.getText().toString(), jsonObject, this);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void showOptions() {
        final CharSequence[] option = {"Tomar foto", "Elegir de galeria", "Cancelar"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(TeamsMgtActivity.this);
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
}
