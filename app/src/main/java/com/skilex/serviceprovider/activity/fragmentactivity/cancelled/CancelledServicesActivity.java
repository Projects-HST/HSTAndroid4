package com.skilex.serviceprovider.activity.fragmentactivity.cancelled;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.skilex.serviceprovider.R;
import com.skilex.serviceprovider.activity.fragmentactivity.assigned.AssignedServicesActivity;
import com.skilex.serviceprovider.adapter.AssignedServiceListAdapter;
import com.skilex.serviceprovider.adapter.CancelledServiceListAdapter;
import com.skilex.serviceprovider.bean.support.AssignedService;
import com.skilex.serviceprovider.bean.support.CancelledService;
import com.skilex.serviceprovider.bean.support.CancelledServiceList;
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

public class CancelledServicesActivity extends BaseActivity implements IServiceListener, DialogClickListener,
        AdapterView.OnItemClickListener{

    private static final String TAG = CancelledServicesActivity.class.getName();

    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private ArrayList<CancelledService> cancelledServiceArrayList = new ArrayList<>();
    private ListView loadMoreListView;
    int totalCount = 0, checkrun = 0;
    protected boolean isLoadingForFirstTime = true;
    CancelledServiceListAdapter cancelledServiceListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancelled_service);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        loadMoreListView = findViewById(R.id.cancelled_service_list);
        loadMoreListView.setOnItemClickListener(this);

        callCancelledService();
    }

    public void callCancelledService() {
        if (CommonUtils.isNetworkAvailable(this)) {
            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            loadCancelledService();
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, "No Network connection available");
        }
    }

    private void loadCancelledService() {
        JSONObject jsonObject = new JSONObject();
        String id = "";
        id = PreferenceStorage.getUserMasterId(this);
        try {
            jsonObject.put(SkilExConstants.USER_MASTER_ID, id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.API_CANCELLED_SERVICE_LIST;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onEvent list item clicked" + position);
        CancelledService service = null;
        if ((cancelledServiceListAdapter != null) && (cancelledServiceListAdapter.ismSearching())) {
            Log.d(TAG, "while searching");
            int actualindex = cancelledServiceListAdapter.getActualEventPos(position);
            Log.d(TAG, "actual index" + actualindex);
            service = cancelledServiceArrayList.get(actualindex);
        } else {
            service = cancelledServiceArrayList.get(position);
        }

        Intent intent = new Intent(this, CancelledServiceDetailActivity.class);
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
                CancelledServiceList cancelledServiceList = gson.fromJson(response.toString(), CancelledServiceList.class);
                if (cancelledServiceList.getServiceArrayList() != null && cancelledServiceList.getServiceArrayList().size() > 0) {
                    totalCount = cancelledServiceList.getCount();
//                    this.ongoingServiceArrayList.addAll(ongoingServiceList.getserviceArrayList());
                    isLoadingForFirstTime = false;
                    updateListAdapter(cancelledServiceList.getServiceArrayList());
                } else {
                    if (cancelledServiceArrayList != null) {
                        cancelledServiceArrayList.clear();
                        updateListAdapter(cancelledServiceList.getServiceArrayList());
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    protected void updateListAdapter(ArrayList<CancelledService> cancelledServiceArrayLists) {
        cancelledServiceArrayList.clear();
        cancelledServiceArrayList.addAll(cancelledServiceArrayLists);
        if (cancelledServiceListAdapter == null) {
            cancelledServiceListAdapter = new CancelledServiceListAdapter(this, cancelledServiceArrayList);
            loadMoreListView.setAdapter(cancelledServiceListAdapter);
        } else {
            cancelledServiceListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onError(String error) {
        progressDialogHelper.hideProgressDialog();
        AlertDialogHelper.showSimpleAlertDialog(this, error);
    }
}
