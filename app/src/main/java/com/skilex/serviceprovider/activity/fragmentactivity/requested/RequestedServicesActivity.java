package com.skilex.serviceprovider.activity.fragmentactivity.requested;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.skilex.serviceprovider.R;
import com.skilex.serviceprovider.adapter.RequestedServiceListAdapter;
import com.skilex.serviceprovider.bean.support.RequestedServiceArray;
import com.skilex.serviceprovider.bean.support.RequestedServiceList;
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

public class RequestedServicesActivity extends BaseActivity implements IServiceListener, DialogClickListener,
        AdapterView.OnItemClickListener{

    private static final String TAG = RequestedServicesActivity.class.getName();

    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private ArrayList<RequestedServiceArray> requestedServiceArrayList = new ArrayList<>();
    private ListView loadMoreListView;
    int totalCount = 0, checkrun = 0;
    protected boolean isLoadingForFirstTime = true;
    RequestedServiceListAdapter requestedServiceListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requested_service);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

//        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

        loadMoreListView = findViewById(R.id.req_service_list);
        loadMoreListView.setOnItemClickListener(this);

        callReqService();
    }

    public void callReqService() {
        if (CommonUtils.isNetworkAvailable(this)) {
            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            loadReqService();
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, "No Network connection available");
        }
    }

    private void loadReqService() {
        JSONObject jsonObject = new JSONObject();
        String id = "";
        id = PreferenceStorage.getUserMasterId(this);
        try {
            jsonObject.put(SkilExConstants.USER_MASTER_ID, id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.API_REQUESTED_SERVICE;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onEvent list item clicked" + position);
        RequestedServiceArray service = null;
        if ((requestedServiceListAdapter != null) && (requestedServiceListAdapter.ismSearching())) {
            Log.d(TAG, "while searching");
            int actualindex = requestedServiceListAdapter.getActualEventPos(position);
            Log.d(TAG, "actual index" + actualindex);
            service = requestedServiceArrayList.get(actualindex);
        } else {
            service = requestedServiceArrayList.get(position);
        }

        Intent intent = new Intent(this, RequestedServiceDetailActivity.class);
        intent.putExtra("serviceObj", service);
        startActivity(intent);
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
                JSONArray getData = response.getJSONArray("list_services_order");
//                    loadMembersList(getData.length());
                Gson gson = new Gson();
                RequestedServiceList requestedServiceList = gson.fromJson(response.toString(), RequestedServiceList.class);
                if (requestedServiceList.getServiceArrayList() != null && requestedServiceList.getServiceArrayList().size() > 0) {
                    totalCount = requestedServiceList.getCount();
//                    this.ongoingServiceArrayList.addAll(ongoingServiceList.getserviceArrayList());
                    isLoadingForFirstTime = false;
                    updateListAdapter(requestedServiceList.getServiceArrayList());
                } else {
                    if (requestedServiceArrayList != null) {
                        requestedServiceArrayList.clear();
                        updateListAdapter(requestedServiceList.getServiceArrayList());
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    protected void updateListAdapter(ArrayList<RequestedServiceArray> requestedServiceArrayLists) {
        requestedServiceArrayList.clear();
        requestedServiceArrayList.addAll(requestedServiceArrayLists);
        if (requestedServiceListAdapter == null) {
            requestedServiceListAdapter = new RequestedServiceListAdapter(this, requestedServiceArrayList);
            loadMoreListView.setAdapter(requestedServiceListAdapter);
        } else {
            requestedServiceListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onError(String error) {
        progressDialogHelper.hideProgressDialog();
        AlertDialogHelper.showSimpleAlertDialog(this, error);
    }
}
