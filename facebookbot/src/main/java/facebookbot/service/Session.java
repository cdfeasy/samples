package facebookbot.service;

/**
 * Created by d.asadullin on 14.07.2016.
 */
public class Session {
    public enum View{
        HOME,
        BALANCE,
        NEWS,
        HELP
    }
    String id;
    Boolean isNew;
    String msisdn;
    Boolean isApproved=false;
    String sendKey;
    int tryCount=0;
    View view= View.HOME;

    public Session(String id) {
        this.id = id;
    }

    public Session() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public Boolean getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(Boolean isApproved) {
        this.isApproved = isApproved;
    }

    public String getSendKey() {
        return sendKey;
    }

    public void setSendKey(String sendKey) {
        this.sendKey = sendKey;
    }

    public int getTryCount() {
        return tryCount;
    }

    public void setTryCount(int tryCount) {
        this.tryCount = tryCount;
    }

    public Boolean getIsNew() {
        return isNew;
    }

    public void setIsNew(Boolean isNew) {
        this.isNew = isNew;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }
    public void clear(){
        setMsisdn(null);
        setIsApproved(false);
        setSendKey(null);
        setTryCount(0);
        setView(View.HOME);
    }
}
