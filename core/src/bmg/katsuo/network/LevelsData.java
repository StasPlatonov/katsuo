package bmg.katsuo.network;

import java.util.ArrayList;
import java.util.List;

public class LevelsData
{
    public List<LevelData> getLevels()
    {
        return levels;
    }

    public void setLevels(List<LevelData> levels)
    {
        this.levels = levels;
    }

    private List<LevelData> levels = new ArrayList<LevelData>();
}
