package com.skilex.serviceprovider.activity.loginmodule;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.skilex.serviceprovider.R;
import com.skilex.serviceprovider.activity.LandingPageActivity;
import com.skilex.serviceprovider.activity.providerregistration.CategorySelectionActivity;
import com.skilex.serviceprovider.activity.providerregistration.DocumentVerificationStatusActivity;
import com.skilex.serviceprovider.activity.providerregistration.DocumentVerifySuccessActivity;
import com.skilex.serviceprovider.activity.providerregistration.WelcomeActivity;
import com.skilex.serviceprovider.customview.CustomOtpEditText;
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

public class OTPVerificationActivity extends BaseActivity implements View.OnClickListener, IServiceListener, DialogClickListener {

    private static final String TAG = OTPVerificationActivity.class.getName();

    private CustomOtpEditText otpEditText;
    private TextView tvResendOTP;
    private ImageView btnConfirm;
    private String mobileNo;
    private String checkVerify;
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    String getUserMasterId, getMobileNumber;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        otpEditText = findViewById(R.id.otp_view);
        tvResendOTP = findViewById(R.id.resend);
        tvResendOTP.setOnClickListener(this);
        btnConfirm = findViewById(R.id.sendcode);
        btnConfirm.setOnClickListener(this);
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
        if (CommonUtils.isNetworkAvailable(getApplicationContext())) {
            if (v == tvResendOTP) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Do you want to resend OTP ?");
                alertDialogBuilder.setMessage("Confirm your mobile number : " + PreferenceStorage.getMobileNo(getApplicationContext()));
                alertDialogBuilder.setPositiveButton("Proceed",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                checkVerify = "Resend";
                                JSONObject jsonObject = new JSONObject();
                                try {

                                    jsonObject.put(SkilExConstants.PHONE_NUMBER, PreferenceStorage.getMobileNo(getApplicationContext()));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
                                String url = SkilExConstants.BUILD_URL + SkilExConstants.MOBILE_VERIFICATION;
                                serviceHelper.makeGetServiceCall(jsonObject.toString(), url);

                            }
                        });
                alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

//                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialogBuilder.show();

            } else if (v == btnConfirm) {
                if (otpEditText.hasValidOTP()) {
                    checkVerify = "Confirm";
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put(SkilExConstants.USER_MASTER_ID, PreferenceStorage.getUserMasterId(getApplicationContext()));
                        jsonObject.put(SkilExConstants.PHONE_NUMBER, PreferenceStorage.getMobileNo(getApplicationContext()));
                        jsonObject.put(SkilExConstants.OTP, otpEditText.getOTP());
                        jsonObject.put(SkilExConstants.DEVICE_TOKEN, PreferenceStorage.getGCM(getApplicationContext()));
                        jsonObject.put(SkilExConstants.MOBILE_TYPE, "1");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
                    String url = SkilExConstants.BUILD_URL + SkilExConstants.LOGIN;
                    serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
                } else {
                    AlertDialogHelper.showSimpleAlertDialog(this, "Invalid OTP");
                }
            }
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, "No Network connection available");
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
                JSONObject userData = response.getJSONObject("userData");
                String loginType = PreferenceStorage.getLoginType(getApplicationContext());
                if (loginType.equalsIgnoreCase("Register")) {
                    Intent i = new Intent(OTPVerificationActivity.this, CategorySelectionActivity.class);
                    i.putExtra("ProviderPersonCheck", "Provider");
                    startActivity(i);
                } else if (loginType.equalsIgnoreCase("Login")) {
                    if (userData.getString("serv_prov_display_status").equalsIgnoreCase("Inactive")) {

                        if (userData.getString("serv_prov_verify_status").equalsIgnoreCase("Pending")) {

                            Intent i = new Intent(OTPVerificationActivity.this, DocumentVerificationStatusActivity.class);
                            startActivity(i);
                            finish();
                        } else if (userData.getString("serv_prov_verify_status").equalsIgnoreCase("Approved")) {

                            Intent i = new Intent(OTPVerificationActivity.this, DocumentVerifySuccessActivity.class);
                            startActivity(i);
                            finish();
                        }
                    } else {

                        String userMasterId = userData.getString("user_master_id");
                        String fullName = userData.getString("full_name");
                        String phoneNo = userData.getString("phone_no");
                        String email = userData.getString("email");
                        String userType = userData.getString("user_type");
                        String profilePic = userData.getString("profile_pic");

                        PreferenceStorage.saveUserMasterId(this, userMasterId);
                        PreferenceStorage.saveFullName(this, fullName);
                        PreferenceStorage.saveMobileNo(this, phoneNo);
                        PreferenceStorage.saveEmail(this, email);
                        PreferenceStorage.saveLoginType(this, userType);
                        PreferenceStorage.saveProfilePicture(this, profilePic);


                        Intent i = new Intent(OTPVerificationActivity.this, LandingPageActivity.class);
                        startActivity(i);
                        finish();
                    }

                }
//                finish();
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
