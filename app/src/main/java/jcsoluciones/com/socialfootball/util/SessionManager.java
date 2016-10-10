package jcsoluciones.com.socialfootball.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.SignalStrength;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Objects;

import jcsoluciones.com.socialfootball.SignInActivity;

/**
 * Created by ADMIN on 12/08/2016.
 */
public class SessionManager {
    private static final String TAG = "SessionManager";
    // Shared Preferences
    SharedPreferences pref;
    // Editor for Shared preferences
    SharedPreferences.Editor editor;
    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;
    // Sharedpref file name
    private static final String PREF_NAME = "AndroidHivePref";
    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";
    // User name (make variable public to access from outside)
    public static final String NAME = "name";
    // Email address (make variable public to access from outside)
    public static final String KEY= "key_user";
    // Content  (make variable public to access from outside)
    public static final String CONTENT_MYCLUB = "content_myclub";
    // ID Content (make variable public to access from outside)
    public static final String ID_CONTENT_MYCLUB = "id_content_myclub";
    // Content  (make variable public to access from outside)
    public static final String CONTENT_ALL_CLUBS = "content_all_clubs";
    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(String name, String key,String id,String content){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
        // Storing name in pref
        editor.putString(NAME, name);
        // Storing email in pref
        editor.putString(KEY, key);
        // Storing content in pref
        editor.putString(CONTENT_MYCLUB, content);
        // Storing content id in pref
        editor.putString(ID_CONTENT_MYCLUB, id);
        // commit changes
        editor.commit();
    }


    /**
     * Create content clubs
     * */
    public void createContentClubs(String list){
        editor.putString(CONTENT_ALL_CLUBS, list);
        editor.commit();
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, SignInActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }



    }


    /**
     * Simple network connection check.
     *
     * @param context
     */
    public boolean checkConnection(Context context) {
        final ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnectedOrConnecting()) {
            Toast.makeText(context,"no  no connection found", Toast.LENGTH_LONG).show();
            Log.e(TAG, "checkConnection - no connection found");
            return true;
        }
        return false;
    }


    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> data = new HashMap<String, String>();
        // user name
        data.put(NAME, pref.getString(NAME, ""));

        // user key
        data.put(KEY, pref.getString(KEY, ""));

        // user  content_myclub
        data.put(CONTENT_MYCLUB, pref.getString(CONTENT_MYCLUB, ""));

        // user id content myclub
        data.put(ID_CONTENT_MYCLUB, pref.getString(ID_CONTENT_MYCLUB, ""));

        // user content all clubs
        data.put(CONTENT_ALL_CLUBS, pref.getString(CONTENT_ALL_CLUBS, ""));

        // return user
        return data;
    }

    /**
     * Clear session details
     * */
    public void logoutUser(Activity activity){
        /* Clearing all data from Shared Preferences */
        editor.clear();
        editor.commit();
        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, activity.getClass());
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // Staring Login Activity
        _context.startActivity(i);
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}
