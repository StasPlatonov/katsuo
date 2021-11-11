package bmg.katsuo.network;

public interface UpdateHandler
{
    void OnUpdateReceived(UpdateData update);
    void OnUpdateError(String error);
}
