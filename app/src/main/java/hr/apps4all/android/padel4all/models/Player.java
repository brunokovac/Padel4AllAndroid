package hr.apps4all.android.padel4all.models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Bruno on 2.2.2018..
 */

public class Player extends Person {

    private List<String> gameIDs = new ArrayList<>();

    private int numberOfGames;

    private int numberOfWins;
    private int numberOfLosses;

    public Player(){
    }

    public Player(String username, String password, String firstName, String lastName, Date dateOfBirth,
                    String email, String description) {
        super(PersonType.PLAYER, username, password, firstName, lastName, dateOfBirth,
                Calendar.getInstance(TimeZone.getDefault()).getTime(), email, description);
    }

    public void addGame(Game game){
        if (!gameIDs.contains(game.getGameID())){
            gameIDs.add(game.getGameID());
        }
    }

    public void removeGame(Game game){
        if (gameIDs.contains(game.getGameID())){
            gameIDs.remove(game.getGameID());
        }
    }

    public void addWin(){
        numberOfWins++;
    }

    public void addLoss(){
        numberOfLosses++;
    }

    public int getNumberOfGames() {
        return numberOfGames;
    }

    public int getNumberOfWins() {
        return numberOfWins;
    }

    public int getNumberOfLosses() {
        return numberOfLosses;
    }

    public List<String> getGameIDs() {
        return gameIDs;
    }
}
