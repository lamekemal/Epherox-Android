package bj.archeos.epherox;


import bj.archeos.epherox.model.EphxGPojo;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface gamify {
    @GET("epherox/v0/put/{email}&&{score}&&{lastesave}&&{securekey}")
    Call<EphxGPojo> saveScore(@Path("email") String email, @Path("score") Integer score, @Path("lastesave") String lastesave, @Path("securekey") String securekey);

    @GET("epherox/v0/get-auth/{email}&&{pin}")
    Call<EphxGPojo> getUser(@Path("email") String email, @Path("pin") Integer pin);

    @GET("epherox/v0/put-auth/{email}&&{pin}&&{score}")
    Call<EphxGPojo> setUser(@Path("email") String email, @Path("pin") Integer pin, @Path("score") Integer score);
}
