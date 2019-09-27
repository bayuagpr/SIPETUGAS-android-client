package fly.speedmeter.grub.view.speed;


import java.util.Objects;

import fly.speedmeter.grub.network.ApiClient;
import fly.speedmeter.grub.network.ApiInterface;
import fly.speedmeter.grub.network.response.DaruratResponse;
import fly.speedmeter.grub.network.response.KecepatanResponse;
import fly.speedmeter.grub.view.speed.SpeedView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class SpeedPresenter {
    SpeedView view;
    ApiInterface apiInterface;
    CompositeDisposable disposable;

    public SpeedPresenter(SpeedView view) {
        this.view = view;
        apiInterface = ApiClient.getClient(Objects.requireNonNull(view.getContext())).create(ApiInterface.class);
        disposable = new CompositeDisposable();
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

    void kirimDarurat(String token, String kategori, String keterangan, String lokasi,
                      int bus_id, int supir_id) {
        disposable.add(
                apiInterface.saveEmerge(token, kategori,keterangan,lokasi, bus_id, supir_id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<DaruratResponse>() {
                            @Override
                            public void onNext(DaruratResponse daruratResponse) {
                                view.statusDaruratSuccess(daruratResponse.getStatus());
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
