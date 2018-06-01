package hr.apps4all.android.padel4all.models;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import hr.apps4all.android.padel4all.dao.DAOProvider;

/**
 * Created by Bruno on 2.2.2018..
 */

public class Game {

    private String gameID;

    private String location;

    private Date start;
    private int durationMinutes = 60;

    private String organizerUsername;
    private List<String> playerUsernames = new ArrayList<>();

    private boolean locked = false;
    private boolean canceled = false;

    private String chatID;
    private List<String> lastSeenByUsernames = new ArrayList<>();

    public Game(){
    }

    public Game(Date start, int durationMinutes){
        this.start = start;
        this.durationMinutes = durationMinutes;
    }

    public void addPlayer(Player player){
        this.playerUsernames.add(player.getUsername());
    }

    public void addPlayer(String playerUsername){
        this.playerUsernames.add(playerUsername);
    }

    public void removePlayer(Player player){
        this.playerUsernames.remove(player.getUsername());
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public void addHalfAnHourToDuration(){
        durationMinutes += 30;
    }

    public void removeHalfAnHourFromDuration(){
        if (durationMinutes > 30){
            durationMinutes -= 30;
        }
    }

    public String getGameID() {
        return gameID;
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    public Date getStart() {
        return start;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public List<String> getPlayerUsernames() {
        return playerUsernames;
    }

    public String getOrganizerUsername() {
        return organizerUsername;
    }

    public void setOrganizerUsername(String organizerUsername) {
        this.organizerUsername = organizerUsername;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isLocked() {
        return locked;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public List<String> getLastSeenByUsernames() {
        return lastSeenByUsernames;
    }

    public void addSeenByUser(Player player){
        if (!lastSeenByUsernames.contains(player.getUsername())) {
            lastSeenByUsernames.add(player.getUsername());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Game game = (Game) o;

        return gameID != null ? gameID.equals(game.gameID) : game.gameID == null;
    }

    @Override
    public int hashCode() {
        return gameID != null ? gameID.hashCode() : 0;
    }
}
