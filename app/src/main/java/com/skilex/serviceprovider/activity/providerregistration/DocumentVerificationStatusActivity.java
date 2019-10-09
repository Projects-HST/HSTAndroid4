package com.skilex.serviceprovider.activity.providerregistration;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.skilex.serviceprovider.R;
import com.skilex.serviceprovider.activity.loginmodule.LoginActivity;
import com.skilex.serviceprovider.activity.loginmodule.OTPVerificationActivity;
import com.skilex.serviceprovider.activity.loginmodule.RegisterActivity;
import com.skilex.serviceprovider.helper.AlertDialogHelper;
import com.skilex.serviceprovider.helper.ProgressDialogHelper;
import com.skilex.serviceprovider.interfaces.DialogClickListener;
import com.skilex.serviceprovider.languagesupport.BaseActivity;
import com.skilex.serviceprovider.servicehelpers.ServiceHelper;
import com.skilex.serviceprovider.serviceinterfaces.IServiceListener;
import com.skilex.serviceprovider.utils.CommonUtils;
import com.skilex.serviceprovider.utils.PreferenceStorage;
import com.skilex.serviceprovider.utils.SkilExConstants;

import org.json.JSONException;
import org.json.JSONObject;

public class DocumentVerificationStatusActivity extends BaseActivity implements IServiceListener, DialogClickListener, View.OnClickListener {

    private static final String TAG = DocumentVerificationStatusActivity.class.getName();
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private Button btnClose;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_verify_display_status);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        btnClose = findViewById(R.id.btnClose);
        btnClose.setOnClickListener(this);

        checkDocumentStatus();

    }

    void checkDocumentStatus() {

        if (CommonUtils.isNetworkAvailable(getApplicationContext())) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(SkilExConstants.USER_MASTER_ID, PreferenceStorage.getUserMasterId(getApplicationContext()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            String url = SkilExConstants.BUILD_URL + SkilExConstants.API_PROVIDER_DOCUMENT_STATUS;
            serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
        }

    }

    @Override
    public void onClick(View v) {

        if (CommonUtils.isNetworkAvailable(getApplicationContext())) {
            if (v == btnClose) {
                Intent i = new Intent(DocumentVerificationStatusActivity.this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        }
    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }

    private boolean validateResponse(JSONObject response) {
        boolean signInSuccess = false;
        if ((response != null)) {
            try {
                String status = response.getString("status");
                String msg = response.getString(SkilExConstants.PARAM_MESSAGE);
                Log.d(TAG, "status val" + status + "msg" + msg);

                if ((status != null)) {
                    if (((status.equalsIgnoreCase("activationError")) || (status.equalsIgnoreCase("alreadyRegistered")) ||
                            (status.equalsIgnoreCase("notRegistered")) || (status.equalsIgnoreCase("error")))) {
                        signInSuccess = false;
                        Log.d(TAG, "Show error dialog");
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
        if (validateResponse(response)) {
            try {
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
