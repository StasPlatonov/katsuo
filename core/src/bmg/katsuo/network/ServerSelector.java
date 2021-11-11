package bmg.katsuo.network;

import bmg.katsuo.IApplication;
import com.badlogic.gdx.Preferences;

public class ServerSelector
{
    private final static String TAG = "ServerSelector";
    private IApplication App;
    private Preferences Prefs;
    private String[] Servers;
    private int ServerIndex = -1;
    //-------------------------------------------------------------------------------------------------------------------------

    public ServerSelector(IApplication app)
    {
        App = app;
        Prefs = App.GetPreferences();

        final String servers = Prefs.getString("Servers", NetworkServiceFactory.DefaultServers);
        Servers = servers.trim().split("\\s*,\\s*");

        if (Servers.length == 0)
        {
            ServerIndex = -1;
        }
        else
        {
            // try to start from last attempt
            if (Prefs.contains("LastServer"))
            {
                int lastServerIndex = Prefs.getInteger("LastServer", -1);
                if ((lastServerIndex >= 0) && (lastServerIndex < Servers.length))
                {
                    ServerIndex = lastServerIndex;
                }
            }
            else
            {
                ServerIndex = 0;
            }
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public String GetCurrentServer()
    {
        if (Servers.length <= 0)
            return "";

        return "http://" + Servers[ServerIndex];
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void Next()
    {
        if (Servers.length <= 0)
        {
            return;
        }

        ServerIndex = (ServerIndex + 1) % Servers.length; // switch to next server

        App.Log(TAG, "Switch to next server: " + Servers[ServerIndex]);

        Prefs.putInteger("LastServer", ServerIndex);
        Prefs.flush();
    }
    //-------------------------------------------------------------------------------------------------------------------------
}
