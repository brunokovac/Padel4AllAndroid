package hr.apps4all.android.padel4all.dao;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import hr.apps4all.android.padel4all.LoginActivity;
import hr.apps4all.android.padel4all.RegisterActivity;
import hr.apps4all.android.padel4all.UserHomeActivity;
import hr.apps4all.android.padel4all.models.Chat;
import hr.apps4all.android.padel4all.models.FirebaseListener;
import hr.apps4all.android.padel4all.models.Game;
import hr.apps4all.android.padel4all.models.GameRequest;
import hr.apps4all.android.padel4all.models.Message;
import hr.apps4all.android.padel4all.models.Person;
import hr.apps4all.android.padel4all.models.Player;

/**
 * Created by Bruno on 5.2.2018..
 */

public class FirebaseDAO implements DAO {


    @Override
    public void getPersonForUsername(final FirebaseListener listener, String username) {
        FirebaseReferenceProvider.getReference().child("users").child(username)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailure();
            }
        });
    }

    @Override
    public void getActivePersonForUsername(final FirebaseListener listener, String username) {
        FirebaseReferenceProvider.getReference().child("users").child(username)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        listener.onSuccess(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        listener.onFailure();
                    }
                });
    }

    @Override
    public void addPerson(final FirebaseListener listener, Player player) {
        FirebaseReferenceProvider.getReference().child("users").child(player.getUsername())
                .setValue(player).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                listener.onSuccess(null);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onFailure();
            }
        });
    }

    @Override
    public void addGame(final FirebaseListener listener, Game game) {
        if (game.getGameID() == null){
            game.setGameID(FirebaseReferenceProvider.getReference().push().getKey());

            String chatKey = FirebaseReferenceProvider.getReference().push().getKey();
            game.setChatID(chatKey);

            Chat chat = new Chat();
            chat.setChatID(chatKey);
            FirebaseReferenceProvider.getReference().child("chats").child(chatKey).setValue(chat);
        }

        FirebaseReferenceProvider.getReference().child("games").child(game.getGameID())
                .setValue(game).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                listener.onSuccess(null);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onFailure();
            }
        });
    }

    @Override
    public void addLastSeenByUsersToGame(Game game) {
        FirebaseReferenceProvider.getReference().child("games")
                .child(game.getGameID()).child("lastSeenByUsernames").setValue(game.getLastSeenByUsernames());
    }

    @Override
    public void getGamesOrganizedByPlayer(final FirebaseListener listener, Player player) {
        FirebaseReferenceProvider.getReference().child("games")
                .orderByChild("organizer")
                .equalTo(player.getUsername())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        listener.onSuccess(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        listener.onFailure();
                    }
                });
    }

    @Override
    public void getGamesPlayerRequested(final FirebaseListener listener, Player player) {
        FirebaseReferenceProvider.getReference().child("requests")
                .orderByChild("sender").equalTo(player.getUsername())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        listener.onSuccess(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        listener.onFailure();
                    }
                });
    }

    @Override
    public void getGamesForDate(final FirebaseListener listener, Date date) {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.setTime(date);

        FirebaseReferenceProvider.getReference().child("games")
                .orderByChild("start/date").equalTo(cal.get(Calendar.DAY_OF_MONTH))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        listener.onSuccess(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        listener.onFailure();
                    }
                });
    }

    @Override
    public void getAllGamesFromTodayOn(final FirebaseListener listener) {
        FirebaseReferenceProvider.getReference().child("games")
                .orderByChild("start/date").startAt(Calendar.getInstance(TimeZone.getDefault()).get(Calendar.DAY_OF_MONTH))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        listener.onSuccess(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        listener.onFailure();
                    }
                });
    }

    @Override
    public void addGameRequest(final FirebaseListener listener, GameRequest request) {
        String key = request.getGameRequestID();

        if (key == null) {
            request.setGameRequestID(String.format("%s_%s", request.getGameID(), request.getSender()));
            key = request.getGameRequestID();
        }

        FirebaseReferenceProvider.getReference().child("requests").child(key)
                .setValue(request).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                listener.onSuccess(null);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onFailure();
            }
        });
    }

    @Override
    public void updateChat(final FirebaseListener listener, Chat chat) {
        FirebaseReferenceProvider.getReference().child("chats").child(chat.getChatID()).setValue(chat)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                listener.onSuccess(null);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onFailure();
            }
        });
    }

    @Override
    public void getChatForChatID(final ValueEventListener listener, String chatID) {
        FirebaseReferenceProvider.getReference().child("chats").child(chatID)
                .addValueEventListener(listener);
    }

    @Override
    public void getOneTimeChatForChatID(final FirebaseListener listener, String chatID) {
        FirebaseReferenceProvider.getReference().child("chats").child(chatID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        listener.onSuccess(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        listener.onFailure();
                    }
                });
    }

    @Override
    public void getOneTimeGameRequestsForGame(final FirebaseListener listener, Game game) {
        FirebaseReferenceProvider.getReference().child("requests")
                .orderByChild("gameID").equalTo(game.getGameID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        listener.onSuccess(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        listener.onFailure();
                    }
                });
    }

    @Override
    public void getGameRequestsForGame(final FirebaseListener listener, Game game) {
        FirebaseReferenceProvider.getReference().child("requests")
                .orderByChild("gameID").equalTo(game.getGameID())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        listener.onSuccess(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        listener.onFailure();
                    }
                });
    }

    @Override
    public void getPersonsForFullNameSearch(final FirebaseListener listener, String entry) {
        FirebaseReferenceProvider.getReference().child("users")
                .orderByChild("fullName")
                .startAt(entry.toUpperCase()).endAt(entry.toUpperCase() + "\uf8ff")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        listener.onSuccess(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        listener.onFailure();
                    }
                });
    }

    @Override
    public void getGameForGameID(final FirebaseListener listener, String gameID) {
        FirebaseReferenceProvider.getReference().child("games")
                .child(gameID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        listener.onSuccess(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        listener.onFailure();
                    }
                });
    }

}
