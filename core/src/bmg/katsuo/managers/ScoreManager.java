package bmg.katsuo.managers;

import bmg.katsuo.Globals;
import bmg.katsuo.IApplication;
import bmg.katsuo.network.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpStatus;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.util.*;

public class ScoreManager
{
    private final static String TAG = "ScoreManager";
    private IApplication App;
    /*
    private Map<String, UserStatistic> UsersStatistic = new HashMap<String, UserStatistic>();
    //-------------------------------------------------------------------------------------------------------------------------

    public class LevelStatistic
    {
        String LevelId;
        long PlayTime;
        boolean Complete;
        int Score;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public class UserStatistic
    {
        public UserStatistic()
        {

        }

        Map<String, LevelStatistic> LevelsStatistic = new HashMap<String, LevelStatistic>();
    }
    //-------------------------------------------------------------------------------------------------------------------------
*/
    public ScoreManager(IApplication app)
    {
        App = app;
    }
    //-------------------------------------------------------------------------------------------------------------------------

/*
    private class DefaultScoreHandler implements ScoreHandler
    {
        @Override
        public void OnScoreReceived(ScoresData scores)
        {
            App.Log(TAG, "Scores received: " + scores);
        }
        //-------------------------------------------------------------------------------------------------------------------------

        @Override
        public void OnScoreError(String error)
        {
            App.Error(TAG, "Failed to get score: " + error);
        }
        //-------------------------------------------------------------------------------------------------------------------------

        @Override
        public void OnScoreSet(String result)
        {
            App.Log(TAG, "Score set:" + result);
        }
        //-------------------------------------------------------------------------------------------------------------------------
    }

    private DefaultScoreHandler Handler;*/
    //-------------------------------------------------------------------------------------------------------------------------

    private ScoresData GenerateScores()
    {
        ScoresData data = new ScoresData();

        List<ScoreData> scores = new ArrayList<ScoreData>();
        for (int i = 0; i < 15; ++i)
        {
            ScoreData score = new ScoreData();
            score.setUser("User_" + (i + 1));
            score.setScore((int)(Math.random() * 100F));
            scores.add(score);
        }
        data.setScores(scores);

        return data;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void GetLocalScore(final ScoreHandler handler)
    {
        ScoresData scores = GenerateScores();
        handler.OnScoreReceived(scores);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void GetGlobalScore(final ScoreHandler handler)
    {
        App.GetCloudServices().GetScores(handler);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void SetScore(String user, int score, final ScoreHandler handler)
    {
        App.GetCloudServices().PostScore(score, handler);
    }
    //-------------------------------------------------------------------------------------------------------------------------
}