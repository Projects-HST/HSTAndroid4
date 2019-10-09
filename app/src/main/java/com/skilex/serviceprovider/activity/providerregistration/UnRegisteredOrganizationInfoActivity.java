package com.skilex.serviceprovider.activity.providerregistration;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.skilex.serviceprovider.R;
import com.skilex.serviceprovider.helper.AlertDialogHelper;
import com.skilex.serviceprovider.helper.ProgressDialogHelper;
import com.skilex.serviceprovider.interfaces.DialogClickListener;
import com.skilex.serviceprovider.languagesupport.BaseActivity;
import com.skilex.serviceprovider.servicehelpers.ServiceHelper;
import com.skilex.serviceprovider.serviceinterfaces.IServiceListener;
import com.skilex.serviceprovider.utils.CommonUtils;
import com.skilex.serviceprovider.utils.PreferenceStorage;
import com.skilex.serviceprovider.utils.SkilExConstants;
import com.skilex.serviceprovider.utils.SkilExValidator;

import org.json.JSONException;
import org.json.JSONObject;

import static android.util.Log.d;

public class UnRegisteredOrganizationInfoActivity extends BaseActivity implements View.OnClickListener, IServiceListener,
        DialogClickListener {


    private static final String TAG = UnRegisteredOrganizationInfoActivity.class.getName();

    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;

    private RadioGroup rdgIndividualType, rdgAlsoServicePerson;
    private RadioButton rdbIndividual, rdbUnRegOrg, rdbYes, rdbNo;
    private EditText edtNumberOfPersons;
    private TextView txtAlsoServicePerson;
    private Button btnSubmit;

    private String noOfServicePersons = "1";
    private String alsoServicePerson = "Y";

    private boolean getNoOfServicePersonStatus = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_un_reg_org_details);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        rdgIndividualType = findViewById(R.id.rdgIndividualType);
        edtNumberOfPersons = findViewById(R.id.edtNoOfPersons);
        txtAlsoServicePerson = findViewById(R.id.txtAlsoServicePerson);
        rdbYes = findViewById(R.id.rdbYes);
        rdbNo = findViewById(R.id.rdbNo);
        rdgAlsoServicePerson = findViewById(R.id.rdgYesNo);
        rdgIndividualType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rdbIndividual:
                        edtNumberOfPersons.setTextColor(getResources().getColor(R.color.grey));
                        edtNumberOfPersons.setEnabled(false);
                        edtNumberOfPersons.setText("");
                        txtAlsoServicePerson.setTextColor(getResources().getColor(R.color.grey));
                        rdbYes.setTextColor(getResources().getColor(R.color.grey));
                        rdbYes.setEnabled(false);
                        rdbNo.setTextColor(getResources().getColor(R.color.grey));
                        rdbNo.setEnabled(false);
                        getNoOfServicePersonStatus = false;
                        noOfServicePersons = "1";
                        alsoServicePerson = "Y";
                        break;
                    case R.id.rdbUnRegOrg:
                        edtNumberOfPersons.setTextColor(getResources().getColor(R.color.black));
                        edtNumberOfPersons.setEnabled(true);
                        txtAlsoServicePerson.setTextColor(getResources().getColor(R.color.black));
                        rdbYes.setTextColor(getResources().getColor(R.color.black));
                        rdbYes.setEnabled(true);
                        rdbNo.setTextColor(getResources().getColor(R.color.black));
                        rdbNo.setEnabled(true);
                        getNoOfServicePersonStatus = true;
                        alsoServicePerson = "Y";
                        break;
                }
            }
        });

        rdgAlsoServicePerson.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rdbYes:
                        noOfServicePersons = edtNumberOfPersons.getText().toString();
                        alsoServicePerson = "Y";
                        break;
                    case R.id.rdbNo:
                        noOfServicePersons = edtNumberOfPersons.getText().toString();
                        alsoServicePerson = "N";
                        break;
                }
            }
        });

        btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    @Override
    public void onClick(View v) {
        if (CommonUtils.haveNetworkConnection(getApplicationContext())) {
            if (v == btnSubmit) {
                if (validateFields()) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put(SkilExConstants.USER_MASTER_ID, PreferenceStorage.getUserMasterId(getApplicationContext()));
                        jsonObject.put(SkilExConstants.KEY_NO_OF_SERVICE_PERSON, noOfServicePersons);
                        jsonObject.put(SkilExConstants.KEY_ALSO_A_SERVICE_PERSON, alsoServicePerson);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
                    String url = SkilExConstants.BUILD_URL + SkilExConstants.PROVIDER_INDIVIDUAL_DETAILS;
                    serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
                }
            }
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, "No Network connection available");
        }
    }

    private boolean validateFields() {
        if (getNoOfServicePersonStatus) {
            if (!SkilExValidator.checkNullString(this.edtNumberOfPersons.getText().toString().trim())) {
                edtNumberOfPersons.setError(getString(R.string.empty_entry));
                requestFocus(edtNumberOfPersons);
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
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

        if (validateSignInResponse(response)) {

            Intent i = new Intent(UnRegisteredOrganizationInfoActivity.this, UnRegOrgDocumentUploadActivity.class);
            startActivity(i);

        }
    }

    @Override
    public void onError(String error) {
        progressDialogHelper.hideProgressDialog();
        AlertDialogHelper.showSimpleAlertDialog(this, error);
    }
}
