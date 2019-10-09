package com.skilex.serviceprovider.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Category {

    @SerializedName("cat_id")
    @Expose
    private String id;
    @SerializedName("cat_name")
    @Expose
    private String main_cat_name;
    @SerializedName("cat_ta_name")
    @Expose
    private String main_cat_ta_name;
    @SerializedName("cat_pic_url")
    @Expose
    private String imgPath;
    @SerializedName("user_preference")
    @Expose
    private String categoryPreference;

    @SerializedName("size")
    @Expose
    private int size = 2;

    /**
     * @return The id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return The main_cat_name
     */
    public String getCategory() {
        return main_cat_name;
    }

    /**
     * @param main_cat_name The main_cat_name
     */
    public void setCategoryTa(String main_cat_name) {
        this.main_cat_name = main_cat_name;
    }

    /**
     * @return The main_cat_ta_name
     */
    public String getCategoryTa() {
        return main_cat_ta_name;
    }

    /**
     * @param main_cat_ta_name The main_cat_ta_name
     */
    public void setCategory(String main_cat_ta_name) {
        this.main_cat_ta_name = main_cat_ta_name;
    }

    /**
     * @return The imgPath
     */
    public String getImgPath() {
        return imgPath;
    }

    /**
     * @param imgPath The img_path
     */
    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    /**
     * @return The categoryPreference
     */
    public String getCategoryPreference() {
        return categoryPreference;
    }

    /**
     * @param categoryPreference The category_preference
     */
    public void setCategoryPreference(String categoryPreference) {
        this.categoryPreference = categoryPreference;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

}
