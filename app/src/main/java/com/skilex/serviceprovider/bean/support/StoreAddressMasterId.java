package com.skilex.serviceprovider.bean.support;

public class StoreAddressMasterId {

    private String addDocId;
    private String addDocName;

    public StoreAddressMasterId(String addDocId, String addDocName) {
        this.addDocId = addDocId;
        this.addDocName = addDocName;
    }

    public String getAddDocId() {
        return addDocId;
    }

    public void setAddDocId(String addDocId) {
        this.addDocId = addDocId;
    }

    public String getDocName() {
        return addDocName;
    }

    public void setDocName(String addDocName) {
        this.addDocName = addDocName;
    }


    //to display object as a string in spinner
    @Override
    public String toString() {
        return addDocName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StoreAddressMasterId) {
            StoreAddressMasterId c = (StoreAddressMasterId) obj;
            if (c.getDocName().equals(addDocName) && c.getAddDocId() == addDocId) return true;
        }

        return false;
    }
}
