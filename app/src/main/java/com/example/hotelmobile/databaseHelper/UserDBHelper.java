package com.example.hotelmobile.databaseHelper;

import com.example.hotelmobile.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserDBHelper {

    private final DatabaseReference userDatabase;

    public UserDBHelper() {
        // Initialize Firebase Database reference
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://hotelmobile-d180a-default-rtdb.asia-southeast1.firebasedatabase.app/");
        userDatabase = firebaseDatabase.getReference("users");
    }

    /**
     * Add a new user to the Firebase Realtime Database.
     * @param user The User object to be added.
     */
    public void addUser(User user) {
        // Generate a unique ID for the user
        String userId = userDatabase.push().getKey();

        if (userId != null) {
            user.setId(Integer.parseInt(userId.hashCode() + "")); // Generate a hash-based ID if needed
            userDatabase.child(userId).setValue(user)
                    .addOnSuccessListener(aVoid -> {
                        // Success callback
                        System.out.println("User added successfully to Firebase.");
                    })
                    .addOnFailureListener(e -> {
                        // Failure callback
                        System.err.println("Failed to add user to Firebase: " + e.getMessage());
                    });
        }
    }

    /**
     * Check if the provided username and password are valid.
     * @param username The username to check.
     * @param password The password to check.
     * @param callback The callback to return the result of the login attempt.
     */
    public void checkLogin(String username, String password, final LoginCallback callback) {
        // Search for the user by username
        userDatabase.orderByChild("userName").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // If the username exists, check if the password matches
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        if (user != null && user.getPassword().equals(password)) {
                            // If the password matches, login is successful
                            callback.onLoginSuccess(user);
                        } else {
                            // If the password doesn't match
                            callback.onLoginFailed("Mật khẩu sai");
                        }
                    }
                } else {
                    // If the username doesn't exist
                    callback.onLoginFailed("Tài khoản không tồn tại");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onLoginFailed("Lỗi kết nối đến cơ sở dữ liệu");
            }
        });
    }

    /**
     * Get the database reference for users.
     * Use this for querying data in other parts of your app.
     * @return DatabaseReference for the "users" node.
     */
    public DatabaseReference getUserDatabase() {
        return userDatabase;
    }

    // Callback interface to handle login result
    public interface LoginCallback {
        void onLoginSuccess(User user);
        void onLoginFailed(String errorMessage);
    }
}
