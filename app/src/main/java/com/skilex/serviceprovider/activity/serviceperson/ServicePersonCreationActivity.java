package com.skilex.serviceprovider.activity.serviceperson;

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
import android.widget.TextView;

import com.skilex.serviceprovider.R;
import com.skilex.serviceprovider.activity.loginmodule.OTPVerificationActivity;
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

public class ServicePersonCreationActivity extends BaseActivity implements DialogClickListener, IServiceListener, View.OnClickListener {

    private static final String TAG = ServicePersonCreationActivity.class.getName();
    Context context;
    private EditText edtServicePersonName, edtServicePersonMobileNumber, edtServicePersonMailId;
    private Button btnSubmit;

    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_person_creation);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        edtServicePersonName = findViewById(R.id.edtSPersonName);
        edtServicePersonMobileNumber = findViewById(R.id.edtSPersonMobile);
        edtServicePersonMailId = findViewById(R.id.edtSPersonEmail);

        btnSubmit = findViewById(R.id.btnSubmitCreation);
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
                        jsonObject.put(SkilExConstants.REGISTRATION_NAME, edtServicePersonName.getText().toString());
                        jsonObject.put(SkilExConstants.REGISTRATION_PHONE_NUMBER, edtServicePersonMobileNumber.getText().toString());
                        jsonObject.put(SkilExConstants.REGISTRATION_EMAIL_ID, edtServicePersonMailId.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
                    String url = SkilExConstants.BUILD_URL + SkilExConstants.API_SERVICE_PERSON_CREATION;
                    serviceHelper.makeGetServiceCall(jsonObject.toString(), url);

                }
            }
        }
    }

    private boolean validateFields() {
        if (!SkilExValidator.checkNullString(this.edtServicePersonName.getText().toString().trim())) {
            edtServicePersonName.setError(getString(R.string.empty_entry));
            requestFocus(edtServicePersonName);
            return false;
        } else if (!SkilExValidator.checkNullString(this.edtServicePersonMobileNumber.getText().toString().trim())) {
            edtServicePersonMobileNumber.setError(getString(R.string.empty_entry));
            requestFocus(edtServicePersonMobileNumber);
            return false;
        } else if (!SkilExValidator.checkMobileNumLength(this.edtServicePersonMobileNumber.getText().toString().trim())) {
            edtServicePersonMobileNumber.setError(getString(R.string.error_number));
            requestFocus(edtServicePersonMobileNumber);
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

            try {

                String servicePersonId = response.getString("serv_person_id");
                PreferenceStorage.saveServicePersonId(getApplicationContext(), servicePersonId);

                Intent i = new Intent(getApplicationContext(), ServicePersonDetailInfoActivity.class);
                startActivity(i);
                finish();

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }

    @Override
    public void onError(String error) {

    }
}
