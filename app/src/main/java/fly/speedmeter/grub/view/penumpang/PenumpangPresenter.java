package fly.speedmeter.grub.view.penumpang;


import java.util.Objects;

import fly.speedmeter.grub.network.ApiClient;
import fly.speedmeter.grub.network.ApiInterface;
import fly.speedmeter.grub.network.response.PenumpangResponse;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class PenumpangPresenter {
    PenumpangView view;
    ApiInterface apiInterface;
    CompositeDisposable disposable;

    public PenumpangPresenter(PenumpangView view) {
        this.view = view;
        apiInterface = ApiClient.getClient(view.getContext()).create(ApiInterface.class);
        disposable = new CompositeDisposable();
    }

    void kirimPenumpang(String token,   int naik_anak , int naik_dewasa, int naik_manula ,
                        int turun_anak , int turun_dewasa, int turun_manula ,
                        String lokasi, int jumlah, int bus_id, int supir_id ) {
        disposable.add(
                apiInterface.savePost(token, naik_anak, naik_dewasa, naik_manula, turun_anak, turun_dewasa, turun_manula,
                                        lokasi, jumlah, bus_id, supir_id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<PenumpangResponse>() {
                            @Override
                            public void onNext(PenumpangResponse penumpangResponse) {
                                view.statusSuccess(penumpangResponse.getStatus());
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
