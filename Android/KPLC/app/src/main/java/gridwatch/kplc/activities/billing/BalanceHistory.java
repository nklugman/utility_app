package gridwatch.kplc.activities.billing;

import java.util.Calendar;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by guoxinyi on 2/9/17.
 */

public class BalanceHistory extends RealmObject{
    private long account;
    @Required
    private Date date;
    private double balance;
    private double usage;
    @Ignore
    private int             sessionId;
    public long getAccount() { return account; }
    public void setAccount(long account) { this.account = account; }
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
    public double getUsage() { return usage; }
    public void setUsage(double usage) { this.usage = usage; }
    public int getSessionId() { return sessionId; }
    public void setSessionId(int sessionId) { this.sessionId = sessionId; }
}
