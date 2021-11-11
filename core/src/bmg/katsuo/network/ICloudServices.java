package bmg.katsuo.network;

import bmg.katsuo.IApplication;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public interface ICloudServices
{
    void Init(IApplication app);
    void Destroy();

    void Pause();
    void Resume();

    void SignIn();
    void SignOut();
    boolean IsSignedIn();

    // Leaderboard
    void PostScore(long score, ScoreHandler handler);
    void GetScores(ScoreHandler handler);
    void PostAchievement(String achievementId, String achievementDescription);

    // Ads
    void ShowBannerAd();

    interface ICompletable
    {
        void OnComplete();
    }
    void ShowInterstitialAd(ICompletable callback);
    void ShowRewardedAd(ICompletable callback);

    // Billing
    void PurchaseAdRemoval();
    void PurchaseExtraContent();

    // Sharing
    void Share(Pixmap pixmap);

    boolean RenderBanner(String unitId, Texture texture);
}
