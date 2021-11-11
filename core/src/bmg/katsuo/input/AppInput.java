package bmg.katsuo.input;

import bmg.katsuo.IApplication;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class AppInput extends InputMultiplexer
{
    public static final float NOT_USED = 0f;

    IApplication App;

    private ObjectMap<String, Float> InputsPressed;
    private ObjectMap<String, Boolean> InputsJustPressed;
    private ObjectMap<String, Array<InputMapping>> Mappings;
    private Array<GestureDetector.GestureListener> GestureListeners;
    private Array<String> InputCheck;

    private boolean TouchedDown = false;
    private boolean JustTouched = false;

    private GestureDetector Gesture;

    //-------------------------------------------------------------------------------------------------------------------------

    public AppInput(IApplication app)
    {
        App = app;
        InputsPressed = new ObjectMap<String, Float>();
        InputsJustPressed = new ObjectMap<String, Boolean>();
        Mappings = new ObjectMap<String, Array<InputMapping>>();
        InputCheck = new Array<String>();
        GestureListeners = new Array<GestureDetector.GestureListener>();

        InputDefaults.AddDefaults(this);

        Gesture = new GestureDetector(new GestureDetector.GestureAdapter()
        {
            @Override
            public boolean pan(float x, float y, float deltaX, float deltaY)
            {
                for (GestureDetector.GestureListener listener : GestureListeners)
                {
                    listener.pan(x, y, deltaX, deltaY);
                }
                return super.pan(x, y, deltaX, deltaY);
            }

            @Override
            public boolean zoom(float initialDistance, float distance)
            {
                for (GestureDetector.GestureListener listener : GestureListeners)
                {
                    listener.zoom(initialDistance, distance);
                }

                return super.zoom(initialDistance, distance);
            }

            @Override
            public boolean tap(float x, float y, int count, int button)
            {
                for (GestureDetector.GestureListener listener : GestureListeners)
                {
                    listener.tap(x, y, count, button);
                }

                return super.tap(x, y, count, button);
            }

            @Override
            public boolean longPress(float x, float y)
            {
                for (GestureDetector.GestureListener listener : GestureListeners)
                {
                    listener.longPress(x, y);
                }

                return super.longPress(x, y);
            }
        });
        //Gesture.setMaxFlingDelay(2);
        addProcessor(Gesture);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void AddGestureMapping(GestureDetector.GestureListener listener)
    {
        GestureListeners.add(listener);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void AddInputMapping(String input, InputMapping mapping)
    {
        if (!Mappings.containsKey(input))
        {
            App.Error("AppInput", "input [" + input + "] has not been registered. mapping not added!");
            return;
        }
        Array<InputMapping> maps = Mappings.get(input);
        for (InputMapping map : maps)
        {
            if (map.IsSameAs(mapping))
            {
                App.Error("AppInput", "duplicate input map not added to [" + input + "].");
                return;
            }
        }
        mapping.SetInput(this);
        mapping.SetCurrentInput(input);
        Mappings.get(input).add(mapping);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void RegisterInput(String input)
    {
        if (InputsPressed.containsKey(input))
        {
            App.Error("AppInput", "failed to register duplicate input [" + input + "]");
            return;
        }
        InputsPressed.put(input, NOT_USED);
        InputsJustPressed.put(input, false);
        Mappings.put(input, new Array<InputMapping>());
        InputCheck.add(input);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public Array<String> GetAllInputs()
    {
        return InputCheck;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void RemoveAllInputs()
    {
        removeProcessor(Gesture);

        for (String input : InputCheck)
        {
            InputsPressed.remove(input);
            InputsJustPressed.remove(input);
            Array<InputMapping> im = Mappings.remove(input);
            for (int i = 0; i < im.size; ++i)
            {
                im.get(i).Removed();
            }
        }
        InputCheck.clear();
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void RemoveInput(String input)
    {
        InputsPressed.remove(input);
        InputsJustPressed.remove(input);
        InputCheck.removeValue(input, false);
        Array<InputMapping> im = Mappings.remove(input);
        for (int i = 0; i < im.size; ++i)
        {
            im.get(i).Removed();
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public boolean RemoveMapping(String input, InputMapping mapping)
    {
        Array<InputMapping> maps = Mappings.get(input);
        if (maps.contains(mapping, true))
        {
            maps.removeValue(mapping, true);
            mapping.Removed();
            return true;
        }
        return false;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public float GetInputValue(String input)
    {
        return InputsPressed.get(input);
    }

    public boolean IsInputPressed(String input)
    {
        return InputsPressed.get(input) != NOT_USED;
    }

    public boolean IsInputJustPressed(String input)
    {
        return InputsJustPressed.get(input);
    }

    public int GetX()
    {
        return Gdx.input.getX();
    }

    public int GetY()
    {
        return Gdx.input.getY();
    }

    public float GetPointerX(int pointer)
    {
        return Gdx.input.getX(pointer);
    }

    public float GetPointerY(int pointer)
    {
        return Gdx.input.getY(pointer);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    /**
     * return true if the user is clicking on the screen with the left mouse button
     * or touching the screen on android - if you would like control of button
     * please use a mousebutton mapping instead of this method
     *
     * @return
     */
    public boolean IsTouchedDown()
    {
        return TouchedDown;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    /**
     * return true if the user has just clicked on the screen with the left mouse
     * button or touching the screen on android - if you would like control of
     * button please use a MouseButtonMapping instead of this method
     *
     * @return
     */
    public boolean IsJustTouched()
    {
        boolean jt = JustTouched;
        JustTouched = false;
        return jt;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    /**
     * if the input mapping allows - reset to default state of each mapping -
     * probably false
     */
    public void ResetAllInputs()
    {
        for (String input : InputCheck)
        {
            for (InputMapping mapping : Mappings.get(input))
            {
                mapping.Reset();
            }
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void Update()
    {
        Array<InputProcessor> processors = getProcessors();

        for (String input : InputCheck)
        {
            float value = NOT_USED;
            for (InputMapping mapping : Mappings.get(input))
            {
                value = mapping.GetInputValue();
                if (value != NOT_USED)
                {
                    break;
                }
            }
            final boolean lastPressed = InputsJustPressed.get(input);
            if (value != NOT_USED && InputsPressed.get(input) == NOT_USED && !lastPressed)
            {
                InputsJustPressed.put(input, !lastPressed);
                InputsPressed.put(input, value);
            }
            else if (value != NOT_USED)
            {
                InputsJustPressed.put(input, false);
                InputsPressed.put(input, value);
            }
            else
            {
                InputsJustPressed.put(input, false);
                InputsPressed.put(input, NOT_USED);
            }
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------
}