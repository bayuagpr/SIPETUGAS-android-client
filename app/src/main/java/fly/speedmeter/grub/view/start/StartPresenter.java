package fly.speedmeter.grub.view.start;

import fly.speedmeter.grub.network.ApiClient;
import fly.speedmeter.grub.network.ApiInterface;
import fly.speedmeter.grub.network.response.AuthResponse;
import fly.speedmeter.grub.network.response.BusResponse;
import fly.speedmeter.grub.network.response.KecepatanResponse;
import fly.speedmeter.grub.network.response.SebuahBusResponse;
import fly.speedmeter.grub.network.response.SebuahSupirResponse;
import fly.speedmeter.grub.network.response.SupirResponse;
import fly.speedmeter.grub.network.response.UserResponse;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class StartPresenter {
    StartView view;
    ApiInterface apiInterface;
    CompositeDisposable disposable;

    public StartPresenter(StartView view) {
        this.view = view;
        apiInterface = ApiClient.getClient(view.getContext()).create(ApiInterface.class);
        disposable = new CompositeDisposable();
    }

    public void getSpecUser(String token) {
        disposable.add(
                apiInterface.getUser(token)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<UserResponse>(){
                            @Override
                            public void onNext(UserResponse userResponse) {
                                view.statusSuccessUser(userResponse);
                            }

                            @Override
                            public void onError(Throwable e) {
                                view.hideProgress();
                                view.statusError(e.getLocalizedMessage());
                            }

                            @Override
                            public void onComplete() {
                                view.hideProgress();
                            }
                        })
        );
    }

    public void getBus(String token) {
        disposable.add(
                apiInterface.groupListBus(token)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<BusResponse>(){
                            @Override
                            public void onNext(BusResponse busResponse) {
                                view.statusSuccessBus(busResponse);
                            }

                            @Override
                            public void onError(Throwable e) {
                                view.hideProgress();
                                view.statusError(e.getLocalizedMessage());
                            }

                            @Override
                            public void onComplete() {
                                view.hideProgress();
                            }
                        })
        );
    }

    public void getPlat(String token, String plat) {
        disposable.add(
                apiInterface.getPlatBus(token, plat)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<SebuahBusResponse>(){
                            @Override
                            public void onNext(SebuahBusResponse busResponse) {
                                view.statusSuccessGetBus(busResponse);
                            }

                            @Override
                            public void onError(Throwable e) {
                                view.hideProgress();
                                view.statusError(e.getLocalizedMessage());
                            }

                            @Override
                            public void onComplete() {
                                view.hideProgress();
                            }
                        })
        );
    }

    public void getSupir(String token) {
        disposable.add(
                apiInterface.groupListSupir(token)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<SupirResponse>(){
                            @Override
                            public void onNext(SupirResponse supirResponse) {
                                view.statusSuccessSupir(supirResponse);
                            }

                            @Override
                            public void onError(Throwable e) {
                                view.hideProgress();
                                view.statusError(e.getLocalizedMessage());
                            }

                            @Override
                            public void onComplete() {
                                view.hideProgress();
                            }
                        })
        );
    }

    public void getSupirName(String token, String nama) {
        disposable.add(
                apiInterface.getNamaSupir(token, nama)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<SebuahSupirResponse>(){
                            @Override
                            public void onNext(SebuahSupirResponse supirResponse) {
                                view.statusSuccessGetSupir(supirResponse);
                            }

                            @Override
                            public void onError(Throwable e) {
                                view.hideProgress();
                                view.statusError(e.getLocalizedMessage());
                            }

                            @Override
                            public void onComplete() {
                                view.hideProgress();
                            }
                        })
        );
    }

    void kirimStatus(String token, int status, int bus_id, int supir_id) {
        disposable.add(
                apiInterface.saveStat(token, status, bus_id, supir_id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<KecepatanResponse>() {
                            @Override
                            public void onNext(KecepatanResponse kecepatanResponse) {
                                view.statusSuccess(kecepatanResponse.getStatus());
                            }

                            @Override
                            public void onError(Throwable e) {
                                view.statusError(e.getLocalizedMessage());
                            }

                            @Override
                            public void onComplete() {

                            }
                        })
        );


    }

    void logout(String token, String status) {
        disposable.add(
                apiInterface.postLogout(token, status)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<AuthResponse>() {
                            @Override
                            public void onNext(AuthResponse authResponse) {
                                view.statusSuccess(authResponse.getStatus());
                            }

                            @Override
                            public void onError(Throwable e) {
                                view.statusError(e.getLocalizedMessage());
                            }

                            @Override
                            public void onComplete() {

                            }
                        })
        );


    }

    public void detachView() {
        disposable.dispose();
    }

}
