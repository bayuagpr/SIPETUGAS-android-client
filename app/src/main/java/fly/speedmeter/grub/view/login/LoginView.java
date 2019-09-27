package fly.speedmeter.grub.view.login;


import android.content.Context;

import fly.speedmeter.grub.network.response.AuthResponse;

public interface LoginView {
        Context getContext();
        void showProgress();
        void hideProgress();
        void statusSuccess(AuthResponse authResponse);
        void statusError(String message);

}
