package fly.speedmeter.grub.network.response;

import java.util.List;

import fly.speedmeter.grub.model.Darurat;

public class DaruratResponse extends BaseResponse{
    List<Darurat> data;

    public List<Darurat> getData() {
        return data;
    }

    public void setData(List<Darurat> data) {
        this.data = data;
    }
}
