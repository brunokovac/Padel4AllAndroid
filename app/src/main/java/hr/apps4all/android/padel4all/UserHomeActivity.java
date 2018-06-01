package hr.apps4all.android.padel4all;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ThrowOnExtraProperties;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hr.apps4all.android.padel4all.dao.DAOProvider;
import hr.apps4all.android.padel4all.dao.FirebaseReferenceProvider;
import hr.apps4all.android.padel4all.models.Chat;
import hr.apps4all.android.padel4all.models.FirebaseListener;
import hr.apps4all.android.padel4all.models.Game;
import hr.apps4all.android.padel4all.models.GameRequest;
import hr.apps4all.android.padel4all.models.Message;
import hr.apps4all.android.padel4all.models.Player;

public class UserHomeActivity extends AppCompatActivity {

    private Player player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        player = (Player) ((MyApplication) getApplication()).getActivePerson();
        ButterKnife.bind(this);
        setupDrawerContent(navigation);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.user_main_layout, new GamesTodayFragment()).commit();

        checkMessages();
    }

    int count = 0;

    private void checkMessages(){
        for (String gameID : player.getGameIDs()){
            DAOProvider.getDao().getGameForGameID(new FirebaseListener() {
                @Override
                public void onSuccess(DataSnapshot snapshot) {
                    Game game = snapshot.getValue(Game.class);

                    if (game == null){
                        return;
                    }

                    ((MyApplication) getApplication()).addMyGame(game);

                    Game activeGame = ((MyApplication) getApplication()).getGame();
                    if (activeGame != null && activeGame.equals(game)
                            && ((MyApplication) getApplication()).getFragment() instanceof GameChatFragment){
                        return;
                    }

                    if (!game.getLastSeenByUsernames().contains(player.getUsername())){
                        if (!game.getLastSeenByUsernames().isEmpty()) {

                            if(((MyApplication) getApplication()).addNewMessageGame(game)) {
                                count++;
                                navigation.getMenu().findItem(R.id.newMessagesItem).setTitle(String.format("Nepročitane poruke (%d)", count));
                                Toast.makeText(UserHomeActivity.this, "Nova poruka!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }else{
                        if (((MyApplication) getApplication()).removeNewMessageGame(game)) {
                            count--;
                            navigation.getMenu().findItem(R.id.newMessagesItem).setTitle(String.format("Nepročitane poruke (%d)", count));
                        }
                    }
                }

                @Override
                public void onFailure() {
                    Toast.makeText(UserHomeActivity.this, "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
                }
            }, gameID);
        }
    }

    @OnClick(R.id.userMyProfileButton)
    void goToEditProfileScreen(){
        ((MyApplication) getApplication()).setFragment(null);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().addToBackStack("").replace(R.id.user_main_layout, new EditProfileFragment()).commit();
    }

    @OnClick(R.id.userLogoutButton)
    void logout(){
        ((MyApplication) getApplication()).setFragment(null);
        startActivity(new Intent(UserHomeActivity.this, LoginActivity.class));
    }

    @BindView(R.id.userNavigation)
    NavigationView navigation;

    @BindView(R.id.userDrawer)
    DrawerLayout drawerLayout;

    @OnClick(R.id.userMenuButton)
    void showMenu(){
        drawerLayout.openDrawer(Gravity.START);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        Fragment fragment = null;

        switch(menuItem.getItemId()) {
            case R.id.todayGamesItem:
                fragment = new GamesTodayFragment();
                break;
            case R.id.offerGameItem:
                fragment = new OfferGameFragment();
                break;
            case R.id.findGameItem:
                fragment = new FindGameFragment();
                break;
            case R.id.myGamesItem:
                fragment = new MyGamesFragment();
                break;
            case R.id.cancellationsItem:
                // TODO
                break;
            case R.id.newMessagesItem:
                fragment = new NewMessagesGamesFragment();
                break;
            default:
                break;
        }

        if (fragment != null){
            ((MyApplication) getApplication()).setFragment(null);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().addToBackStack("").replace(R.id.user_main_layout, fragment).commit();
        }

        drawerLayout.closeDrawers();
    }

    @Override
    public void onBackPressed() {
        ((MyApplication) getApplication()).setFragment(null);
        if (getSupportFragmentManager().getBackStackEntryCount() > 0){
            getSupportFragmentManager().popBackStack();
        }
    }

}
