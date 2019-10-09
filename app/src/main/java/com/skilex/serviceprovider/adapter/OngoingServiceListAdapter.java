package com.skilex.serviceprovider.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.skilex.serviceprovider.R;
import com.skilex.serviceprovider.bean.support.OngoingService;
import com.skilex.serviceprovider.helper.ProgressDialogHelper;
import com.skilex.serviceprovider.servicehelpers.ServiceHelper;

import java.util.ArrayList;

public class OngoingServiceListAdapter extends BaseAdapter {

    //    private final Transformation transformation;
    private Context context;
    private ArrayList<OngoingService> services;
    private boolean mSearching = false;
    private boolean mAnimateSearch = false;
    Boolean click = false;
    private ArrayList<Integer> mValidSearchIndices = new ArrayList<Integer>();
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;

//    DynamicSubCatFragment dsf = new DynamicSubCatFragment();

    public OngoingServiceListAdapter(Context context, ArrayList<OngoingService> services) {
        this.context = context;
        this.services = services;
//        Collections.reverse(services);
//        transformation = new RoundedTransformationBuilder()
//                .cornerRadiusDp(0)
//                .oval(false)
//                .build();
        mSearching = false;
    }


    @Override
    public int getCount() {
        if (mSearching) {
            if (!mAnimateSearch) {
                mAnimateSearch = true;
            }
            return mValidSearchIndices.size();
        } else {
            return services.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if (mSearching) {
            return services.get(mValidSearchIndices.get(position));
        } else {
            return services.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final OngoingServiceListAdapter.ViewHolder holder;
//        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.ongoing_service_list_item, parent, false);

            holder = new OngoingServiceListAdapter.ViewHolder();
            holder.txtCatName = convertView.findViewById(R.id.category_name);
            holder.txtSubCatName = convertView.findViewById(R.id.sub_category_name);
//            if (PreferenceStorage.getLang(context).equalsIgnoreCase("tamil")) {
//                holder.txtCatName.setText(services.get(position).getServiceCategoryMainNameTA());
//                holder.txtSubCatName.setText(services.get(position).getServiceNameTA());
//            } else {
            holder.txtCatName.setText(services.get(position).getServiceCategoryMainName());
            holder.txtSubCatName.setText(services.get(position).getServiceSubCategoryName());
//            }
            holder.txtDate = convertView.findViewById(R.id.service_date);
            holder.txtDate.setText(services.get(position).getServiceOrderDate());
            holder.txtTime = convertView.findViewById(R.id.service_time_slot);
            holder.txtTime.setText(services.get(position).getServiceOrderFromTime());
            holder.txtExpert = convertView.findViewById(R.id.service_expert);
            holder.txtExpert.setText(services.get(position).getServiceExpertName());
            holder.txtStatus = convertView.findViewById(R.id.service_status);
            holder.txtStatus.setText(services.get(position).getServiceOrderStatus());
            convertView.setTag(holder);

        } else {
            holder = (OngoingServiceListAdapter.ViewHolder) convertView.getTag();

            holder.txtCatName = convertView.findViewById(R.id.category_name);
            holder.txtSubCatName = convertView.findViewById(R.id.sub_category_name);
            /*if (PreferenceStorage.getLang(context).equalsIgnoreCase("tamil")) {
                holder.txtCatName.setText(services.get(position).getServiceCategoryMainNameTA());
                holder.txtSubCatName.setText(services.get(position).getServiceNameTA());
            } else {*/
            holder.txtCatName.setText(services.get(position).getServiceCategoryMainName());
            holder.txtSubCatName.setText(services.get(position).getServiceSubCategoryName());
//            }
            holder.txtDate = convertView.findViewById(R.id.service_date);
            holder.txtDate.setText(services.get(position).getServiceOrderDate());
            holder.txtTime = convertView.findViewById(R.id.service_time_slot);
            holder.txtTime.setText(services.get(position).getServiceOrderFromTime());
            holder.txtExpert = convertView.findViewById(R.id.service_expert);
            holder.txtExpert.setText(services.get(position).getServiceExpertName());
            holder.txtStatus = convertView.findViewById(R.id.service_status);
            holder.txtStatus.setText(services.get(position).getServiceOrderStatus());
        }

        if (mSearching) {
            position = mValidSearchIndices.get(position);

        } else {
            Log.d("Event List Adapter", "getview pos called" + position);
        }

        return convertView;
    }

    public void startSearch(String eventName) {
        mSearching = true;
        mAnimateSearch = false;
        Log.d("EventListAdapter", "serach for event" + eventName);
        mValidSearchIndices.clear();
        for (int i = 0; i < services.size(); i++) {
            String homeWorkTitle = services.get(i).getServiceName();
            if ((homeWorkTitle != null) && !(homeWorkTitle.isEmpty())) {
                if (homeWorkTitle.toLowerCase().contains(eventName.toLowerCase())) {
                    mValidSearchIndices.add(i);
                }
            }
        }
        Log.d("Event List Adapter", "notify" + mValidSearchIndices.size());
    }

    public void exitSearch() {
        mSearching = false;
        mValidSearchIndices.clear();
        mAnimateSearch = false;
    }

    public void clearSearchFlag() {
        mSearching = false;
    }

    public class ViewHolder {
        public TextView txtCatName, txtSubCatName, txtDate, txtTime, txtExpert, txtStatus;
    }

    public boolean ismSearching() {
        return mSearching;
    }

    public int getActualEventPos(int selectedSearchpos) {
        if (selectedSearchpos < mValidSearchIndices.size()) {
            return mValidSearchIndices.get(selectedSearchpos);
        } else {
            return 0;
        }
    }
}
