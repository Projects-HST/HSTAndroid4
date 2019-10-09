package com.skilex.serviceprovider.activity.fragmentactivity.ongoing;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;
import com.skilex.serviceprovider.R;
import com.skilex.serviceprovider.bean.support.OngoingService;
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

public class InitiatedServiceActivity extends BaseActivity implements IServiceListener, DialogClickListener, View.OnClickListener {

    private static final String TAG = InitiatedServiceActivity.class.getName();
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    OngoingService ongoingService;
    private TextView cusName, cusPhone, cusAddress;
    private ImageView imgCall;
    private Button btnTrack;
    String res = "";
    private MapView mapView;
    private GoogleMap mMap;
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

    LatLng livLoc;
    Marker currentLocationMarker;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initiated_service_detail);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
        ongoingService = (OngoingService) getIntent().getSerializableExtra("serviceObj");

        cusName = findViewById(R.id.txt_customer_name);
        cusPhone = findViewById(R.id.txt_customer_number);
        cusAddress = findViewById(R.id.txt_address);

        imgCall = findViewById(R.id.img_call_button);
        imgCall.setOnClickListener(this);

        btnTrack = findViewById(R.id.btn_track);
        btnTrack.setOnClickListener(this);

        loadServiceDetails();
    }

    void loadServiceDetails() {
        res = "serviceDetail";
        JSONObject jsonObject = new JSONObject();
        String id = "";
        id = PreferenceStorage.getUserMasterId(this);
        try {
            jsonObject.put(SkilExConstants.USER_MASTER_ID, id);
            jsonObject.put(SkilExConstants.SERVICE_ORDER_ID, ongoingService.getServiceOrderId());

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.API_INITIATED_SERVICE_DETAIL;
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

                    cusName.setText(getServiceData.getString("contact_person_name"));
                    cusPhone.setText(getServiceData.getString("contact_person_number"));
                    cusAddress.setText(getServiceData.getString("service_address"));
                } else if (res.equalsIgnoreCase("initiateService")) {
                    finish();

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
