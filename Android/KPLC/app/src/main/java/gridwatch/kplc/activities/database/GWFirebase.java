package gridwatch.kplc.activities.database;

import com.loopj.android.http.RequestParams;

/**
 * Created by nklugman on 11/27/16.
 */

public class GWFirebase {


    private long mTime;
    private String mText;
    private String mID;
    private String mVersion;
    private String mBattery;

    public GWFirebase() {

    }

    public GWFirebase(long time, String text, String id, String version, String battery, String type) {
        mTime = time;
        mText = text;
        mID = id;
        mVersion = version;
        mBattery = battery;
    }

    public RequestParams toRequestParams() {
        RequestParams values = new RequestParams();
        values.put("time", mTime);
        values.put("text", mText);
        values.put("id", mID);
        values.put("version", mVersion);
        values.put("battery", mBattery);
        return values;
    }

    public String toString() {
        return "t:" + String.valueOf(mTime) + ",m:" + mText + ",i:" + mID + ",v:" + mVersion + ",b:" + mBattery;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long mTime) {
        this.mTime = mTime;
    }

    public String getText() {
        return mText;
    }

    public void setText(String mText) {
        this.mText = mText;
    }

    public String getID() {
        return mID;
    }

    public void setID(String mGWID) {
        this.mID = mGWID;
    }

    public String getVersion() {
        return mVersion;
    }

    public void setVersion(String version) {
        this.mVersion = version;
    }

    public String getBattery() {
        return mBattery;
    }

    public void setBattery(String battery) {
        this.mBattery = battery;
    }





}
