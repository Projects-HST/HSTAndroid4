package com.skilex.serviceprovider.activity.fragmentactivity.completed;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.skilex.serviceprovider.R;
import com.skilex.serviceprovider.activity.fragmentactivity.ongoing.OngoingServiceDetailActivity;
import com.skilex.serviceprovider.bean.support.CompletedService;
import com.skilex.serviceprovider.helper.AlertDialogHelper;
import com.skilex.serviceprovider.helper.ProgressDialogHelper;
import com.skilex.serviceprovider.interfaces.DialogClickListener;
import com.skilex.serviceprovider.languagesupport.BaseActivity;
import com.skilex.serviceprovider.servicehelpers.ServiceHelper;
import com.skilex.serviceprovider.serviceinterfaces.IServiceListener;
import com.skilex.serviceprovider.utils.PreferenceStorage;
import com.skilex.serviceprovider.utils.SkilExConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.util.Log.d;

public class CompletedServiceDetailActivity extends BaseActivity implements IServiceListener, DialogClickListener, View.OnClickListener {

    private static final String TAG = OngoingServiceDetailActivity.class.getName();
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    CompletedService completedService;

    private TextView txtCategory, txtServiceName, txtCustomerName, txtServiceDate, txtServiceTimeSlot, txtServiceProvider,
            txtServicePerson, txtServiceStartDate, txtServiceEndDate, txtMaterialUsed, txtServiceAmount, txtAdditionalServiceAmount,
            txtSubTotalAmount, txtCouponContent, txtCouponAmount, txtAdvanceAmount, txtGrandTotal;

    String res = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_service_details);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        completedService = (CompletedService) getIntent().getSerializableExtra("serviceObj");

        init();
        loadServiceDetail();
    }

    void init() {
        txtCategory = findViewById(R.id.service_category);
        txtServiceName = findViewById(R.id.service_name);
        txtCustomerName = findViewById(R.id.customer_name_txt);
        txtServiceDate = findViewById(R.id.service_date_txt);
        txtServiceTimeSlot = findViewById(R.id.service_time_slot_txt);
        txtServiceProvider = findViewById(R.id.service_provider_name_txt);
        txtServicePerson = findViewById(R.id.service_person_txt);
        txtServiceStartDate = findViewById(R.id.service_start_time);
        txtServiceEndDate = findViewById(R.id.service_end_time);
        txtMaterialUsed = findViewById(R.id.material_list);
        txtServiceAmount = findViewById(R.id.service_charge_amount);
        txtAdditionalServiceAmount = findViewById(R.id.additional_service_charge_amount);
        txtSubTotalAmount = findViewById(R.id.sub_total_amount);
        txtCouponContent = findViewById(R.id.coupon_content);
        txtCouponAmount = findViewById(R.id.coupon_applied_amount);
        txtAdvanceAmount = findViewById(R.id.advance_charge_amount);
        txtGrandTotal = findViewById(R.id.grand_total_amount);
    }

    private void loadServiceDetail() {
        res = "serviceDetail";
        JSONObject jsonObject = new JSONObject();
        String id = "";
        id = PreferenceStorage.getUserMasterId(this);
        try {
            jsonObject.put(SkilExConstants.USER_MASTER_ID, id);
            jsonObject.put(SkilExConstants.SERVICE_ORDER_ID, completedService.getServiceOrderId());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = SkilExConstants.BUILD_URL + SkilExConstants.API_COMPLETED_SERVICE_DETAIL;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    @Override
    public void onClick(View v) {

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

                    txtCategory.setText(getServiceData.getString("main_cat_name"));
                    txtServiceName.setText(getServiceData.getString("service_name"));
                    txtCustomerName.setText(getServiceData.getString("contact_person_name"));
                    txtServiceDate.setText(getServiceData.getString("order_date"));
                    txtServiceTimeSlot.setText(getServiceData.getString("from_time"));
                    txtServiceProvider.setText(getServiceData.getString("service_provider"));
                    txtServicePerson.setText(getServiceData.getString("service_person"));
                    txtServiceStartDate.setText(getServiceData.getString("start_datetime"));
                    txtServiceEndDate.setText(getServiceData.getString("finish_datetime"));
                    txtMaterialUsed.setText(getServiceData.getString("material_notes"));

                    JSONArray getData1 = response.getJSONArray("transaction_details");
                    Gson gson1 = new Gson();
                    JSONObject getServiceData1 = getData1.getJSONObject(0);

                    txtServiceAmount.setText(getServiceData1.getString("service_amount"));
                    txtAdditionalServiceAmount.setText(getServiceData1.getString("discount_amt"));
                    txtSubTotalAmount.setText(getServiceData1.getString("total_service_amount"));
                    txtCouponContent.setText(getServiceData1.getString("coupon_id"));
                    txtCouponAmount.setText(getServiceData1.getString("discount_amt"));
                    txtAdvanceAmount.setText(getServiceData1.getString("paid_advance_amount"));
                    txtGrandTotal.setText(getServiceData1.getString("payable_amount"));

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
