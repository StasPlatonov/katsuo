package bmg.katsuo.network;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ScoresData
{
    public List<ScoreData> getScores()
    {
        return scores;
    }

    public void setScores(List<ScoreData> scores)
    {
        this.scores = scores;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private List<ScoreData> scores = new ArrayList<ScoreData>();

    //-------------------------------------------------------------------------------------------------------------------------

    public void Sort()
    {
        scores.sort(new Comparator<ScoreData>()
        {
            @Override
            public int compare(ScoreData left, ScoreData right)
            {
                return (right.getScore() - left.getScore());
            }
        });
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        for (ScoreData score : scores)
        {
            sb.append(score.toString() + " ");
        }

        return sb.toString();
    }
    //-------------------------------------------------------------------------------------------------------------------------
}
