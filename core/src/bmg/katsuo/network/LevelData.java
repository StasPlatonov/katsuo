package bmg.katsuo.network;

public class LevelData
{
    private String name;
    private String id;
    private String description;
    private short rating;
    private String creator;
    private byte[] screenshot;
    private boolean available;
    private long dataSize;
    private byte[] data;
    //-------------------------------------------------------------------------------------------------------------------------

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public short getRating()
    {
        return rating;
    }

    public void setRating(short rating)
    {
        this.rating = rating;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public String getCreator()
    {
        return creator;
    }

    public void setCreator(String creator)
    {
        this.creator = creator;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public byte[] getScreenshot()
    {
        return screenshot;
    }

    public void setScreenshot(byte[] screenshot)
    {
        this.screenshot = screenshot;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public boolean isAvailable()
    {
        return available;
    }

    public void setAvailable(boolean available)
    {
        this.available = available;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public long getDataSize()
    {
        return dataSize;
    }

    public void setDataSize(long dataSize)
    {
        this.dataSize = dataSize;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public byte[] getData()
    {
        return data;
    }

    public void setData(byte[] data)
    {
        this.data = data;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public String toString()
    {
        return getName() + "(id:" + getId() + ", size:" + getDataSize() + ")";
    }
}
//-------------------------------------------------------------------------------------------------------------------------

