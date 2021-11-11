package bmg.katsuo.managers;

import bmg.katsuo.objects.GameObject;
import bmg.katsuo.objects.GameState;
import bmg.katsuo.utils.UserData;

public abstract class StateManager
{
    private GameState State;

    public void ProvideGameState(GameState state)
    {
        this.State = state;
    }

    public GameState GetState()
    {
        return State;
    }
/*
    public UserData GetStateData()
    {
        return State.GetData();
    }
*/
    /**
     * called before all layer data is instantiated
     * perfect time to add layer_managers
     */
    public abstract void AddLayerSystems(GameState state);

    /**
     * called when this state is Set
     */
    public abstract void Init(GameState state);

    /**
     * called every Update
     */
    public abstract void Update(GameState state, float delta);

    /**
     * called when the
     *
     * @param state
     */
    public abstract void dispose(GameState state);

    /**
     * called whent he game is resized -- should be overriden manually
     *
     * @param width
     * @param height
     */
    public void Resize(int width, int height)
    {
    }

    public GameObject SpawnPlayer(GameState state) { return null; }

    public void SetCheckpoint(String checkpoint) {};

    public void RespawnPlayer(GameState state) {}
}
