package com.skilex.serviceprovider.activity.providerregistration;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.skilex.serviceprovider.R;
import com.skilex.serviceprovider.activity.LandingPageActivity;
import com.skilex.serviceprovider.activity.loginmodule.OTPVerificationActivity;
import com.skilex.serviceprovider.helper.AlertDialogHelper;
import com.skilex.serviceprovider.languagesupport.BaseActivity;
import com.skilex.serviceprovider.utils.CommonUtils;
import com.skilex.serviceprovider.utils.PreferenceStorage;

public class WelcomeActivity extends BaseActivity implements View.OnClickListener {

    private Button btnStart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);

        btnStart = findViewById(R.id.btnStart);
        btnStart.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (CommonUtils.haveNetworkConnection(getApplicationContext())) {
            if (v == btnStart) {
                Intent i = new Intent(WelcomeActivity.this, LandingPageActivity.class);
                startActivity(i);
                finish();
            }
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, "No Network connection available");
        }
    }
}
