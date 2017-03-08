package gridwatch.kplc.activities.network;

import java.io.Serializable;

/**
 * Created by nklugman on 11/18/16.
 */

public class AckRetrofit implements Serializable {

    private String i; //phone_id
    private String c; //cmd
    private String r; //route
    private String t; //time
    //private String z; //

    public AckRetrofit(String phone_id, String cmd,
                       String route, String time) {
        this.c = cmd;
        this.r = route;
        this.i = phone_id;
        this.t = time;
        //z = NetworkConfig.ACK;
    }


    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public String getI() {
        return i;
    }

    public void setI(String i) {
        this.i = i;
    }

    public String getR() {
        return r;
    }

    public void setR(String r) {
        this.r = r;}

    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
    }

}


