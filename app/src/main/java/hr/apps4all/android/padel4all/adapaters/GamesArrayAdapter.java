package hr.apps4all.android.padel4all.adapaters;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import hr.apps4all.android.padel4all.LookingGameFragment;
import hr.apps4all.android.padel4all.MyApplication;
import hr.apps4all.android.padel4all.OrganizerGameFragment;
import hr.apps4all.android.padel4all.R;
import hr.apps4all.android.padel4all.models.Game;
import hr.apps4all.android.padel4all.models.Player;

/**
 * Created by Bruno on 4.1.2018..
 */

public class GamesArrayAdapter extends ArrayAdapter<Game> {

    private Context context;
    private List<Game> games;

    public GamesArrayAdapter(Context context, List<Game> games){
        super(context, -1, games);
        this.context = context;
        this.games = games;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable final View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.find_game_item, parent, false);

        final Game game = games.get(position);

        EditText locationField = (EditText) rowView.findViewById(R.id.findGameItemLocationField);
        EditText startField = (EditText) rowView.findViewById(R.id.findGameItemStartField);

        locationField.setText(game.getLocation());

        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.setTime(game.getStart());
        startField.setText(String.format("%d.%d.%d. %d:%02dh",
                cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR),
                cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE)));

        final Player player = (Player) ((MyApplication) (((AppCompatActivity)context).getApplication())).getActivePerson();

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MyApplication) (((AppCompatActivity)context).getApplication())).setGame(game);
                FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();

                if (game.getOrganizerUsername().equals(player.getUsername())){
                    fragmentManager.beginTransaction().addToBackStack("").
                            replace(R.id.user_main_layout, new OrganizerGameFragment()).commit();
                }else{
                    fragmentManager.beginTransaction().addToBackStack("").
                            replace(R.id.user_main_layout, new LookingGameFragment()).commit();
                }
            }
        };

        locationField.setOnClickListener(listener);
        startField.setOnClickListener(listener);
        rowView.setOnClickListener(listener);

        return rowView;
    }
}
