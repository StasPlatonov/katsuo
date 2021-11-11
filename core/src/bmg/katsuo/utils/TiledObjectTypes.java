package bmg.katsuo.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

public class TiledObjectTypes
{
    private XmlReader Reader;
    private Element Root;
    private ObjectMap<String, TiledObjectType> Types;
    //-------------------------------------------------------------------------------------------------------------------------

    public TiledObjectTypes(String file)
    {
        Reader = new XmlReader();
        Root = Reader.parse(Gdx.files.internal(file));
        Types = new ObjectMap<String, TiledObjectTypes.TiledObjectType>();

        if (Root == null)
        {
            throw new GdxRuntimeException(StringUtils.Format("Unable to parse file %s. make sure it is the correct path.", file));
        }
        Array<Element> types = Root.getChildrenByName("objecttype");
        for (Element element : types)
        {
            TiledObjectType tot = new TiledObjectType(element.get("name"));
            Array<Element> properties = element.getChildrenByName("property");
            for (int i = 0; i < properties.size; i++)
            {
                Element element2 = properties.get(i);
                TypeProperty property = new TypeProperty(element2.get("name"), element2.get("type"), element2.hasAttribute("default") ? element2.get("default") : "");
                tot.AddProperty(property);
            }
            this.Types.put(tot.Name, tot);
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public XmlReader GetXmlReader()
    {
        return Reader;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public TiledObjectType Get(String name)
    {
        if (!Types.containsKey(name))
        {
            return null;
        }
        return Types.get(name);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public String toString()
    {
        String ret = "TiledObjectTypes:========" + "\n";
        Array<TiledObjectType> t = Types.values().toArray();
        for (int i = 0; i < t.size; i++)
        {
            ret += t.get(i).toString() + "\n";
        }
        ret += " ================= END ================";
        return ret;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public static class TiledObjectType
    {
        private String Name;
        private ObjectMap<String, TypeProperty> Properties;
        //-------------------------------------------------------------------------------------------------------------------------

        public TiledObjectType(String name)
        {
            this.Name = name;
            Properties = new ObjectMap<String, TiledObjectTypes.TypeProperty>();
        }
        //-------------------------------------------------------------------------------------------------------------------------

        public String GetName()
        {
            return Name;
        }
        //-------------------------------------------------------------------------------------------------------------------------

        public void AddProperty(String name, String type, String value)
        {
            AddProperty(new TypeProperty(name, type, value));
        }
        //-------------------------------------------------------------------------------------------------------------------------

        public Array<TypeProperty> GetProperties()
        {
            return Properties.values().toArray();
        }
        //-------------------------------------------------------------------------------------------------------------------------

        public void AddProperty(TypeProperty property)
        {
            Properties.put(property.GetName(), property);
        }
        //-------------------------------------------------------------------------------------------------------------------------

        public TypeProperty GetProperty(String name)
        {
            if (!Properties.containsKey(name))
            {
                Gdx.app.error(getClass().getName(), "Property[" + name + "] not found in Type [" + this.Name + "]");
                return null;
            }
            return Properties.get(name);
        }
        //-------------------------------------------------------------------------------------------------------------------------

        public int GetInt(String name, int default_value)
        {
            TypeProperty property = GetProperty(name);
            if (property == null || !property.CheckInt())
            {
                return default_value;
            }
            return property.GetInt();
        }
        //-------------------------------------------------------------------------------------------------------------------------

        public float GetFloat(String name, float default_value)
        {
            TypeProperty property = GetProperty(name);
            if (property == null || !property.CheckFloat())
            {
                return default_value;
            }
            return property.GetFloat();
        }
        //-------------------------------------------------------------------------------------------------------------------------

        public boolean GetBool(String name, boolean default_value)
        {
            TypeProperty property = GetProperty(name);
            if (property == null || !property.CheckBool())
            {
                return default_value;
            }
            return property.GetBool();
        }
        //-------------------------------------------------------------------------------------------------------------------------

        public String GetString(String name, String default_value)
        {
            TypeProperty property = GetProperty(name);
            if (property == null || !property.CheckString())
            {
                return default_value;
            }
            return property.GetString();
        }
        //-------------------------------------------------------------------------------------------------------------------------

        public Color GetColor(String name, Color default_value)
        {
            TypeProperty property = GetProperty(name);
            if (property == null || !property.CheckColor())
            {
                return default_value;
            }
            return property.GetColor();
        }
        //-------------------------------------------------------------------------------------------------------------------------


        @Override
        public String toString()
        {
            String ret = "Object Type:" + Name + "=======" + "\n";
            Array<String> property_names = Properties.keys().toArray();
            for (int i = 0; i < property_names.size; i++)
            {
                ret += "---" + Properties.get(property_names.get(i)).toString() + "\n";
            }
            return ret;
        }
        //-------------------------------------------------------------------------------------------------------------------------
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public static class TypeProperty
    {
        public static final String INTEGER = "int";
        public static final String BOOLEAN = "bool";
        public static final String FLOAT = "float";
        public static final String STRING = "string";
        public static final String COLOR = "color";

        private String Name;
        private String Type;
        private String Value;
        //-------------------------------------------------------------------------------------------------------------------------

        public TypeProperty(String name, String type, String value)
        {
            this.Name = name;
            this.Type = type;
            this.Value = value;
        }
        //-------------------------------------------------------------------------------------------------------------------------

        public String GetType()
        {
            return Type;
        }

        public String GetName()
        {
            return Name;
        }

        public String GetValue()
        {
            return Value;
        }
        //-------------------------------------------------------------------------------------------------------------------------

        public Object GetAsObject()
        {
            if (Type.equals(INTEGER))
            {
                return Integer.parseInt(Value);
            }
            else if (Type.equals(BOOLEAN))
            {
                return Boolean.parseBoolean(Value);
            }
            else if (Type.equals(FLOAT))
            {
                return Float.parseFloat(Value);
            }
            else if (Type.equals(COLOR))
            {
                return Color.valueOf(Value);
            }
            else
            {
                return Value;
            }
        }
        //-------------------------------------------------------------------------------------------------------------------------

        public int GetInt()
        {
            return Integer.parseInt(Value);
        }
        //-------------------------------------------------------------------------------------------------------------------------

        public boolean GetBool()
        {
            return Boolean.parseBoolean(Value);
        }
        //-------------------------------------------------------------------------------------------------------------------------

        public float GetFloat()
        {
            return Float.parseFloat(Value);
        }
        //-------------------------------------------------------------------------------------------------------------------------

        public String GetString()
        {
            return Value;
        }
        //-------------------------------------------------------------------------------------------------------------------------

        public Color GetColor() { return Color.valueOf(Value); }

        public boolean CheckInt()
        {
            return Type.equals(INTEGER);
        }

        public boolean CheckBool()
        {
            return Type.equals(BOOLEAN);
        }

        public boolean CheckFloat()
        {
            return Type.equals(FLOAT);
        }

        public boolean CheckString()
        {
            return Type.equals(STRING);
        }

        public boolean CheckColor()
        {
            return Type.equals(COLOR);
        }
        //-------------------------------------------------------------------------------------------------------------------------

        @Override
        public String toString()
        {
            return "property:[name=" + Name + " type=" + Type + " value=" + Value + "]";
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------
}
