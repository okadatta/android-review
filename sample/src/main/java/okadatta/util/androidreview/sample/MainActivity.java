package okadatta.util.androidreview.sample;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import okadatta.util.androidreview.ReviewRequestManager;
import okadatta.util.androidreview.dao.LaunchHistoryDAO;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Register Launch History
        final ReviewRequestManager manager = ReviewRequestManager.getInstance(this);
        manager.registerLaunchHistory(this);

        // 2. Set preferrences
        manager.setLaunchCountThreshold(0)
                .setDaysSinceFirstLaunch(0)
                .setRecentLaunchesCount(1)
                .setDaysForCountRecentLaunches(1)
                .setDaysSinceLastReviewRequest(0)
                .setDaysSinceLastReview(0);

        // 3. Call ReviewRequestManager.requestReview() at anywhere you want to request review
        findViewById(R.id.requestReview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReviewRequestManager.getInstance().requestReview(MainActivity.this);
                setViews();
            }
        });

        setViews();
    }

    private void setViews() {
        // Set preferences
        final ReviewRequestManager manager = ReviewRequestManager.getInstance();

        ((EditText)findViewById(R.id.launchCountThreshold)).setText(Integer.toString(manager.getLaunchCountThreshold()));
        ((EditText)findViewById(R.id.daysSinceFirstLaunch)).setText(Integer.toString(manager.getDaysSinceFirstLaunch()));
        ((EditText)findViewById(R.id.recentLaunchCountThreshold)).setText(Integer.toString(manager.getRecentLaunchesCount()));
        ((EditText)findViewById(R.id.daysForCountRecentLaunches)).setText(Integer.toString(manager.getDaysForCountRecentLaunches()));
        ((EditText)findViewById(R.id.daysSinceLastReview)).setText(Integer.toString(manager.getDaysSinceLastReview()));
        ((EditText)findViewById(R.id.daysSinceLastReviewRequest)).setText(Integer.toString(manager.getDaysSinceLastReviewRequest()));

        // Set listener for applying preferences
        findViewById(R.id.applySettings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.setLaunchCountThreshold(
                        Integer.parseInt(((EditText)findViewById(R.id.launchCountThreshold)).getText().toString())
                );
                manager.setDaysSinceFirstLaunch(
                        Integer.parseInt(((EditText)findViewById(R.id.daysSinceFirstLaunch)).getText().toString())
                );
                manager.setRecentLaunchesCount(
                        Integer.parseInt(((EditText)findViewById(R.id.recentLaunchCountThreshold)).getText().toString())
                );
                manager.setDaysForCountRecentLaunches(
                        Integer.parseInt(((EditText)findViewById(R.id.daysForCountRecentLaunches)).getText().toString())
                );
                manager.setDaysSinceLastReview(
                        Integer.parseInt(((EditText)findViewById(R.id.daysSinceLastReview)).getText().toString())
                );
                manager.setDaysSinceLastReviewRequest(
                        Integer.parseInt(((EditText)findViewById(R.id.daysSinceLastReviewRequest)).getText().toString())
                );
                setViews();
            }
        });

        // Set histories
        SharedPreferences preferences = this.getPreferences(Context.MODE_PRIVATE);

        ((TextView)findViewById(R.id.launchCount)).setText(Integer.toString(LaunchHistoryDAO.getLaunchCount(this)));
        ((TextView)findViewById(R.id.firstLaunch)).setText(
                Long.toString(LaunchHistoryDAO.getLaunchDateTimeMillis(this, LaunchHistoryDAO.getLaunchCount(this)))
        );
        ((TextView)findViewById(R.id.launchFrequency)).setText(
                "Last "
                + manager.getRecentLaunchesCount()
                + "th launch : "
                + LaunchHistoryDAO.getLaunchDateTimeMillis(this, manager.getRecentLaunchesCount())
        );
        ((TextView)findViewById(R.id.lastJump)).setText(
                Long.toString(preferences.getLong("storeReview", 0))
        );
        ((TextView)findViewById(R.id.lastRequest)).setText(
                Long.toString(preferences.getLong("reviewRequest", 0))
        );
    }
}
