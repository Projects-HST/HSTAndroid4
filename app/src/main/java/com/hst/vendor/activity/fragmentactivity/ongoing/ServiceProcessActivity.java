package com.hst.vendor.activity.fragmentactivity.ongoing;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hst.vendor.R;
import com.hst.vendor.activity.LandingPageActivity;
import com.hst.vendor.bean.support.OngoingService;
import com.hst.vendor.helper.AlertDialogHelper;
import com.hst.vendor.helper.ProgressDialogHelper;
import com.hst.vendor.interfaces.DialogClickListener;
import com.hst.vendor.languagesupport.BaseActivity;
import com.hst.vendor.servicehelpers.ServiceHelper;
import com.hst.vendor.serviceinterfaces.IServiceListener;
import com.hst.vendor.utils.CommonUtils;
import com.hst.vendor.utils.PreferenceStorage;
import com.hst.vendor.utils.SkilExConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.util.Log.d;

public class ServiceProcessActivity extends BaseActivity implements IServiceListener, DialogClickListener, View.OnClickListener {

    private static final String TAG = OngoingServiceDetailActivity.class.getName();
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    OngoingService ongoingService;

    private TextView txtServiceCategory, txtSubCategory, txtCustomerName, txtServiceDate, txtServiceTime, txtServiceProvider;
    private EditText edtOTP;
    private TextView txtRequestOTP;
    private Button btnStartService;
    String res = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_process);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
        ongoingService = (OngoingService) getIntent().getSerializableExtra("serviceObj");

        init();
        loadServiceDetail();
    }

    void init() {
        txtServiceCategory = findViewById(R.id.txt_service_cat);
        txtSubCategory = findViewById(R.id.txt_service_sub_cat);
        txtCustomerName = findViewById(R.id.txt_customer_name);
        txtServiceDate = findViewById(R.id.txt_service_date);
        txtServiceTime = findViewById(R.id.txt_service_time);
        txtServiceProvider = findViewById(R.id.txt_service_provider);
        edtOTP = findViewById(R.id.edt_otp);
        txtRequestOTP = findViewById(R.id.txt_request_otp);
        txtRequestOTP.setOnClickListener(this);
        btnStartService = findViewById(R.id.btn_start);
        btnStartService.setOnClickListener(this);
    }

    private void loadServiceDetail() {
        res = "serviceDetail";
        JSONObject jsonObject = new JSONObject();
        String id = "";
        id = PreferenceStorage.getUserMasterId(this);
        try {
            jsonObject.put(SkilExConstants.USER_MASTER_ID, id);
            jsonObject.put(SkilExConstants.SERVICE_ORDER_ID, ongoingService.getServiceOrderId());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = SkilExConstants.BUILD_URL + SkilExConstants.API_SERVICE_PROCESS;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void requestOTP() {
        edtOTP.setEnabled(true);
        res = "opt";
        JSONObject jsonObject = new JSONObject();
        String id = "";
        id = PreferenceStorage.getUserMasterId(this);
        try {
            jsonObject.put(SkilExConstants.USER_MASTER_ID, id);
            jsonObject.put(SkilExConstants.SERVICE_ORDER_ID, ongoingService.getServiceOrderId());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = SkilExConstants.BUILD_URL + SkilExConstants.API_REQUEST_OTP;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void serviceStart() {
        res = "start";
        JSONObject jsonObject = new JSONObject();
        String id = "", otp = "";
        id = PreferenceStorage.getUserMasterId(this);
        otp = edtOTP.getText().toString();
        try {
            jsonObject.put(SkilExConstants.USER_MASTER_ID, id);
            jsonObject.put(SkilExConstants.SERVICE_ORDER_ID, ongoingService.getServiceOrderId());
            jsonObject.put(SkilExConstants.SERVICE_OTP, otp);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = SkilExConstants.BUILD_URL + SkilExConstants.API_START_SERVICE;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    @Override
    public void onClick(View v) {

        if (CommonUtils.haveNetworkConnection(getApplicationContext())) {
            if (v == txtRequestOTP) {
                requestOTP();
            } else if (v == btnStartService) {
                serviceStart();
            }
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, "No Network connection available");
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
            try {
                if (res.equalsIgnoreCase("serviceDetail")) {
                    JSONArray getData = response.getJSONArray("detail_services_order");
                    Gson gson = new Gson();
                    JSONObject getServiceData = getData.getJSONObject(0);

                    txtServiceCategory.setText(getServiceData.getString("main_cat_name"));
                    txtSubCategory.setText(getServiceData.getString("sub_cat_name"));
                    txtCustomerName.setText(getServiceData.getString("contact_person_name"));
                    txtServiceDate.setText(getServiceData.getString("order_date"));
                    txtServiceTime.setText(getServiceData.getString("from_time"));
                    txtServiceProvider.setText(getServiceData.getString("service_provider"));
                } else if (res.equalsIgnoreCase("otp")) {

                    Toast.makeText(getApplicationContext(), "OTP has been sent to your customer number", Toast.LENGTH_LONG).show();

                } else if (res.equalsIgnoreCase("start")) {
                    Toast.makeText(getApplicationContext(), "Service has been started!", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(getApplicationContext(), LandingPageActivity.class);
                    startActivity(i);
                    finish();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onError(String error) {
        progressDialogHelper.hideProgressDialog();
        AlertDialogHelper.showSimpleAlertDialog(this, error);
    }
}
