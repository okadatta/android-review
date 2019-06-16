package okadatta.util.androidreview.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.google.firebase.analytics.FirebaseAnalytics;

import okadatta.util.androidreview.R;
import okadatta.util.androidreview.ReviewRequestManager;
import okadatta.util.androidreview.firebase.FirebaseAnalyticsLogger;

public class ReviewDialogFragment extends DialogFragment {

    private String dialogTitle;
    private String reviewButtonText;
    private String cancelButtonText;

    public ReviewDialogFragment() {
        super();
        ReviewRequestManager manager = ReviewRequestManager.getInstance();
        dialogTitle = manager.getReviewDialogTitle();
        reviewButtonText = manager.getReviewButtonText();
        cancelButtonText = manager.getReviewCancelButtonText();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(dialogTitle);

        // Set review button
        builder.setPositiveButton(reviewButtonText, null);

        // Set cancel button
        builder.setNegativeButton(cancelButtonText, null);

        // Set view
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        builder.setView(inflater.inflate(R.layout.review, null));

        // Create dialog
        AlertDialog dialog = builder.create();

        // Disable closing by touching outside
        dialog.setCanceledOnTouchOutside(false);

        // Define process required to be executed after dialog has been created
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    FirebaseAnalytics firebaseAnalytics = ReviewRequestManager.getInstance(getActivity()).getFirebaseAnalytics();
                    FirebaseAnalyticsLogger logger = new FirebaseAnalyticsLogger(firebaseAnalytics);

                    @Override
                    public void onClick(View view) {
                        // Send log to firebase
                        logger.logEvent("ReviewRequestDialog : openStoreButton");

                        // Close dialog
                        dismiss();

                        // Register access history to GooglePlay
                        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putLong("storeReview", System.currentTimeMillis());
                        editor.apply();

                        // Jump to GooglePlay
                        final String appPackageName = getActivity().getPackageName();
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                    }
                });

                ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                    FirebaseAnalytics firebaseAnalytics = ReviewRequestManager.getInstance(getActivity()).getFirebaseAnalytics();
                    FirebaseAnalyticsLogger logger = new FirebaseAnalyticsLogger(firebaseAnalytics);

                    @Override
                    public void onClick(View view) {
                        // Send log to firebase
                        logger.logEvent("ReviewRequestDialog : cancelButton");

                        // Close dialog
                        dismiss();
                    }
                });
            }
        });

        return dialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }
}
