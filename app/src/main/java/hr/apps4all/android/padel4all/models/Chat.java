package hr.apps4all.android.padel4all.models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Bruno on 7.2.2018..
 */

public class Chat {

    private String chatID;

    private List<Message> messages = new ArrayList<>();

    public Chat(){
    }

    public void addNewMessage(String content, Player sender){
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        messages.add(new Message(String.format("%s?%s", sender.getUsername(), sender.getFirstName()), cal.getTime(), content));
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public List<Message> getMessages() {
        return messages;
    }
}
