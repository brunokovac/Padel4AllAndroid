package hr.apps4all.android.padel4all;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
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
import butterknife.OnClick;
import hr.apps4all.android.padel4all.adapaters.GamesArrayAdapter;
import hr.apps4all.android.padel4all.dao.DAOProvider;
import hr.apps4all.android.padel4all.models.FirebaseListener;
import hr.apps4all.android.padel4all.models.Game;
import hr.apps4all.android.padel4all.models.Player;

public class NewMessagesGamesFragment extends Fragment {

    private Player player;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_messages_games, container, false);
        ButterKnife.bind(this, view);

        setupData();

        return view;
    }

    @BindView(R.id.newMessagesGamesError)
    TextView findError;

    @BindView(R.id.newMessagesGamesList)
    ListView gameList;

    private void setupData(){
        findError.setVisibility(View.GONE);

        List<Game> games = ((MyApplication) getActivity().getApplication()).getNewMessagesGames();

        if (games.isEmpty()){
            findError.setVisibility(View.VISIBLE);
        }else{
            findError.setVisibility(View.GONE);
        }

        ArrayAdapter<Game> adapter = new GamesArrayAdapter(getActivity(), games);
        gameList.setAdapter(adapter);
    }

}
