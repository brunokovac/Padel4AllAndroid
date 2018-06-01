package hr.apps4all.android.padel4all.models;

import java.util.Date;

/**
 * Created by Bruno on 3.2.2018..
 */

public class Message {

    private String senderInfo;
    private Date sendingTime;
    private String content;

    public Message(){
    }

    public Message(String senderInfo, Date sendingTime, String content){
        this.senderInfo = senderInfo;
        this.sendingTime = sendingTime;
        this.content = content;
    }

    public String getSenderInfo() {
        return senderInfo;
    }

    public Date getSendingTime() {
        return sendingTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setSenderInfo(String senderInfo) {
        this.senderInfo = senderInfo;
    }

    public void setSendingTime(Date sendingTime) {
        this.sendingTime = sendingTime;
    }
}
