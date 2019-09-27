package fly.speedmeter.grub.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import fly.speedmeter.grub.model.Supir;

public class SebuahSupirResponse extends BaseResponse {


    @Expose
    @SerializedName("data") Supir sebuahData;

    public Supir getSebuahData() {
        return sebuahData;
    }

    public void setSebuahData(Supir sebuahData) {
        this.sebuahData = sebuahData;
    }


}
