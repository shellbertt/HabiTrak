package com.cmput301f21t34.habittrak;

import android.util.Log;

import androidx.annotation.NonNull;

import com.cmput301f21t34.habittrak.user.Database_Pointer;
import com.cmput301f21t34.habittrak.user.Habit;
import com.cmput301f21t34.habittrak.user.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

/**
 * @author Tauseef Nafee Fattah
 * @author Henry
 * @see User
 * @version 1.0
 * Implemented the getter methods only the setters methods will be implemented soon
 */

public class DatabaseManager {
    private FirebaseFirestore database;
    private boolean isUnique;
    String username;
    String password;

    public DatabaseManager() {
        database = FirebaseFirestore.getInstance();
    }

    /**
     * validCredentials
     *
     * checks if given password matches the actual password of an email
     *
     * @author Henry
     * @param email
     * @param password
     * @return
     * returns true if credentials match, false otherwise
     */
    public boolean validCredentials(String email, String password) {

        boolean validCredentials = false;

        String TAG = "ValidCredentials";

        final CollectionReference collectionReference = database.collection("users");

        try {
            DocumentReference docref = collectionReference.document(email);
            Task<DocumentSnapshot> task = docref.get();
            while (!task.isComplete()) ;
            DocumentSnapshot document = task.getResult();
            if (document.getData() != null) {
                if (document.get("Password").equals(password)) {
                    validCredentials = true;
                }
            }
        }
        catch (Exception ignored) {}

        return validCredentials;
    }

    /**
     * Checks to see if the user with the provided email already exists
     *
     * @author Henry
     * @param email
     * @return boolean
     */

    public boolean isUnique(String email) {

        boolean isUnique = false;

        String TAG = "uniqueUser";

        final CollectionReference collectionReference = database.collection("users");

        try {
            DocumentReference docref = collectionReference.document(email);
            Task<DocumentSnapshot> task = docref.get();
            while (!task.isComplete()) ;
            DocumentSnapshot document = task.getResult();
            if (!document.exists()) {
                isUnique = true;
                return isUnique;
            }
        }

        catch (Exception ignored) {}

        return isUnique;
    }

    /**
     * createNewUser
     *
     * @author Henry
     * returns true if it is successful or false if it is not
     */
    public boolean createNewUser(String user_email, String username, String password, String biography) {

        String TAG = "Unique";

        if (isUnique(user_email)) {
            Log.d(TAG, "Unique");
            final CollectionReference collectionReference = database.collection("users");

            HashMap<String, Object> data = new HashMap<>();
            data.put("Password", password);
            data.put("Username", username);
            data.put("Biography",biography);
            data.put("habitList", new ArrayList<Habit>());
            data.put("followerList", new ArrayList<Database_Pointer>());
            data.put("followingList", new ArrayList<Database_Pointer>());
            data.put("followReqList", new ArrayList<Database_Pointer>());
            data.put("followRequestedList", new ArrayList<Database_Pointer>());
            data.put("blockList", new ArrayList<Database_Pointer>());
            data.put("blockedByList", new ArrayList<Database_Pointer>());

            collectionReference
                    .document(user_email)
                    .set(data);

            return true;
        }
        Log.d(TAG, "Not unique");
        return false;
    }



    /**
     * getUser
     *
     * @author Tauseef
     * @author Henry
     *
     * returns the user with the provided email
     * @param email
     * @return User
     */
    // TODO: DO NOT USE THIS CLASS. It currently returns a dummy user BEFORE data is read from firestore
    public User getUser(String email) {
        User user;

        String name = "";
        String password = "";
        String bio = "";
        ArrayList<Habit> habitList = new ArrayList<Habit>();
        ArrayList<Database_Pointer>followerList = new ArrayList<Database_Pointer>();
        ArrayList<Database_Pointer>followingList = new ArrayList<Database_Pointer>();
        ArrayList<Database_Pointer>followReqList = new ArrayList<Database_Pointer>();
        ArrayList<Database_Pointer>followRequestedList = new ArrayList<Database_Pointer>();
        ArrayList<Database_Pointer>blockList = new ArrayList<Database_Pointer>();
        ArrayList<Database_Pointer>blockedByList = new ArrayList<Database_Pointer>();

        try {
            DocumentReference docref = database.collection("users").document(email);
            Task<DocumentSnapshot> task = docref.get();
            while (!task.isComplete());
            DocumentSnapshot document = task.getResult();

            if (document.getData() != null) {
                habitList = (ArrayList<Habit>) document.get("habitList");
                followerList = (ArrayList<Database_Pointer>) document.get("followerList");
                followingList = (ArrayList<Database_Pointer>) document.get("followingList");
                blockList = (ArrayList<Database_Pointer>) document.get("blockList");
                blockedByList = (ArrayList<Database_Pointer>) document.get("blockedByList");
                followReqList = (ArrayList<Database_Pointer>) document.get("followReqList");
                followRequestedList= (ArrayList<Database_Pointer>) document.get("followRequestedList");
                name = (String) document.get("Username");
                password = (String) document.get("Password");
                bio = (String) document.get("Biography");
            }

            user = new User(email);

            user.setPassword(password);
            user.setUsername(name);
            user.setHabitList(habitList);
            user.setFollowerList(followerList);
            user.setFollowingList(followingList);
            user.setBlockList(blockList);
            user.setBlockedByList(blockedByList);
            user.setFollowerReqList(followReqList);
            user.setFollowerRequestedList(followRequestedList);
            return user;
        }
        catch (Exception ignored) {}
        user = new User();
        return user;
    }

    /**
     * getUserName
     * gets the user name of the provided email
     * @param user_email
     * @return username (string)
     */
    /*
    public String getUserName(String user_email) {
        DatabaseReference usersReference = database.getReference("users");
        username = "";
        usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    if (ds.child("userInfo").child("email").getValue(String.class).equals(user_email)){
                        username = ds.child("userInfo").child("name").getValue(String.class);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return username;
    }*/

    /**
     * getHabitList
     * gets the habit list of the provided user email
     * @param user_email
     * @return habitList
     */
    /*
    public ArrayList<Habit> getHabitList(String user_email) {
        DatabaseReference usersReference = database.getReference("users");
        // returns empty list if no habit exists
        returnHabitList.clear();

        usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot ds: snapshot.getChildren()) {
                    if (ds.child("userInfo").child("email").getValue(String.class).equals(user_email)) {
                        for (DataSnapshot dataSnapshot: ds.child("Habits").getChildren()) {
                            returnHabitList.add(dataSnapshot.getValue(Habit.class));
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        return returnHabitList;
    }*/

    /**
     * getFollowerList
     * gets the follower list of the provided email
     * @param user_email
     * @return Follower list
     */
    /*
    public ArrayList<Database_Pointer> getFollowerList(String user_email) {
        DatabaseReference usersReference = database.getReference("users");
        // returns empty list if no habit exists
        returnFollowerList.clear();

        usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot ds: snapshot.getChildren()){
                    if(ds.child("userInfo").child("email").getValue(String.class).equals(user_email)) {
                        for(DataSnapshot dataSnapshot: ds.child("Followers").getChildren()){
                            returnFollowerList.add(new Database_Pointer(dataSnapshot.getValue(String.class)));
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        return returnFollowerList;
    }*/

    /**
     * getFollowingList
     * gets the following list of the provided email
     * @param user_email
     * @return Following list
     */
    /*
    public ArrayList<Database_Pointer> getFollowingList(String user_email ){
        DatabaseReference usersReference = database.getReference("users");
        // returns empty list if no habit exists
        returnFollowingList.clear();

        usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot ds: snapshot.getChildren()){
                    if(ds.child("userInfo").child("email").getValue(String.class).equals(user_email)){
                        for(DataSnapshot dataSnapshot: ds.child("Followings").getChildren()){
                            returnFollowingList.add(new Database_Pointer(dataSnapshot.getValue(String.class)));
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        return returnFollowingList;
    }*/

    /**
     * getFollowReqList
     * returns followReq list of the provided email (The followReq list contains all the users email
     * the main user(the one with the id) has requested to follow)
     * Example: user 1 has requested to follow user 2 and user 3 then this function will return user 2 and user 3
     * the input is user 1 email
     * *** I can change this function to do the opposite if needed
     * @param user_email
     * @return follow req list
     */
    /*
    public ArrayList<Database_Pointer> getFollowReqList(String user_email) {
        DatabaseReference usersReference = database.getReference("users");
        // returns empty list if no habit exists
        returnFollowReqList.clear();

        usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot ds: snapshot.getChildren()){
                    if(ds.child("userInfo").child("email").getValue(String.class).equals(user_email)) {
                        for(DataSnapshot dataSnapshot: ds.child("FollowReqs").getChildren()){
                            returnFollowReqList.add(new Database_Pointer(dataSnapshot.getValue(String.class)));
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        return returnFollowReqList;
    }*/

    /**
     * getFollowRequestedList
     * returns followRequested list of the provided email (The followReq list contains all the users email
     * that have requested to follow the main user(the one with the id)
     * Example: user 2 and user 3 has requested to follow user 1 then this function will return user 2 and user 3
     * the input is user 1 email
     * I can change this function to do the opposite if needed
     * @param user_email
     * @return
     */
    /*
    public ArrayList<Database_Pointer> getFollowRequestedList(String user_email) {
        DatabaseReference usersReference = database.getReference("users");
        // returns empty list if no habit exists
        returnFollowRequestedList.clear();

        usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot ds: snapshot.getChildren()){
                    if(ds.child("userInfo").child("email").getValue(String.class).equals(user_email)) {
                        for(DataSnapshot dataSnapshot: ds.child("FollowRequesteds").getChildren()){
                            returnFollowRequestedList.add(new Database_Pointer(dataSnapshot.getValue(String.class)));
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        return returnFollowRequestedList;
    }*/

    /**
     * getBlockList
     * returns the block list of the provided email (The block list contains all the users email
     * that the user have blocked
     * Example: user 2 and user 3 has been blocked by user 1 then this function will return user 2 and user 3
     * the input is user 1 email
     * I can change this function to do the opposite if needed
     * @param user_email
     * @return
     */
    /*
    public ArrayList<Database_Pointer> getBlockList(String user_email) {
        DatabaseReference usersReference = database.getReference("users");
        // returns empty list if no habit exists
        returnBlockList.clear();

        usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot ds: snapshot.getChildren()){
                    if(ds.child("userInfo").child("email").getValue(String.class).equals(user_email)) {
                        for(DataSnapshot dataSnapshot: ds.child("BlockList").getChildren()){
                            returnBlockList.add(new Database_Pointer(dataSnapshot.getValue(String.class)));
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        return returnBlockList;
    }*/

    /**
     * getBlockedByList
     * returns followRequested list of the provided email (The followReq list contains all the users email
     * that have requested to follow the main user(the one with the id)
     * Example: user 2 and user 3 has requested to follow user 1 then this function will return user 2 and user 3
     * the input is user 1 email
     * I can change this function to do the opposite if needed
     * @param user_email
     * @return
     */
    /*
    public ArrayList<Database_Pointer> getBlockedByList(String user_email) {
        DatabaseReference usersReference = database.getReference("users");
        // returns empty list if no habit exists
        returnBlockedByList.clear();

        usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot ds: snapshot.getChildren()){
                    if(ds.child("userInfo").child("email").getValue(String.class).equals(user_email)) {
                        for(DataSnapshot dataSnapshot: ds.child("BlockedByList").getChildren()){
                            returnBlockedByList.add(new Database_Pointer(dataSnapshot.getValue(String.class)));
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        return returnBlockedByList;
    }*/
    /**
     * setHabitListDb
     * sets the habit list in the database
     * have 2 implementations (one is directly changing entire habit list another is changing habit events individually)
     * @param user_email
     * @param habitList
     * @param givenHabitId
     * @param habitEventList
     */
    /*
    public void setHabitListDb(String user_email, ArrayList<Habit> habitList, String givenHabitId,
                               ArrayList<Habit> habitEventList) {
        DatabaseReference usersReference = database.getReference("users");
    }
    */
    /**
     * addfollower
     * email2 is now a follower of email1 (so add email2 to the follower list of email1 and add email1 to the following list of email2)
     * @param email1
     * @param email2
     */

    //public void addfollower(String email1, String email2){}
    /**
     * addfollow req
     * email1 asked to follow email2 (so add email2 to the followerReq list of email1 and add email1 to the followerRequested list of email2)
     * followerRequested list means the people that asked to follow the user
     * followerReq list means the people that the user requested to follow
     * @param email1
     * @param email2
     */

    //public void addFollowerReq(String email1, String email2){}
    /**
     * addtoblocklist
     * email2 is now blocked by email1 (so add email2 to the block list of email1 and add email1 to the blockedby list of email2)
     * @param email1
     * @param email2
     */

    //public void addBlockList(String email1, String email2){}
    // change username and password and for habit just update
    /**
     * changePassword
     * @param email
     * @param password
     */
    //public void changePassword(String email, String password)

    /**
     * changeUserName
     * @param email
     * @param username
     */
    //public void changeUsername(String email, String username)


}