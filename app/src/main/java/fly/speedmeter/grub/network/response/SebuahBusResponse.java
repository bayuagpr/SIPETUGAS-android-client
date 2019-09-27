package fly.speedmeter.grub.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import fly.speedmeter.grub.model.Bus;

public class SebuahBusResponse extends BaseResponse {


    @Expose
    @SerializedName("data") Bus sebuahData;

    public Bus getSebuahData() {
        return sebuahData;
    }

    public void setSebuahData(Bus sebuahData) {
        this.sebuahData = sebuahData;
    }


}
