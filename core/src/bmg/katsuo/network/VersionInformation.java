package bmg.katsuo.network;

public class VersionInformation
{
    private int VersionNumber;
    private String VersionDescription;
    private String Server;
    //-------------------------------------------------------------------------------------------------------------------------

    VersionInformation(int versionNumber, String versionDescription, String server)
    {
        VersionNumber = versionNumber;
        VersionDescription = versionDescription;
        Server = server;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public String toString()
    {
        return GetServer() + ": '" + GetVersionDescription() + "' (" + GetVersionNumber() + ")";
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public int GetVersionNumber()
    {
        return VersionNumber;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public String GetVersionDescription()
    {
        return VersionDescription;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public String GetServer()
    {
        return Server;
    }
    //-------------------------------------------------------------------------------------------------------------------------
}
