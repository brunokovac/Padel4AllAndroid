package hr.apps4all.android.padel4all;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hr.apps4all.android.padel4all.adapaters.GamesArrayAdapter;
import hr.apps4all.android.padel4all.adapaters.MessagesArrayAdapter;
import hr.apps4all.android.padel4all.dao.DAO;
import hr.apps4all.android.padel4all.dao.DAOProvider;
import hr.apps4all.android.padel4all.dao.FirebaseReferenceProvider;
import hr.apps4all.android.padel4all.models.Chat;
import hr.apps4all.android.padel4all.models.FirebaseListener;
import hr.apps4all.android.padel4all.models.Game;
import hr.apps4all.android.padel4all.models.Message;
import hr.apps4all.android.padel4all.models.Player;

public class GameChatFragment extends Fragment {

    private Player player;
    private Game game;
    private Chat chat;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_chat, container, false);
        ButterKnife.bind(this, view);

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        newMessageField.setMaxWidth(metrics.widthPixels / 10 * 8);

        player = (Player) ((MyApplication) getActivity().getApplication()).getActivePerson();
        game = ((MyApplication) getActivity().getApplication()).getGame();

        ((MyApplication) getActivity().getApplication()).setFragment(this);
        setupMessages();

        return view;
    }

    @BindView(R.id.gameChatMessagesList)
    ListView messagesList;

    private ValueEventListener listener;

    public void setupMessages(){
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!(((MyApplication) getActivity().getApplication()).getFragment() instanceof GameChatFragment)){
                    return;
                }

                chat = dataSnapshot.getValue(Chat.class);

                List<Message> messages = chat.getMessages();
                ArrayAdapter<Message> adapter = new MessagesArrayAdapter(getActivity(), messages);

                messagesList.setAdapter(adapter);
                messagesList.setSelection(messages.size() - 1);

                if (!messages.isEmpty()){
                    game.addSeenByUser(player);
                    DAOProvider.getDao().addLastSeenByUsersToGame(game);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
            }
        };

        DAOProvider.getDao().getChatForChatID(listener, game.getChatID());
    }

    @BindView(R.id.gameChatNewMessageField)
    EditText newMessageField;

    @OnClick(R.id.gameChatSendButton)
    void sendMessage(){
        String stringMessage = newMessageField.getText().toString().trim();
        if (stringMessage.isEmpty()){
            return;
        }

        chat.addNewMessage(stringMessage, player);
        DAOProvider.getDao().updateChat(new FirebaseListener() {
            @Override
            public void onSuccess(DataSnapshot snapshot) {
                newMessageField.setText("");
            }

            @Override
            public void onFailure() {
                Toast.makeText(getActivity(), "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
            }
        }, chat);

        game.getLastSeenByUsernames().clear();
        game.addSeenByUser(player);
        DAOProvider.getDao().addLastSeenByUsersToGame(game);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FirebaseReferenceProvider.getReference().child("chats").child(game.getChatID()).removeEventListener(listener);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        FirebaseReferenceProvider.getReference().child("chats").child(game.getChatID()).removeEventListener(listener);
    }
}
