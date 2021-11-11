package bmg.katsuo.managers;

import bmg.katsuo.network.LevelData;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class LevelItem
{
    private String Id;
    private String Name;
    private String Description;
    private short Rating;
    private String Creator;
    private Sprite Screenshot;
    private int Index;
    private boolean Local;
    private boolean Available;
    private boolean ShowName = false;
    private boolean ShowIndex = true;

    public LevelItem(String id, String name, short rating, int index)
    {
        setId(id);
        setName(name);
        setDescription(name);
        setRating(rating);
        setIndex(index);
        setLocal(true);
        setAvailable(true);
    }

    public LevelItem(LevelData data)
    {
        setId(data.getId());
        setName(data.getName());
        setDescription(data.getDescription());
        setRating(data.getRating());
        setCreator(data.getCreator());
        setAvailable(data.isAvailable());
    }

    public String getName()
    {
        return Name;
    }

    public void setName(String name)
    {
        Name = name;
    }

    public String getId()
    {
        return Id;
    }

    public void setId(String id)
    {
        Id = id;
    }

    public String getDescription()
    {
        return Description;
    }

    public void setDescription(String description)
    {
        Description = description;
    }

    public short getRating()
    {
        return Rating;
    }

    public void setRating(short rating)
    {
        Rating = rating;
    }

    public String getCreator()
    {
        return Creator;
    }

    public void setCreator(String creator)
    {
        Creator = creator;
    }

    public Sprite getScreenshot()
    {
        return Screenshot;
    }

    public void setScreenshot(Sprite screenshot)
    {
        Screenshot = screenshot;
    }

    public int getIndex()
    {
        return Index;
    }

    public void setIndex(int index)
    {
        Index = index;
    }

    public boolean isLocal()
    {
        return Local;
    }

    public void setLocal(boolean local)
    {
        Local = local;
    }

    public boolean isAvailable()
    {
        return Available;
    }

    public void setAvailable(boolean available)
    {
        Available = available;
    }

    public void setShowName(boolean showName) { ShowName = showName; }

    public boolean isShowName() { return ShowName; }

    public void setShowIndex(boolean showIndex) { ShowIndex = showIndex; }

    public boolean isShowIndex() { return ShowIndex; }
}
//-------------------------------------------------------------------------------------------------------------------------

