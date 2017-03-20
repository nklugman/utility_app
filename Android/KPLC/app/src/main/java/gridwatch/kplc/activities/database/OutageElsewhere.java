package gridwatch.kplc.activities.database;

import io.realm.RealmObject;

/**
 * Created by nklugman on 11/27/16.
 */

public class OutageElsewhere extends RealmObject {


    private long mTime;
    private String mOtherAccount;
    private String mName;
    private String mAccount;
    private String mMeter;
    private String mAddress;
    private String mNotes;
    private String mIsOnline;
    private String mLat;
    private String mLng;


    public String toString() {
        return "t:" + String.valueOf(mTime) + ",w:" +
                mAccount + ",g:" + mAddress + ",v:" + mNotes +
                ",h:" + mOtherAccount + ",e:" + mMeter + ",a:" + mIsOnline + ",n:" + mName + ",l:" + mLat + ",e:" + mLng;
    }


    public OutageElsewhere() {

    }

    public OutageElsewhere(long time, String account, String meter, String other_account, String address, String notes, boolean isOnline, String name, String lat, String lng) {
        mTime = time;
        mAccount = account;
        mMeter = meter;
        mAddress = address;
        mNotes = notes;
        mOtherAccount = other_account;
        if (isOnline) {
            mIsOnline = "t";
        } else {
            mIsOnline = "f";
        }
        mName = name;
        mLat = lat;
        mLng = lng;
    }

    public String getLat() { return mLat; }
    public void setLat(String lat) { this.mLat = lat;}

    public String getLng() { return mLng; }
    public void setLng(String lng) { this.mLng = lng;}

    public String getAccount() { return mAccount; }
    public void setAccount(String account) { this.mAccount = account;}

    public String getName() { return mName; }
    public void setName(String name) { this.mName = name;}

    public String getAddress() { return mAddress;}
    public void setAddress(String address) { this.mAddress = address;}

    public String getNotes() { return mNotes;}
    public void setNotes(String notes) {this.mNotes = notes;}

    public long getTime() {
        return mTime;
    }

    public void setTime(long mTime) {
        this.mTime = mTime;
    }

    public String getOtherAccount() {
        return mOtherAccount;
    }

    public void setOtherAccount(String account) {
        this.mOtherAccount = account;
    }

    public String getMeter() {
        return mMeter;
    }

    public void setMeter(String meter) {
        this.mMeter = meter;
    }


    public String getIsOnline() {
        return mIsOnline;
    }

    public void setmIsOnline(String isOnline) {
        this.mIsOnline = isOnline;
    }

}