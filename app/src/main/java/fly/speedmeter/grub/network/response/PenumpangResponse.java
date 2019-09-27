package fly.speedmeter.grub.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import fly.speedmeter.grub.model.Penumpang;

public class PenumpangResponse extends BaseResponse {

    @Expose
    @SerializedName("data") List<Penumpang> data;

    public List<Penumpang> getData() {
        return data;
    }

    public void setData(List<Penumpang> data) {
        this.data = data;
    }
}
