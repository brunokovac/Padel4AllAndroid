package hr.apps4all.android.padel4all.models;

/**
 * Created by Bruno on 6.2.2018..
 */

public class GameRequest {

    private String gameRequestID;

    private String gameID;
    private String sender;

    private boolean accepted = false;
    private boolean checked = false;
    private boolean canceled = false;

    public GameRequest(){
    }

    public void accept(){
        accepted = true;
        checked = true;
    }

    public void decline(){
        accepted = false;
        checked = true;
    }

    public String getGameRequestID() {
        return gameRequestID;
    }

    public void setGameRequestID(String gameRequestID) {
        this.gameRequestID = gameRequestID;
    }

    public String getGameID() {
        return gameID;
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }
}
