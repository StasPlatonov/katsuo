package bmg.katsuo.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;
import bmg.katsuo.utils.TiledObjectTypes.TiledObjectType;
import bmg.katsuo.utils.TiledObjectTypes.TypeProperty;

public class TiledTemplates
{
    private TiledObjectTypes Types;
    private String TemplateFolder;

    public String GetCurrentMap()
    {
        return CurrentMap;
    }

    public void SetCurrentMap(String currentMap)
    {
        CurrentMap = currentMap;
    }

    private String CurrentMap;
    private ObjectMap<String, TiledTemplate> Templates;
    //-------------------------------------------------------------------------------------------------------------------------

    public TiledTemplates(TiledObjectTypes object_types, String template_folder)
    {
        this.Types = object_types;
        this.TemplateFolder = template_folder;
        Templates = new ObjectMap<String, TiledTemplates.TiledTemplate>();
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void AddTemplate(String template)
    {
        XmlReader xml = Types.GetXmlReader();
        Element root = null;

        String templateName = template.substring(template.lastIndexOf("/") + 1);
        templateName = templateName.substring(0, templateName.lastIndexOf('.'));

        root = xml.parse(Gdx.files.internal(TemplateFolder + template));
        if (root == null)
        {
            throw new GdxRuntimeException("Unable to parse template [" + template + "]. Make sure it exists or that you have the correct template folder Set.");
        }

        Element tileset = root.getChildByName("tileset");

        Element object = root.getChildByName("object");
        if (object != null)
        {
            final String parent_type = object.hasAttribute("type") ? object.getAttribute("type") : "BaseGameObject";
            TiledTemplate t = new TiledTemplate(template, object.getAttribute("name"), parent_type.isEmpty() ? null : Types.Get(parent_type));
            t.SetXML(object);
            t.SetWidth(object.getFloat("width", 0f));
            t.SetHeight(object.getFloat("height", 0f));
            t.SetGid(object.getAttribute("gid", null));
            t.SetRotation(object.getFloat("rotation", 0f));
            t.SetTileset(tileset == null ? "" : tileset.getAttribute("source", null));

            if (object.hasChild("properties"))
            {
                Array<Element> properties = object.getChildByName("properties").getChildrenByName("property");
                for (Element property : properties)
                {
                    t.GetType().AddProperty(property.get("name"), property.hasAttribute("type") ? property.get("type") : TypeProperty.STRING, property.get("value"));
                }
            }

            Gdx.app.log(getClass().getName(), "template [" + template + "] has been added");
            Templates.put(template, t);
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public TiledTemplate GetTemplate(String template)
    {
        if (!Templates.containsKey(template))
        {
            //Gdx.app.error(getClass().getName(), "template does not exist [" + template + "].");
            return null;
        }
        return Templates.get(template);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public static class TiledTemplate
    {
        private String Name;
        private TiledObjectType ParentType;
        private TiledObjectType Type;
        private float Width;
        private float Height;
        private String GID;
        private float Rotation;
        private Element XML;

        public String GetTileset()
        {
            return Tileset;
        }

        public void SetTileset(String tileset)
        {
            Tileset = tileset;
        }

        private String Tileset;
        //-------------------------------------------------------------------------------------------------------------------------

        public TiledTemplate(String template_name, String name, TiledObjectType parent_type)
        {
            this.Name = template_name;
            Type = new TiledObjectType(name);
            this.ParentType = parent_type;
            Width = 0;
            Height = 0;
            GID = null;
            Rotation = 0;
        }
        //-------------------------------------------------------------------------------------------------------------------------

        public void SetRotation(float rotation)
        {
            this.Rotation = rotation;
        }

        public float GetRotation()
        {
            return Rotation;
        }
        //-------------------------------------------------------------------------------------------------------------------------

        public void SetGid(String gid)
        {
            this.GID = gid;
        }

        public String GetGid()
        {
            return GID;
        }
        //-------------------------------------------------------------------------------------------------------------------------

        /**
         * this is the name of the template file (.tx extension)
         * to get the name of the template object use getTemplateType().getName();
         *
         * @return
         */
        public String GetName()
        {
            return Name;
        }

        public String GetParentTypeName()
        {
            return ParentType != null ? ParentType.GetName() : "";
        }
        //-------------------------------------------------------------------------------------------------------------------------

        public void SetWidth(float width)
        {
            this.Width = width;
        }

        public void SetHeight(float height)
        {
            this.Height = height;
        }
        //-------------------------------------------------------------------------------------------------------------------------

        public float GetWidth()
        {
            return Width;
        }

        public float GetHeight()
        {
            return Height;
        }
        //-------------------------------------------------------------------------------------------------------------------------

        public TiledObjectType GetType()
        {
            return Type;
        }
        //-------------------------------------------------------------------------------------------------------------------------

        public String GetTypeName()
        {
            return Type.GetName();
        }
        //-------------------------------------------------------------------------------------------------------------------------

        public Array<TypeProperty> GetProperties()
        {
            Array<TypeProperty> ret_properties = new Array<TiledObjectTypes.TypeProperty>();
            ret_properties.addAll(Type.GetProperties());

            if (ParentType != null)
            {
                Array<TypeProperty> parent_properties = ParentType.GetProperties();
                for (int i = 0; i < parent_properties.size; i++)
                {
                    boolean added = false;
                    for (int l = 0; l < ret_properties.size; l++)
                    {
                        if (parent_properties.get(i).GetName().equals(ret_properties.get(l).GetName()))
                        {
                            added = true;
                            break;
                        }
                    }
                    if (!added)
                    {
                        ret_properties.add(parent_properties.get(i));
                    }
                }
            }
            return ret_properties;
        }
        //-------------------------------------------------------------------------------------------------------------------------

        void SetXML(Element xml)
        {
            XML = xml;
        }

        public Element GetXML()
        {
            return XML;
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------
}