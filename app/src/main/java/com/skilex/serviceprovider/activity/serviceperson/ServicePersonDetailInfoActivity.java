package com.skilex.serviceprovider.activity.serviceperson;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.skilex.serviceprovider.R;
import com.skilex.serviceprovider.activity.loginmodule.LoginActivity;
import com.skilex.serviceprovider.activity.loginmodule.OTPVerificationActivity;
import com.skilex.serviceprovider.activity.providerregistration.CategorySelectionActivity;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import static android.util.Log.d;

public class ServicePersonDetailInfoActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener,
        DialogClickListener, IServiceListener, View.OnClickListener {

    private static final String TAG = ServicePersonDetailInfoActivity.class.getName();

    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;

    private EditText edtFullName, edtDob, edtAddress, edtCity, edtPinCode, edtState, edtLangKnown, edtEduQualification;
    private Spinner spnGender;
    private String strGender;
    private Button btnUpdate;

    private DatePickerDialog mDatePicker;
    private SimpleDateFormat mDateFormatter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_person_detail_info);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        edtFullName = findViewById(R.id.edtFullName);
        spnGender = findViewById(R.id.spnGender);
        edtDob = findViewById(R.id.edtDOB);
        edtDob.setFocusable(false);
        edtDob.setOnClickListener(this);
        edtAddress = findViewById(R.id.edtAddress);
        edtCity = findViewById(R.id.edtCity);
        edtPinCode = findViewById(R.id.edtZipCode);
        edtState = findViewById(R.id.edtState);
        edtLangKnown = findViewById(R.id.edtLangKnown);
        edtEduQualification = findViewById(R.id.edtEduQualifi);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(this);

        spnGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strGender = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        mDateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

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
            if (v == edtDob) {
                Log.d(TAG, "birthday widget selected");
                showBirthdayDate();
            } else if (v == btnUpdate) {

                if (validateFields()) {

                    String spId = PreferenceStorage.getServicePersonId(getApplicationContext());
                    String providerId = PreferenceStorage.getUserMasterId(getApplicationContext());
                    String spName = edtFullName.getText().toString();
                    String spGender = spnGender.getSelectedItem().toString();
                    String spDOB = edtDob.getText().toString();
                    String spAddress = edtAddress.getText().toString();
                    String spCity = edtCity.getText().toString();
                    String spPinCode = edtPinCode.getText().toString();
                    String spState = edtState.getText().toString();
                    String spLang = edtLangKnown.getText().toString();
                    String spEdu = edtEduQualification.getText().toString();

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put(SkilExConstants.USER_MASTER_ID, providerId);
                        jsonObject.put(SkilExConstants.KEY_SERVICE_PERSON_ID, spId);
                        jsonObject.put(SkilExConstants.KEY_FULL_NAME, spName);
                        jsonObject.put(SkilExConstants.KEY_GENDER, spGender);
                        jsonObject.put(SkilExConstants.KEY_ADDRESS, spAddress);
                        jsonObject.put(SkilExConstants.KEY_CITY, spCity);
                        jsonObject.put(SkilExConstants.KEY_PIN_CODE, spPinCode);
                        jsonObject.put(SkilExConstants.KEY_STATE, spState);
                        jsonObject.put(SkilExConstants.KEY_LANGUAGE_KNOWN, spLang);
                        jsonObject.put(SkilExConstants.KEY_EDUCATION_QUALIFICATION, spEdu);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
                    String url = SkilExConstants.BUILD_URL + SkilExConstants.API_SERVICE_PERSON_UPDATE;
                    serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
                }
            }

        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, "No Network connection available");
        }
    }

    private boolean validateFields() {
        String gender = spnGender.getSelectedItem().toString();
        if (!SkilExValidator.checkNullString(this.edtFullName.getText().toString().trim())) {
            edtFullName.setError(getString(R.string.error_entry));
            requestFocus(edtFullName);
            return false;
        } else if (gender.equalsIgnoreCase("Select gender")) {
            Toast.makeText(getApplicationContext(), "Select gender", Toast.LENGTH_LONG).show();
            return false;
        } else if (!SkilExValidator.checkNullString(this.edtDob.getText().toString().trim())) {
            edtDob.setError(getString(R.string.empty_entry));
            requestFocus(edtDob);
            return false;
        } else if (!SkilExValidator.checkNullString(this.edtAddress.getText().toString().trim())) {
            edtAddress.setError(getString(R.string.empty_entry));
            requestFocus(edtAddress);
            return false;
        } else if (!SkilExValidator.checkNullString(this.edtCity.getText().toString().trim())) {
            edtCity.setError(getString(R.string.empty_entry));
            requestFocus(edtCity);
            return false;
        } else if (!SkilExValidator.checkNullString(this.edtPinCode.getText().toString().trim())) {
            edtPinCode.setError(getString(R.string.empty_entry));
            requestFocus(edtPinCode);
            return false;
        } else if (!SkilExValidator.checkNullString(this.edtState.getText().toString().trim())) {
            edtState.setError(getString(R.string.empty_entry));
            requestFocus(edtState);
            return false;
        } else if (!SkilExValidator.checkNullString(this.edtLangKnown.getText().toString().trim())) {
            edtLangKnown.setError(getString(R.string.empty_entry));
            requestFocus(edtLangKnown);
            return false;
        } else if (!SkilExValidator.checkNullString(this.edtEduQualification.getText().toString().trim())) {
            edtEduQualification.setError(getString(R.string.empty_entry));
            requestFocus(edtEduQualification);
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

    private void showBirthdayDate() {
        Log.d(TAG, "Show the birthday date");
        Calendar newCalendar = Calendar.getInstance();
        String currentdate = edtDob.getText().toString();
        Log.d(TAG, "current date is" + currentdate);
        int month = newCalendar.get(Calendar.MONTH);
        int day = newCalendar.get(Calendar.DAY_OF_MONTH);
        int year = newCalendar.get(Calendar.YEAR);
        if ((currentdate != null) && !(currentdate.isEmpty())) {
            //extract the date/month and year
            try {
                Date startDate = mDateFormatter.parse(currentdate);
                Calendar newDate = Calendar.getInstance();

                newDate.setTime(startDate);
                month = newDate.get(Calendar.MONTH);
                day = newDate.get(Calendar.DAY_OF_MONTH);
                year = newDate.get(Calendar.YEAR);
                Log.d(TAG, "month" + month + "day" + day + "year" + year);

            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                mDatePicker = new DatePickerDialog(this, R.style.datePickerTheme, this, year, month, day);
                mDatePicker.show();
            }
        } else {
            Log.d(TAG, "show default date");

            mDatePicker = new DatePickerDialog(this, R.style.datePickerTheme, this, year, month, day);
            mDatePicker.show();
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
                Intent i = new Intent(getApplicationContext(), CategorySelectionActivity.class);
                i.putExtra("ProviderPersonCheck", "Person");
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

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar userAge = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        Calendar minAdultAge = new GregorianCalendar();
        minAdultAge.add(Calendar.YEAR, -18);
        if (minAdultAge.before(userAge)) {
//            SHOW_ERROR_MESSAGE;
            Toast.makeText(getApplicationContext(), "Age must be 18+", Toast.LENGTH_LONG).show();
        } else {
            edtDob.setText(mDateFormatter.format(userAge.getTime()));
//            edtDob.setText(mDateFormatter.format(userAge.getTime()));
            getAge(year, monthOfYear, dayOfMonth);
        }

//        Calendar newDate = Calendar.getInstance();
//        newDate.set(year, monthOfYear, dayOfMonth);
//        etCandidateDOB.setText(mDateFormatter.format(userAge.getTime()));
//        getAge(year, monthOfYear, dayOfMonth);
    }

    private String getAge(int year, int month, int day) {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        if (ageInt >= 10) {
            edtDob.setText(ageS);
        } else {
            edtDob.setText(ageS);
        }

        return ageS;
    }
}
