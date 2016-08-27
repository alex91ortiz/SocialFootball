package jcsoluciones.com.socialfootball;

import java.util.List;

import jcsoluciones.com.socialfootball.models.RequestTeamBody;
import jcsoluciones.com.socialfootball.models.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RequestInterface {

    @POST("team")
    Call<ResponseBody> registerTeam(@Body RequestTeamBody body);

    @GET("searchteams/{name}/{city}")
    Call<List<RequestTeamBody>> searchTeams(@Path("name") String name,@Path("city") String city);
}
