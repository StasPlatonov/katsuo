package bmg.katsuo.network;

import bmg.katsuo.Globals;

import java.util.Date;

public class UpdateData
{
    private int version;
    private String name;
    private String description;

    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private Date date;
    private long dataSize;
    private byte[] data;
    //-------------------------------------------------------------------------------------------------------------------------

    public int getVersion()
    {
        return version;
    }

    public void setVersion(int version)
    {
        this.version = version;
    }
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

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
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

    public long getDataSize()
    {
        return dataSize;
    }

    public void setDataSize(long dataSize)
    {
        this.dataSize = dataSize;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public String toString()
    {
        return getName() + "(" + getVersion() + " created on " + Globals.DateTimeFormat.format(getDate()) + "): " + getDescription();
    }
    //-------------------------------------------------------------------------------------------------------------------------
}
