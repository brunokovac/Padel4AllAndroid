package hr.apps4all.android.padel4all.models;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Bruno on 2.2.2018..
 */

public class Admin extends Person {

    public Admin(){
    }

    public Admin(String username, String password, String firstName, String lastName, Date dateOfBirth,
                    String email, String description) {
        super(PersonType.ADMIN, username, password, firstName, lastName, dateOfBirth,
                Calendar.getInstance(TimeZone.getDefault()).getTime(), email, description);
    }

}
