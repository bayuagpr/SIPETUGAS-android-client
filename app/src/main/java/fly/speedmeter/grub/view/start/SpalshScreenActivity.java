package fly.speedmeter.grub.view.start;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import fly.speedmeter.grub.R;
import fly.speedmeter.grub.utils.SessionManager;
import fly.speedmeter.grub.view.login.LoginActivity;

public class SpalshScreenActivity extends AppCompatActivity {
    private SessionManager session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        session = new SessionManager(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalsh_screen);
        new Handler().postDelayed(() -> {
            Intent activityIntent;
            if (session.isLoggedIn()) {
                activityIntent = new Intent(this, StartActivity.class);
            } else {
                activityIntent = new Intent(this, LoginActivity.class);
            }

            startActivity(activityIntent);
            finish();
        },2000);
    }
}
