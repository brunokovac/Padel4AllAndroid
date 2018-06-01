package hr.apps4all.android.padel4all;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hr.apps4all.android.padel4all.dao.DAOProvider;
import hr.apps4all.android.padel4all.models.FirebaseListener;
import hr.apps4all.android.padel4all.models.Person;
import hr.apps4all.android.padel4all.models.Player;

public class EditProfileFragment extends Fragment {

    private Player player;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        ButterKnife.bind(this, view);

        player = (Player) ((MyApplication) getActivity().getApplication()).getActivePerson();
        setupData();

        usernameField.requestFocus();

        return view;
    }

    @BindView(R.id.editProfileUsernameField)
    EditText usernameField;

    @BindView(R.id.editProfileOldPasswordField)
    EditText oldPasswordField;

    @BindView(R.id.editProfileNewPasswordField)
    EditText newPasswordField;

    @BindView(R.id.editProfileFirstNameField)
    EditText firstNameField;

    @BindView(R.id.editProfileLastNameField)
    EditText lastNameField;

    @BindView(R.id.editProfileDateField)
    EditText dateField;

    @BindView(R.id.editProfileRegistrationDateField)
    EditText registrationDateField;

    @BindView(R.id.editProfileEmailField)
    EditText emailField;

    @BindView(R.id.editProfileDescriptionField)
    EditText descriptionField;

    private void setupData(){
        usernameField.setText(player.getUsername());
        firstNameField.setText(player.getFirstName());
        lastNameField.setText(player.getLastName());

        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.setTime(player.getDateOfBirth());
        dateField.setText(String.format("%d.%d.%d.", cal.get(Calendar.DAY_OF_MONTH),
                cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR)));

        cal.setTime(player.getRegistrationDate());
        registrationDateField.setText(String.format("%d.%d.%d.", cal.get(Calendar.DAY_OF_MONTH),
                cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR)));

        emailField.setText(player.getEmail());
        descriptionField.setText(player.getDescription());
    }

    @OnClick(R.id.editProfileSaveChangesButton)
    void saveChanges(){
        if (isDataCorrect()){
            player.changeEmail(emailField.getText().toString().trim());
            player.changeDescription(descriptionField.getText().toString().trim());

            DAOProvider.getDao().addPerson(new FirebaseListener() {
                @Override
                public void onSuccess(DataSnapshot snapshot) {
                    ((MyApplication) getActivity().getApplication()).setActivePerson(player);
                    Toast.makeText(getActivity(), "Promjene su uspješno spremljene!", Toast.LENGTH_SHORT).show();
                    getActivity().onBackPressed();
                }

                @Override
                public void onFailure() {
                    Toast.makeText(getActivity(), "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
                }
            }, player);
        }
    }

    private boolean isDataCorrect(){
        boolean correct = true;

        final Drawable d = getResources().getDrawable(R.drawable.error_icon);
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());

        String oldPassword = oldPasswordField.getText().toString().trim();
        String newPassword = newPasswordField.getText().toString().trim();
        if (!newPassword.isEmpty()){
            if (newPassword.length() < 5){
                newPasswordField.setError("Lozinka mora imati barem 5 znakova!", d);
                correct = false;
            }else if (!player.changePassword(oldPassword, newPassword)){
                oldPasswordField.setError("Stara lozinka nije ispravna!", d);
                correct = false;
            }
        }

        String email = emailField.getText().toString().trim();
        if (email.isEmpty()){
            emailField.setError("E-mail adresa mora biti upisana!", d);
            correct = false;
        }else if (!email.contains("@")){
            emailField.setError("Neispravan format e-mail adrese!", d);
            correct = false;
        }

        return correct;
    }

}
