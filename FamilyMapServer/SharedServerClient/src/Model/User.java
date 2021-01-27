package Model;

public class User {

    private String userName, password, email, firstName, lastName, gender, personID;

    public User() {}

    /**
     * Creates a user model object.
     *
     * @param UserName Username of the user, not null, unique
     * @param Password Password of the user, not null
     * @param Email Email of the user, not null
     * @param FirstName First name of the user, not null
     * @param LastName Lase name of the user, not null
     * @param Gender Gender of the user, not null, either "m" or "f"
     * @param PersonID Person ID assigned to the user's generated Person object, not null, unique
     */
    public User(String UserName, String Password, String Email,
         String FirstName, String LastName,
         String Gender, String PersonID){

        this.userName = UserName;
        this.password = Password;
        this.email = Email;
        this.firstName = FirstName;
        this.lastName = LastName;
        this.gender = Gender;
        this.personID = PersonID;
    }

    public User(String userName, String password,
                String email, String firstName,
                String lastName, String gender) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

}
