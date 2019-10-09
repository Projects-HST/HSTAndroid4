package com.skilex.serviceprovider.bean.support;

public class StoreMasterId {

    private String docId;
    private String docName;

    public StoreMasterId(String docId, String docName) {
        this.docId = docId;
        this.docName = docName;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }


    //to display object as a string in spinner
    @Override
    public String toString() {
        return docName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StoreMasterId) {
            StoreMasterId c = (StoreMasterId) obj;
            if (c.getDocName().equals(docName) && c.getDocId() == docId) return true;
        }

        return false;
    }

}
