package hr.apps4all.android.padel4all.dao;

import com.google.firebase.database.DatabaseReference;

public class FirebaseReferenceProvider {

    private static DatabaseReference reference;

    public static void setReference(DatabaseReference reference) {
        FirebaseReferenceProvider.reference = reference;
    }

    public static DatabaseReference getReference() {
        return reference;
    }
}