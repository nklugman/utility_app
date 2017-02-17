package gridwatch.kplc.activities;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.Required;

/**
 * Created by guoxinyi on 2/9/17.
 */

public class Newsfeed extends RealmObject{
    @Required
    private Date time;
    private int source;
    private String content;
    @Ignore
    private int             sessionId;
    public Date getTime() { return time; }
    public void setTime(Date time) { this.time = time; }
    public int getSource() { return source; }
    public void setSource(int source) { this.source = source; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public int getSessionId() { return sessionId; }
    public void setSessionId(int sessionId) { this.sessionId = sessionId; }
}
