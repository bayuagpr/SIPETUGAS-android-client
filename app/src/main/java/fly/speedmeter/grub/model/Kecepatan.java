package fly.speedmeter.grub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Kecepatan {


    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("bus_id")
    @Expose
    private Integer busId;
    @SerializedName("supir_id")
    @Expose
    private Integer supirId;


    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getBusId() {
        return busId;
    }

    public void setBusId(Integer busId) {
        this.busId = busId;
    }

    public Integer getSupirId() {
        return supirId;
    }

    public void setSupirId(Integer supirId) {
        this.supirId = supirId;
    }



}
