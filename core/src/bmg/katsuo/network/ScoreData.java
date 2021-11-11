package bmg.katsuo.network;

import com.badlogic.gdx.graphics.Pixmap;

import bmg.katsuo.Globals;
//import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class ScoreData
{
    private String user;
    private int score;
    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private Date date;

    private Pixmap Icon;
    //-------------------------------------------------------------------------------------------------------------------------

    public ScoreData()
    {
        this("", 0);
    }

    public ScoreData(String user, int score)
    {
        this(user, score, new Date());
    }

    public ScoreData(String user, int score, Date date)
    {
        setUser(user);
        setScore(score);
        setDate(date);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public String getUser()
    {
        return user;
    }

    public void setUser(String user)
    {
        this.user = user;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public int getScore()
    {
        return score;
    }

    public void setScore(int score)
    {
        this.score = score;
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
        return Globals.DateTimeFormatScores.format(getDate()) + ": " + getUser() + " - " + getScore();
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void SetPixmap(Pixmap pm)
    {
        Icon = pm;
    }

    public Pixmap GetPixmap()
    {
        return Icon;
    }
};
//-------------------------------------------------------------------------------------------------------------------------

