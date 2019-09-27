package fly.speedmeter.grub.view.speed;


import android.content.Context;

public interface SpeedView {
    Context getContext();
    void statusDaruratSuccess(String message);
    void statusSuccess(String message);
    void statusError(String message);

}
