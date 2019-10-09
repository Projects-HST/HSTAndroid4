package com.hst.vendor.activity.providerregistration;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.hst.vendor.ccavenue.activity.InitialScreenActivity;
import com.hst.vendor.interfaces.DialogClickListener;
import com.hst.vendor.languagesupport.BaseActivity;
import com.hst.vendor.servicehelpers.ServiceHelper;
import com.hst.vendor.serviceinterfaces.IServiceListener;
import com.hst.vendor.R;
import com.hst.vendor.ccavenue.utility.ServiceUtility;
import com.hst.vendor.helper.AlertDialogHelper;
import com.hst.vendor.helper.ProgressDialogHelper;
import com.hst.vendor.utils.CommonUtils;
import com.hst.vendor.utils.PreferenceStorage;

import org.json.JSONObject;

public class InitialDepositActivity extends BaseActivity implements IServiceListener, DialogClickListener, View.OnClickListener {

    private static final String TAG = InitialDepositActivity.class.getName();

    private Button btnPay;
    private String payment = "1.00", orderId;

    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_deposit);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        btnPay = findViewById(R.id.btnPay);
        btnPay.setOnClickListener(this);

        Integer randomNum = ServiceUtility.randInt(0, 9999999);
        orderId = randomNum.toString() + "-" + PreferenceStorage.getUserMasterId(getApplicationContext());

    }

    @Override
    public void onClick(View v) {
        if (CommonUtils.haveNetworkConnection(getApplicationContext())) {
            if (v == btnPay) {

                PreferenceStorage.savePaymentType(getApplicationContext(), "advance");
                Intent i = new Intent(getApplicationContext(), InitialScreenActivity.class);
                i.putExtra("amount", payment);
                i.putExtra("orderid", orderId);
                startActivity(i);
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

    @Override
    public void onResponse(JSONObject response) {

    }

    @Override
    public void onError(String error) {

    }
}
