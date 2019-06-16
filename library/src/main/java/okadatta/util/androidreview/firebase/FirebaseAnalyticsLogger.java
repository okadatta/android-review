package okadatta.util.androidreview.firebase;

import android.os.Bundle;
import com.google.firebase.analytics.FirebaseAnalytics;

public class FirebaseAnalyticsLogger {

    private FirebaseAnalytics firebaseAnalytics;

    public FirebaseAnalyticsLogger(FirebaseAnalytics firebaseAnalytics){
        this.firebaseAnalytics = firebaseAnalytics;
    }

    public void logEvent(String itemName){
        if (firebaseAnalytics == null) return;
        Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.CONTENT_TYPE, itemName);
        params.putString(FirebaseAnalytics.Param.ITEM_NAME, itemName);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, params);
    }

    public void logEvent(String itemName, String info){
        if (firebaseAnalytics == null) return;
        Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.CONTENT_TYPE, itemName);
        params.putString(FirebaseAnalytics.Param.ITEM_NAME, itemName);
        params.putString(FirebaseAnalytics.Param.VALUE, info);
        params.putString(FirebaseAnalytics.Param.ITEM_ID, info);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, params);
    }
}
