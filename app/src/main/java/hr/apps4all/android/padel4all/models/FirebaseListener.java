package hr.apps4all.android.padel4all.models;

import com.google.firebase.database.DataSnapshot;

/**
 * Created by Bruno on 5.2.2018..
 */

public interface FirebaseListener {

    void onSuccess(DataSnapshot snapshot);

    void onFailure();

}
