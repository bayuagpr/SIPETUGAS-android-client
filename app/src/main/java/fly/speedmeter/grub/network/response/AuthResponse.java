package fly.speedmeter.grub.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import fly.speedmeter.grub.model.Auth;

public class AuthResponse extends BaseResponse{

    @Expose
    @SerializedName("data")
    Auth data;

    public Auth getData() {
        return data;
    }

    public void setData(Auth data) {
        this.data = data;
    }
}
