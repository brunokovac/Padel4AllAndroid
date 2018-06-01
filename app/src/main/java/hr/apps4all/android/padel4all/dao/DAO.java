package hr.apps4all.android.padel4all.dao;

import com.google.firebase.database.ValueEventListener;

import java.util.Date;

import hr.apps4all.android.padel4all.models.Chat;
import hr.apps4all.android.padel4all.models.FirebaseListener;
import hr.apps4all.android.padel4all.models.Game;
import hr.apps4all.android.padel4all.models.GameRequest;
import hr.apps4all.android.padel4all.models.Message;
import hr.apps4all.android.padel4all.models.Player;

public interface DAO {

    void getPersonForUsername(FirebaseListener listener, String username);

    void getActivePersonForUsername(FirebaseListener listener, String username);

    void addPerson(FirebaseListener listener, Player player);

    void addGame(FirebaseListener listener, Game game);

    void addLastSeenByUsersToGame(Game game);

    void getGamesOrganizedByPlayer(FirebaseListener listener, Player player);

    void getGamesPlayerRequested(FirebaseListener listener, Player player);

    void getGamesForDate(FirebaseListener listener, Date date);

    void getAllGamesFromTodayOn(FirebaseListener listener);

    void addGameRequest(FirebaseListener listener, GameRequest request);

    void updateChat(FirebaseListener listener, Chat chat);

    void getChatForChatID(ValueEventListener listener, String chatID);

    void getOneTimeChatForChatID(FirebaseListener listener, String chatID);

    void getOneTimeGameRequestsForGame(FirebaseListener listener, Game game);

    void getGameRequestsForGame(FirebaseListener listener, Game game);

    void getPersonsForFullNameSearch(FirebaseListener listener, String entry);

    void getGameForGameID(FirebaseListener listener, String gameID);

}