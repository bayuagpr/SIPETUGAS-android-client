package fly.speedmeter.grub.utils;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class PenumpangSharedViewModel extends ViewModel {
    private final MutableLiveData<Boolean> selected = new MutableLiveData<Boolean>();

    public void select(Boolean item) {
        selected.setValue(item);
    }

    public LiveData<Boolean> getSelected() {
        return selected;
    }
}
