package hr.apps4all.android.padel4all.models;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Bruno on 2.2.2018..
 */

public class Person {

    protected PersonType type;

    protected String username;
    protected String password;

    protected String firstName;
    protected String lastName;
    protected Date dateOfBirth;

    protected Date registrationDate;

    protected String email;
    protected String description;

    public Person(){
    }

    public Person(PersonType type, String username, String password, String firstName, String lastName, Date dateOfBirth,
                  Date registrationDate, String email, String description) {
        this.type = type;
        this.username = username;
        this.password = Encrypter.encrypt(password);
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.registrationDate = registrationDate;
        this.email = email;
        this.description = description;
    }

    public boolean changePassword(String oldPassword, String newPassword){
        if (!Encrypter.encrypt(oldPassword).equals(this.password)){
            return false;
        }

        this.password = Encrypter.encrypt(newPassword);
        return true;
    }

    public void changeEmail(String email){
        this.email = email;
    }

    public void changeDescription(String description){
        this.description = description;
    }

    public int calculateAge(){
        Calendar currentDate = Calendar.getInstance(TimeZone.getDefault());
        Calendar dateOfBirth = Calendar.getInstance(TimeZone.getDefault());
        dateOfBirth.setTime(this.dateOfBirth);

        int diff = currentDate.get(Calendar.YEAR) - dateOfBirth.get(Calendar.YEAR);
        if (dateOfBirth.get(Calendar.MONTH) > currentDate.get(Calendar.MONTH) ||
                (dateOfBirth.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH) &&
                        dateOfBirth.get(Calendar.DATE) > currentDate.get(Calendar.DATE))) {
            diff--;
        }

        return diff;
    }

    public String getFullName(){
        return String.format("%s %s", firstName.toUpperCase(), lastName.toUpperCase());
    }

    public PersonType getType() {
        return type;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public String getEmail() {
        return email;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        return username != null ? username.equals(person.username) : person.username == null;
    }

    @Override
    public int hashCode() {
        return username != null ? username.hashCode() : 0;
    }

}
