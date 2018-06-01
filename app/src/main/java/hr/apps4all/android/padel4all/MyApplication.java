package hr.apps4all.android.padel4all;

import android.app.Application;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import hr.apps4all.android.padel4all.models.Chat;
import hr.apps4all.android.padel4all.models.FirebaseListener;
import hr.apps4all.android.padel4all.models.Game;
import hr.apps4all.android.padel4all.models.Person;

/**
 * Created by Bruno on 3.2.2018..
 */

public class MyApplication extends Application {

    private Person activePerson;
    private Game game;
    private Person personBeingChecked;

    private List<Game> newMessagesGames = new ArrayList<>();

    private List<Game> myGames = new ArrayList<>();

    private Fragment fragment;

    public Person getActivePerson() {
        return activePerson;
    }

    public void setActivePerson(Person activePerson) {
        this.activePerson = activePerson;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Person getPersonBeingChecked() {
        return personBeingChecked;
    }

    public void setPersonBeingChecked(Person personBeingChecked) {
        this.personBeingChecked = personBeingChecked;
    }

    public List<Game> getNewMessagesGames() {
        return newMessagesGames;
    }

    public void setNewMessagesGames(List<Game> newMessagesGames) {
        this.newMessagesGames = newMessagesGames;
    }

    public boolean addNewMessageGame(Game game){
        if (!newMessagesGames.contains(game)) {
            newMessagesGames.add(game);
            return true;
        }

        return false;
    }

    public boolean removeNewMessageGame(Game game){
        return newMessagesGames.remove(game);
    }

    public List<Game> getMyGames() {
        return myGames;
    }

    public void addMyGame(Game game){
        if (!myGames.contains(game)){
            myGames.add(game);
        }
    }

    public void setMyGames(List<Game> myGames) {
        this.myGames = myGames;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public Fragment getFragment() {
        return fragment;
    }

}
