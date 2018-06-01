package hr.apps4all.android.padel4all.adapaters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import hr.apps4all.android.padel4all.LookingGameFragment;
import hr.apps4all.android.padel4all.MyApplication;
import hr.apps4all.android.padel4all.OrganizerGameFragment;
import hr.apps4all.android.padel4all.R;
import hr.apps4all.android.padel4all.dao.DAO;
import hr.apps4all.android.padel4all.dao.DAOProvider;
import hr.apps4all.android.padel4all.models.FirebaseListener;
import hr.apps4all.android.padel4all.models.Game;
import hr.apps4all.android.padel4all.models.GameRequest;
import hr.apps4all.android.padel4all.models.Player;

/**
 * Created by Bruno on 4.1.2018..
 */

public class GameRequestsArrayAdapter extends ArrayAdapter<GameRequest> {

    private Context context;
    private List<GameRequest> requests;

    public GameRequestsArrayAdapter(Context context, List<GameRequest> requests){
        super(context, -1, requests);
        this.context = context;
        this.requests = requests;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable final View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.game_request_item, parent, false);

        final GameRequest request = requests.get(position);

        EditText nameField = (EditText) rowView.findViewById(R.id.gameRequestItemNameField);
        EditText locationField = (EditText) rowView.findViewById(R.id.gameRequestItemLocationField);
        EditText startField = (EditText) rowView.findViewById(R.id.gameRequestItemStartField);

        final Game game = ((MyApplication) (((AppCompatActivity)context).getApplication())).getGame();

        nameField.setText(request.getSender().split("\\?")[1]);
        locationField.setText(game.getLocation());

        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.setTime(game.getStart());
        startField.setText(String.format("%d.%d.%d. %d:%02dh",
                cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR),
                cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE)));

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DAOProvider.getDao().getPersonForUsername(new FirebaseListener() {
                    @Override
                    public void onSuccess(DataSnapshot snapshot) {
                        OrganizerGameFragment.createPlayerDialog((Activity) context, snapshot.getValue(Player.class));
                    }

                    @Override
                    public void onFailure() {

                    }
                }, request.getSender().split("\\?")[0]);
            }
        };

        locationField.setOnClickListener(listener);
        startField.setOnClickListener(listener);
        rowView.setOnClickListener(listener);

        ((Button) rowView.findViewById(R.id.gameRequestItemDeclineButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkDeclineRequest(request);
            }
        });

        ((Button) rowView.findViewById(R.id.gameRequestItemAcceptButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request.accept();
                DAOProvider.getDao().addGameRequest(new FirebaseListener() {
                    @Override
                    public void onSuccess(DataSnapshot snapshot) {
                        game.addPlayer(request.getSender().split("\\?")[0]);

                        DAOProvider.getDao().addGame(new FirebaseListener() {
                            @Override
                            public void onSuccess(DataSnapshot snapshot) {
                                Toast.makeText(((AppCompatActivity) context), "Zahtjev je prihvaćen!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure() {
                                Toast.makeText(((AppCompatActivity) context), "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
                            }
                        }, game);
                    }

                    @Override
                    public void onFailure() {
                        Toast.makeText(((AppCompatActivity) context), "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
                    }
                }, request);

                DAOProvider.getDao().getPersonForUsername(new FirebaseListener() {
                    @Override
                    public void onSuccess(DataSnapshot snapshot) {
                        Player player = snapshot.getValue(Player.class);
                        player.addGame(game);

                        DAOProvider.getDao().addPerson(new FirebaseListener() {
                            @Override
                            public void onSuccess(DataSnapshot snapshot) {
                            }

                            @Override
                            public void onFailure() {
                                Toast.makeText(((AppCompatActivity) context), "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
                            }
                        }, player);
                    }

                    @Override
                    public void onFailure() {
                        Toast.makeText(((AppCompatActivity) context), "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
                    }
                }, request.getSender().split("\\?")[0]);
            }
        });

        return rowView;
    }

    private void checkDeclineRequest(final GameRequest request){

        AlertDialog.Builder alertDialogBuilder =
                new AlertDialog.Builder(context)
                        .setTitle("Odbijanje zahtjeva")
                        .setMessage(String.format("%n%s", "Jeste li sigurni da želite odbiti ovaj zahtjev?"))
                        .setPositiveButton("ODBIJ", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                request.decline();
                                DAOProvider.getDao().addGameRequest(new FirebaseListener() {
                                    @Override
                                    public void onSuccess(DataSnapshot snapshot) {
                                        Toast.makeText(((AppCompatActivity) context), "Zahtjev je odbijen!", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure() {
                                        Toast.makeText(((AppCompatActivity) context), "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
                                    }
                                }, request);

                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("Odustani", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

        alertDialogBuilder.show();

    }
}
