package bmg.katsuo.input;

public class InputDefaults
{
    public static final String MOVE_DOWN = "move_down";
    public static final String MOVE_UP = "move_up";
    public static final String MOVE_LEFT = "move_left";
    public static final String MOVE_RIGHT = "move_right";

    public static final String JUMP_BUTTON = "jump_button";
    public static final String ACTION_BUTTON = "action_button";
    public static final String FIRE_BUTTON = "fire_button";

    public static final String EXIT = "exit";
    public static final String ENTER = "enter";

    public static final String STOP = "stop";
    public static final String START = "start";

    public static final String ADD = "add";
    public static final String SUB = "sub";

    public static final String UI_UP = "ui_up";
    public static final String UI_DOWN = "ui_down";
    public static final String UI_LEFT = "ui_left";
    public static final String UI_RIGHT = "ui_right";

    public static final String F = "fF";
    public static final String X = "xX";
    public static final String Y = "yY";
    public static final String Z = "zZ";
    public static final String L = "lL";
    public static final String P = "pP";
    public static final String H = "hH";

    public static final String DEV_CONSOLE = "dev_console";

    public static final String FLING = "fling";
    public static final String ZOOM = "zoom";
    //-------------------------------------------------------------------------------------------------------------------------

    public static void RemoveDefaults(AppInput input)
    {
        input.RemoveInput(MOVE_DOWN);
        input.RemoveInput(MOVE_UP);
        input.RemoveInput(MOVE_LEFT);
        input.RemoveInput(MOVE_RIGHT);

        input.RemoveInput(JUMP_BUTTON);
        input.RemoveInput(ACTION_BUTTON);
        input.RemoveInput(FIRE_BUTTON);

        input.RemoveInput(EXIT);
        input.RemoveInput(ENTER);

        input.RemoveInput(STOP);
        input.RemoveInput(START);

        input.RemoveInput(ADD);
        input.RemoveInput(SUB);

        input.RemoveInput(UI_UP);
        input.RemoveInput(UI_DOWN);
        input.RemoveInput(UI_LEFT);
        input.RemoveInput(UI_RIGHT);

        input.RemoveInput(F);
        input.RemoveInput(X);
        input.RemoveInput(Y);
        input.RemoveInput(Z);
        input.RemoveInput(L);
        input.RemoveInput(P);
        input.RemoveInput(H);

        input.RemoveInput(DEV_CONSOLE);

        input.RemoveInput(FLING);
        input.RemoveInput(ZOOM);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public static void AddDefaults(AppInput input)
    {
        input.RegisterInput(MOVE_DOWN);
        input.RegisterInput(MOVE_UP);
        input.RegisterInput(MOVE_LEFT);
        input.RegisterInput(MOVE_RIGHT);

        input.RegisterInput(JUMP_BUTTON);
        input.RegisterInput(ACTION_BUTTON);
        input.RegisterInput(FIRE_BUTTON);

        input.RegisterInput(EXIT);
        input.RegisterInput(ENTER);

        input.RegisterInput(STOP);
        input.RegisterInput(START);

        input.RegisterInput(ADD);
        input.RegisterInput(SUB);

        input.RegisterInput(UI_UP);
        input.RegisterInput(UI_DOWN);
        input.RegisterInput(UI_LEFT);
        input.RegisterInput(UI_RIGHT);

        input.RegisterInput(F);
        input.RegisterInput(X);
        input.RegisterInput(Y);
        input.RegisterInput(Z);
        input.RegisterInput(L);
        input.RegisterInput(P);
        input.RegisterInput(H);

        input.RegisterInput(DEV_CONSOLE);

        input.RegisterInput(FLING);
        input.RegisterInput(ZOOM);
    }
    //-------------------------------------------------------------------------------------------------------------------------D
}
