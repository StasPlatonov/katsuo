package bmg.katsuo.utils;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Logger
{
    private FileHandle m_LogFile = null;
    private static Logger instance = null;

    public static final int LOG_LEVEL_NONE = 0;
    public static final int LOG_LEVEL_ERROR = 1;
    public static final int LOG_LEVEL_INFORMATION = 2;
    public static final int LOG_LEVEL_DEBUG = 3;

    private int Level = LOG_LEVEL_DEBUG;
    //----------------------------------------------------------------------------------------------

    private static Logger GetInstance()
    {
        if (instance == null)
        {
            instance = new Logger(Gdx.files);
        }
        return instance;
    }
    //----------------------------------------------------------------------------------------------

    public static void SetLogLevel(int level)
    {
        GetInstance().SetLevel(level);
    }

    public static int GetLogLevel()
    {
        return GetInstance().GetLevel();
    }
    //----------------------------------------------------------------------------------------------

    public static void Debug(String tag, String text)
    {
        Gdx.app.debug(tag, text);
        GetInstance().LogString(LOG_LEVEL_DEBUG, tag, text, true);
    }

    public static void Info(String tag, String text)
    {
        Gdx.app.log(tag, text);
        GetInstance().LogString(LOG_LEVEL_INFORMATION, tag, text, true);
    }

    public static void Error(String tag, String text)
    {
        Gdx.app.error(tag, text);
        GetInstance().LogString(LOG_LEVEL_ERROR, tag, "ERROR: " + text, true);
    }
    //----------------------------------------------------------------------------------------------

    public Logger(Files files)
    {
        final String FILE_NAME = "katsuo.log";
        try
        {
            m_LogFile = (Gdx.app.getType() == Application.ApplicationType.Desktop) ?
                files.local("../../" + FILE_NAME) :
                files.absolute(files.getExternalStoragePath() + "/Katsuo/" + FILE_NAME);
        }
        catch (Exception e)
        {
            m_LogFile = null;
        }

        LogString(Level, "Logger", "Init logging system [" + m_LogFile.path() + "]", false);
    }
    //----------------------------------------------------------------------------------------------

    public void SetLevel(int level)
    {
        Level = level;
        int appLogLevel = Application.LOG_DEBUG;

        switch (Level)
        {
            case LOG_LEVEL_NONE:
                appLogLevel = Application.LOG_NONE;
                break;
            case LOG_LEVEL_ERROR:
                appLogLevel = Application.LOG_ERROR;
                break;
            case LOG_LEVEL_INFORMATION:
                appLogLevel = Application.LOG_INFO;
                break;
            case LOG_LEVEL_DEBUG:
                appLogLevel = Application.LOG_DEBUG;
                break;
        }

        Gdx.app.setLogLevel(appLogLevel);
    }
    //----------------------------------------------------------------------------------------------

    public int GetLevel()
    {
        return Level;
    }
    //----------------------------------------------------------------------------------------------

    private SimpleDateFormat DTF = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");

    private void LogString(int level, String tag, String text, boolean append)
    {
        if (level > Level)
        {
            return;
        }

        //long curTime = System.currentTimeMillis();

        //sdf.setTimeZone(TimeZone.getDefault());
        final String timeStr = DTF.format(new Date());

        final String str = timeStr + " [" + tag + "] " + text + System.getProperty("line.separator");

        synchronized (m_LogFile)
        {
            if (m_LogFile != null)
            {
                try
                {
                    m_LogFile.writeString(str, append);
                }
                catch (Exception e)
                {
                    Gdx.app.log("Logger", str + "(" + e.getMessage() + ")");
                }
            }
        }
    }
    //----------------------------------------------------------------------------------------------
}
