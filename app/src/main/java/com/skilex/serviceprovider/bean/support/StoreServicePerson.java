package com.skilex.serviceprovider.bean.support;

public class StoreServicePerson {

    private String expertId;
    private String expertName;

    public StoreServicePerson(String expertId, String expertName) {
        this.expertId = expertId;
        this.expertName = expertName;
    }

    public String getServicePersonId() {
        return expertId;
    }

    public void setServicePersonId(String cancelMasterId) {
        this.expertId = cancelMasterId;
    }

    public String getServicePersonName() {
        return expertName;
    }

    public void setServicePersonName(String expertName) {
        this.expertName = expertName;
    }

    //to display object as a string in spinner
    @Override
    public String toString() {
        return expertName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StoreServicePerson) {
            StoreServicePerson c = (StoreServicePerson) obj;
            if (c.getServicePersonName().equals(expertName) && c.getServicePersonId() == expertId)
                return true;
        }

        return false;
    }

}
