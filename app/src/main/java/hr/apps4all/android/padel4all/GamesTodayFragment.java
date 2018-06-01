package hr.apps4all.android.padel4all;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import hr.apps4all.android.padel4all.adapaters.GamesArrayAdapter;
import hr.apps4all.android.padel4all.dao.DAOProvider;
import hr.apps4all.android.padel4all.models.FirebaseListener;
import hr.apps4all.android.padel4all.models.Game;
import hr.apps4all.android.padel4all.models.Player;

public class GamesTodayFragment extends Fragment {

    private Player player;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_games_today, container, false);
        ButterKnife.bind(this, view);

        player = (Player) ((MyApplication) getActivity().getApplication()).getActivePerson();
        setupData();

        return view;
    }

    @BindView(R.id.gamesTodayError)
    TextView findError;

    @BindView(R.id.gamesTodayList)
    ListView gameList;

    private void setupData(){
        findError.setVisibility(View.GONE);

        final Calendar cal1 = Calendar.getInstance(TimeZone.getDefault());

        DAOProvider.getDao().getGamesForDate(new FirebaseListener() {
            @Override
            public void onSuccess(DataSnapshot snapshot) {
                List<Game> games = new ArrayList<>();

                for (DataSnapshot childSnapshot : snapshot.getChildren()){
                    Game game = childSnapshot.getValue(Game.class);

                    Calendar cal2 = Calendar.getInstance(TimeZone.getDefault());
                    cal2.setTime(game.getStart());

                    if (!game.isLocked() && !game.isCanceled() && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
                            && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                            && cal2.getTime().compareTo(cal1.getTime()) >= 0){
                        games.add(game);
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
        }, cal1.getTime());
    }


}
