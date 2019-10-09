package com.skilex.serviceprovider.activity.loginmodule;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.skilex.serviceprovider.R;
import com.skilex.serviceprovider.helper.AlertDialogHelper;
import com.skilex.serviceprovider.helper.ProgressDialogHelper;
import com.skilex.serviceprovider.interfaces.DialogClickListener;
import com.skilex.serviceprovider.languagesupport.BaseActivity;
import com.skilex.serviceprovider.languagesupport.LocaleManager;
import com.skilex.serviceprovider.servicehelpers.ServiceHelper;
import com.skilex.serviceprovider.serviceinterfaces.IServiceListener;
import com.skilex.serviceprovider.utils.CommonUtils;
import com.skilex.serviceprovider.utils.FirstTimePreference;
import com.skilex.serviceprovider.utils.PermissionUtil;
import com.skilex.serviceprovider.utils.PreferenceStorage;
import com.skilex.serviceprovider.utils.SkilExConstants;
import com.skilex.serviceprovider.utils.SkilExValidator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.util.Log.d;

public class LoginActivity extends BaseActivity implements DialogClickListener, IServiceListener, View.OnClickListener {

    private static final String TAG = LoginActivity.class.getName();
    private static final int PERMISSION_REQUEST_CODE = 1;
    Context context;
    private EditText edtMobileNo;
    private TextView txtRegister;
    private Button btnLogin;
    String res = "";
    ImageView lang;

    private static String[] PERMISSIONS_ALL = {Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    private static final int REQUEST_PERMISSION_All = 111;

    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        edtMobileNo = findViewById(R.id.edtMobileNumber);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
        txtRegister = findViewById(R.id.btnRegister);
        txtRegister.setOnClickListener(this);
        lang = findViewById(R.id.langues);
        lang.setOnClickListener(this);

        FirstTimePreference prefFirstTime = new FirstTimePreference(getApplicationContext());

        if (prefFirstTime.runTheFirstTime("FirstTimePermit")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                requestAllPermissions();
            }
        }

//        if (LocaleManager.getLanguagePref(this).isEmpty()) {
//            showLangAlert();
//        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.local_english:
                LocaleManager.setNewLocale(this, LocaleManager.LANGUAGE_KEY_ENGLISH);
                recreate();
                return true;
            case R.id.local_tamil:
                LocaleManager.setNewLocale(this, LocaleManager.LANGUAGE_KEY_TAMIL);
                recreate();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void requestAllPermissions() {

        boolean requestPermission = PermissionUtil.requestAllPermissions(this);

        if (requestPermission) {

            Log.i(TAG,
                    "Displaying contacts permission rationale to provide additional context.");

            // Display a SnackBar with an explanation and a button to trigger the request.

            ActivityCompat
                    .requestPermissions(this, PERMISSIONS_ALL,
                            REQUEST_PERMISSION_All);
        } else {

            ActivityCompat.requestPermissions(this, PERMISSIONS_ALL, REQUEST_PERMISSION_All);
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
    public void onClick(View v) {
        if (CommonUtils.haveNetworkConnection(getApplicationContext())) {
            if (v == btnLogin) {
                if (validateFields()) {
                    res = "mob_verify";
                    String number = edtMobileNo.getText().toString();
                    PreferenceStorage.saveMobileNo(this, number);
                    String GCMKey = PreferenceStorage.getGCM(getApplicationContext());

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put(SkilExConstants.PHONE_NUMBER, number);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
                    String url = SkilExConstants.BUILD_URL + SkilExConstants.MOBILE_VERIFICATION;
                    serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
                }
            }
            if (v == txtRegister) {
//                callGetSubCategoryService();
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
//                finish();
            } if(v==lang){
                showLangAlert();
            }
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, "No Network connection available");
        }
    }

    private boolean validateFields() {
        if (!SkilExValidator.checkMobileNumLength(this.edtMobileNo.getText().toString().trim())) {
            edtMobileNo.setError(getString(R.string.error_number));
            requestFocus(edtMobileNo);
            return false;
        }
        if (!SkilExValidator.checkNullString(this.edtMobileNo.getText().toString().trim())) {
            edtMobileNo.setError(getString(R.string.empty_entry));
            requestFocus(edtMobileNo);
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
                PreferenceStorage.saveMobileNo(this, edtMobileNo.getText().toString());
                PreferenceStorage.saveLoginType(getApplicationContext(), "Login");

                Intent i = new Intent(getApplicationContext(), OTPVerificationActivity.class);
                startActivity(i);
                finish();

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

    private void showLangAlert() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Language");
        alertDialogBuilder.setMessage("Choose your prefered language");
        alertDialogBuilder.setPositiveButton("English", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                LocaleManager.setNewLocale(getApplicationContext(), LocaleManager.LANGUAGE_KEY_ENGLISH);
                recreate();
            }
        });
        alertDialogBuilder.setNegativeButton("தமிழ்", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LocaleManager.setNewLocale(getApplicationContext(), LocaleManager.LANGUAGE_KEY_TAMIL);
                recreate();
            }
        });
        alertDialogBuilder.show();
    }
}
