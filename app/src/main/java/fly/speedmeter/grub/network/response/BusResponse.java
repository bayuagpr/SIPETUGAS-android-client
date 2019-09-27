package fly.speedmeter.grub.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import fly.speedmeter.grub.model.Bus;

public class BusResponse extends BaseResponse {

    @Expose
    @SerializedName("data") List<Bus> data;

    public List<Bus> getData() {
        return data;
    }

    public void setData(List<Bus> data) {
        this.data = data;
    }
}
