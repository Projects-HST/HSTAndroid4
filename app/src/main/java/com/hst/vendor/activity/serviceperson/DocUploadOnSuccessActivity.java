package com.hst.vendor.activity.serviceperson;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.hst.vendor.languagesupport.BaseActivity;
import com.hst.vendor.R;
import com.hst.vendor.activity.LandingPageActivity;

public class DocUploadOnSuccessActivity extends BaseActivity {

    private Button btnBackHome;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_upload_on_success);

        btnBackHome = findViewById(R.id.btnBack);
        btnBackHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), LandingPageActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}
