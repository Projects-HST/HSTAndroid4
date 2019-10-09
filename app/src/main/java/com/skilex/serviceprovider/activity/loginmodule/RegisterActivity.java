package com.skilex.serviceprovider.activity.loginmodule;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.skilex.serviceprovider.R;
import com.skilex.serviceprovider.bean.support.Preference;
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

public class RegisterActivity extends BaseActivity implements IServiceListener, DialogClickListener, View.OnClickListener {

    private static final String TAG = RegisterActivity.class.getName();
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private Button btnSubmit;
    private EditText edtRegisterName, edtRegisterPhoneNumber, edtRegisterEmailID;
    ActionBar actionBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

//        actionBar.setDisplayHomeAsUpEnabled(true);

        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(this);
        edtRegisterName = findViewById(R.id.edtRegName);
        edtRegisterPhoneNumber = findViewById(R.id.edtRegPhoneNo);
        edtRegisterEmailID = findViewById(R.id.edtRegMailId);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
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
                        jsonObject.put(SkilExConstants.REGISTRATION_NAME, edtRegisterName.getText().toString());
                        jsonObject.put(SkilExConstants.REGISTRATION_PHONE_NUMBER, edtRegisterPhoneNumber.getText().toString());
                        jsonObject.put(SkilExConstants.REGISTRATION_EMAIL_ID, edtRegisterEmailID.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
                    String url = SkilExConstants.BUILD_URL + SkilExConstants.REGISTER_SERVICE_PROVIDER;
                    serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
                }
            }
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, "No Network connection available");
        }
    }

    private boolean validateFields() {
        if (!SkilExValidator.checkNullString(this.edtRegisterName.getText().toString().trim())) {
            edtRegisterName.setError(getString(R.string.empty_entry));
            requestFocus(edtRegisterName);
            return false;
        } else if (!SkilExValidator.checkNullString(this.edtRegisterPhoneNumber.getText().toString().trim())) {
            edtRegisterPhoneNumber.setError(getString(R.string.empty_entry));
            requestFocus(edtRegisterPhoneNumber);
            return false;
        } else if (!SkilExValidator.checkMobileNumLength(this.edtRegisterPhoneNumber.getText().toString().trim())) {
            edtRegisterPhoneNumber.setError(getString(R.string.error_number));
            requestFocus(edtRegisterPhoneNumber);
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

                String saveUserMasterId = response.getString("user_master_id");
                PreferenceStorage.saveUserMasterId(this, saveUserMasterId);
                PreferenceStorage.saveMobileNo(this, edtRegisterPhoneNumber.getText().toString());
                PreferenceStorage.saveLoginType(getApplicationContext(), "Register");

                Intent i = new Intent(RegisterActivity.this, OTPVerificationActivity.class);
                startActivity(i);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void onError(String error) {
        progressDialogHelper.hideProgressDialog();
        AlertDialogHelper.showSimpleAlertDialog(this, error);
    }
}
