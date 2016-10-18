package cn.ucai.fulicenter.bean;

/**
 * Created by Administrator on 2016/10/13.
 */
public class Result {

    private int retCode;
    private boolean retMsg;
    private Object retData;
    public void setRetCode(int retcode) {
        this.retCode = retcode;
    }
    public int getRetCode() {
        return retCode;
    }

    public void setRetMsg(boolean retmsg) {
        this.retMsg = retmsg;
    }
    public boolean getRetMsg() {
        return retMsg;
    }

    public void setRetData(String retdata) {
        this.retData = retdata;
    }
    public Object getRetData() {
        return retData;
    }

    public Result() {
    }

    @Override
    public String toString() {
        return "Result{" +
                "retcode=" + retCode +
                ", retmsg=" + retMsg +
                ", retdata='" + retData + '\'' +
                '}';
    }
}
