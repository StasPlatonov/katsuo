package bmg.katsuo.network;

import bmg.katsuo.IApplication;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateCallback implements Callback<UpdateData>
{
    private final static String TAG = "UpdateCallback";

    private IApplication App;
    private UpdateHandler Handler;
    private String Url;
    //-------------------------------------------------------------------------------------------------------------------------

    UpdateCallback(IApplication app, UpdateHandler handler)
    {
        App = app;
        Handler = handler;
    }
    //-------------------------------------------------------------------------------------------------------------------------

    public void GetUpdate(String url, boolean lite)
    {
        if (url == null)
        {
            return;
        }

        Url = url;
        App.Log(TAG, "CheckVersion to check version on " + Url);

        NetworkService service = NetworkServiceFactory.getNetworkService(url);
        if (service == null)
        {
            App.Error(TAG, "Failed to create greeting service");
            return;
        }

        Call<UpdateData> call = service.GetUpdate(lite);
        if (call == null)
        {
            App.Error(TAG, "Failed to create call");
            return;
        }

        call.enqueue(this);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void onResponse(Call<UpdateData> call, Response<UpdateData> response)
    {
        if (response == null)
        {
            Handler.OnUpdateError("Null response");
            return;
        }

        if (!response.isSuccessful())
        {
            Handler.OnUpdateError("Failed to get update: code " + response.code() + ", msg: " + response.message());
            return;
        }

        UpdateData update = response.body();
        Handler.OnUpdateReceived(update);
    }
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
    public void onFailure(Call<UpdateData> cl, Throwable t)
    {
        Handler.OnUpdateError(t.getMessage());
    }
    //-------------------------------------------------------------------------------------------------------------------------
};