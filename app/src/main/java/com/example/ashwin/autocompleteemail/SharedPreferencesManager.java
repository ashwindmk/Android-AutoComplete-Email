package com.example.ashwin.autocompleteemail;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ashwin on 7/9/16.
 */

public class SharedPreferencesManager
{

    private Context mContext;
    private static SharedPreferences mSharedPreferences;
    private static final String PREFERENCES = "my_preferences";

    public SharedPreferencesManager(Context context)
    {
        mSharedPreferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        mContext = context;
    }


    //checks if user has denied contacts permission with never ask again
    private static final String NEVER_ASK_FOR_CONTACTS_PERMISSION = "Never Ask For Contacts Permission";

    public Boolean getNeverAskForContactsPermission()
    {
        return  mSharedPreferences.getBoolean(NEVER_ASK_FOR_CONTACTS_PERMISSION, false);
    }

    public void setNeverAskForContactsPermission(boolean askForContactsPermission)
    {
        mSharedPreferences.edit().putBoolean(NEVER_ASK_FOR_CONTACTS_PERMISSION, askForContactsPermission).commit();
    }


    //checks if user has cancelled contacts permission request
    private static final String CANCELLED_CONTACTS_PERMISSION = "Cancelled Contacts Permission";

    public Boolean getCancelledContactsPermission()
    {
        return  mSharedPreferences.getBoolean(CANCELLED_CONTACTS_PERMISSION, false);
    }

    public void setCancelledContactsPermission(boolean cancelledContactsPermission)
    {
        mSharedPreferences.edit().putBoolean(CANCELLED_CONTACTS_PERMISSION, cancelledContactsPermission).commit();
    }

}
