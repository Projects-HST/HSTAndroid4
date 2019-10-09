package com.skilex.serviceprovider.activity.serviceperson;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.skilex.serviceprovider.R;
import com.skilex.serviceprovider.activity.providerregistration.RegOrgDocumentUploadActivity;
import com.skilex.serviceprovider.bean.support.StoreMasterId;
import com.skilex.serviceprovider.helper.AlertDialogHelper;
import com.skilex.serviceprovider.helper.ProgressDialogHelper;
import com.skilex.serviceprovider.interfaces.DialogClickListener;
import com.skilex.serviceprovider.languagesupport.BaseActivity;
import com.skilex.serviceprovider.servicehelpers.ServiceHelper;
import com.skilex.serviceprovider.serviceinterfaces.IServiceListener;
import com.skilex.serviceprovider.utils.CommonUtils;
import com.skilex.serviceprovider.utils.FilePath;
import com.skilex.serviceprovider.utils.PreferenceStorage;
import com.skilex.serviceprovider.utils.SkilExConstants;
import com.skilex.serviceprovider.utils.SkilExValidator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import static android.util.Log.d;

public class ServicePersonDocumentUploadActivity extends BaseActivity implements View.OnClickListener, IServiceListener,
        DialogClickListener {

    private static final String TAG = ServicePersonDocumentUploadActivity.class.getName();

    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;

    private EditText edtProofNo1;
    private TextView txtUploadProof1;
    private Spinner spnIdProofType1;

    private EditText edtProofNo2;
    private TextView txtUploadProof2;
    private Spinner spnIdProofType2;

    private Button btnSubmit;

    private String storeDocumentNumber = "";
    private String storeDocumentMasterId = "";
    private String checkValue = "";
    private int flag = 1;
    private String spinnerValue1 = "";
    private String spinnerValue2 = "";

    private static final int PICK_FILE_REQUEST = 1;
    private String selectedFilePath;
    File sizeCge;
    ProgressDialog dialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_person_doc_upload);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        edtProofNo1 = findViewById(R.id.edtProof1);
        txtUploadProof1 = findViewById(R.id.txtUploadProof1);
        txtUploadProof1.setOnClickListener(this);
        spnIdProofType1 = findViewById(R.id.spnIdProofType1);

        edtProofNo2 = findViewById(R.id.edtProof2);
        txtUploadProof2 = findViewById(R.id.txtUploadProof2);
        txtUploadProof2.setOnClickListener(this);
        spnIdProofType2 = findViewById(R.id.spnIdProofType2);

        btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(this);

        getPersonIdProofType();
        spnIdProofType1.setEnabled(false);

        spnIdProofType1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                StoreMasterId classList = (StoreMasterId) parent.getSelectedItem();
                spinnerValue1 = classList.getDocId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnIdProofType2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                StoreMasterId classList = (StoreMasterId) parent.getSelectedItem();
                spinnerValue2 = classList.getDocId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    private void getPersonIdProofType() {

        checkValue = "IdProof1";

        if (CommonUtils.isNetworkAvailable(this)) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(SkilExConstants.KEY_COMPANY_TYPE, "Individual");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            String url = SkilExConstants.BUILD_URL + SkilExConstants.ID_PROOF_LIST;
            serviceHelper.makeGetServiceCall(jsonObject.toString(), url);

        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, "No Network connection");
        }
    }

    @Override
    public void onClick(View v) {
        if (CommonUtils.haveNetworkConnection(getApplicationContext())) {
            if (v == txtUploadProof1) {
                if (validateFields()) {
                    storeDocumentNumber = edtProofNo1.getText().toString();
                    storeDocumentMasterId = spinnerValue1;
                    showFileChooser();
                }
            } else if (v == txtUploadProof2) {
                if (flag == 2) {
                    if (validateFields()) {
                        String spn1 = spnIdProofType1.getSelectedItem().toString();
                        String spn2 = spnIdProofType2.getSelectedItem().toString();

                        if (spn2.equalsIgnoreCase(spn1)) {
                            Toast.makeText(getApplicationContext(), "Try some other Id proof", Toast.LENGTH_LONG).show();
                        } else {
                            storeDocumentMasterId = spinnerValue2;
                            storeDocumentNumber = edtProofNo2.getText().toString();
                            showFileChooser();
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Complete first proof upload", Toast.LENGTH_LONG).show();
                }
            } else if (v == btnSubmit) {
                if (flag == 3) {
                    Intent i = new Intent(getApplicationContext(), DocUploadOnSuccessActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Complete second proof upload", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, "No Network connection available");
        }
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        //sets the select file to all types of files
        intent.setType("*/*");
        //allows to select data and return it
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //starts new activity to select file and return data
        startActivityForResult(Intent.createChooser(intent, "Choose file to upload.."), PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_FILE_REQUEST) {
                if (data == null) {
                    //no data present
                    return;
                }

                Uri selectedFileUri = data.getData();
                selectedFilePath = FilePath.getPath(this, selectedFileUri);
                Log.i(TAG, "Selected File Path:" + selectedFilePath);

                if (selectedFilePath != null && !selectedFilePath.equals("")) {
                    sizeCge = new File(selectedFilePath);
                    if (sizeCge.length() >= 40000000) {
                        AlertDialogHelper.showSimpleAlertDialog(this, "File size too large");
                        selectedFilePath = null;
                    } else {
                        Toast.makeText(this, "File ready to upload", Toast.LENGTH_SHORT).show();
                        dialog = ProgressDialog.show(ServicePersonDocumentUploadActivity.this, "", "Uploading File...", true);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                //creating new thread to handle Http Operations
//                        uploadFile(selectedFilePath);
                                new PostDataAsyncTask().execute();
                            }
                        }).start();
                    }
                } else {
                    Toast.makeText(this, "Cannot upload file to server", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private class PostDataAsyncTask extends AsyncTask<Void, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        //android upload file to server
        private String uploadFile() {

            int serverResponseCode = 0;
            String serverResponseMessage = null;
            HttpURLConnection connection;
            DataOutputStream dataOutputStream;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";

            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            File selectedFile = new File(selectedFilePath);
            double len = selectedFile.length();

            String[] parts = selectedFilePath.split("/");
            final String fileName = parts[parts.length - 1];

            if (!selectedFile.isFile()) {
                dialog.dismiss();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        tvFileName.setText("Source File Doesn't Exist: " + selectedFilePath);
                    }
                });
                return "";
            } else {
                try {
                    String id = PreferenceStorage.getUserMasterId(getApplicationContext());
//                    String id = "118";
                    String document_master_id = storeDocumentMasterId;
                    String document_proof_number = storeDocumentNumber;

                    FileInputStream fileInputStream = new FileInputStream(selectedFile);
                    String SERVER_URL = SkilExConstants.BUILD_URL + SkilExConstants.UPLOAD_DOCUMENT + "" + id + "/" + document_master_id + "/" + document_proof_number + "/";
                    URI uri = new URI(SERVER_URL.replace(" ", "%20"));
                    String baseURL = uri.toString();
                    URL url = new URL(baseURL);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);//Allow Inputs
                    connection.setDoOutput(true);//Allow Outputs
                    connection.setUseCaches(false);//Don't use a cached Copy
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Connection", "Keep-Alive");
                    connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                    connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    connection.setRequestProperty("doc_file", selectedFilePath);
//                    connection.setRequestProperty("user_id", id);
//                    connection.setRequestProperty("doc_name", title);
//                    connection.setRequestProperty("doc_month_year", start);

                    //creating new dataoutputstream
                    dataOutputStream = new DataOutputStream(connection.getOutputStream());

                    //writing bytes to data outputstream
                    dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                    dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"document_file\";filename=\""
                            + selectedFilePath + "\"" + lineEnd);

                    dataOutputStream.writeBytes(lineEnd);

                    //returns no. of bytes present in fileInputStream
                    bytesAvailable = fileInputStream.available();
                    //selecting the buffer size as minimum of available bytes or 1 MB
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    //setting the buffer as byte array of size of bufferSize
                    buffer = new byte[bufferSize];

                    //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    //loop repeats till bytesRead = -1, i.e., no bytes are left to read
                    while (bytesRead > 0) {
                        //write the bytes read from inputstream
                        dataOutputStream.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }

                    dataOutputStream.writeBytes(lineEnd);
                    dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    serverResponseCode = connection.getResponseCode();
                    serverResponseMessage = connection.getResponseMessage();

                    Log.i(TAG, "Server Response is: " + serverResponseMessage + ": " + serverResponseCode);

                    //response code of 200 indicates the server status OK
                    if (serverResponseCode == 200) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                            tvFileName.setText("File Upload completed.\n\n You can see the uploaded file here: \n\n" + "http://coderefer.com/extras/uploads/"+ fileName);
//                                tvFileName.setText("File Upload completed.\n\n"+ fileName);
                            }
                        });
                    }

                    //closing the input and output streams
                    fileInputStream.close();
                    dataOutputStream.flush();
                    dataOutputStream.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "File Not Found", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "URL error!", Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Cannot Read/Write File!", Toast.LENGTH_SHORT).show();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
                return serverResponseMessage;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(TAG, "Response from server: " + result);
            progressDialogHelper.hideProgressDialog();

            super.onPostExecute(result);
            if ((result.contains("OK"))) {
                Toast.makeText(getApplicationContext(), "Uploaded successfully!", Toast.LENGTH_SHORT).show();

                if (flag == 1) {
                    edtProofNo1.setEnabled(false);
                    edtProofNo1.setFocusable(false);
                    spnIdProofType1.setEnabled(false);
                    txtUploadProof1.setText("");
                    txtUploadProof1.setEnabled(false);
                    txtUploadProof1.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_upload_successful, 0);
                    flag = 2;
                } else if (flag == 2) {
                    edtProofNo2.setEnabled(false);
                    edtProofNo2.setFocusable(false);
                    spnIdProofType2.setEnabled(false);
                    txtUploadProof2.setText("");
                    txtUploadProof2.setEnabled(false);
                    txtUploadProof2.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_upload_successful, 0);
                    flag = 3;
                }
            } else {
                Toast.makeText(getApplicationContext(), "Unable to upload file", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    private boolean validateFields() {

        if (flag == 1) {
            if (!SkilExValidator.checkNullString(this.edtProofNo1.getText().toString().trim())) {
                edtProofNo1.setError(getString(R.string.empty_entry));
                requestFocus(edtProofNo1);
                return false;
            }
        }
        if (flag == 2) {
            if (!SkilExValidator.checkNullString(this.edtProofNo2.getText().toString().trim())) {
                edtProofNo2.setError(getString(R.string.empty_entry));
                requestFocus(edtProofNo2);
                return false;
            }
        } else {
            return true;
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }

    private boolean validateSignInResponse(JSONObject response) {
        boolean signInSuccess = false;
        if ((response != null)) {
            try {
                String status = response.getString("status");
                String msg = response.getString(SkilExConstants.PARAM_MESSAGE);
                d(TAG, "status val" + status + "msg" + msg);

                if ((status != null)) {
                    if (((status.equalsIgnoreCase("activationError")) || (status.equalsIgnoreCase("alreadyRegistered")) ||
                            (status.equalsIgnoreCase("notRegistered")) || (status.equalsIgnoreCase("error")))) {
                        signInSuccess = false;
                        d(TAG, "Show error dialog");
                        AlertDialogHelper.showSimpleAlertDialog(this, msg);

                    } else {
                        signInSuccess = true;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return signInSuccess;
    }

    @Override
    public void onResponse(JSONObject response) {
        progressDialogHelper.hideProgressDialog();
        try {
            if (validateSignInResponse(response)) {
                if (checkValue.equalsIgnoreCase("IdProof1")) {

                    JSONArray getData = response.getJSONArray("proof_list");
                    JSONObject userData = getData.getJSONObject(0);
                    int getLength = getData.length();
                    String subjectName = null;
                    Log.d(TAG, "userData dictionary" + userData.toString());

                    String docId = "";
                    String docName = "";
                    ArrayList<StoreMasterId> docNumberList = new ArrayList<>();

                    for (int i = 0; i < getLength; i++) {

                        docId = getData.getJSONObject(i).getString("id");
                        docName = getData.getJSONObject(i).getString("doc_name");

                        docNumberList.add(new StoreMasterId(docId, docName));
                    }

                    //fill data in spinner
                    ArrayAdapter<StoreMasterId> adapter = new ArrayAdapter<StoreMasterId>(getApplicationContext(), R.layout.spinner_item_ns, docNumberList);
                    spnIdProofType1.setAdapter(adapter);

                    //fill data in spinner
                    ArrayAdapter<StoreMasterId> adapter1 = new ArrayAdapter<StoreMasterId>(getApplicationContext(), R.layout.spinner_item_ns, docNumberList);
                    spnIdProofType2.setAdapter(adapter1);

                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onError(String error) {
        progressDialogHelper.hideProgressDialog();
        AlertDialogHelper.showSimpleAlertDialog(this, error);
    }
}
