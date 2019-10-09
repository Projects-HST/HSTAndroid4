package com.hst.vendor.activity.fragmentactivity.completed;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.hst.vendor.interfaces.DialogClickListener;
import com.hst.vendor.languagesupport.BaseActivity;
import com.hst.vendor.servicehelpers.ServiceHelper;
import com.hst.vendor.serviceinterfaces.IServiceListener;
import com.hst.vendor.R;
import com.hst.vendor.adapter.CompletedServiceListAdapter;
import com.hst.vendor.bean.support.CompletedService;
import com.hst.vendor.bean.support.CompletedServiceList;
import com.hst.vendor.helper.AlertDialogHelper;
import com.hst.vendor.helper.ProgressDialogHelper;
import com.hst.vendor.utils.CommonUtils;
import com.hst.vendor.utils.PreferenceStorage;
import com.hst.vendor.utils.SkilExConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.util.Log.d;

public class CompletedServicesActivity extends BaseActivity implements IServiceListener, DialogClickListener,
        AdapterView.OnItemClickListener {

    private static final String TAG = CompletedServicesActivity.class.getName();

    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private ArrayList<CompletedService> completedServiceArrayList = new ArrayList<>();
    private ListView loadMoreListView;
    int totalCount = 0, checkrun = 0;
    protected boolean isLoadingForFirstTime = true;
    CompletedServiceListAdapter completedServiceListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_service);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        loadMoreListView = findViewById(R.id.completed_service_list);
        loadMoreListView.setOnItemClickListener(this);

        callAssignService();

    }

    public void callAssignService() {
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
        String url = SkilExConstants.BUILD_URL + SkilExConstants.API_COMPLETED_SERVICE_LIST;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onEvent list item clicked" + position);
        CompletedService service = null;
        if ((completedServiceListAdapter != null) && (completedServiceListAdapter.ismSearching())) {
            Log.d(TAG, "while searching");
            int actualindex = completedServiceListAdapter.getActualEventPos(position);
            Log.d(TAG, "actual index" + actualindex);
            service = completedServiceArrayList.get(actualindex);
        } else {
            service = completedServiceArrayList.get(position);
        }

        Intent intent = new Intent(this, CompletedServiceDetailActivity.class);
        intent.putExtra("serviceObj", service);
        startActivity(intent);
        finish();
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
                CompletedServiceList completedServiceList = gson.fromJson(response.toString(), CompletedServiceList.class);
                if (completedServiceList.getServiceArrayList() != null && completedServiceList.getServiceArrayList().size() > 0) {
                    totalCount = completedServiceList.getCount();
//                    this.ongoingServiceArrayList.addAll(ongoingServiceList.getserviceArrayList());
                    isLoadingForFirstTime = false;
                    updateListAdapter(completedServiceList.getServiceArrayList());
                } else {
                    if (completedServiceArrayList != null) {
                        completedServiceArrayList.clear();
                        updateListAdapter(completedServiceList.getServiceArrayList());
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    protected void updateListAdapter(ArrayList<CompletedService> completedServiceArrayLists) {
        completedServiceArrayList.clear();
        completedServiceArrayList.addAll(completedServiceArrayLists);
        if (completedServiceListAdapter == null) {
            completedServiceListAdapter = new CompletedServiceListAdapter(this, completedServiceArrayList);
            loadMoreListView.setAdapter(completedServiceListAdapter);
        } else {
            completedServiceListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onError(String error) {
        progressDialogHelper.hideProgressDialog();
        AlertDialogHelper.showSimpleAlertDialog(this, error);
    }
}
