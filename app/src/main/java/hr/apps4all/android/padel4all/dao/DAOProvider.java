package hr.apps4all.android.padel4all.dao;

public class DAOProvider {

    private static DAO dao = new FirebaseDAO();

    public static DAO getDao() {
        return dao;
    }

}