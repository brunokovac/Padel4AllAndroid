package hr.apps4all.android.padel4all;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hr.apps4all.android.padel4all.dao.DAOProvider;
import hr.apps4all.android.padel4all.dao.FirebaseReferenceProvider;
import hr.apps4all.android.padel4all.models.Admin;
import hr.apps4all.android.padel4all.models.Encrypter;
import hr.apps4all.android.padel4all.models.FirebaseListener;
import hr.apps4all.android.padel4all.models.Person;
import hr.apps4all.android.padel4all.models.Player;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);

        usernameField.requestFocus();
    }

    @BindView(R.id.registerUsernameField)
    EditText usernameField;

    @BindView(R.id.registerPasswordField)
    EditText passwordField;

    @BindView(R.id.registerFirstNameField)
    EditText firstNameField;

    @BindView(R.id.registerLastNameField)
    EditText lastNameField;

    @BindView(R.id.registerDatePicker)
    DatePicker datePicker;

    @BindView(R.id.registerEmailField)
    EditText emailField;

    @BindView(R.id.registerDescriptionField)
    EditText descriptionField;

    @OnClick(R.id.registerButton)
    void processRegistration(){
        if (isDataCorrect()){
            Player player = processData();

            DAOProvider.getDao().addPerson(new FirebaseListener() {
                @Override
                public void onSuccess(DataSnapshot snapshot) {
                    Toast.makeText(RegisterActivity.this, "Registracija je uspješno provedena!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                }

                @Override
                public void onFailure() {
                    Toast.makeText(RegisterActivity.this, "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
                }
            }, player);

        }
    }

    private boolean isDataCorrect(){
        final AtomicBoolean correct = new AtomicBoolean(true);

        final Drawable d = getResources().getDrawable(R.drawable.error_icon);
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());

        String password = passwordField.getText().toString().trim();
        if (password.length() < 5) {
            passwordField.setError("Lozinka mora imati barem 5 znakova!", d);
            correct.set(false);
        }

        String firstName = firstNameField.getText().toString().trim();
        if (firstName.isEmpty()) {
            firstNameField.setError("Ime mora biti upisano!", d);
            correct.set(false);
        }

        String lastName = lastNameField.getText().toString().trim();
        if (lastName.isEmpty()) {
            lastNameField.setError("Prezime mora biti upisano!", d);
            correct.set(false);
        }

        String email = emailField.getText().toString().trim();
        if (email.isEmpty()){
            emailField.setError("E-mail adresa mora biti upisana!", d);
            correct.set(false);
        }else if (!email.contains("@")){
            emailField.setError("Neispravan format e-mail adrese!", d);
            correct.set(false);
        }

        final String username = usernameField.getText().toString().trim();
        if (username.length() < 5) {
            usernameField.setError("Korisničko ime mora imati barem 5 znakova!", d);
            correct.set(false);
        } else {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    DAOProvider.getDao().getPersonForUsername(new FirebaseListener() {
                        @Override
                        public void onSuccess(DataSnapshot snapshot) {
                            Person person = snapshot.getValue(Person.class);

                            if (person != null) {
                                usernameField.setError("Korisničko ime već postoji!", d);
                                correct.set(false);
                            }
                        }

                        @Override
                        public void onFailure() {
                            usernameField.setError("Korisničko ime već postoji!", d);
                            correct.set(false);
                        }
                    }, username);
                }
            };

            Thread t = new Thread(r);
            t.start();
            try {
                t.join();
            } catch (InterruptedException e) {
                Toast.makeText(this, "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
            }
        }

        return correct.get();
    }

    private Player processData(){
        String username = usernameField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        String firstName = firstNameField.getText().toString().trim();
        String lastName = lastNameField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        String description = descriptionField.getText().toString().trim();

        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());

        return new Player(username, password, firstName, lastName, cal.getTime(), email, description);
    }

}
