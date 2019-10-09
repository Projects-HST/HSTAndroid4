package com.skilex.serviceprovider.ccavenue.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skilex.serviceprovider.R;
import com.skilex.serviceprovider.activity.loginmodule.LoginActivity;
import com.skilex.serviceprovider.activity.loginmodule.OTPVerificationActivity;
import com.skilex.serviceprovider.activity.loginmodule.RegisterActivity;
import com.skilex.serviceprovider.activity.providerregistration.InitialDepositActivity;
import com.skilex.serviceprovider.helper.ProgressDialogHelper;
import com.skilex.serviceprovider.interfaces.DialogClickListener;
import com.skilex.serviceprovider.servicehelpers.ServiceHelper;
import com.skilex.serviceprovider.serviceinterfaces.IServiceListener;
import com.skilex.serviceprovider.utils.PreferenceStorage;
import com.skilex.serviceprovider.utils.SkilExConstants;

import org.json.JSONException;
import org.json.JSONObject;


public class StatusActivity extends AppCompatActivity implements IServiceListener, DialogClickListener {

    LinearLayout advLayout, payLayout;
    ImageView paymentIcon, bookingIcon;
    TextView paymentStatus, paymentComment, bookingStatus, bookingComment;
    Button booking, rate;
    String page = "";
    String status = "";
    String paymentType = "";

    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_status);

        initVals();

        Intent mainIntent = getIntent();

        TextView tv4 = (TextView) findViewById(R.id.textView1);
        tv4.setText(mainIntent.getStringExtra("transStatus"));
        status = mainIntent.getStringExtra("transStatus");
        paymentType = PreferenceStorage.getPaymentType(getApplicationContext());

        if (paymentType.equalsIgnoreCase("advance")) {
            payLayout.setVisibility(View.VISIBLE);
            if (status.equalsIgnoreCase("Transaction Declined!") || status.equalsIgnoreCase("Transaction Cancelled!")) {
                payLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.payment_failed_bg));
                paymentIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_payment_failed));
                paymentStatus.setText(R.string.payment_failed);
                paymentStatus.setTextColor(ContextCompat.getColor(this, R.color.payment_failed_font));
                paymentComment.setText(R.string.payment_failed_comment);
                paymentComment.setTextColor(ContextCompat.getColor(this, R.color.payment_failed_font));
                rate.setText(R.string.try_again);
                rate.setBackground(ContextCompat.getDrawable(this, R.drawable.button_try_again));
                rate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), InitialDepositActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            } else {
                payLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.payment_success_bg));
                paymentIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_payment_success));
                paymentStatus.setText(R.string.payment_success);
                paymentComment.setText(R.string.payment_success_comment);
                rate.setText(R.string.alert_button_ok);
                rate.setBackground(ContextCompat.getDrawable(this, R.drawable.button_rate_service));
                rate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

            }
        }
    }

    private void initVals() {

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        payLayout = findViewById(R.id.final_payment_layout);
        paymentIcon = findViewById(R.id.payment_status_icon);
        paymentStatus = findViewById(R.id.payment_status_text);
        paymentComment = findViewById(R.id.payment_status_comment_text);
        rate = findViewById(R.id.rate_service);
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