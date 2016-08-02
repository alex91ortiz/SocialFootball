package jcsoluciones.com.socialfootball;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import com.shephertz.app42.paas.sdk.android.upload.Upload;
import com.shephertz.app42.paas.sdk.android.upload.UploadFileType;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

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
     * The status Image of Gallery
     */
    private  int SELECT_FILE = 1;

    /**
     * The Button for open image
     */

    private FloatingActionButton floatingActionButton;
    private Uri selectedImage;
    private ImageView mImg;
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

        mImg= (ImageView) findViewById(R.id.ImageTeams);

        floatingActionButton  = (FloatingActionButton) findViewById(R.id.fabImage);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(
                        Intent.createChooser(intent, "Seleccione una imagen"),
                        SELECT_FILE);
            }
        });

        Bundle bundle =getIntent().getExtras();
        if(bundle!=null){
            try {
                JSONObject jsonObject = new JSONObject(bundle.getString("object",""));
                edtname.getEditText().setText(jsonObject.getString("name"));
                edtphone.getEditText().setText(jsonObject.getString("phone"));
                edtdescrip.getEditText().setText(jsonObject.getString("desc"));
                asyncService.getImage(jsonObject.getString("name"),this);
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
            String srtname  = edtname.getEditText().getText().toString();
            String srtphone = edtphone.getEditText().getText().toString();
            String srtdesc  = edtdescrip.getEditText().getText().toString();

            JSONObject jsonTeams = new JSONObject();
            try {
                jsonTeams.put("name",srtname);
                jsonTeams.put("phone",srtphone);
                jsonTeams.put("desc", (srtdesc.length() > 0) ? srtdesc : " ");


                if(selectedImage!=null) {
                    progressDialog = ProgressDialog.show(this, "", "Saving..");
                    progressDialog.setCancelable(true);
                    asyncService.insertJSONDoc(Constants.App42DBName, "Teams", jsonTeams, this);
                    asyncService.uploadImage(srtname, selectedImage.getPath().toString(), UploadFileType.IMAGE, srtdesc, this);
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

    }

    @Override
    public void onFindDocSuccess(Storage response) {
        /*progressDialog.dismiss();
        jsonDocList = response.getJsonDocList();
        final List<Diagnostico> convertList = new ArrayList<>();
        try {
            for (int i = 0; i < jsonDocList.size(); i++) {
                Storage.JSONDocument jsonDocument = jsonDocList.get(i);
                String docId = jsonDocument.getDocId();
                JSONObject jsonObject = new JSONObject(jsonDocument.getJsonDoc());
                Diagnostico diagnostico = new Diagnostico();
                diagnostico.setId(docId);
                diagnostico.setDescripcion(jsonObject.getString("descripcion"));
                diagnostico.setSchema(jsonDocument.getJsonDoc());
                diagnostico.setCantidadtest(jsonObject.getInt("cantidadtest"));
                diagnostico.setTotalcalificacion((float) jsonObject.getDouble("totalcalificacion"));
                Drawable drawable = mProvider.getRoundWithBorder(diagnostico.getDescripcion().substring(0, 1).toUpperCase());
                diagnostico.setDrawable(drawable);
                convertList.add(diagnostico);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        DiagnosticsAdapter diagnosticsAdapter = new DiagnosticsAdapter(this, 0, convertList);
        listDianostic.setAdapter(diagnosticsAdapter);/*progressDialog.dismiss();
        jsonDocList = response.getJsonDocList();
        final List<Diagnostico> convertList = new ArrayList<>();
        try {
            for (int i = 0; i < jsonDocList.size(); i++) {
                Storage.JSONDocument jsonDocument = jsonDocList.get(i);
                String docId = jsonDocument.getDocId();
                JSONObject jsonObject = new JSONObject(jsonDocument.getJsonDoc());
                Diagnostico diagnostico = new Diagnostico();
                diagnostico.setId(docId);
                diagnostico.setDescripcion(jsonObject.getString("descripcion"));
                diagnostico.setSchema(jsonDocument.getJsonDoc());
                diagnostico.setCantidadtest(jsonObject.getInt("cantidadtest"));
                diagnostico.setTotalcalificacion((float) jsonObject.getDouble("totalcalificacion"));
                Drawable drawable = mProvider.getRoundWithBorder(diagnostico.getDescripcion().substring(0, 1).toUpperCase());
                diagnostico.setDrawable(drawable);
                convertList.add(diagnostico);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        listDianostic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), TestActivity.class);
                intent.putExtra("id", convertList.get(position).getId());
                intent.putExtra("diagnostico", convertList.get(position).getSchema());
                startActivity(intent);
            }
        });
        listDianostic.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);*/
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
    public void onUploadImageSuccess(Upload response) {

    }

    @Override
    public void onUploadImageFailed(App42Exception ex) {

    }

    @Override
    public void onGetImageSuccess(Upload response) {
        Upload upload = (Upload)response;
        ArrayList<Upload.File> fileList = upload.getFileList();
        for(int i = 0; i < fileList.size();i++ )
        {
            new DownloadImageTask(mImg).execute(fileList.get(i).getUrl());
        }

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

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        Uri selectedImageUri = null;


        String filePath = null;

        if (resultCode == Activity.RESULT_OK) {
            selectedImage = imageReturnedIntent.getData();

            if (requestCode == SELECT_FILE) {

                if (selectedImage != null) {
                    InputStream imageStream = null;
                    try {
                        imageStream = getContentResolver().openInputStream(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    // Transformamos la URI de la imagen a inputStream y este a un Bitmap
                    Bitmap bmp = BitmapFactory.decodeStream(imageStream);
                    mImg.setImageBitmap(bmp);

                }
            }
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
