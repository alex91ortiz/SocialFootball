package jcsoluciones.com.socialfootball;

import org.json.JSONArray;
import org.json.JSONObject;
import jcsoluciones.com.socialfootball.models.RequestInviteBody;
import jcsoluciones.com.socialfootball.models.RequestTeamBody;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface RequestInterface {

    @POST("team")
    Call<RequestTeamBody> registerTeam(@Body RequestTeamBody body);

    @PUT("team")
    Call<RequestTeamBody> updateTeam(@Body RequestTeamBody body);

    @GET("team/{email}/{token}")
    Call<JSONObject> getTeams(@Path("email") String email,@Path("token") String token);

    @GET("searchteams/{name}/{city}/{email}")
    Call<JSONArray> searchTeams(@Path("name") String name,@Path("city") String city,@Path("email") String email);

    @POST("invites/{email}")
    Call<JSONArray> invites(@Path("email") String email);

    @Multipart
    @POST("upload")
    Call<JSONObject> upload(@Part("id") RequestBody description,@Part MultipartBody.Part file);

    @POST("invite")
    Call<RequestInviteBody> registerInvite(@Body RequestInviteBody body);
}
