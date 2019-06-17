# android-review

android-review is a library to implement review request with smart strategy quite easily.

This library request in-app rating at first. Then if the user choose high rating, the library request the user to post review at GooglePlay. If the user choose row rating, the library request to send feedback with your own web page instead of GooglePlay.

Of course, you can control when your app request review by some settings. Also, you can change whole text displayed in dialogs.

- When a user give high rate to your app, this library request the user to post review at GooglePlay.
<img src="https://okadatta.github.io/android-review/review.gif" width="320">

- When a user give low rate to your app, this library request the user to send concrete feedback with your own website.
<img src="https://okadatta.github.io/android-review/feedback.gif" width="320">

# Preparation

This library requires feedback web page url that will be shown when a user give low rate to your app.
If you don't have a web page to collect user feedback, I recommend you to create one using [Google Forms](https://docs.google.com/forms/) before installing this library.

# Install

1. Add repository to your application level build.gradle.

    ```groovy
    repositories {
        maven {
            url 'http://okadatta.github.io/android-review/repository'
        }
    }
    ```

    If you get some error, confirm that the above description is written to application level build.gradle (NOT project level).

2. Add dependencies to your application level build.gradle.

    ```groovy
    dependencies {
      implementation 'okadatta.util:android-review:1.0.1'
    }
    ```

# Usage

## Get Instance and Set Required Parameter

The only class you need to consider is ReviewRequestManager. You can get the singleton instance and set required parameter with below code.

```java
final ReviewRequestManager manager = ReviewRequestManager.getInstance(context);
manager.setFeedbackWebPageUrl("Set your feedback web page url");
```

Notice that ```ReviewRequestManager#getInstance()``` returns null if you have never called ```ReviewRequestManager#getInstance(Context)``` before.

### Optional

If you are using FirebaseAnalytics in your application, this library can send log to FirebaseAnalytics.
Just pass a FirerbaseAnalytics instance when you get a instance of ReviewRequestManager.

```java
FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
final ReviewRequestManager manager = ReviewRequestManager.getInstance(context, firebaseAnalytics);
```

## Register App's Launch History

You just need to call method ```ReviewRequestManager#registerLaunchHistory(Context)```. All history information is recorded automatically.

```java
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Record Launch History
        final ReviewRequestManager manager = ReviewRequestManager.getInstance(this);
        manager.registerLaunchHistory(this);
```

## Request Review

You just need to call method ```ReviewRequestManager#requestReview(Activity)```. The library automatically compare conditions and launch history and decide whether to request review.

```java
ReviewRequestManager.getInstance().requestReview(MainActivity.this);
```

# Conditions

1. The application has been launched for N or more times (default: 100)
1. N or more days has passed since user launch the application for the first time (default: 14)
1. X days has passed since last Y-th launch of the application (default: X: 21 Y: 30)
1. N or more days has passed since last last review request by this library (default: 180)
1. N or more days has passed since last jump to GooglePlay vis this library (default: 365)
1. Rating by a user at the rating dialog is N or more stars (default: 4)

# Change Default Settings

## Conditions

```java
ReviewRequestManager.getInstance()
                    .setLaunchCountThreshold(50)
                    .setDaysSinceFirstLaunch(3)
                    .setRecentLaunchesCount(5)
                    .setDaysForCountRecentLaunches(3)
                    .setDaysSinceLastReviewRequest(30)
                    .setDaysSinceLastReview(180)
                    .setRateThreshold(3);
```

## Messages

```java
ReviewRequestManager.getInstance()
                    .setRateDialogTitle("Set title of rating dialog")
                    .setRateButtonText("Set submit button text of rating dialog")
                    .setRateCancelButtonText("Set cancel button text of rating dialog")
                    .setRateAtLeastOneStar("Set message which displayed when user press submit button without choosing any rate")
                    .setReviewDialogTitle("Set title of review requesting dialog")
                    .setReviewButtonText("Set submit button text of review requesting dialog")
                    .setReviewCancelButtonText("Set cancel button text of review requesting dialog")
                    .setFeedbackDialogTitle("Set title of feedback requesting dialog")
                    .setFeedbackButtonText("Set submit button text of feedback requesting dialog")
                    .setFeedbackCancelButtonText("Set cancel button text of feedback requesting dialog")
                    .setFeedbackWebPageUrl("Set your feedback web page url")
                    .setFeedbackByAnotherWayExplanation("If you have another way to access feedback url in your application, explain that here to notify user via Toast");
```

# Contribute

Your contribution is really appreciated.

1. Fork it.
1. Create your feature branch.
1. Commit your changes to the branch.
1. Push the branch to origin.
1. Create new Pull Request into master branch.
