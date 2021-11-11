package bmg.katsuo.gameplay.events;

import bmg.katsuo.IApplication;
import bmg.katsuo.managers.ResourcesCollection;
import bmg.katsuo.managers.SoundManager;
import bmg.katsuo.objects.GameState;
import bmg.katsuo.utils.Event;

public class LevelStartEventListener implements bmg.katsuo.utils.EventListener<LevelStartEventArgs>
{
    private IApplication App;

    public LevelStartEventListener(IApplication app)
    {
        App = app;
    }

    @Override
    public void Process(Event<LevelStartEventArgs> event, LevelStartEventArgs args)
    {
        //App.Log("LevelStartEventListener", "level started: " + args.LevelId);

        // save that level started
        //GameProgressDescription progress = App.GetProgress().GetUserProgress(PLAYER_ID);
        //progress.SetLevel(args.LevelId);
        //progress.SetCheckpointId(""); // it means first checkpoint
        //App.SaveProgress(progress);
        //App.SetCheckpoint("");

        final GameState state = App.GetState();
        final ResourcesCollection stateResources = state.GetResources();
        String file = stateResources.GetMusicFile(args.LevelId);
        if ((file != null) && !file.isEmpty())
        {
            App.GetSoundManager().PlayMusic(SoundManager.MUSIC, file, true);
        }
    }
};
//-------------------------------------------------------------------------------------------------------------------------


