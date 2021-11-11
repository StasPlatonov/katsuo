package bmg.katsuo.controllers;

import bmg.katsuo.Globals;
import bmg.katsuo.gameplay.objects.LadderObject;
import bmg.katsuo.gameplay.objects.PlatformerObject;
import bmg.katsuo.gameplay.objects.PlayerObject;
import bmg.katsuo.input.AppInput;
import bmg.katsuo.input.InputDefaults;
import bmg.katsuo.objects.GameObject;
import bmg.katsuo.objects.GameObjectController;
import com.badlogic.gdx.math.Vector2;

import java.util.HashSet;
import java.util.Set;

import static bmg.katsuo.gameplay.objects.PlatformerObject.*;

public class PlayerController extends GameObjectController
{
    private AppInput TheInput;
    private PlayerObject Player;
    private Box2dController Phys;
    private AnimationController Anim;

    private float WalkSpeed;
    private float RunSpeed;
    private float AirMoveSpeedK;
    private float AirMoveSpeed;
    private float JumpSpeed;
    private float LadderSpeed;
    private float LadderJumpHeight;

    private boolean NeedMoveLeft = false;
    private boolean NeedMoveRight = false;
    private boolean NeedMoveUp = false;
    private boolean NeedMoveDown = false;

    private boolean NeedHoldOnLadder = false;
    private boolean WithinLadder = false;
    private Set<LadderObject> Ladders = new HashSet<LadderObject>();
    private Vector2 LadderContact = new Vector2();
    private boolean LadderAvailable = false;
    private boolean AutoJump = true;

    private final static float LEAVE_GROUND_TIME = 0.5f; // Time after which (IsOnGround() && ES_JUMP) means landing
    private final static float JUMPING_TIME_MAX = 0.2f; // Time of applying force up when keep pressed jump button
    private final static short MAX_AIR_JUMPS_ALLOWED = 1; // Maximum jumps in the air
    private final static float WALK_TO_RUN_TIME = 0.3f; // Time after that ES_WALK will be switched to ES_RUN
    private final static float DO_NOT_MOVE_AFTER_SLIDING_TIME = 0.25f; // Timeout after jump off the wall player can not move
    private short AirJumpCounter = 0;

    private int StateBeforeJump;
    //-------------------------------------------------------------------------------------------------------------------------

    public PlayerController(AppInput input)
    {
        TheInput = input;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void Init(GameObject object)
    {
        if (object.getParent() == null)
        {
            return;
        }

        assert(object instanceof PlatformerObject);
        Player = (PlayerObject)object;

        SetPriority(0);
        SetJumpSpeed(object.GetProperties().get("jump_speed", 1f, Float.class));
        SetWalkSpeed(object.GetProperties().get("walk_speed", 0.5f, Float.class));
        SetRunSpeed(object.GetProperties().get("run_speed", 1f, Float.class));
        AirMoveSpeedK = object.GetProperties().get("air_move_speed_k", 1f, Float.class);

        SetLadderSpeed(object.GetProperties().get("ladder_speed", 1f, Float.class));
        LadderJumpHeight = 0.2f;

        Phys = Player.GetController(Box2dController.class);
        Anim = Player.GetController(AnimationController.class);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private void SetAirMoveSpeed(float airSpeed)
    {
        AirMoveSpeed = airSpeed;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private void SetWalkSpeed(float walkSpeed)
    {
        WalkSpeed = walkSpeed;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private void SetRunSpeed(float runSpeed)
    {
        RunSpeed = runSpeed;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private void SetJumpSpeed(float jumpSpeed)
    {
        JumpSpeed = jumpSpeed;
    }

    public float GetJumpSpeed() { return JumpSpeed; }
    //-------------------------------------------------------------------------------------------------------------------------

    public void SetLadderSpeed(float speed)
    {
        LadderSpeed = speed;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void SetNeedMoveLeft(boolean needMoveLeft)
    {
        NeedMoveLeft = needMoveLeft;
    }

    public void SetNeedMoveRight(boolean needMoveRight)
    {
        NeedMoveRight = needMoveRight;
    }

    public void SetNeedMoveUp(boolean needMoveUp)
    {
        NeedMoveUp = needMoveUp;
    }

    public void SetNeedMoveDown(boolean needMoveDown)
    {
        NeedMoveDown = needMoveDown;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private boolean IsNeedMoveLeft()
    {
        return TheInput.IsInputPressed(InputDefaults.MOVE_LEFT) || NeedMoveLeft;
    }

    private boolean IsNeedMoveRight()
    {
        return TheInput.IsInputPressed(InputDefaults.MOVE_RIGHT) || NeedMoveRight;
    }

    private boolean IsNeedMoveUp()
    {
        return TheInput.IsInputPressed(InputDefaults.MOVE_UP) || NeedMoveUp;
    }

    private boolean IsNeedMoveDown()
    {
        return TheInput.IsInputPressed(InputDefaults.MOVE_DOWN) || NeedMoveDown;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private final static int MD_NONE = 0x0000;
    private final static int MD_LEFT = 0x0001;
    private final static int MD_RIGHT = 0x0002;
    private final static int MD_UP = 0x0004;
    private final static int MD_DOWN = 0x0008;
    //-------------------------------------------------------------------------------------------------------------------------

    private int GetMoveState()
    {
        int result = MD_NONE;
        result |= IsNeedMoveLeft() ? MD_LEFT : 0;
        result |= IsNeedMoveRight() ? MD_RIGHT : 0;
        result |= IsNeedMoveUp() ? MD_UP : 0;
        result |= IsNeedMoveDown() ? MD_DOWN : 0;
        return result;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private void UpdateInput(float delta)
    {
        int moveState = GetMoveState();

        boolean needLeft = (moveState & MD_LEFT) == MD_LEFT;
        boolean needRight = (moveState & MD_RIGHT) == MD_RIGHT;
        boolean needUp = (moveState & MD_UP) == MD_UP;
        boolean needDown = (moveState & MD_DOWN) == MD_DOWN;

        boolean pressedSomething = needLeft || needRight || needUp || needDown;

        float jumpingTime = Player.GetLife() - Player.GetJumpStartedTime();

        /*
        if (moveState == 0)
        {
            MoveDir = MD_NONE;
            MoveState = MD_NONE;
        }
        else
        {
            int moveDiff = MoveState ^ moveState; // detect changes
            int pressedMoves = moveDiff & moveState; // keep only pressed bits

            boolean pressedLeft = (pressedMoves & MD_LEFT) == MD_LEFT;
            boolean pressedRight = (pressedMoves & MD_RIGHT) == MD_RIGHT;
            boolean pressedUp = (pressedMoves & MD_UP) == MD_UP;
            boolean pressedDown = (pressedMoves & MD_DOWN) == MD_DOWN;

            MoveState = moveState;

            if (pressedLeft)
                MoveDir = MD_LEFT;

            if (pressedRight)
                MoveDir = MD_RIGHT;

            if (pressedUp)
                MoveDir = MD_UP;

            if (pressedDown)
                MoveDir = MD_DOWN;
        }

        boolean needLeft = MoveDir == MD_LEFT;
        boolean needRight = MoveDir == MD_RIGHT;
        boolean needUp = MoveDir == MD_UP;
        boolean needDown = MoveDir == MD_DOWN;
        */

        boolean isSliding = (Player.GetCurrentState() == ES_SLIDING);

        boolean canMove = !isSliding && ((Player.GetLife() - StopSlidingTime) > DO_NOT_MOVE_AFTER_SLIDING_TIME);

        if (needLeft)
        {
            if (canMove)
                Move(MoveDirection.MD_LEFT, delta);
        }
        if (needRight)
        {
            if (canMove)
                Move(MoveDirection.MD_RIGHT, delta);
        }

        // When get out of ladder - allow automatic jump on
        if (!IsLadderAvailable())
        {
            AutoJump = true;
        }

        if (IsWithinLadder())
        {
            // If player intersects at least one ladder and still is not ON the ladder
            if (!IsOnLadder())
            {
                // Check if up is pressed
                if (TheInput.IsInputPressed(InputDefaults.MOVE_UP))
                {
                    JumpOnLadder();
                }
                else // Check automatic jump on at the air
                if (AutoJump && !Player.IsOnGround())
                {
                    JumpOnLadder();
                }
            }
            else
            {
                // Move up/down only on ladder
                if (needUp)
                {
                    MoveUp(delta);
                }
                if (needDown)
                {
                    MoveDown(delta);
                }
                // Player can get off he ladder if his center goes out of ladder border.
                // If player is ON the ladder - check borders
                if (CheckOffLadder() || Player.IsOnGround())
                {
                    JumpOffLadder();
                }
            }
        }

        // When start jumping, use speed to kick player up. Next (if keep pressing jump button) - continue to push him up for a fixed time
        // This allows player to jump different height
        if (TheInput.IsInputJustPressed(InputDefaults.JUMP_BUTTON))
        {
            if (!isSliding)
            {
                Jump();
            }
            else
            {
                StopSliding(true);
                isSliding = false;
            }
            jumpingTime = 0f;
        }

        if (TheInput.IsInputPressed(InputDefaults.JUMP_BUTTON))
        {
            if (!isSliding && (jumpingTime < JUMPING_TIME_MAX))
            {
                Jumping();
            }
        }

        if (TheInput.IsInputJustPressed(InputDefaults.FIRE_BUTTON))
        {
            Fire();
        }

        if (IsOnLadder())
        {
            Player.PauseAnimation(!pressedSomething);
            if (!pressedSomething)
            {
                Stop();
            }
            return;
        }

        if (!pressedSomething)
        {
            // Detect player land on ground (excluding short time when player off the ground but still feeling it)
            if (Player.GetCurrentState() == ES_JUMP)
            {
                if (Player.IsOnGround() && (jumpingTime > LEAVE_GROUND_TIME))
                {
                    Stop();
                }
            }
            else if (Player.GetCurrentState() != ES_IDLE) // Detect player stops move/climb
            {
                // Not in the air
                if (Player.IsOnGround())
                {
                    Stop();
                }
            }

            // Stop sliding when move buttons are released
            if (isSliding)
            {
                StopSliding(false);
                isSliding = false;
            }
        }
        else
        {
            // Stop sliding if end of wall was reached
            if (isSliding)
            {
                if (Player.IsOnGround() || (!Player.IsFeelLeftWall() && !Player.IsFeelRightWall()))
                {
                    StopSliding(false);
                    isSliding = false;
                }
            }
        }

        if (!Player.IsOnGround())
        {
            // Check start sliding
            if (!isSliding)
            {
                if (Phys.GetSpeed().y < 0)
                {
                    Player.Falling();
                }

                boolean canSlide = (Player.GetLife() - StopSlidingTime) > DO_NOT_MOVE_AFTER_SLIDING_TIME;

                if (canSlide)
                {
                    if (Player.IsFeelLeftWall() && needLeft)
                    {
                        StartSliding(true);
                    }
                    if (Player.IsFeelRightWall() && needRight)
                    {
                        StartSliding(false);
                    }
                }
            }
        }

        if (isSliding)
        {
            Phys.SetSpeed(0f, Phys.GetSpeed().y);
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private void StartSliding(boolean left)
    {
        AirJumpCounter = 0;

        Player.Sliding(left);
        Phys.SetSliding(true);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    float StopSlidingTime = 0;

    private void StopSliding(boolean jumped)
    {
        StopSlidingTime = Player.GetLife();

        Phys.SetSliding(false);

        if (jumped)
        {
            boolean leftWall = Player.IsFeelLeftWall();
            if (leftWall)
            {
                Jump(RunSpeed * AirMoveSpeedK, JumpSpeed);
            }
            else
            {
                Jump(-RunSpeed * AirMoveSpeedK, JumpSpeed);
            }
        }
        else
        {
            if (!Player.IsOnGround())
            {
                Player.Falling();
            }
            else
            {
                Stop();
            }
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void Update(GameObject object, float delta)
    {
        if (!Player.IsUpdatable())
        {
            return;
        }

        if (Player.IsDying())
        {
            return;
        }

        LadderAvailable = IsAtLeastOneLadderAvailable();

        UpdateInput(delta);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void Remove(GameObject object)
    {
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private boolean IsOnLadder()
    {
        return IsWithinLadder() && NeedHoldOnLadder;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private boolean IsLadderAvailable()
    {
        return LadderAvailable;
    }

    private boolean IsAtLeastOneLadderAvailable()
    {
        // If center of player in the ladder - player should not off the ladder
        float centerX = Phys.GetPosition().x;
        float centerY = Phys.GetPosition().y;

        for (LadderObject ladder : Ladders)
        {
            Box2dController ladderPhys = ladder.GetController(Box2dController.class);

            if (ladderPhys.TestPoint(centerX, centerY))
            {
                return true;
            }
        }

        return false;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void OnLanded()
    {
        AirJumpCounter = 0;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private boolean CheckOffLadder()
    {
        if (Ladders.isEmpty())
        {
            return true;
        }

        return !IsLadderAvailable();
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private void JumpOffLadder()
    {
        if (!IsOnLadder())
        {
            return;
        }
        Player.GetApp().Log(TAG, "Player ladder mode is OFF");
        NeedHoldOnLadder = false;
        Phys.SetOnLadder(false);
        // Do not clean Ladders here, because player can be able to jump ON ladder without re collide with ladder

        Player.SetState(ES_IDLE);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private void JumpOnLadder()
    {
        if (Ladders.isEmpty())
        {
            return;
        }

        if (!IsLadderAvailable())
        {
            return;
        }

        Player.GetApp().Debug(TAG, "Player in ON ladder");

        Player.SetState(ES_CLIMB);

        NeedHoldOnLadder = true;
        Phys.SetOnLadder(true);

        float angle = Phys.GetBody().getAngle();
        final Vector2 pos = Phys.GetBody().getPosition();

        // If jump on ladder from ground - real jump a little bit (higher than ground sensors, as they will jump player off the ladder)
        float deltaH = Player.IsOnGround() ? Player.getHeight() * LadderJumpHeight * Globals.PIXELS_TO_METERS : 0f;
        if (Ladders.size() > 1)
        {
            if (deltaH != 0f)
            {
                Phys.GetBody().setTransform(pos.x, pos.y + deltaH, angle);
            }
            return;
        }

        // Stick to ladder
        LadderObject ladder = Ladders.iterator().next();

        float lx = ladder.GetTrueX();
        float ly = ladder.GetTrueY();
        float lw = ladder.getWidth();
        float lh = ladder.getHeight();

        // Automatic move player to the center of ladder if ladder is not wide enough
        //boolean needAutoAlign = (lw != 0f) && (lw < Player.getWidth() * 1.5);
        boolean needAutoAlign = ladder.IsAutoAlign();
        if (needAutoAlign || (deltaH != 0f))
        {
            Phys.GetBody().setTransform(needAutoAlign ? (lx + lw * .5f) * Globals.PIXELS_TO_METERS : pos.x, pos.y + deltaH, angle);
        }
      }
    //-------------------------------------------------------------------------------------------------------------------------

    public void Jump(float kx, float ky)
    {
        if (!Player.IsUpdatable() || Player.IsDying())
        {
            return;
        }

        boolean isSliding = (Player.GetCurrentState() == ES_SLIDING);

        if (!isSliding)
        {
            if (!Player.IsOnGround())
            {
                ++AirJumpCounter;

                if (AirJumpCounter > MAX_AIR_JUMPS_ALLOWED)
                {
                    return;
                }
            }
            else
            {
                StateBeforeJump = Player.GetCurrentState();
            }
        }
        else
        {
            StateBeforeJump = ES_RUN;
            SetAirMoveSpeed(RunSpeed * AirMoveSpeedK);
        }

        Player.Jump(kx, ky);

        // When jump force get off ladder
        JumpOffLadder();
        AutoJump = false;
    }

    public void Jump()
    {
        Jump(Phys.GetSpeed().x, JumpSpeed);
    }

    private void Jumping()
    {
        Player.Jumping();
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void JumpTo(float x, float y)
    {
        Vector2 spd = new Vector2();
        spd.set(x, y).sub(Player.getX(), Player.getY()).nor().scl(JumpSpeed);

        Jump(spd.x, spd.y);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private void Move(MoveDirection direction, float delta)
    {
        if (IsOnLadder())
        {
            Player.Climbing(direction, LadderSpeed, delta);
        }
        else if (Player.IsOnGround())
        {
            boolean isJumping = (Player.GetCurrentState() == ES_JUMP) && (Player.GetLife() - Player.GetJumpStartedTime() > LEAVE_GROUND_TIME);

            // No jumping or landed after jump (excluding short time when player off the ground but still feeling it)
            if ((Player.GetCurrentState() != ES_JUMP) || isJumping)
            {
                float walkTime = Player.GetLife() - Player.GetStartWalkTime();

                // Start to run (when walking more than specific time) or keep running
                if (((Player.GetCurrentState() == ES_WALK) && (walkTime > WALK_TO_RUN_TIME)) || (Player.GetCurrentState() == ES_RUN))
                {
                    Player.Running(direction, RunSpeed, delta);
                    SetAirMoveSpeed(RunSpeed * AirMoveSpeedK);
                }
                else if (isJumping)// landing after jump
                {
                    if (StateBeforeJump == ES_RUN) // If started jump after run - keep running when landing
                    {
                        Player.Running(direction, RunSpeed, delta);
                        SetAirMoveSpeed(RunSpeed * AirMoveSpeedK);
                    }
                    else //if (StateBeforeJump == ES_WALK) // If started jump after walk - keep walking when landing
                    {
                        Player.Walking(direction, WalkSpeed, delta);
                        SetAirMoveSpeed(WalkSpeed * AirMoveSpeedK);
                    }
                }
                else
                {
                    // Start to walk or keep walking
                    Player.Walking(direction, WalkSpeed, delta);
                    SetAirMoveSpeed(WalkSpeed * AirMoveSpeedK);
                }
            }
        }
        else
        {
            Player.Flying(direction, AirMoveSpeed, delta);
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private void MoveUp(float delta)
    {
        if (!Player.IsUpdatable())
        {
            return;
        }

        if (Player.IsDying())
        {
            return;
        }

        if (IsOnLadder())
        {
            //Vector2 pos = Entity.GetTruePosition().cpy();
            //pos.y += WalkSpeed * delta * LadderSpeed;
            //Entity.setPosition(pos.x, pos.y);

            //Vector2 pos = Phys.GetPosition().cpy();
            //pos.y += WalkSpeed * delta * LadderSpeed * Globals.METERS_TO_PIXELS;
            //Phys.SetPosition(pos.x, pos.y);
            final float impulse = Phys.GetMass() * LadderSpeed * delta;
            //final Vector2 impulsePos = Phys.GetBody().getWorldCenter();
            //Phys.Impulse(0f, impulse, impulsePos.x, impulsePos.y);
            Phys.SetForce(0f, impulse);
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private void MoveDown(float delta)
    {
        if (!Player.IsUpdatable())
        {
            return;
        }

        if (Player.IsDying())
        {
            return;
        }

        if (IsOnLadder())
        {
            //Vector2 pos = Entity.GetTruePosition().cpy();
            //pos.y -= WalkSpeed * delta * LadderSpeed;
            //Entity.setPosition(pos.x, pos.y);

            //Vector2 pos = Phys.GetPosition().cpy();
            //pos.y -= WalkSpeed * delta * LadderSpeed * Globals.METERS_TO_PIXELS;
            //Phys.SetPosition(pos.x, pos.y);

            final float impulse = Phys.GetMass() * LadderSpeed * delta;
            //final Vector2 impulsePos = Phys.GetBody().getWorldCenter();
            //Phys.Impulse(0f, -impulse, impulsePos.x, impulsePos.y);
            Phys.SetForce(0f, -impulse);
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private void Stop()
    {
        if (!Player.IsUpdatable() || Player.IsDying())
        {
            return;
        }

        if (!IsOnLadder())
        {
            Player.StopMoving();
        }
        else
        {
            Player.StopClimb();
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void Fire()
    {
        if (!Player.IsUpdatable())
        {
            return;
        }

        if (Player.IsDying())
        {
            return;
        }

        Player.Fire();
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public boolean IsWithinLadder()
    {
        return WithinLadder;//!Ladders.isEmpty();
    }

    public boolean IsWithinLadder(LadderObject ladder)
    {
        return Ladders.contains(ladder);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void SetWithinLadder(LadderObject ladder, boolean withinLadder, Vector2 contact)
    {
        LadderContact = contact;

        if (withinLadder)
        {
            Ladders.add(ladder);
        }
        else
        {
            Ladders.remove(ladder);
        }

        WithinLadder = !Ladders.isEmpty();
    }
    //-------------------------------------------------------------------------------------------------------------------------
}
