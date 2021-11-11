package bmg.katsuo.utils;

import bmg.katsuo.objects.BaseGameObject;
import bmg.katsuo.objects.GameObject;
import com.badlogic.gdx.math.Rectangle;

public class CollisionUtils
{
    private static GameObject ZTest;

    // checks if the objects have colliding depth+thickess
    public static boolean DepthCollision(GameObject o1, GameObject o2)
    {
        if (o1.GetDepth() + o1.GetThickness() < o2.GetDepth())
        {
            return false;
        }
        if (o1.GetDepth() > o2.GetDepth() + o2.GetThickness())
        {
            return false;
        }
        return true;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    // checks if the objects have colliding collision bounds
    public static boolean BoundsCollision(GameObject o1, GameObject o2)
    {
        return false;//o1.GetCollisionBounds().overlaps(o2.GetCollisionBounds());
    }
    //-------------------------------------------------------------------------------------------------------------------------

    // checks if the objects collide in 3d space using depth+thickness as the third
    public static boolean BoxCollision(GameObject o1, GameObject o2)
    {
        return BoundsCollision(o1, o2) && DepthCollision(o1, o2);
    }
    //-------------------------------------------------------------------------------------------------------------------------
/*
    private static GameObject GetZTest()
    {
        if (ZTest == null)
        {
            ZTest = new BaseGameObject();
        }
        return ZTest;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    // get a game object to test with. There is only ever one instance - so you must
    // use it before creating another one. Do not keep a reference to this object
    // or it may cause bugs
    public static GameObject GetTestObject(Rectangle bounds, String name, int group, int filter)
    {
        GameObject z = GetZTest();
        z.setPosition(bounds.x, bounds.y);
        z.setSize(bounds.width, bounds.height);
        z.SetCollisionBounds(0, 0, bounds.width, bounds.height);
        z.setName(name);
        //z.SetGroupId(group);
        //z.SetFilter(filter);
        return z;
    }*/
    //-------------------------------------------------------------------------------------------------------------------------
}
