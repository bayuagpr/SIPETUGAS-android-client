package fly.speedmeter.grub.view.start;


import android.content.Context;

import fly.speedmeter.grub.network.response.BusResponse;
import fly.speedmeter.grub.network.response.SebuahBusResponse;
import fly.speedmeter.grub.network.response.SebuahSupirResponse;
import fly.speedmeter.grub.network.response.SupirResponse;
import fly.speedmeter.grub.network.response.UserResponse;

public interface StartView {
    Context getContext();
    void showProgress();
    void hideProgress();
    void statusSuccessUser(UserResponse userResponse);
    void statusSuccessSupir(SupirResponse supirResponse);
    void statusSuccessBus(BusResponse busResponse);
    void statusSuccessGetSupir(SebuahSupirResponse supirResponse);
    void statusSuccessGetBus(SebuahBusResponse busResponse);
    void statusSuccess(String message);
    void statusError(String message);
}
