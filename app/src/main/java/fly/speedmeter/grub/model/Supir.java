package fly.speedmeter.grub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Supir {
    @SerializedName("id")
    @Expose
    private Integer idSupir;
    @SerializedName("nama_supir")
    @Expose
    private String nama_supir;
    @SerializedName("status")
    @Expose
    private String status;


    // Getter Methods

    public Integer getIdSupir() {
        return idSupir;
    }

    public String getNama_supir() {
        return nama_supir;
    }

    public String getStatus() {
        return status;
    }


    // Setter Methods

    public void setIdSupir(Integer id) {
        this.idSupir = id;
    }

    public void setNama_supir(String nama_supir) {
        this.nama_supir = nama_supir;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
