package bmg.katsuo.network;

public interface ScoreHandler
{
    void OnScoreReceived(ScoresData scores);
    void OnScoreError(String error);
    void OnScoreSet(String result);
    void OnIconUpdated(ScoreData item);
}
