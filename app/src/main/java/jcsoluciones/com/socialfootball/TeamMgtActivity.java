package jcsoluciones.com.socialfootball;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Storage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ADMIN on 01/08/2016.
 */

public class TeamMgtActivity extends AppCompatActivity implements AsyncApp42ServiceApi.App42StorageServiceListener {
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
    private EditText edtname;
    /**
     * The phone
     */
    private EditText edtphone;
    /**
     * The description
     */
    private EditText edtdescrip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teamsmanagement);
        asyncService = AsyncApp42ServiceApi.instance(this);

        edtname = (EditText) findViewById(R.id.input_layout_name);
        edtphone = (EditText) findViewById(R.id.input_layout_phone);
        edtdescrip = (EditText) findViewById(R.id.input_layout_desc);
       /* progressDialog = ProgressDialog.show(this, "", "Searching..");
        progressDialog.setCancelable(true);
        asyncService.findDocByKeyValue(Constants.App42DBName, "diagnosticos", "id_creator", docId, this);*/

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

            JSONObject jsonTeams = new JSONObject();
            try {
                jsonTeams.put("name",srtname);
                jsonTeams.put("phone",srtphone);
                jsonTeams.put("desc", (srtdesc.length()>0)? srtdesc : " ");
                progressDialog = ProgressDialog.show(this, "", "Saving..");
                progressDialog.setCancelable(true);
                asyncService.insertJSONDoc(Constants.App42DBName, "Teams", jsonTeams,this);
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
        progressDialog.dismiss();
        createAlertDialog("Exception Occurred : " + ex.getMessage());
    }

    @Override
    public void onUpdateDocFailed(App42Exception ex) {

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

}
