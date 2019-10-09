package com.skilex.serviceprovider.activity.fragmentactivity.requested;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.skilex.serviceprovider.R;
import com.skilex.serviceprovider.activity.LandingPageActivity;
import com.skilex.serviceprovider.activity.fragmentactivity.cancelled.CancelRequestedServiceActivity;
import com.skilex.serviceprovider.bean.support.RequestedServiceArray;
import com.skilex.serviceprovider.bean.support.StoreServicePerson;
import com.skilex.serviceprovider.helper.AlertDialogHelper;
import com.skilex.serviceprovider.helper.ProgressDialogHelper;
import com.skilex.serviceprovider.interfaces.DialogClickListener;
import com.skilex.serviceprovider.languagesupport.BaseActivity;
import com.skilex.serviceprovider.servicehelpers.ServiceHelper;
import com.skilex.serviceprovider.serviceinterfaces.IServiceListener;
import com.skilex.serviceprovider.utils.CommonUtils;
import com.skilex.serviceprovider.utils.PreferenceStorage;
import com.skilex.serviceprovider.utils.SkilExConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.util.Log.d;

public class RequestedServiceDetailActivity extends BaseActivity implements IServiceListener, DialogClickListener,
        View.OnClickListener {

    private static final String TAG = RequestedServiceDetailActivity.class.getName();
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    RequestedServiceArray requestedServiceArray;
    private TextView catName, subCatName, custName, serviceDate, serviceTimeSlot, orderID, custNumber, custAddress,
            estimatedCost, serviceLocation, serviceNumber;
    private Spinner spnExpertDropDown;
    Button btnCancel, btnAccept, btnAssign;
    String res = "";
    String expertId = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requested_service_detail);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        requestedServiceArray = (RequestedServiceArray) getIntent().getSerializableExtra("serviceObj");

        setUpUI();

    }

    void setUpUI() {

        catName = findViewById(R.id.category_name);
        catName.setText(requestedServiceArray.getServiceCategoryMainName());
        subCatName = findViewById(R.id.sub_category_name);
        subCatName.setText(requestedServiceArray.getServiceSubCategoryName());
        serviceDate = findViewById(R.id.service_date);
        serviceDate.setText(requestedServiceArray.getServiceOrderDate());
        serviceTimeSlot = findViewById(R.id.service_time_slot);
        serviceTimeSlot.setText(requestedServiceArray.getServiceOrderFromTime());
        serviceLocation = findViewById(R.id.service_location);
        serviceLocation.setText(requestedServiceArray.getServiceLocation());
        btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(this);
        btnAccept = findViewById(R.id.btnAccept);
        btnAccept.setOnClickListener(this);
        serviceNumber = findViewById(R.id.txtServiceNumber);
        serviceNumber.setText("Service Number: " + requestedServiceArray.getServiceOrderId());
        spnExpertDropDown = findViewById(R.id.spnExpertList);
        spnExpertDropDown.setEnabled(false);
        btnAssign = findViewById(R.id.btnAssign);
        btnAssign.setOnClickListener(this);
        btnAssign.setEnabled(false);

        spnExpertDropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                StoreServicePerson expert = (StoreServicePerson) parent.getSelectedItem();
                expertId = expert.getServicePersonId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
        try {
            if (validateSignInResponse(response)) {
                if (res.equalsIgnoreCase("accept")) {

                    spnExpertDropDown.setEnabled(true);
                    btnAssign.setEnabled(true);
                    loadServicePerson();

                } else if (res.equalsIgnoreCase("assign")) {
                    Intent intent = new Intent(this, LandingPageActivity.class);
                    startActivity(intent);
                    finish();

                } else if (res.equalsIgnoreCase("loadExpert")) {
                    JSONArray getData = response.getJSONArray("list_service_persons");
                    JSONObject userData = getData.getJSONObject(0);
                    int getLength = getData.length();
                    String subjectName = null;
                    Log.d(TAG, "userData dictionary" + userData.toString());

                    String expertID = "";
                    String expertName = "";
                    ArrayList<StoreServicePerson> expertList = new ArrayList<>();

                    for (int i = 0; i < getLength; i++) {

                        expertID = getData.getJSONObject(i).getString("user_master_id");
                        expertName = getData.getJSONObject(i).getString("full_name");

                        expertList.add(new StoreServicePerson(expertID, expertName));
                    }

                    //fill data in spinner
                    ArrayAdapter<StoreServicePerson> adapter = new ArrayAdapter<StoreServicePerson>(getApplicationContext(), R.layout.spinner_item_ns, expertList);
                    spnExpertDropDown.setAdapter(adapter);
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onError(String error) {
        progressDialogHelper.hideProgressDialog();
        AlertDialogHelper.showSimpleAlertDialog(this, error);
    }

    @Override
    public void onClick(View v) {
        if (CommonUtils.haveNetworkConnection(getApplicationContext())) {
            if (v == btnCancel) {
                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(RequestedServiceDetailActivity.this);
                alertDialogBuilder.setTitle(R.string.cancel);
                alertDialogBuilder.setMessage(R.string.cancel_service_noadvance_alert1);
                alertDialogBuilder.setPositiveButton(R.string.alert_button_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        cancelOrder();
                    }
                });
                alertDialogBuilder.setNegativeButton(R.string.alert_button_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialogBuilder.show();
            } else if (v == btnAccept) {
                acceptOrder();
            } else if (v == btnAssign) {
                assignOrder();
            }
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, "No Network connection available");
        }
    }

    private void cancelOrder() {
        Intent intent = new Intent(this, CancelRequestedServiceActivity.class);
        intent.putExtra("serviceOrderId", requestedServiceArray.getServiceOrderId());
        startActivity(intent);
        finish();
    }

    private void acceptOrder() {

        res = "accept";
        String userMasterId = PreferenceStorage.getUserMasterId(getApplicationContext());
        String serviceId = requestedServiceArray.getServiceOrderId();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(SkilExConstants.USER_MASTER_ID, userMasterId);
            jsonObject.put(SkilExConstants.SERVICE_ORDER_ID, serviceId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.API_ACCEPT_SERVICE_ORDER;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void assignOrder() {
        res = "assign";
        String userMasterId = PreferenceStorage.getUserMasterId(getApplicationContext());
        String serviceId = requestedServiceArray.getServiceOrderId();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(SkilExConstants.USER_MASTER_ID, userMasterId);
            jsonObject.put(SkilExConstants.SERVICE_ORDER_ID, serviceId);
            jsonObject.put(SkilExConstants.KEY_SERVICE_PERSON_ID, expertId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.API_ASSIGN_SERVICE_ORDER;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void loadServicePerson() {
        res = "loadExpert";
        String userMasterId = PreferenceStorage.getUserMasterId(getApplicationContext());

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(SkilExConstants.USER_MASTER_ID, userMasterId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.API_SERVICE_PERSON_LIST;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }
}
