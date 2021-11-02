package com.cmput301f21t34.habittrak.user;

/**
 * Database_Pointer
 *
 * @author Dakota
 *
 * A pointer to a particular user in the database
 *
 * @version 2.0
 * @since 2021-10-27
 * @see User
 */
public class Database_Pointer {

    private final String email; // id can only be assigned once

    public Database_Pointer(String email){
       this.email = email;
    }


    /**
     * getID
     *
     * gets the id of the follow object
     *
     * @author Dakota
     * @return String id of a particular user
     */
    public String getEmail(){
        return this.email;
    }

    /**
     * isApproved
     *
     * gets is a user is approved or not
     *
     * @author Dakota
     * @return boolean true if user is approved, false if not
     */

    /**
     * custom hashCode() and equals() for Database_Pointer
     *
     * Reference: https://stackoverflow.com/a/17801750
     * @return
     */
    @Override
    public int hashCode() {
        return email.hashCode();
    }

    // equals Implementation
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Database_Pointer other = (Database_Pointer) obj;
        if (email != other.getEmail())
            return false;
        return true;
    }
    public boolean Equals(Database_Pointer dp){
        return this.getEmail().equals(dp.getEmail());
    }
}
