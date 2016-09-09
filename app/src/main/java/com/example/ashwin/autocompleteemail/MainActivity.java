package com.example.ashwin.autocompleteemail;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{

    SharedPreferencesManager mSharedPreferencesManager;
    private static final int GET_ACCOUNTS = 100;
    AutoCompleteTextView mEmail;
    Button mClear;
    TextView mMessage;
    String[] fullList;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSharedPreferencesManager = new SharedPreferencesManager(this);

        mSharedPreferencesManager.setCancelledContactsPermission(false);

        mEmail = (AutoCompleteTextView) findViewById(R.id.userEmail);
        mClear = (Button) findViewById(R.id.clearEmail);
        mMessage = (TextView) findViewById(R.id.message);

        //initial clear button visibility
        mClear.setVisibility(View.GONE);

        //clear button visibility for manual typing
        mEmail.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void afterTextChanged(Editable s)
            {
                //do nothing
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
                //do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if(s.length() != 0)
                {
                    mClear.setVisibility(View.VISIBLE);
                }
                else
                {
                    mClear.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        initAutoCompleteTextView();
    }

    //adapter initialization
    private void initAutoCompleteTextView()
    {

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED)
        {
            //has contacts permission
            mSharedPreferencesManager.setNeverAskForContactsPermission(false);
            mMessage.setText("You already have contacts permission");
            initAutoCompleteTextViewHasPermission();
        }
        else
        {
            //do not have contacts permission
            if( !mSharedPreferencesManager.getCancelledContactsPermission() )
            {
                if( !mSharedPreferencesManager.getNeverAskForContactsPermission() )
                {
                    mMessage.setText("You do not have contacts permission");
                    initAutoCompleteTextViewNoPermission();
                }
                else
                {
                    mMessage.setText("You have selected to never ask again");
                }
            }
        }

    }

    //adapter when has permission
    private void initAutoCompleteTextViewHasPermission()
    {
        //calls getEmail method which returns all email ids separated by "\n"
        String emails = getAllEmails(this);

        if(emails != "")
        {
            //creates an array of email ids on device
            String[] emailAccounts = emails.split(" ");

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, emailAccounts);

            mEmail.setThreshold(0);       //will start working from first character

            mEmail.setAdapter(adapter);   //setting the adapter data into the AutoCompleteTextView

            mEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if(hasFocus)
                        mEmail.showDropDown();
                }
            });

            //shows drop down list on touch
            mEmail.setOnTouchListener(new View.OnTouchListener()
            {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    mEmail.showDropDown();
                    return false;
                }
            });

            //text change listener
            mEmail.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    //do nothing
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    //do nothing
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(s.length() != 0) {
                        mClear.setVisibility(View.VISIBLE);
                        mEmail.showDropDown();
                    } else {
                        mClear.setVisibility(View.GONE);
                    }
                }
            });

        }

    }

    //adapter when no permission
    private void initAutoCompleteTextViewNoPermission()
    {
        //creates an array of email ids on device
        fullList = new String[]{"Allow Contacts Permission"};

        AutoCompleteAdapter adapter = new AutoCompleteAdapter(this, fullList);

        mEmail.setThreshold(0);

        mEmail.setAdapter(adapter);

        adapter.setOnPermissionClickListener(new AutoCompleteAdapter.OnPermissionClickListener()
        {
                @Override
                public void onPermissionClicked()
                {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.GET_ACCOUNTS}, GET_ACCOUNTS);
                    mEmail.dismissDropDown();
                }
        });

    }


    //gives the \n separated string of all email accounts on device
    private String getAllEmails(Context context)
    {
        AccountManager accountManager = AccountManager.get(context);

        //calls getAllAccounts method
        Account[] accounts = getAllAccounts(accountManager);

        String str_accounts = "";

        if (accounts == null)
        {
            //do nothing
        }
        else
        {

            for (Account account : accounts)
            {
                String str_id = account.name;
                str_accounts = str_accounts + " " + str_id;
            }

            //removes the first "\n" from the str_accounts
            str_accounts = str_accounts.replaceFirst(" ", "");

        }

        return str_accounts;

    }

    //sends array of all accounts on device
    private static Account[] getAllAccounts(AccountManager accountManager)
    {
        Account[] accounts = accountManager.getAccountsByType("com.google");
        return accounts;
    }

    //this method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {

        //checking the request is code of our request
        if(requestCode == GET_ACCOUNTS)
        {

            //if permission is granted else denied
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                //permission is granted
                Toast.makeText(this,"Yippie permission granted",Toast.LENGTH_LONG).show();
            }
            else
            {
                //permission denied
                if( !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.GET_ACCOUNTS) )
                {
                    //permission denied with never ask again
                    Toast.makeText(this, "You have selected to never ask again. But you can grant permission by clicking on Go To Settings > Permissions", Toast.LENGTH_LONG).show();
                    mSharedPreferencesManager.setNeverAskForContactsPermission(true);
                }
                else
                {
                    Toast.makeText(this, "Oops permission denied", Toast.LENGTH_LONG).show();
                }

                mSharedPreferencesManager.setCancelledContactsPermission(true);

            }

        }

    }

    //onclick clear button
    public void clearEmail(View view)
    {
        mEmail.setText("");
    }

}
