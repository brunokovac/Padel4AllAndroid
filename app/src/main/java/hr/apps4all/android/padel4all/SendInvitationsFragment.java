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
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
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
import hr.apps4all.android.padel4all.adapaters.SendInvitationPlayersArrayAdapter;
import hr.apps4all.android.padel4all.dao.DAO;
import hr.apps4all.android.padel4all.dao.DAOProvider;
import hr.apps4all.android.padel4all.models.FirebaseListener;
import hr.apps4all.android.padel4all.models.Game;
import hr.apps4all.android.padel4all.models.Person;
import hr.apps4all.android.padel4all.models.PersonType;
import hr.apps4all.android.padel4all.models.Player;

public class SendInvitationsFragment extends Fragment {

    private Player player;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_send_invitations, container, false);
        ButterKnife.bind(this, view);

        player = (Player) ((MyApplication) getActivity().getApplication()).getActivePerson();
        setupSearch();

        return view;
    }

    @BindView(R.id.sendInvitationSearchNameField)
    EditText nameField;

    @BindView(R.id.sendInvitationsError)
    TextView findError;

    @BindView(R.id.sendInvitationsList)
    ListView playersList;

    private List<Player> emptyList = new ArrayList<>();

    private void setupSearch(){
        nameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUser(s.toString().toUpperCase().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {
                searchUser(s.toString().toUpperCase().trim());
            }
        });
    }

    private void searchUser(final String entry){
        if (entry.isEmpty()){
            playersList.setAdapter(new SendInvitationPlayersArrayAdapter(getActivity(), emptyList));
            return;
        }

        DAOProvider.getDao().getPersonsForFullNameSearch(new FirebaseListener() {
            @Override
            public void onSuccess(DataSnapshot snapshot) {
                if (entry.isEmpty()){
                    return;
                }

                List<Player> players = new ArrayList<>();

                for (DataSnapshot child : snapshot.getChildren()){
                    Person person = child.getValue(Person.class);
                    if (person.getType() == PersonType.PLAYER && !person.getUsername().equals(player.getUsername())
                            && person.getFullName().startsWith(entry)){
                        players.add(child.getValue(Player.class));
                    }
                }

                if (players.isEmpty()){
                    findError.setVisibility(View.VISIBLE);
                }else{
                    findError.setVisibility(View.GONE);
                }

                ArrayAdapter<Player> adapter = new SendInvitationPlayersArrayAdapter(getActivity(), players);
                playersList.setAdapter(adapter);
            }

            @Override
            public void onFailure() {
                Toast.makeText(getActivity(), "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
            }
        }, entry);
    }

}
