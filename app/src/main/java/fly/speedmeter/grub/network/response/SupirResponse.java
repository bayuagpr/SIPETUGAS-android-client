package fly.speedmeter.grub.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import fly.speedmeter.grub.model.Supir;

public class SupirResponse extends BaseResponse {

    @Expose
    @SerializedName("data") List<Supir> data;

    public List<Supir> getData() {
        return data;
    }

    public void setData(List<Supir> data) {
        this.data = data;
    }
}
