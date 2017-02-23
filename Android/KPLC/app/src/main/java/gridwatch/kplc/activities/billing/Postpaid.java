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

public class Postpaid extends RealmObject{
    @Required
    private String account;
    @Required
    private Date month;
    private double balance;
    private double usage;
    private Date payDate;
    private Date dueDate;
    private String statement;
    @Ignore
    private int             sessionId;
    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }
    public Date getMonth() { return month; }
    public void setMonth(Date month) { this.month = month; }
    public Date getPayDate() { return payDate; }
    public void setPayDate(Date payDate) { this.payDate = payDate; }
    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
    public double getUsage() { return usage; }
    public void setUsage(double usage) { this.usage = usage; }
    public String getStatement() { return statement; }
    public void setStatement(String statement) { this.statement = statement; }
    public int getSessionId() { return sessionId; }
    public void setSessionId(int sessionId) { this.sessionId = sessionId; }
}
