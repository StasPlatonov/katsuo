package bmg.katsuo.input;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;

public abstract class InputMapping implements InputProcessor
{
    public abstract float GetInputValue();

    public abstract boolean IsSameAs(InputMapping m);
    //-------------------------------------------------------------------------------------------------------------------------

    protected AppInput TheInput;
    private String CurrentInput;

    protected void SetCurrentInput(String CurrentInput)
    {
        this.CurrentInput = CurrentInput;
    }

    public String GetCurrentInput()
    {
        return this.CurrentInput;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    /**
     * get the TheInput object this mapping is a part of - will return null if it
     * has not been added to any
     *
     * @return
     */
    public AppInput GetInput()
    {
        return TheInput;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    protected void SetInput(AppInput input)
    {
        TheInput = input;
        TheInput.addProcessor(this);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void Removed()
    {
        TheInput.removeProcessor(this);
        CurrentInput = null;
        TheInput = null;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public boolean RemoveMapping()
    {
        if (this.TheInput != null && this.CurrentInput != null)
        {
            return this.TheInput.RemoveMapping(CurrentInput, this);
        }
        return false;
    }
    //-------------------------------------------------------------------------------------------------------------------------
/*
    @Override
    public boolean touchDown(float x, float y, int pointer, int button)
    {
        return false;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public boolean tap(float x, float y, int count, int button)
    {
        return false;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public boolean longPress(float x, float y)
    {
        return false;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public boolean fling(float velocityX, float velocityY, int button)
    {
        return false;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY)
    {
        return false;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public boolean panStop(float x, float y, int pointer, int button)
    {
        return false;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public boolean zoom(float initialDistance, float distance)
    {
        return false;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2)
    {
        return false;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void pinchStop()
    {

    }
*/
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public boolean keyDown(int keycode)
    {
        return false;
    }

    @Override
    public boolean keyUp(int keycode)
    {
        return false;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public boolean keyTyped(char character)
    {
        return false;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button)
    {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button)
    {
        return false;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer)
    {
        return false;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public boolean mouseMoved(int screenX, int screenY)
    {
        return false;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public boolean scrolled(int amount)
    {
        return false;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void Reset()
    {
    }
    //-------------------------------------------------------------------------------------------------------------------------
}