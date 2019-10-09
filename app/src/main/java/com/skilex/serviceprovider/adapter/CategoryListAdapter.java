package com.skilex.serviceprovider.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.skilex.serviceprovider.R;
import com.skilex.serviceprovider.activity.providerregistration.CategorySelectionActivity;
import com.skilex.serviceprovider.bean.support.Category;
import com.skilex.serviceprovider.utils.SkilExValidator;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;


import java.util.ArrayList;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.ViewHolder> {

    private ArrayList<Category> categoryArrayList;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private View.OnClickListener onClickListener;
    private final Transformation transformation;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView mImageView, Selecttick;
        public CheckBox checkTick;
        public TextView mPrefTextView;
        public RelativeLayout rlPref;
        public RelativeLayout slPref;

        public ViewHolder(View v, int viewType) {
            super(v);
            mImageView = v.findViewById(R.id.txt_preference_name);
            mPrefTextView = v.findViewById(R.id.txt_pref_category_name);
            Selecttick = v.findViewById(R.id.pref_tick);
            checkTick = v.findViewById(R.id.checkBoxTick);
            if (viewType == 1) {
                rlPref = (RelativeLayout) v.findViewById(R.id.rlPref);
            } else {
                rlPref = (RelativeLayout) v;
            }

            rlPref.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public CategoryListAdapter(Context context, ArrayList<Category> categoryArrayList, OnItemClickListener onItemClickListener) {
        this.categoryArrayList = categoryArrayList;
        this.context = context;
        this.onItemClickListener = onItemClickListener;

        transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(5)
                .oval(false)
                .build();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CategoryListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        // create a new view
        View parentView;
        parentView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_selection_items, parent, false);


        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(parentView, viewType);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mPrefTextView.setText(categoryArrayList.get(position).getCategory());

        if (SkilExValidator.checkNullString(categoryArrayList.get(position).getImgPath())) {
//            Picasso.with(this.context).load(categoryArrayList.get(position).getImgPath()).fit().transform(this.transformation).placeholder(R.drawable.ic_logo_blue).error(R.drawable.ic_logo_blue).into(holder.mImageView);
            Picasso.get().load(categoryArrayList.get(position).getImgPath()).fit().transform(this.transformation).placeholder(R.drawable.ic_logo_blue).error(R.drawable.ic_logo_blue).into(holder.mImageView);
        } else {
            holder.mImageView.setImageResource(R.drawable.ic_logo_blue);
        }


        if (categoryArrayList.get(position).getCategoryPreference().equals("N")) {

            holder.checkTick.setChecked(false);

        } else {
            if (context instanceof CategorySelectionActivity) {
                ((CategorySelectionActivity) context).onCategorySelected(position);
            }

            holder.checkTick.setChecked(true);

        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return categoryArrayList.size();

    }

    public Category getItem(int position) {
        return categoryArrayList.get(position);
    }

    @Override
    public int getItemViewType(int position) {

        if (categoryArrayList.get(position) != null || categoryArrayList.get(position).getSize() > 0)
            return categoryArrayList.get(position).getSize();
        else
            return 1;
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }
}
