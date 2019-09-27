package fly.speedmeter.grub.view.login;


import android.util.Log;

import fly.speedmeter.grub.network.ApiClient;
import fly.speedmeter.grub.network.ApiInterface;
import fly.speedmeter.grub.network.response.AuthResponse;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class LoginPresenter {
    private LoginView view;
    private ApiInterface apiInterface;
    private CompositeDisposable disposable;

    public LoginPresenter(LoginView view) {
        this.view = view;
        apiInterface = ApiClient.getClient(view.getContext()).create(ApiInterface.class);
        disposable = new CompositeDisposable();
    }


    public void loginAuth(String username, String password) {
        Log.d("Login attempt", "Login with:" +username+password);
        view.showProgress();
        disposable.add(
          apiInterface.postAuth(username, password)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<AuthResponse>(){
                        @Override
                        public void onNext(AuthResponse authResponse) {
                            view.hideProgress();
                            if (authResponse.getStatus().equals("true")) {
                                Log.d("Login attempt", "berhasil");
                                view.statusSuccess(authResponse);
                            } else {
                                Log.d("Login attempt", "gagal");
                                view.statusError(authResponse.getStatus());
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d("Login attempt", "error");
                            Log.d("Error disposable", "onError " + e.getMessage());
                            view.hideProgress();
                        }

                        @Override
                        public void onComplete() {
                            Log.d("Login attempt", "komplit");
                            view.hideProgress();
                        }
                    })
        );
    }

    public void detachView() {
        disposable.dispose();
    }
}
