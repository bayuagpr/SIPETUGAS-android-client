package fly.speedmeter.grub.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import fly.speedmeter.grub.model.Kecepatan;

public class KecepatanResponse extends BaseResponse {

    @Expose
    @SerializedName("data") List<Kecepatan> data;

    public List<Kecepatan> getData() {
        return data;
    }

    public void setData(List<Kecepatan> data) {
        this.data = data;
    }
}
