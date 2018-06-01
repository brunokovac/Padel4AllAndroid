package hr.apps4all.android.padel4all;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hr.apps4all.android.padel4all.dao.DAO;
import hr.apps4all.android.padel4all.dao.DAOProvider;
import hr.apps4all.android.padel4all.dao.FirebaseReferenceProvider;
import hr.apps4all.android.padel4all.models.Admin;
import hr.apps4all.android.padel4all.models.Encrypter;
import hr.apps4all.android.padel4all.models.FirebaseListener;
import hr.apps4all.android.padel4all.models.Game;
import hr.apps4all.android.padel4all.models.Person;
import hr.apps4all.android.padel4all.models.Player;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseReferenceProvider.setReference(FirebaseDatabase.getInstance().getReference());

        ButterKnife.bind(this);

        MyApplication application = (MyApplication) getApplication();
        application.setFragment(null);
        application.setGame(null);
        application.setActivePerson(null);
        application.setMyGames(new ArrayList<Game>());
        application.setNewMessagesGames(new ArrayList<Game>());
    }

    @BindView(R.id.loginUsernameField)
    EditText usernameField;

    @BindView(R.id.loginPasswordField)
    EditText passwordField;

    @OnClick(R.id.loginButton)
    void processLogin(){
        final String username = usernameField.getText().toString().trim();
        final String password = Encrypter.encrypt(passwordField.getText().toString().trim());

        if (username.isEmpty() || password.isEmpty()){
            return;
        }

        final MyApplication application = (MyApplication) getApplication();

        DAOProvider.getDao().getPersonForUsername(new FirebaseListener() {
            @Override
            public void onSuccess(DataSnapshot snapshot) {
                final Person person = snapshot.getValue(Person.class);

                if (person == null || !person.getPassword().equals(password)){
                    Toast.makeText(LoginActivity.this,
                            "Nepostojeća kombinacija korisničkog imena i lozinke!", Toast.LENGTH_SHORT).show();
                }else{

                    DAOProvider.getDao().getActivePersonForUsername(new FirebaseListener() {
                        @Override
                        public void onSuccess(DataSnapshot snapshot) {
                            Class next = null;

                            switch (person.getType()){
                                case ADMIN:
                                    application.setActivePerson(snapshot.getValue(Admin.class));
                                    next = AdminHomeActivity.class;
                                    break;
                                case PLAYER:
                                    application.setActivePerson(snapshot.getValue(Player.class));
                                    next = UserHomeActivity.class;
                                    break;
                            }

                            startActivity(new Intent(LoginActivity.this, next));
                        }

                        @Override
                        public void onFailure() {
                            Toast.makeText(LoginActivity.this, "Došlo je do pogreške...",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }, username);

                }
            }

            @Override
            public void onFailure() {
                Toast.makeText(LoginActivity.this, "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
            }
        }, username);

    }

    @OnClick(R.id.registerLink)
    void processRegistration(){
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }

    @Override
    public void onBackPressed() {
    }
}
