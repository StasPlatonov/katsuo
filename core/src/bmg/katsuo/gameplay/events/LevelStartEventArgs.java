package bmg.katsuo.gameplay.events;

public class LevelStartEventArgs implements EventArgs
{
    public String LevelId;

    public LevelStartEventArgs(String levelId)
    {
        LevelId = levelId;
    }
}
