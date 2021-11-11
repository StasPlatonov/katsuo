package bmg.katsuo.gameplay;

import java.util.Date;

public class GameProgressDescription
{
    public GameProgressDescription(String playerName, String levelId, String checkPointId, Date progressDate, int score)
    {
        PlayerName = playerName;
        LevelId = levelId;
        CheckPointId = checkPointId;
        ProgressDate = progressDate;

        SetScore(score);
    }

    public GameProgressDescription(String playerName, String levelId, String checkPointId, int score)
    {
        this(playerName, levelId, checkPointId, new Date(), score);
    }

    public GameProgressDescription(String playerName, String levelId, int score)
    {
        this(playerName, levelId, null, score);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public static GameProgressDescription EOG(String playerName)
    {
        return new GameProgressDescription(playerName, GameLevelDescription.EOG, 0);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public String GetPlayerName()
    {
        return PlayerName;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public String GetLevelId()
    {
        return LevelId;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public String GetCheckpointId()
    {
        return CheckPointId;
    }

    public GameProgressDescription SetCheckpointId(String checkpointId)
    {
        CheckPointId = checkpointId;
        return this;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public GameProgressDescription SetLevel(String levelId)
    {
        LevelId = levelId;
        return this;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public GameProgressDescription Update(String levelId, String checkpointId)
    {
        LevelId = levelId;
        CheckPointId = checkpointId;
        ProgressDate = new Date();
        return this;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public Date GetProgressDate()
    {
        return ProgressDate;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private String PlayerName;
    private String LevelId;
    private String CheckPointId;
    private Date ProgressDate;
    private int Score;

    public int GetScore()
    {
        return Score;
    }

    public void SetScore(int score)
    {
        Score = score;
    }
}
