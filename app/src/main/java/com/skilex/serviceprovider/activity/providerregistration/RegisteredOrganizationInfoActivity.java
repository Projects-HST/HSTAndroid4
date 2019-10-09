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

public class RegisteredOrganizationInfoActivity extends BaseActivity implements View.OnClickListener, IServiceListener,
        DialogClickListener {

    private static final String TAG = RegisteredOrganizationInfoActivity.class.getName();

    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;

    private EditText edtRegCompanyName, edtNoOfWorkPeople, edtRegCompanyAddress1, edtRegCompanyAddress2,
            edtRegCompanyCity, edtRegCompanyPinCode, edtRegCompanyState;

    private RadioGroup rdgBuildType;
    private RadioButton rdbOwnBuild, rdbRentalBuild;
    private String buildingType = "Own";

    private Button btnSubmit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_org_details);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        edtRegCompanyName = findViewById(R.id.edtRegCompanyName);
        edtNoOfWorkPeople = findViewById(R.id.edtNoOfWorkPeople);
        edtRegCompanyAddress1 = findViewById(R.id.edtRegCompanyAddress1);
        edtRegCompanyAddress2 = findViewById(R.id.edtRegCompanyAddress2);
        edtRegCompanyCity = findViewById(R.id.edtRegCompanyCity);
        edtRegCompanyPinCode = findViewById(R.id.edtRegCompanyPinCode);
        edtRegCompanyState = findViewById(R.id.edtRegCompanyState);

        rdgBuildType = findViewById(R.id.rdgBuildingType);
        rdbOwnBuild = findViewById(R.id.rdbOwn);
        rdbRentalBuild = findViewById(R.id.rdbTenancy);
        rdgBuildType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rdbOwn:
                        buildingType = "Own";
                        break;
                    case R.id.rdbTenancy:
                        buildingType = "Tenancy";
                        break;
                }
            }
        });


        btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (CommonUtils.haveNetworkConnection(getApplicationContext())) {
            if (v == btnSubmit) {
                if (validateFields()) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put(SkilExConstants.KEY_USER_MASTER_ID, PreferenceStorage.getUserMasterId(getApplicationContext()));
                        jsonObject.put(SkilExConstants.KEY_COMPANY_NAME, edtRegCompanyName.getText().toString());
                        jsonObject.put(SkilExConstants.KEY_REG_NO_OF_PERSON, edtNoOfWorkPeople.getText().toString());
                        jsonObject.put(SkilExConstants.KEY_COMPANY_ADDRESS, edtRegCompanyAddress1.getText().toString());
                        jsonObject.put(SkilExConstants.KEY_COMPANY_CITY, edtRegCompanyCity.getText().toString());
                        jsonObject.put(SkilExConstants.KEY_COMPANY_STATE, edtRegCompanyState.getText().toString());
                        jsonObject.put(SkilExConstants.KEY_COMPANY_ZIP, edtRegCompanyPinCode.getText().toString());
                        jsonObject.put(SkilExConstants.KEY_COMPANY_BUILDING_TYPE, buildingType);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
                    String url = SkilExConstants.BUILD_URL + SkilExConstants.PROVIDER_REGISTERED_ORG_DETAILS;
                    serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
                }
            }
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, "No Network connection available");
        }
    }

    private boolean validateFields() {

        if (!SkilExValidator.checkNullString(this.edtRegCompanyName.getText().toString().trim())) {
            edtRegCompanyName.setError(getString(R.string.empty_entry));
            requestFocus(edtRegCompanyName);
            return false;
        } else if (!SkilExValidator.checkNullString(this.edtNoOfWorkPeople.getText().toString().trim())) {
            edtNoOfWorkPeople.setError(getString(R.string.empty_entry));
            requestFocus(edtNoOfWorkPeople);
            return false;
        } else if (!SkilExValidator.checkNullString(this.edtRegCompanyAddress1.getText().toString().trim())) {
            edtRegCompanyAddress1.setError(getString(R.string.empty_entry));
            requestFocus(edtRegCompanyAddress1);
            return false;
        } else if (!SkilExValidator.checkNullString(this.edtRegCompanyAddress2.getText().toString().trim())) {
            edtRegCompanyAddress2.setError(getString(R.string.empty_entry));
            requestFocus(edtRegCompanyAddress2);
            return false;
        } else if (!SkilExValidator.checkNullString(this.edtRegCompanyCity.getText().toString().trim())) {
            edtRegCompanyCity.setError(getString(R.string.empty_entry));
            requestFocus(edtRegCompanyCity);
            return false;
        } else if (!SkilExValidator.checkNullString(this.edtRegCompanyPinCode.getText().toString().trim())) {
            edtRegCompanyPinCode.setError(getString(R.string.empty_entry));
            requestFocus(edtRegCompanyPinCode);
            return false;
        }
//        else if (!SkilExValidator.checkPinCodeLength(this.edtRegCompanyPinCode.getText().toString().trim())) {
//            edtRegCompanyPinCode.setError(getString(R.string.empty_entry));
//            requestFocus(edtRegCompanyPinCode);
//            return false;
//        }
        else if (!SkilExValidator.checkNullString(this.edtRegCompanyState.getText().toString().trim())) {
            edtRegCompanyState.setError(getString(R.string.empty_entry));
            requestFocus(edtRegCompanyState);
            return false;
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
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
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
            Intent i = new Intent(getApplicationContext(), RegOrgDocumentUploadActivity.class);
            startActivity(i);
        }
    }

    @Override
    public void onError(String error) {
        progressDialogHelper.hideProgressDialog();
        AlertDialogHelper.showSimpleAlertDialog(this, error);
    }
}
