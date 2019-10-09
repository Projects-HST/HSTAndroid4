package com.skilex.serviceprovider.activity.loginmodule;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.google.firebase.iid.FirebaseInstanceId;
import com.skilex.serviceprovider.R;
import com.skilex.serviceprovider.activity.LandingPageActivity;
import com.skilex.serviceprovider.activity.providerregistration.InitialDepositActivity;
import com.skilex.serviceprovider.activity.providerregistration.UnRegOrgDocumentUploadActivity;
import com.skilex.serviceprovider.utils.PreferenceStorage;
import com.skilex.serviceprovider.utils.SkilExValidator;


public class SplashScreenActivity extends Activity {

    private static int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_display);

        String GCMKey = PreferenceStorage.getGCM(getApplicationContext());
        if (GCMKey.equalsIgnoreCase("")) {
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            PreferenceStorage.saveGCM(getApplicationContext(), refreshedToken);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (PreferenceStorage.getUserMasterId(getApplicationContext()) != null && SkilExValidator.checkNullString(PreferenceStorage.getUserMasterId(getApplicationContext()))) {
                    if (PreferenceStorage.getActiveStatus(getApplicationContext()).equalsIgnoreCase("Live")) {
                        Intent i = new Intent(SplashScreenActivity.this, LandingPageActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        finish();
                    } else {
                        Intent i = new Intent(SplashScreenActivity.this, LoginActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        finish();
                    }

                } else {

                    Intent i = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);
    }
}

