package com.skilex.serviceprovider.activity.providerregistration;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.skilex.serviceprovider.R;
import com.skilex.serviceprovider.activity.loginmodule.OTPVerificationActivity;
import com.skilex.serviceprovider.activity.loginmodule.RegisterActivity;
import com.skilex.serviceprovider.ccavenue.activity.InitialScreenActivity;
import com.skilex.serviceprovider.ccavenue.utility.ServiceUtility;
import com.skilex.serviceprovider.helper.AlertDialogHelper;
import com.skilex.serviceprovider.helper.ProgressDialogHelper;
import com.skilex.serviceprovider.interfaces.DialogClickListener;
import com.skilex.serviceprovider.languagesupport.BaseActivity;
import com.skilex.serviceprovider.servicehelpers.ServiceHelper;
import com.skilex.serviceprovider.serviceinterfaces.IServiceListener;
import com.skilex.serviceprovider.utils.CommonUtils;
import com.skilex.serviceprovider.utils.PreferenceStorage;
import com.skilex.serviceprovider.utils.SkilExConstants;

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
