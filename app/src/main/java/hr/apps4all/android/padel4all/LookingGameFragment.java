package hr.apps4all.android.padel4all;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hr.apps4all.android.padel4all.dao.DAOProvider;
import hr.apps4all.android.padel4all.models.FirebaseListener;
import hr.apps4all.android.padel4all.models.Game;
import hr.apps4all.android.padel4all.models.GameRequest;
import hr.apps4all.android.padel4all.models.Player;

public class LookingGameFragment extends Fragment {

    private Player player;
    private Game game;

    @BindView(R.id.lookingGameChatButton)
    Button chatButton;

    @BindView(R.id.lookingGameSendRequestButton)
    Button requestButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_looking5, container, false);
        ButterKnife.bind(this, view);

        player = (Player) ((MyApplication) getActivity().getApplication()).getActivePerson();
        game = ((MyApplication) getActivity().getApplication()).getGame();

        if (!game.getPlayerUsernames().contains(player.getUsername())) {
            chatButton.setVisibility(View.GONE);
        }

        DAOProvider.getDao().getOneTimeGameRequestsForGame(new FirebaseListener() {
            @Override
            public void onSuccess(DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    if (childSnapshot.getValue(GameRequest.class).getSender().split("\\?")[0].equals(player.getUsername())) {
                        requestButton.setText("Otkaži");
                    }
                }
            }

            @Override
            public void onFailure() {
                Toast.makeText(getActivity(), "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
            }
        }, game);

        setupData();

        return view;
    }

    @BindView(R.id.lookingGameLocationField)
    EditText locationField;

    @BindView(R.id.lookingGameStartField)
    EditText startField;

    @BindView(R.id.lookingGameDurationField)
    EditText durationField;

    @BindView(R.id.lookingGamePlayer1Layout)
    LinearLayout player1;

    @BindView(R.id.lookingGamePlayer1InfoField)
    TextView player1InfoField;

    @BindView(R.id.lookingGamePlayer2Layout)
    LinearLayout player2;

    @BindView(R.id.lookingGamePlayer2InfoField)
    TextView player2InfoField;

    @BindView(R.id.lookingGamePlayer3Layout)
    LinearLayout player3;

    @BindView(R.id.lookingGamePlayer3InfoField)
    TextView player3InfoField;

    @BindView(R.id.lookingGamePlayer4Layout)
    LinearLayout player4;

    @BindView(R.id.lookingGamePlayer4InfoField)
    TextView player4InfoField;

    private void setupData() {
        locationField.setText(game.getLocation());

        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.setTime(game.getStart());
        startField.setText(String.format("%d.%d.%d. %d:%02dh",
                cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR),
                cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE)));

        int hours = game.getDurationMinutes() / 60;
        int minutes = game.getDurationMinutes() - 60 * hours;
        durationField.setText(String.format("%d:%02dh", hours, minutes));

        final List<LinearLayout> playerLayouts = Arrays.asList(player1, player2, player3, player4);
        final List<TextView> playerInfoFields = Arrays.asList(player1InfoField, player2InfoField, player3InfoField, player4InfoField);
        for (int i = 0; i < game.getPlayerUsernames().size(); i++) {

            final int index = i;
            DAOProvider.getDao().getPersonForUsername(new FirebaseListener() {
                @Override
                public void onSuccess(DataSnapshot snapshot) {
                    final Player player = snapshot.getValue(Player.class);
                    setPlayerButtonsActions(playerLayouts.get(index), playerInfoFields.get(index), player);
                }

                @Override
                public void onFailure() {
                    Toast.makeText(getActivity(), "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
                }

            }, game.getPlayerUsernames().get(i));
        }
    }

    private void setPlayerButtonsActions(final LinearLayout layout, final TextView playerInfoField, final Player player) {
        layout.setVisibility(View.VISIBLE);
        playerInfoField.setText(String.format("%c.%c.", player.getFirstName().toUpperCase().charAt(0),
                player.getLastName().toUpperCase().charAt(0)));

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity()).setView(R.layout.fragment_player_info)
                        .setNegativeButton("Zatvori", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                AlertDialog dialog = dialogBuilder.show();

                ((ProgressBar) dialog.findViewById(R.id.playerInfoProgressBar)).setProgress((int) (
                        (double) player.getNumberOfWins() / (player.getNumberOfWins() + player.getNumberOfLosses()) * 100
                ));
                ((TextView) dialog.findViewById(R.id.playerInfoWinsField)).setText(String.valueOf(player.getNumberOfWins()));
                ((TextView) dialog.findViewById(R.id.playerInfoLossesField)).setText(String.valueOf(player.getNumberOfLosses()));


                ((EditText) dialog.findViewById(R.id.playerInfoFullNameField)).setText(
                        String.format("%s %s", player.getFirstName(), player.getLastName())
                );

                ((EditText) dialog.findViewById(R.id.playerInfoAgeField)).setText(String.valueOf(player.calculateAge()));
                ((EditText) dialog.findViewById(R.id.playerInfoEmailField)).setText(player.getEmail());
                ((EditText) dialog.findViewById(R.id.playerInfoDescriptionField)).setText(player.getDescription());
            }
        });
    }

    @OnClick(R.id.lookingGameChatButton)
    void goToChat() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().addToBackStack("").replace(R.id.user_main_layout, new GameChatFragment()).commit();
    }

    @OnClick(R.id.lookingGameSendRequestButton)
    void sendRequest() {
        if (requestButton.getText().toString().toLowerCase().equals("otkaži")) {
            processCancelRequest();
        } else {
            processSendRequest();
        }
    }

    private void processSendRequest() {

        final GameRequest request = new GameRequest();
        request.setGameID(game.getGameID());
        request.setSender(String.format("%s?%s %s", player.getUsername(), player.getFirstName(), player.getLastName()));

        DAOProvider.getDao().addGameRequest(new FirebaseListener() {
            @Override
            public void onSuccess(DataSnapshot snapshot) {
                Toast.makeText(getActivity(), "Zahtjev je poslan!", Toast.LENGTH_SHORT).show();
                requestButton.setText("Otkaži");
            }

            @Override
            public void onFailure() {
                Toast.makeText(getActivity(), "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
            }
        }, request);
    }

    private void processCancelRequest(){

        final GameRequest request = new GameRequest();
        request.setGameID(game.getGameID());
        request.setSender(String.format("%s?%s %s", player.getUsername(), player.getFirstName(), player.getLastName()));

        DAOProvider.getDao().getOneTimeGameRequestsForGame(new FirebaseListener() {
            @Override
            public void onSuccess(DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()){
                    if (childSnapshot.getValue(GameRequest.class).getSender().split("\\?")[0].equals(player.getUsername())){

                        GameRequest oldRequest = childSnapshot.getValue(GameRequest.class);
                        oldRequest.setCanceled(true);

                        DAOProvider.getDao().addGameRequest(new FirebaseListener() {
                            @Override
                            public void onSuccess(DataSnapshot snapshot) {
                                game.getPlayerUsernames().remove(player.getUsername());

                                DAOProvider.getDao().addGame(new FirebaseListener() {
                                    @Override
                                    public void onSuccess(DataSnapshot snapshot) {
                                        Toast.makeText(getActivity(), "Zahtjev je otkazan!", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure() {
                                        Toast.makeText(getActivity(), "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
                                    }
                                }, game);
                            }

                            @Override
                            public void onFailure() {
                                Toast.makeText(getActivity(), "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
                            }
                        }, request);

                    }
                }
            }

            @Override
            public void onFailure() {
                Toast.makeText(getActivity(), "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
            }
        }, game);

    }

}
