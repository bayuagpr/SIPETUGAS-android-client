package fly.speedmeter.grub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Bus {
    @SerializedName("id")
    @Expose
    private Integer idBus;
    @SerializedName("plat_nomer")
    @Expose
    private String plat_nomer;
    @SerializedName("kapasitas")
    @Expose
    private Integer kapasitas;
    @SerializedName("status")
    @Expose
    private String status;


    // Getter Methods

    public Integer getIdBus() {
        return idBus;
    }

    public String getPlat_nomer() {
        return plat_nomer;
    }

    public float getKapasitas() {
        return kapasitas;
    }

    public String getStatus() {
        return status;
    }


    // Setter Methods

    public void setIdBus(Integer idBus) {
        this.idBus = idBus;
    }

    public void setPlat_nomer(String plat_nomer) {
        this.plat_nomer = plat_nomer;
    }

    public void setKapasitas(Integer kapasitas) {
        this.kapasitas = kapasitas;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
