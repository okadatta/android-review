package okadatta.util.androidreview;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.analytics.FirebaseAnalytics;

import okadatta.util.androidreview.dao.LaunchHistoryDAO;
import okadatta.util.androidreview.fragment.RateDialogFragment;

public class ReviewRequestManager {

    private static ReviewRequestManager singleton;
    private FirebaseAnalytics firebaseAnalytics;

    // Values for deciding whether request review or not
    private int launchCountThreshold = 100;         // Request review if your app is launched for greater than or equals to this times since installation
    private int daysSinceFirstLaunch = 14;          // Request review if greater than or equals to this days has passed since the first launch
    private int recentLaunchesCount = 30;           // Request review if your app has been launched for greater than or equals to this times during specified period
    private int daysForCountRecentLaunches = 21;    // Specify the period for counting recent app launch
    private int daysSinceLastReview = 365;          // Request review if greater than or equals to this days has passed since the last review by a user
    private int daysSinceLastReviewRequest = 180;   // Request review if greater than or equals to this days has passed since the last review request

    // Values for rating dialog
    private String rateDialogTitle;
    private String rateButtonText;
    private String rateCancelButtonText;
    private String rateAtLeastOneStar;
    private int rateThreshold = 4;

    // Values for review dialog
    private String reviewDialogTitle;
    private String reviewButtonText;
    private String reviewCancelButtonText;

    // Values for feedback dialog
    private String feedbackDialogTitle;
    private String feedbackButtonText;
    private String feedbackCancelButtonText;
    private String feedbackWebPageUrl;
    private String feedbackByAnotherWayExplanation;

    private ReviewRequestManager(Context context) {
        LaunchHistoryDAO.registerLaunchHistory(context);

        rateDialogTitle = context.getResources().getString(R.string.rateDialogTitle);
        rateButtonText = context.getResources().getString(R.string.rateButton);
        rateCancelButtonText = context.getResources().getString(R.string.rateCancelButton);
        rateAtLeastOneStar = context.getResources().getString(R.string.rateAtLeastOneStar);

        reviewDialogTitle = context.getResources().getString(R.string.reviewDialogTitle);
        reviewButtonText = context.getResources().getString(R.string.reviewButton);
        reviewCancelButtonText = context.getResources().getString(R.string.reviewCancelButton);

        feedbackDialogTitle = context.getResources().getString(R.string.feedbackDialogTitle);
        feedbackButtonText = context.getResources().getString(R.string.feedbackButton);
        feedbackCancelButtonText = context.getResources().getString(R.string.feedbackCancelButton);
        feedbackWebPageUrl = context.getResources().getString(R.string.feedbackWebPageUrl);
        feedbackByAnotherWayExplanation = context.getResources().getString(R.string.anotherFeedbackWay);
    }

    private ReviewRequestManager(Context context, FirebaseAnalytics firebaseAnalytics) {
        this(context);
        this.firebaseAnalytics = firebaseAnalytics;
    }

    public static ReviewRequestManager getInstance() {
        if (singleton == null) {
            return null;
        }
        return singleton;
    }

    public static ReviewRequestManager getInstance(Context context) {
        if (singleton == null) {
            singleton = new ReviewRequestManager(context);
        }
        return singleton;
    }

    public static ReviewRequestManager getInstance(Context context, FirebaseAnalytics firebaseAnalytics) {
        if (singleton == null) {
            singleton = new ReviewRequestManager(context, firebaseAnalytics);
        } else if (singleton.firebaseAnalytics == null) {
            singleton.setFirebaseAnalytics(firebaseAnalytics);
        }
        return singleton;
    }

    public void requestReview(Activity activity){

        // Check history of jumping to GooglePlay
        SharedPreferences preferences = activity.getPreferences(Context.MODE_PRIVATE);
        long fromLastReviewInSecond = System.currentTimeMillis() / 1000 - daysSinceLastReview * 24 * 60 * 60;
        if (fromLastReviewInSecond < preferences.getLong("storeReview", 0) / 1000) {
            return;
        }

        // Check history of requesting review
        long fromLastReviewRequestInSecond = System.currentTimeMillis() / 1000 - daysSinceLastReviewRequest * 24 * 60 * 60;
        if (fromLastReviewRequestInSecond < preferences.getLong("reviewRequest", 0) / 1000) {
            return;
        }

        // Check launch count
        if (LaunchHistoryDAO.getLaunchCount(activity.getApplicationContext()) < launchCountThreshold) {
            return;
        }

        // Check history of first launch
        long firstLaunchInSecond = LaunchHistoryDAO.getLaunchDateTimeMillis(activity.getApplicationContext(), LaunchHistoryDAO.getLaunchCount(activity.getApplicationContext())) / 1000;
        long firstLaunchBorderInSecond = System.currentTimeMillis() / 1000 - daysSinceFirstLaunch * 24 * 60 * 60;
        if (firstLaunchBorderInSecond < firstLaunchInSecond) {
            // 初回起動が指定日よりも最近ならレビュー依頼しない
            return;
        }

        // Check launch frequency
        long recentUsageBorderInSecond = System.currentTimeMillis() / 1000 - daysForCountRecentLaunches * 24 * 60 * 60;
        if (LaunchHistoryDAO.getLaunchDateTimeMillis(activity.getApplicationContext(), recentLaunchesCount) / 1000 < recentUsageBorderInSecond) {
            return;
        }

        // Register history of requesting review
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("reviewRequest", System.currentTimeMillis());
        editor.apply();

        // Show rating request dialog
        RateDialogFragment fragment = new RateDialogFragment();
        fragment.show(activity.getFragmentManager(), "rate");

    }

    public void setFirebaseAnalytics(FirebaseAnalytics firebaseAnalytics) {
        this.firebaseAnalytics = firebaseAnalytics;
    }

    public ReviewRequestManager setLaunchCountThreshold(int launchCountThreshold) {
        this.launchCountThreshold = launchCountThreshold;
        return this;
    }

    public ReviewRequestManager setDaysSinceFirstLaunch(int daysSinceFirstLaunch) {
        this.daysSinceFirstLaunch = daysSinceFirstLaunch;
        return this;
    }

    public ReviewRequestManager setRecentLaunchesCount(int recentLaunchesCount) {
        this.recentLaunchesCount = recentLaunchesCount;
        return this;
    }

    public ReviewRequestManager setDaysForCountRecentLaunches(int daysForCountRecentLaunches) {
        this.daysForCountRecentLaunches = daysForCountRecentLaunches;
        return this;
    }

    public ReviewRequestManager setDaysSinceLastReview(int daysSinceLastReview) {
        this.daysSinceLastReview = daysSinceLastReview;
        return this;
    }

    public ReviewRequestManager setDaysSinceLastReviewRequest(int daysSinceLastReviewRequest) {
        this.daysSinceLastReviewRequest = daysSinceLastReviewRequest;
        return this;
    }

    public ReviewRequestManager setRateDialogTitle(String rateDialogTitle) {
        this.rateDialogTitle = rateDialogTitle;
        return this;
    }

    public ReviewRequestManager setRateButtonText(String rateButtonText) {
        this.rateButtonText = rateButtonText;
        return this;
    }

    public ReviewRequestManager setRateCancelButtonText(String rateCancelButtonText) {
        this.rateCancelButtonText = rateCancelButtonText;
        return this;
    }

    public ReviewRequestManager setRateAtLeastOneStar(String rateAtLeastOneStar) {
        this.rateAtLeastOneStar = rateAtLeastOneStar;
        return this;
    }

    public ReviewRequestManager setRateThreshold(int rateThreshold) {
        this.rateThreshold = rateThreshold;
        return this;
    }

    public ReviewRequestManager setReviewDialogTitle(String reviewDialogTitle) {
        this.reviewDialogTitle = reviewDialogTitle;
        return this;
    }

    public ReviewRequestManager setReviewButtonText(String reviewButtonText) {
        this.reviewButtonText = reviewButtonText;
        return this;
    }

    public ReviewRequestManager setReviewCancelButtonText(String reviewCancelButtonText) {
        this.reviewCancelButtonText = reviewCancelButtonText;
        return this;
    }

    public ReviewRequestManager setFeedbackDialogTitle(String feedbackDialogTitle) {
        this.feedbackDialogTitle = feedbackDialogTitle;
        return this;
    }

    public ReviewRequestManager setFeedbackButtonText(String feedbackButtonText) {
        this.feedbackButtonText = feedbackButtonText;
        return this;
    }

    public ReviewRequestManager setFeedbackCancelButtonText(String feedbackCancelButtonText) {
        this.feedbackCancelButtonText = feedbackCancelButtonText;
        return this;
    }

    public ReviewRequestManager setFeedbackWebPageUrl(String feedbackWebPageUrl) {
        this.feedbackWebPageUrl = feedbackWebPageUrl;
        return this;
    }

    public ReviewRequestManager setFeedbackByAnotherWayExplanation(String feedbackByAnotherWayExplanation) {
        this.feedbackByAnotherWayExplanation = feedbackByAnotherWayExplanation;
        return this;
    }

    public int getLaunchCountThreshold() {
        return launchCountThreshold;
    }

    public int getDaysSinceFirstLaunch() {
        return daysSinceFirstLaunch;
    }

    public int getRecentLaunchesCount() {
        return recentLaunchesCount;
    }

    public int getDaysForCountRecentLaunches() {
        return daysForCountRecentLaunches;
    }

    public int getDaysSinceLastReview() {
        return daysSinceLastReview;
    }

    public int getDaysSinceLastReviewRequest() {
        return daysSinceLastReviewRequest;
    }

    public FirebaseAnalytics getFirebaseAnalytics() {
        return firebaseAnalytics;
    }

    public String getRateDialogTitle() {
        return rateDialogTitle;
    }

    public String getRateButtonText() {
        return rateButtonText;
    }

    public String getRateCancelButtonText() {
        return rateCancelButtonText;
    }

    public String getRateAtLeastOneStar() {
        return rateAtLeastOneStar;
    }

    public int getRateThreshold() {
        return rateThreshold;
    }

    public String getReviewDialogTitle() {
        return reviewDialogTitle;
    }

    public String getReviewButtonText() {
        return reviewButtonText;
    }

    public String getReviewCancelButtonText() {
        return reviewCancelButtonText;
    }

    public String getFeedbackDialogTitle() {
        return feedbackDialogTitle;
    }

    public String getFeedbackButtonText() {
        return feedbackButtonText;
    }

    public String getFeedbackCancelButtonText() {
        return feedbackCancelButtonText;
    }

    public String getFeedbackWebPageUrl() {
        return feedbackWebPageUrl;
    }

    public String getFeedbackByAnotherWayExplanation() {
        return feedbackByAnotherWayExplanation;
    }
}
