package fly.speedmeter.grub.view.penumpang;


import android.content.Context;

public interface PenumpangView {
    Context getContext();
    void statusSuccess(String message);
    void statusError(String message);

}
