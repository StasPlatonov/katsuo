package bmg.katsuo.gameplay;

import bmg.katsuo.managers.ResourceManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlWriter;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GameProgress
{
    private final static String TAG = "PROGRESS";

    private Map<String, GameProgressDescription> Data = new HashMap<String, GameProgressDescription>();

    private final static SimpleDateFormat Fmt = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");
    //-------------------------------------------------------------------------------------------------------------------------

    public GameProgress()
    {
        Load();
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public static GameProgressDescription EOG(String playerName)
    {
        return GameProgressDescription.EOG(playerName);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void Save()
    {
        try
        {
            FileHandle handle = ResourceManager.CreateApplicationFile("progress.xml", null);

            Writer writer = new BufferedWriter(new OutputStreamWriter(handle.write(false)));
            XmlWriter xml = new XmlWriter(writer);

            xml.element("progress");

            Set<String> users = Data.keySet();
            for (String user : users)
            {
                final GameProgressDescription progress = Data.get(user);
                SaveToXmlWriter(progress, xml);
            }

            xml.pop();
            xml.close();

            Gdx.app.log(TAG, "Progress saved to " + handle.path());
        }
        catch (Exception e)
        {
            Gdx.app.error(TAG, "Failed to save progress: " + e.getMessage());
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private void SaveToXmlWriter(GameProgressDescription progress, XmlWriter writer) throws Exception
    {
        writer.element("player");

        writer.attribute("id", progress.GetPlayerName());
        writer.attribute("level", progress.GetLevelId());
        writer.attribute("check_point", progress.GetCheckpointId());
        writer.attribute( "date", Fmt.format(progress.GetProgressDate()));
        writer.attribute("score", progress.GetScore());

        writer.pop();
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public GameProgressDescription GetUserProgress(String user)
    {
        return Data.containsKey(user) ? Data.get(user) : null;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void SetUserProgress(GameProgressDescription progress)
    {
        Data.put(progress.GetPlayerName(), progress);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void Load()
    {
        Data.clear();

        try
        {
            FileHandle handle = ResourceManager.CreateApplicationFile("progress.xml", null);

            XmlReader xmlReader = new XmlReader();
            XmlReader.Element root = xmlReader.parse(handle);

            if (!root.getName().equals("progress"))
            {
                throw new Exception("Invalid node name " + root.getName() + " for progress data");
            }

            Array<XmlReader.Element> usersElements = root.getChildrenByName("player");
            for (XmlReader.Element element : usersElements)
            {
                GameProgressDescription progress = LoadFromXMLElement(element);
                if (progress == null)
                {
                    throw new Exception("Invalid progress record");
                }
                Data.put(progress.GetPlayerName(), progress);
            }
            Gdx.app.log(TAG, "Progress loaded from " + handle.path());
        }
        catch (Exception e)
        {
            Gdx.app.error(TAG, "Failed to load progress data: " + e.getMessage());
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private GameProgressDescription LoadFromXMLElement(XmlReader.Element xml) throws Exception
    {
        if (!xml.getName().equals("player"))
        {
            return null;
        }

        String name = xml.getAttribute("id");
        String levelId = xml.getAttribute("level");
        String checkPointId = xml.getAttribute("check_point");
        Date progressDate = Fmt.parse(xml.getAttribute("date"));
        int score = Integer.parseInt(xml.getAttribute("score", "0"));

        GameProgressDescription result = new GameProgressDescription(name, levelId, checkPointId, progressDate, score);

        Gdx.app.log(TAG, "Progress for " + name + " loaded (" + levelId + "," + checkPointId + "," + progressDate +")");

        return result;
    }
    //-------------------------------------------------------------------------------------------------------------------------
}
