package fly.speedmeter.grub.network;

import java.util.List;

import fly.speedmeter.grub.model.Bus;
import fly.speedmeter.grub.model.Kecepatan;
import fly.speedmeter.grub.model.Penumpang;
import fly.speedmeter.grub.model.Supir;
import fly.speedmeter.grub.network.response.AuthResponse;
import fly.speedmeter.grub.network.response.BusResponse;
import fly.speedmeter.grub.network.response.DaruratResponse;
import fly.speedmeter.grub.network.response.KecepatanResponse;
import fly.speedmeter.grub.network.response.PenumpangResponse;
import fly.speedmeter.grub.network.response.SebuahBusResponse;
import fly.speedmeter.grub.network.response.SebuahSupirResponse;
import fly.speedmeter.grub.network.response.SupirResponse;
import fly.speedmeter.grub.network.response.UserResponse;
import io.reactivex.Completable;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

//Auth
    @FormUrlEncoded
    @POST("login")
    Observable<AuthResponse> postAuth(@Field("email") String username,
                                      @Field("password") String password);

    @FormUrlEncoded
    @POST("login")
    Observable<AuthResponse> postLogout(@Header("Authorization") String token,
                                      @Field("token") String accessToken);

    @POST("penumpangs")
    @FormUrlEncoded
    Observable<PenumpangResponse> savePost(@Header("Authorization") String token,
                                           @Field("naik_anak") int naik_anak,
                                           @Field("naik_dewasa") int naik_dewasa,
                                           @Field("naik_manula") int naik_manula,
                                           @Field("turun_anak") int turun_anak,
                                           @Field("turun_dewasa") int turun_dewasa,
                                           @Field("turun_manula") int turun_manula,
                                           @Field("lokasi") String lokasi,
                                           @Field("jumlah") int jumlah ,
                                           @Field("bus_id") int bus_id,
                                           @Field("supir_id") int supir_id );

    @POST("monitoring")
    @FormUrlEncoded
    Observable<KecepatanResponse> saveStat(@Header("Authorization") String token,
                                           @Field("status") int status,
                                           @Field("bus_id") int bus_id,
                                           @Field("supir_id") int supir_id);
    @POST("darurat")
    @FormUrlEncoded
    Observable<DaruratResponse> saveEmerge(@Header("Authorization") String token,
                                           @Field("kategori") String kategori,
                                           @Field("keterangan") String keterangan,
                                           @Field("lokasi") String lokasi,
                                           @Field("bus_id") int bus_id,
                                           @Field("supir_id") int supir_id);

    @GET("users/current")
    Observable<UserResponse> getUser(@Header("Authorization") String token);

    @GET("bus")
    Observable<BusResponse> groupListBus(@Header("Authorization") String token);

    @GET("bus/search/{plat_nomer}")
    Observable<SebuahBusResponse> getPlatBus(@Header("Authorization") String token,
                                             @Path("plat_nomer") String platNomer);

    @GET("supirs/search/{nama_supir}")
    Observable<SebuahSupirResponse> getNamaSupir(@Header("Authorization") String token,
                                                 @Path("nama_supir") String namaSupir);

    @GET("supirs")
    Observable<SupirResponse> groupListSupir(@Header("Authorization") String token);


}
