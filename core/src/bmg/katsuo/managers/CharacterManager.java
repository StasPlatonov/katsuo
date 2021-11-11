package bmg.katsuo.managers;

import bmg.katsuo.IApplication;
import bmg.katsuo.gameplay.objects.AnimationEffectParameters;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static bmg.katsuo.gameplay.objects.PlatformerObject.*;
import static bmg.katsuo.managers.CharacterParameters.EffectType.*;

public class CharacterManager
{
    private final static String TAG = CharacterManager.class.getSimpleName();
    private IApplication App;
    private Map<String, CharacterParameters> Characters = new HashMap<String, CharacterParameters>();
    //-------------------------------------------------------------------------------------------------------------------------

    public CharacterManager(IApplication app)
    {
        App = app;
        CharacterParameters greeny = new CharacterParameters("Greeny", "Greeny", "player")
                .AddStateAnimation(ES_IDLE,  .16f, Animation.PlayMode.LOOP)
                .AddStateAnimation(ES_WALK,  .16f, Animation.PlayMode.LOOP)
                .AddStateAnimation(ES_RUN,  .08f, Animation.PlayMode.LOOP)
                .AddStateAnimation(ES_JUMP,  .08f, Animation.PlayMode.NORMAL)
                .AddStateAnimation(ES_CLIMB,  .08f, Animation.PlayMode.LOOP)
                .AddStateAnimation(ES_FALL,  .08f, Animation.PlayMode.LOOP)
                .AddStateAnimation(ES_DYING,  .08f, Animation.PlayMode.NORMAL, false)
                .AddStateAnimation(ES_ATTACK,  .08f, Animation.PlayMode.NORMAL)
                .AddStateAnimation(ES_HURT,  .08f, Animation.PlayMode.NORMAL)
                .AddEffect(ET_JUMP,52, 20, "FxJump", 0.06f, Color.WHITE, 1f)
                .AddEffect(ET_AIR_JUMP,32, 32, "GreenyDoubleJumpDust", 0.06f, Color.SKY, 0.7f)
                .AddEffect(ET_LAND_DUST,48, 16, "land_dust", 0.04f, Color.LIGHT_GRAY, 1f)
                .AddEffect(ET_WALK_PUSH, 32, 32, "GreenyWalkRunPushDust", 0.06f, Color.LIGHT_GRAY, 0.7f);
        AddCharacter(greeny);

        CharacterParameters dudeMonster = new CharacterParameters("DudeMonster", "DudeMonster", "dude")
                .AddStateAnimation(ES_IDLE,  .16f, Animation.PlayMode.LOOP)
                .AddStateAnimation(ES_WALK,  .16f, Animation.PlayMode.LOOP)
                .AddStateAnimation(ES_RUN,  .08f, Animation.PlayMode.LOOP)
                .AddStateAnimation(ES_JUMP,  .08f, Animation.PlayMode.NORMAL)
                .AddStateAnimation(ES_CLIMB,  .08f, Animation.PlayMode.LOOP)
                .AddStateAnimation(ES_FALL,  .08f, Animation.PlayMode.LOOP)
                .AddStateAnimation(ES_DYING,  .1f, Animation.PlayMode.NORMAL, false)
                .AddStateAnimation(ES_ATTACK,  .08f, Animation.PlayMode.NORMAL)
                .AddStateAnimation(ES_HURT,  .08f, Animation.PlayMode.NORMAL)
                .AddEffect(ET_JUMP,52, 20, "FxJump", 0.06f, Color.WHITE, 1f)
                .AddEffect(ET_AIR_JUMP,32, 32, "DudeMonsterDoubleJumpDust", 0.06f, Color.SKY, 0.7f)
                .AddEffect(ET_LAND_DUST,48, 16, "land_dust", 0.04f, Color.LIGHT_GRAY, 1f)
                .AddEffect(ET_WALK_PUSH, 32, 32, "DudeMonsterWalkRunPushDust", 0.06f, Color.LIGHT_GRAY, 0.7f);
        AddCharacter(dudeMonster);

        CharacterParameters owletMonster = new CharacterParameters("OwletMonster", "OwletMonster", "owlet")
                .AddStateAnimation(ES_IDLE,  .16f, Animation.PlayMode.LOOP)
                .AddStateAnimation(ES_WALK,  .16f, Animation.PlayMode.LOOP)
                .AddStateAnimation(ES_RUN,  .08f, Animation.PlayMode.LOOP)
                .AddStateAnimation(ES_JUMP,  .08f, Animation.PlayMode.NORMAL)
                .AddStateAnimation(ES_CLIMB,  .08f, Animation.PlayMode.LOOP)
                .AddStateAnimation(ES_FALL,  .08f, Animation.PlayMode.LOOP)
                .AddStateAnimation(ES_DYING,  .1f, Animation.PlayMode.NORMAL)
                .AddStateAnimation(ES_ATTACK,  .08f, Animation.PlayMode.NORMAL)
                .AddStateAnimation(ES_HURT,  .04f, Animation.PlayMode.NORMAL)
                .AddEffect(ET_JUMP,52, 20, "FxJump", 0.06f, Color.WHITE, 1f)
                .AddEffect(ET_AIR_JUMP,32, 32, "OwletMonsterDoubleJumpDust", 0.06f, Color.SKY, 1f)
                .AddEffect(ET_LAND_DUST,52, 20, "FxFall", 0.04f, Color.WHITE, 1f)
                .AddEffect(ET_WALK_PUSH, 52, 20, "FxRun", 0.06f, Color.WHITE, 1f);
        AddCharacter(owletMonster);

        CharacterParameters virtualGuy = new CharacterParameters("VirtualGuy", "VirtualGuy", "player")
                .AddStateAnimation(ES_IDLE,  "Idle(32x32)", .55f, Animation.PlayMode.LOOP)
                .AddStateAnimation(ES_WALK,  "Run(32x32)", .4f, Animation.PlayMode.LOOP)
                .AddStateAnimation(ES_RUN,  "Run(32x32)", .6f, Animation.PlayMode.LOOP)
                .AddStateAnimation(ES_JUMP,  "Jump(32x32)", 1f, Animation.PlayMode.LOOP)
                .AddStateAnimation(ES_CLIMB,  .25f, Animation.PlayMode.LOOP)
                .AddStateAnimation(ES_FALL,  "Fall(32x32)", 1f, Animation.PlayMode.LOOP)
                .AddStateAnimation(ES_DYING,  .3f, Animation.PlayMode.NORMAL)
                .AddStateAnimation(ES_ATTACK,  .25f, Animation.PlayMode.NORMAL)
                .AddStateAnimation(ES_HURT,  "Hit(32x32)", .35f, Animation.PlayMode.NORMAL)
                .AddStateAnimation(ES_SLIDING,  "WallJump(32x32)", .1f, Animation.PlayMode.LOOP)
                .AddEffect(ET_JUMP,52, 20, "FxJump", 0.06f, Color.WHITE, 1f)
                .AddEffect(ET_AIR_JUMP,32, 32, "OwletMonsterDoubleJumpDust", 0.06f, Color.SKY, 0.7f)
                .AddEffect(ET_LAND_DUST,52, 20, "FxFall", 0.04f, Color.WHITE, 1f)
                .AddEffect(ET_WALK_PUSH, 52, 20, "FxRun", 0.06f, Color.WHITE, 1f);
        AddCharacter(virtualGuy);

        CharacterParameters sberCat = new CharacterParameters("Sbercat", "Sbercat", "player")
                .AddStateAnimation(ES_IDLE,.16f, Animation.PlayMode.LOOP)
                .AddStateAnimation(ES_WALK,.16f, Animation.PlayMode.LOOP)
                .AddStateAnimation(ES_RUN,.08f, Animation.PlayMode.LOOP)
                .AddStateAnimation(ES_JUMP,.08f, Animation.PlayMode.LOOP)
                .AddStateAnimation(ES_CLIMB,  .08f, Animation.PlayMode.LOOP)
                .AddStateAnimation(ES_FALL, .08f, Animation.PlayMode.LOOP)
                .AddStateAnimation(ES_DYING,  .1f, Animation.PlayMode.NORMAL)
                .AddStateAnimation(ES_ATTACK,  .08f, Animation.PlayMode.NORMAL)
                .AddStateAnimation(ES_HURT, .04f, Animation.PlayMode.NORMAL)
                //.AddStateAnimation(ES_SLIDING,  "WallJump(32x32)", .1f, Animation.PlayMode.LOOP)
                .AddEffect(ET_JUMP,52, 20, "FxJump", 0.06f, Color.WHITE, 1f)
                .AddEffect(ET_AIR_JUMP,32, 32, "OwletMonsterDoubleJumpDust", 0.06f, Color.SKY, 0.7f)
                .AddEffect(ET_LAND_DUST,52, 20, "FxFall", 0.04f, Color.WHITE, 1f)
                .AddEffect(ET_WALK_PUSH, 52, 20, "FxRun", 0.06f, Color.WHITE, 1f);
        AddCharacter(sberCat);

        String characters = "";
        final Set<String> characterNames = Characters.keySet();
        for (String characterName : characterNames)
        {
            characters += characterName + " ";
        }

        App.Log(TAG, "Characters created: " + characters);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    private void AddCharacter(CharacterParameters characterParameters) { Characters.put(characterParameters.Name, characterParameters); }
    //-------------------------------------------------------------------------------------------------------------------------

    public CharacterParameters GetCharacter(String name)
    {
        return Characters.containsKey(name) ? Characters.get(name) : null;
    }
    //-------------------------------------------------------------------------------------------------------------------------
}
