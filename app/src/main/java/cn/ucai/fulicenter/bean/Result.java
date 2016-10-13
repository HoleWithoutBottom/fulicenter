package cn.ucai.fulicenter.bean;

/**
 * Created by Administrator on 2016/10/13.
 */
public class Result {

    private int retcode;
    private boolean retmsg;
    private Object retdata;
    public void setRetcode(int retcode) {
        this.retcode = retcode;
    }
    public int getRetcode() {
        return retcode;
    }

    public void setRetmsg(boolean retmsg) {
        this.retmsg = retmsg;
    }
    public boolean getRetmsg() {
        return retmsg;
    }

    public void setRetdata(String retdata) {
        this.retdata = retdata;
    }
    public Object getRetdata() {
        return retdata;
    }

    public Result() {
    }

    @Override
    public String toString() {
        return "Result{" +
                "retcode=" + retcode +
                ", retmsg=" + retmsg +
                ", retdata='" + retdata + '\'' +
                '}';
    }
}
