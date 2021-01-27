package android.bignerdranch.familymapclient;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import AsyncTasks.LoginTask;
import AsyncTasks.RegisterTask;
import Data.DataCache;
import Model.HttpPort;
import Requests.LoginRequest;
import Requests.RegisterRequest;

public class LoginRegisterFragment extends Fragment implements
        LoginTask.LoginListener,
        RegisterTask.RegisterListener {

    public interface Listener {
        void onSuccessfulLogin();
    }

    public Listener mListener;

    private final String DEBUG_TAG = "Login fragment debug";

    private LoginRequest mLoginRequest;
    private RegisterRequest mRegisterRequest;

    private HttpPort mPort;

    private EditText mServerHostField;
    private EditText mServerPortField;
    private EditText mUserNameField;
    private EditText mPasswordField;
    private EditText mFirstNameField;
    private EditText mLastNameField;
    private EditText mEmailField;

    private RadioGroup mGendersGroup;

    private Button mSignInButton;
    private Button mRegisterButton;

    private String mUserFirstName;
    private String mUserLastName;

    public LoginRegisterFragment(Listener l) { mListener = l; }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLoginRequest = new LoginRequest();
        mRegisterRequest = new RegisterRequest();
        mPort = new HttpPort();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.fragment_login, container, false);

        wireUpWidgets(v);

        return v;
    }

    private void wireUpWidgets(View v) {

        mServerHostField = (EditText) v.findViewById(R.id.server_host_field);
        mServerHostField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mPort.setServerHost(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                enableButtons();
            }
        });

        mServerPortField = (EditText) v.findViewById(R.id.server_port_field);
        mServerPortField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mPort.setServerPort(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                enableButtons();
            }
        });

        mUserNameField = (EditText) v.findViewById(R.id.user_name_field);
        mUserNameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mLoginRequest.setUserName(charSequence.toString());
                mRegisterRequest.setUserName(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                enableButtons();
            }
        });

        mPasswordField = (EditText) v.findViewById(R.id.password_field);
        mPasswordField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mLoginRequest.setPassword(charSequence.toString());
                mRegisterRequest.setPassword(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                enableButtons();
            }
        });

        mFirstNameField = (EditText) v.findViewById(R.id.first_name_field);
        mFirstNameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mRegisterRequest.setFirstName(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                enableButtons();
            }
        });

        mLastNameField = (EditText) v.findViewById(R.id.last_name_field);
        mLastNameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mRegisterRequest.setLastName(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                enableButtons();
            }
        });

        mEmailField = (EditText) v.findViewById(R.id.email_field);
        mEmailField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mRegisterRequest.setEmail(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                enableButtons();
            }
        });

        mGendersGroup = (RadioGroup) v.findViewById(R.id.radio_gender_group);
//        mGendersGroup.check(R.id.radio_male); // Sets default to male
//        mRegisterRequest.setGender("m");
        mGendersGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.radio_male) {
                    mRegisterRequest.setGender("m");
                }
                else {
                    mRegisterRequest.setGender("f");
                }
                enableButtons();
            }
        });

        mSignInButton = (Button) v.findViewById(R.id.sign_in_button);
        mSignInButton.setEnabled(false);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (signInFieldsOK()) {
                    login();
                }
                else {
                    Toast.makeText(getActivity(),
                            "Please enter required sign in fields",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        mRegisterButton = (Button) v.findViewById(R.id.register_button);
        mRegisterButton.setEnabled(false);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (registerFieldsOK()) {
                    register();
                }
                else {
                    Toast.makeText(getActivity(),
                            "Please enter required register fields",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean signInFieldsOK() {

        if (!mPort.anyNull() && !mLoginRequest.anyNull() && !mLoginRequest.anyEmpty())
            return true;

        return false;
    }

    private boolean registerFieldsOK() {

        if (!mPort.anyNull() && !mRegisterRequest.anyNull() && !mRegisterRequest.anyEmpty())
            return true;

        return false;
    }

    private void enableButtons() {

        if (signInFieldsOK()) {
            mSignInButton.setEnabled(true);
            Log.d(DEBUG_TAG, "All sign in fields ok, button enabled.");
        }
        else {
            mSignInButton.setEnabled(false);
        }

        if (registerFieldsOK()) {
            mRegisterButton.setEnabled(true);
            Log.d(DEBUG_TAG, "All register fields ok, button enabled.");
        }
        else {
            mRegisterButton.setEnabled(false);
        }
    }

    private void login() {

        LoginTask loginTask = new LoginTask(this);
        loginTask.setHttpPort(mPort);
        loginTask.execute(mLoginRequest);
    }

    private void register() {

        RegisterTask registerTask = new RegisterTask(this);
        registerTask.setHttpPort(mPort);
        registerTask.execute(mRegisterRequest);
    }

    @Override
    public void onPostExecuteLogin(boolean isSuccess) {

        // Unsuccessful login attempt
            // Note: LoginResult.isSuccess() method is flipped and needs to be changed.
        if (!isSuccess) {
            // Notify user that login attempt was unsuccessful
            Toast.makeText(getActivity(),
                    "Username or password was incorrect.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        mListener.onSuccessfulLogin();
//        displaySuccessfulToast();
    }

    @Override
    public void onPostExecuteRegister(String message, boolean isSuccess) {

        // Unsuccessful register attempt
        if (!isSuccess) {
            // Notify user that login attempt was unsuccessful
            Toast.makeText(getActivity(),
                    message,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        mListener.onSuccessfulLogin();
//        displaySuccessfulToast();
    }

    private void displaySuccessfulToast() {

        DataCache cache = DataCache.getInstance();
        mUserFirstName = cache.getFirstName();
        mUserLastName = cache.getLastName();

        Toast.makeText(getActivity(),
                "Success: " + mUserFirstName +
                        " " + mUserLastName,
                Toast.LENGTH_SHORT).show();
    }
}