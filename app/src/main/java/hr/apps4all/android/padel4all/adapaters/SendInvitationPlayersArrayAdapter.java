package hr.apps4all.android.padel4all.adapaters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import hr.apps4all.android.padel4all.LookingGameFragment;
import hr.apps4all.android.padel4all.MyApplication;
import hr.apps4all.android.padel4all.OrganizerGameFragment;
import hr.apps4all.android.padel4all.R;
import hr.apps4all.android.padel4all.models.Game;
import hr.apps4all.android.padel4all.models.Person;
import hr.apps4all.android.padel4all.models.Player;

/**
 * Created by Bruno on 4.1.2018..
 */

public class SendInvitationPlayersArrayAdapter extends ArrayAdapter<Player> {

    private Context context;
    private List<Player> players;

    public SendInvitationPlayersArrayAdapter(Context context, List<Player> players){
        super(context, -1, players);
        this.context = context;
        this.players = players;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable final View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.send_invitation_player_item, parent, false);

        final Player player = players.get(position);

        EditText nameField = (EditText) rowView.findViewById(R.id.sendInvitationNameField);

        nameField.setText(player.getFullName());
        nameField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrganizerGameFragment.createPlayerDialog((AppCompatActivity) context, player);
            }
        });

        return rowView;
    }
}
