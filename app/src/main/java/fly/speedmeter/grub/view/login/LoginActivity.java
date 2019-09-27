package fly.speedmeter.grub.view.login;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.app.ProgressDialog;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator.ValidationListener;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.ornach.nobobutton.NoboButton;

import java.util.List;

import fly.speedmeter.grub.network.response.AuthResponse;
import fly.speedmeter.grub.utils.SessionManager;
import fly.speedmeter.grub.view.BaseLoginActivity;
import fly.speedmeter.grub.view.start.MainActivity;
import fly.speedmeter.grub.R;
import fly.speedmeter.grub.view.start.StartActivity;


public class LoginActivity extends BaseLoginActivity implements View.OnClickListener, LoginView, ValidationListener  {
    LoginPresenter presenter;
    ProgressDialog progressDialog;
    SessionManager sessionManager;

    @NotEmpty(message = "Email tidak boleh kosong")
    @Email(message = "Bukan alamat email")
    private EditText editTextEmail;

    @NotEmpty(message = "Sandi tidak boleh kosong")
    private EditText editTextPassword;

    Validator validator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        setContentView(R.layout.activity_login);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        findViewById(R.id.buttonLogin).setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        presenter = new LoginPresenter(this);
        sessionManager = new SessionManager(this);
        validator = new Validator(this);
        validator.setValidationListener(this);
    }



    private void userLogin() {

        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        presenter.loginAuth(
                email,
                password
        );


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonLogin:
                validator.validate();
                break;
        }
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    @Override
    public void showProgress() {
        progressDialog.show();
    }

    @Override
    public void hideProgress() {
        progressDialog.dismiss();
    }

    @Override
    public void statusSuccess(AuthResponse authResponse) {
        Log.d("Berhasil", "berhasil login:" +authResponse.getData().getId()+authResponse.getData().getUsername()+authResponse.getData().getToken());
        sessionManager.createLoginSession(
                authResponse.getData().getId(),
                authResponse.getData().getUsername(),
                "Bearer " + authResponse.getData().getToken()
        );
        finish();
        Intent intent = new Intent(LoginActivity.this, StartActivity.class);
        startActivity(intent);
    }

    @Override
    public void statusError(String message) {
        Log.d("Error", "statusError: " + message);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    @Override
    public void onValidationSucceeded() {
        userLogin();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }
    }

