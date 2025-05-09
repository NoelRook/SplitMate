package com.example.paisehpay.databaseHandler;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.paisehpay.blueprints.Group;
import com.example.paisehpay.blueprints.User;
import com.example.paisehpay.databaseHandler.Interfaces.FirebaseDatabaseAdapter;
import com.example.paisehpay.databaseHandler.Interfaces.OperationCallbacks;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.zip.CRC32;

public class UserAdapter extends FirebaseDatabaseAdapter<User> {

    public UserAdapter() {
        super("Users");
    }

    // save user object in the database
    @Override
    public void create(User object, OperationCallbacks.OperationCallback callback) throws IllegalArgumentException {

        validateObjectType(object, User.class, callback);
        if (object == null) {
            callback.onError(DatabaseError.fromException(new IllegalArgumentException("User ID cannot empty")));
            return;
        }
        User user = (User) object;
        String userId = databaseRef.push().getKey();
        if (userId == null) {
            callback.onError(DatabaseError.fromException(new Exception("Failed to generate user ID")));
            return;
        }

        user.setFriendKey(genFriendKey(user.getEmail()));
        databaseRef
                .child(userId)
                .setValue(user.toMap())
                .addOnCompleteListener(new OnCompleteListener<Void>(){
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                            callback.onSuccess();
                        } else {
                            callback.onError(DatabaseError.fromException(task.getException()));
                        }
                    }
                });
    }

    // get all users from the database
    @Override
    public void get(OperationCallbacks.ListCallback<User> callback) {
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<User> users = new ArrayList<>();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (user != null) {
                        user.setUserId(userSnapshot.getKey());
                        users.add(user);
                    }
                }
                callback.onListLoaded(users);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError);
            }
        });
    }

    // update user based on given id and user Object
    @Override
    public void update(String id, User object, OperationCallbacks.OperationCallback callback) {
        validateObjectType(object, User.class, callback);
        if (id == null || id.isEmpty()) {
            callback.onError(DatabaseError.fromException(new IllegalArgumentException("User ID cannot be null or empty")));
            return;
        }
        databaseRef
                .child(id)
                .setValue(((User) object).toMap())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                            callback.onSuccess();
                        } else {
                            callback.onError(DatabaseError.fromException(Objects.requireNonNull(task.getException())));
                        }
                    }
                });
    }


    //delete user based on string id given
    @Override
    public void delete(String id, OperationCallbacks.OperationCallback callback) {
        if (id == null || id.isEmpty()) {
            callback.onError(DatabaseError.fromException(new IllegalArgumentException("User ID cannot be null or empty")));
            return;
        }

        databaseRef
                .child(id)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                            callback.onSuccess();
                        } else {
                            callback.onError(DatabaseError.fromException(task.getException()));
                        }
                    }
                });
    }

    // generate friendkey upon account creation
    public String genFriendKey(String email){
        Date now = Calendar.getInstance().getTime(); // get the current time
        String key = now.getTime() + email; // concat with user's email

        CRC32 crc = new CRC32(); // instantiate a new CRC32 object
        crc.update(key.getBytes()); // update the key from string to bytes and feeds it into the crc algo
        long hash = crc.getValue(); // computes and returns the final crc value as a long

        // Convert to base36 (0-9, a-z) to make it alphanumeric
        String base36 = Long.toString(hash, 36);

        // Ensure exactly 8 characters (pad with zeros or truncate)
        if (base36.length() > 8) {
            Log.d("genKey",base36.substring(0, 8));

            return base36.substring(0, 8);
        } else {
            Log.d("genKey",String.format("%8s", base36).replace(' ', '0'));
            return String.format("%8s", base36).replace(' ', '0');
        }
    }

}
