package gridwatch.kplc.activities.payment;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by guoxinyi on 3/1/17.
 */

public class Token extends RealmObject{


    @Required
    private String account;
    private double token;
    private Date time;
    @Ignore
    private int             sessionId;
    public Date getTime() { return time; }
    public void setTime(Date time) { this.time = time; }
    public double getToken() { return token; }
    public void setToken(double token) { this.token = token;}
    public String getAccount() { return account; }
    public void setAccount(String content) { this.account = account;}
    public int getSessionId() { return sessionId; }
    public void setSessionId(int sessionId) { this.sessionId = sessionId; }
}
