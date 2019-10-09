package com.skilex.serviceprovider.activity.providerregistration;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;

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

import org.json.JSONException;
import org.json.JSONObject;

import static android.util.Log.d;

public class OrganizationTypeSelectionActivity extends BaseActivity implements View.OnClickListener, IServiceListener, DialogClickListener {

    private static final String TAG = OrganizationTypeSelectionActivity.class.getName();
    private LinearLayout llRegOrg, llUnRegOrg;

    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;

    private String organizationType = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_organization_type);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        llRegOrg = findViewById(R.id.ll_reg_org);
        llRegOrg.setOnClickListener(this);

        llUnRegOrg = findViewById(R.id.ll_un_reg_org);
        llUnRegOrg.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (CommonUtils.haveNetworkConnection(getApplicationContext())) {
            if (v == llRegOrg) {
                organizationType = "Company";
                setOrganizationType();
            } else if (v == llUnRegOrg) {
                organizationType = "Individual";
                setOrganizationType();
            }
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, "No Network connection available");
        }
    }

    private void setOrganizationType() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(SkilExConstants.USER_MASTER_ID, PreferenceStorage.getUserMasterId(getApplicationContext()));
            jsonObject.put(SkilExConstants.KEY_TYPE_SELECTION, organizationType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.ORGANIZATION_TYPE_SELECTION;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
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
                if (organizationType.equalsIgnoreCase("Company")) {
                    Intent intent = new Intent(this, RegisteredOrganizationInfoActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else if (organizationType.equalsIgnoreCase("Individual")) {
                    Intent intent = new Intent(this, UnRegisteredOrganizationInfoActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
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
