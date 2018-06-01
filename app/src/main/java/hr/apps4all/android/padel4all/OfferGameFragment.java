package hr.apps4all.android.padel4all;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hr.apps4all.android.padel4all.dao.DAOProvider;
import hr.apps4all.android.padel4all.models.FirebaseListener;
import hr.apps4all.android.padel4all.models.Game;
import hr.apps4all.android.padel4all.models.Player;

public class OfferGameFragment extends Fragment {

    private Player player;
    private Game game = new Game();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offer_game, container, false);
        ButterKnife.bind(this, view);

        player = (Player) ((MyApplication) getActivity().getApplication()).getActivePerson();
        showDuration();
        locationField.requestFocus();

        return view;
    }

    @BindView(R.id.offerGameLocationField)
    EditText locationField;

    @BindView(R.id.offerGameStartField)
    EditText startField;

    @OnClick(R.id.offerGameStartButton)
    void chooseStart(){
        final Calendar cal = Calendar.getInstance(TimeZone.getDefault());

        DatePickerDialog datePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, monthOfYear);
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                TimePickerDialog timePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        cal.set(Calendar.MINUTE, minute);

                        startField.setText(String.format("%d.%d.%d. %d:%02dh",
                                cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR),
                                cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE)));

                        game.setStart(cal.getTime());
                    }
                }, 14, 5, true);

                timePicker.setTitle("Vrijeme početka");
                timePicker.show();
            }
        },
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));

        datePicker.getDatePicker().setMinDate(System.currentTimeMillis());
        datePicker.setTitle("Datum početka");

        datePicker.show();
    }

    @OnClick(R.id.offerGameDurationUpButton)
    void plusDuration(){
        game.addHalfAnHourToDuration();
        showDuration();
    }

    @OnClick(R.id.offerGameDurationDownButton)
    void minusDuration(){
        game.removeHalfAnHourFromDuration();
        showDuration();
    }

    @BindView(R.id.offerGameDurationField)
    EditText durationField;

    private void showDuration(){
        int hours = game.getDurationMinutes() / 60;
        int minutes = game.getDurationMinutes() - 60*hours;

        durationField.setText(String.format("%d:%02dh", hours, minutes));
    }

    @BindView(R.id.offerGameDateError)
    TextView dateError;

    private boolean isDataCorrect(){
        boolean correct = true;

        final Drawable d = getResources().getDrawable(R.drawable.error_icon);
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());

        String location = locationField.getText().toString().trim();
        if (location.isEmpty()){
            locationField.setError("Lokacija mora biti upisana!", d);
            correct = false;
        }

        Date start = game.getStart();
        if (start == null){
            dateError.setText("Datum i vrijeme početka moraju biti upisani!");
            dateError.setVisibility(View.VISIBLE);
            correct = false;
        }else{
            Calendar cal = Calendar.getInstance(TimeZone.getDefault());
            if (cal.getTime().compareTo(game.getStart()) >= 0){
                dateError.setText("Datum i vrijeme početka moraju biti kasniji od trenutnog vremena!");
                dateError.setVisibility(View.VISIBLE);
                correct = false;
            }
        }

        return correct;
    }

    @OnClick(R.id.offerGameOfferButton)
    void offerGame(){
        if (isDataCorrect()){
            game.setLocation(locationField.getText().toString().trim().toUpperCase());
            game.setOrganizerUsername(player.getUsername());
            game.addPlayer(player);

            DAOProvider.getDao().addGame(new FirebaseListener() {
                @Override
                public void onSuccess(DataSnapshot snapshot) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().addToBackStack("").replace(R.id.user_main_layout, new OfferGameFragment()).commit();

                    Toast.makeText(getActivity(), "Termin je uspješno ponuđen!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure() {
                    Toast.makeText(getActivity(), "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
                }
            }, game);

            player.addGame(game);
            DAOProvider.getDao().addPerson(new FirebaseListener() {
                @Override
                public void onSuccess(DataSnapshot snapshot) {
                }

                @Override
                public void onFailure() {
                    Toast.makeText(getActivity(), "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
                }
            }, player);
        }
    }

}
