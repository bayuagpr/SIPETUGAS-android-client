package fly.speedmeter.grub.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Penumpang {
    @SerializedName("naik_anak")
    @Expose
    private Integer naik_anak;
    @SerializedName("naik_dewasa")
    @Expose
    private Integer naik_dewasa;
    @SerializedName("naik_manula")
    @Expose
    private Integer naik_manula;
    @SerializedName("turun_anak")
    @Expose
    private Integer turun_anak;
    @SerializedName("turun_dewasa")
    @Expose
    private Integer turun_dewasa;
    @SerializedName("turun_manula")
    @Expose
    private Integer turun_manula;

    @SerializedName("jumlah")
    @Expose
    private Integer jumlah;

    @SerializedName("lokasi")
    @Expose
    private String lokasi;

    @SerializedName("bus_id")
    @Expose
    private Integer bus_id;

    @SerializedName("supir_id")
    @Expose
    private Integer supir_id;

    public Integer getNaik_anak() {
        return naik_anak;
    }

    public void setNaik_anak(Integer naik_anak) {
        this.naik_anak = naik_anak;
    }

    public Integer getNaik_dewasa() {
        return naik_dewasa;
    }

    public void setNaik_dewasa(Integer naik_dewasa) {
        this.naik_dewasa = naik_dewasa;
    }

    public Integer getNaik_manula() {
        return naik_manula;
    }

    public void setNaik_manula(Integer naik_manula) {
        this.naik_manula = naik_manula;
    }

    public Integer getTurun_anak() {
        return turun_anak;
    }

    public void setTurun_anak(Integer turun_anak) {
        this.turun_anak = turun_anak;
    }

    public Integer getTurun_dewasa() {
        return turun_dewasa;
    }

    public void setTurun_dewasa(Integer turun_dewasa) {
        this.turun_dewasa = turun_dewasa;
    }

    public Integer getTurun_manula() {
        return turun_manula;
    }

    public void setTurun_manula(Integer turun_manula) {
        this.turun_manula = turun_manula;
    }

    public Integer getJumlah() {
        return jumlah;
    }

    public void setJumlah(Integer jumlah) {
        this.jumlah = jumlah;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public Integer getBus_id() {
        return bus_id;
    }

    public void setBus_id(Integer bus_id) {
        this.bus_id = bus_id;
    }

    public Integer getSupir_id() {
        return supir_id;
    }

    public void setSupir_id(Integer supir_id) {
        this.supir_id = supir_id;
    }



}