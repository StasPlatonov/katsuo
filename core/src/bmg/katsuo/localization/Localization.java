package bmg.katsuo.localization;

import bmg.katsuo.Globals;
import bmg.katsuo.utils.Logger;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.I18NBundle;

import java.util.*;

public class Localization
{
    private final static String TAG = "Locatlization";

    private Map<String, I18NBundle> Bundles = new HashMap<String, I18NBundle>();

    private final static String DefaultFontName = "Oswald-Regular";
    private Map<String, String> SupportedLanguages = new HashMap<String, String>();
    private String CurrentLanguage;

    //private Map<String, BitmapFont> Fonts = new HashMap<String, BitmapFont>();
    private Map<String, Map<Integer, BitmapFont>> Fonts = new HashMap<String, Map<Integer, BitmapFont>>();

    private BitmapFont DefaultFont;

    public Localization(String currentLanguage)
    {
        CurrentLanguage = (currentLanguage == null) ? "en" : currentLanguage;
        DefaultFont = new BitmapFont();

        FileHandle fh = Gdx.files.internal("Fonts/MyBundle");

        Array<String> langIds = ReadSupportedLanguages();
        for (String langId : langIds)
        {
            I18NBundle bundle = I18NBundle.createBundle(fh, new Locale(langId));
            Bundles.put(langId, bundle);
            SupportedLanguages.put(langId, bundle.format("language_desc"));

            //Fonts.put(langId, CreateFont(langId, Globals.UI_FONT_SIZE));

            Map<Integer, BitmapFont> sizedFonts = new HashMap<Integer, BitmapFont>();
            sizedFonts.put(Globals.UI_FONT_SIZE, CreateFont(langId, Globals.UI_FONT_SIZE));

            Fonts.put(langId, sizedFonts);
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void dispose()
    {
        Set<String> langs = Fonts.keySet();

        for (String lang : langs)
        {
            Map<Integer, BitmapFont> sizedFonts = Fonts.get(lang);

            Set<Integer> sizes = sizedFonts.keySet();
            for (int size : sizes)
            {
                Logger.Debug(TAG, "Destroying font " + lang + " of size " + size);
                BitmapFont font = sizedFonts.get(size);
                sizedFonts.put(size, null);

                font.dispose();
            }
            sizedFonts.clear();
        }

        Fonts.clear();
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public BitmapFont GetDefaultFont()
    {
        return DefaultFont;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public String GetCurrentLanguage()
    {
        return CurrentLanguage;
    }

    public void SetCurrentLanguage(String langId)
    {
        CurrentLanguage = (langId == null) ? "en" : langId;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public BitmapFont GetCurrentFont()
    {
        return GetFont(CurrentLanguage);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public BitmapFont GetFont(String langId)
    {
        return GetFont(langId,  Globals.UI_FONT_SIZE);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public BitmapFont GetFont(String langId, int size)
    {
        if (Fonts.containsKey(langId))
        {
            Map<Integer, BitmapFont> sizedFonts = Fonts.get(langId);

            if (sizedFonts.containsKey(size))
            {
                return sizedFonts.get(size);
            }
        }

        return CreateFont(langId, size);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private Array<String> ReadSupportedLanguages()
    {
        Array<String> result = new Array<String>();
        //@TODO: enumerate bundle files
        result.add("en");
        result.add("ru");
        result.add("de");
        result.add("it");

        return result;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public Map<String, String> GetSupportedLanguages()
    {
        return SupportedLanguages;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public String GetLocalizedString(String id, Object... args)
    {
        if (Bundles.containsKey(CurrentLanguage))
        {
            I18NBundle bundle = Bundles.get(CurrentLanguage);
            try
            {
                return bundle.format(id, args);
            }
            catch (Exception e)
            {
            }
        }

        return id;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private BitmapFont CreateFont(String langId, int size)
    {
        return CreateFont(langId, size, DefaultFontName);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private BitmapFont CreateFont(String langId, int size, String fontName)
    {
        return CreateFont(langId, size, fontName, false, true);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public BitmapFont CreateFont(String langId, int size, boolean outlined, boolean smoothed)
    {
        return CreateFont(langId, size, DefaultFontName, outlined, smoothed);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private BitmapFont CreateFont(String langId, int size, String fontName, boolean outlined, boolean smoothed)
    {
        if (!SupportedLanguages.containsKey(langId))
        {
            Logger.Error(TAG, "Language " + langId + " is not supported for creation. Use default font");
            return DefaultFont;
        }

        Logger.Debug(TAG, "Creating font '" + fontName + "' of size " + size + " for language " + langId);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Fonts/" + fontName + ".ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = size;// * Gdx.graphics.getDensity());
        param.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "абвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";
        if (outlined)
        {
            param.borderColor = Color.BLACK;
            param.borderWidth = 2;
        }
        if (smoothed)
        {
            param.minFilter = Texture.TextureFilter.Linear;
            param.magFilter = Texture.TextureFilter.Linear;
        }
        else
        {
            param.minFilter = Texture.TextureFilter.Nearest;
            param.magFilter = Texture.TextureFilter.Nearest;
        }

        BitmapFont font = generator.generateFont(param);

        generator.dispose();

        if (!Fonts.containsKey(langId))
        {
            Fonts.put(langId, new HashMap<Integer, BitmapFont>());
        }

        Map<Integer, BitmapFont> sizedFonts = Fonts.get(langId);

        if (sizedFonts.containsKey(size))
        {
            BitmapFont existingFont = sizedFonts.get(size);
            existingFont.dispose();

            Logger.Error(TAG, "Font " + langId + " of size " + size + " already exists. Updating...");
        }
        sizedFonts.put(size, font);

        return font;
    }
    //-------------------------------------------------------------------------------------------------------------------------
}
