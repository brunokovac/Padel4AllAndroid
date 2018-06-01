package hr.apps4all.android.padel4all;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
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
import hr.apps4all.android.padel4all.dao.DAOProvider;
import hr.apps4all.android.padel4all.dao.FirebaseReferenceProvider;
import hr.apps4all.android.padel4all.models.FirebaseListener;
import hr.apps4all.android.padel4all.models.Game;
import hr.apps4all.android.padel4all.models.Player;

public class FindGameFragment extends Fragment {

    private Player player;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find_game, container, false);
        ButterKnife.bind(this, view);

        player = (Player) ((MyApplication) getActivity().getApplication()).getActivePerson();
        datePicker.setMinDate(System.currentTimeMillis());

        return view;
    }

    @BindView(R.id.findGameLocationField)
    EditText locationField;

    @BindView(R.id.findGameDatePicker)
    DatePicker datePicker;

    @BindView(R.id.findGameError)
    TextView findError;

    @BindView(R.id.findGameList)
    ListView gameList;

    @OnClick(R.id.findGameFindButton)
    void findGames(){
        final String location = locationField.getText().toString().trim().toUpperCase();

        findError.setVisibility(View.GONE);

        Calendar cal = Calendar.getInstance(TimeZone.getDefault());

        cal.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
        cal.set(Calendar.MONTH, datePicker.getMonth());
        cal.set(Calendar.YEAR, datePicker.getYear());

        DAOProvider.getDao().getGamesForDate(new FirebaseListener() {
            @Override
            public void onSuccess(DataSnapshot snapshot) {
                List<Game> games = new ArrayList<>();

                for (DataSnapshot childSnapshot : snapshot.getChildren()){
                    Game game = childSnapshot.getValue(Game.class);

                    if (game.getStart().compareTo(Calendar.getInstance(TimeZone.getDefault()).getTime()) < 0){
                        continue;
                    }

                    if (!game.getPlayerUsernames().contains(player.getUsername())){
                        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
                        cal.setTime(game.getStart());

                        if (!game.isLocked() && !game.isCanceled() && cal.get(Calendar.DAY_OF_MONTH) == datePicker.getDayOfMonth()
                                && cal.get(Calendar.MONTH) == datePicker.getMonth() && cal.get(Calendar.YEAR) == datePicker.getYear()){
                            if (location.isEmpty()){
                                games.add(game);
                            }else{
                                if (game.getLocation().startsWith(location)){
                                    games.add(game);
                                }

                            }
                        }
                    }
                }

                if (games.isEmpty()){
                    findError.setVisibility(View.VISIBLE);
                }else{
                    findError.setVisibility(View.GONE);
                }

                ArrayAdapter<Game> adapter = new GamesArrayAdapter(getActivity(), games);
                gameList.setAdapter(adapter);
            }

            @Override
            public void onFailure() {
                Toast.makeText(getActivity(), "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
            }
        }, cal.getTime());
    }


}
