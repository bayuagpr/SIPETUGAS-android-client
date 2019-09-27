package fly.speedmeter.grub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Darurat {
    @Expose
    @SerializedName("kategori")
    private String kategori;

    @Expose
    @SerializedName("keterangan")
    private String keterangan;

    @Expose
    @SerializedName("lokasi")
    private String lokasi;

    @Expose
    @SerializedName("bus_id")
    private String bus_id;

    @Expose
    @SerializedName("supir_id")
    private String supir_id;

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public String getBus_id() {
        return bus_id;
    }

    public void setBus_id(String bus_id) {
        this.bus_id = bus_id;
    }

    public String getSupir_id() {
        return supir_id;
    }

    public void setSupir_id(String supir_id) {
        this.supir_id = supir_id;
    }


}
