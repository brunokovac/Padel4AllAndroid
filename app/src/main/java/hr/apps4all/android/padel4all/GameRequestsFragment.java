package hr.apps4all.android.padel4all;

import android.app.DownloadManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
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
import hr.apps4all.android.padel4all.adapaters.GameRequestsArrayAdapter;
import hr.apps4all.android.padel4all.adapaters.GamesArrayAdapter;
import hr.apps4all.android.padel4all.dao.DAOProvider;
import hr.apps4all.android.padel4all.models.FirebaseListener;
import hr.apps4all.android.padel4all.models.Game;
import hr.apps4all.android.padel4all.models.GameRequest;
import hr.apps4all.android.padel4all.models.Player;

public class GameRequestsFragment extends Fragment {

    private Player player;
    private Game game;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_requests, container, false);
        ButterKnife.bind(this, view);

        player = (Player) ((MyApplication) getActivity().getApplication()).getActivePerson();
        game = ((MyApplication) getActivity().getApplication()).getGame();
        setupData();

        return view;
    }

    @BindView(R.id.gameRequestsError)
    TextView findError;

    @BindView(R.id.gameRequestsList)
    ListView requestsList;

    private void setupData(){
        findError.setVisibility(View.GONE);

        final Calendar cal1 = Calendar.getInstance(TimeZone.getDefault());

        DAOProvider.getDao().getGameRequestsForGame(new FirebaseListener() {
            @Override
            public void onSuccess(DataSnapshot snapshot) {
                List<GameRequest> requests = new ArrayList<>();

                for (DataSnapshot childSnapshot : snapshot.getChildren()){
                    GameRequest request = childSnapshot.getValue(GameRequest.class);

                    if (!request.isChecked() && !request.isCanceled()) {
                        requests.add(childSnapshot.getValue(GameRequest.class));
                    }
                }

                if (requests.isEmpty()){
                    findError.setVisibility(View.VISIBLE);
                }else{
                    findError.setVisibility(View.GONE);
                }

                ArrayAdapter<GameRequest> adapter = new GameRequestsArrayAdapter(getActivity(), requests);
                requestsList.setAdapter(adapter);
            }

            @Override
            public void onFailure() {
                Toast.makeText(getActivity(), "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
            }
        }, game);
    }

}
