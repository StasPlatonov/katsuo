package bmg.katsuo.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.util.List;

public interface NetworkService
{
    // Get scores table for all users
    @GET("api/score")
    Call<ScoresData> GetScore();

    // Change score for specific user
    @POST("api/score")
    Call<String> SetScore(@Query("score") ScoreData score);

    // Check or download update
    @GET("api/update")
    Call<UpdateData> GetUpdate(@Query("lite") Boolean lite);

    // Get available levels
    @GET("api/levels")
    Call<LevelsData> GetLevels();

    // Download specific level
    @GET("api/level")
    Call<LevelData> GetLevel(@Query("id") String id);
}
