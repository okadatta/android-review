package okadatta.util.androidreview.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import okadatta.util.androidreview.R;
import okadatta.util.androidreview.ReviewRequestManager;
import okadatta.util.androidreview.firebase.FirebaseAnalyticsLogger;

public class FeedbackDialogFragment extends DialogFragment {

    private String dialogTitle;
    private String feedbackButtonText;
    private String cancelButtonText;
    private String webPageUrl;
    private String anotherFeedbackWay;

    public FeedbackDialogFragment() {
        super();
        ReviewRequestManager manager = ReviewRequestManager.getInstance();
        dialogTitle = manager.getFeedbackDialogTitle();
        feedbackButtonText = manager.getFeedbackButtonText();
        cancelButtonText = manager.getFeedbackCancelButtonText();
        webPageUrl = manager.getFeedbackWebPageUrl();
        anotherFeedbackWay = manager.getFeedbackByAnotherWayExplanation();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(dialogTitle);

        // Set feedback button
        builder.setPositiveButton(feedbackButtonText, null);

        // Set cancel button
        builder.setNegativeButton(cancelButtonText, null);

        // Set view
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        builder.setView(inflater.inflate(R.layout.feedback, null));

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
                        logger.logEvent("FeedbackRequestDialog : feedbackButton");

                        // Close dialog
                        dismiss();

                        // Jump to feedback page url
                        Uri uri = Uri.parse(webPageUrl);
                        Intent i = new Intent(Intent.ACTION_VIEW,uri);
                        startActivity(i);
                    }
                });

                ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                    FirebaseAnalytics firebaseAnalytics = ReviewRequestManager.getInstance(getActivity()).getFirebaseAnalytics();
                    FirebaseAnalyticsLogger logger = new FirebaseAnalyticsLogger(firebaseAnalytics);

                    @Override
                    public void onClick(View view) {
                        // Send log to firebase
                        logger.logEvent("FeedbackRequestDialog : cancelButton");

                        // Show toast to inform that feedback can be sent by another way (e.g. menu item)
                        if (anotherFeedbackWay != null) {
                            if (!"".equals(anotherFeedbackWay)) {
                                Toast.makeText(getActivity(), anotherFeedbackWay, Toast.LENGTH_SHORT).show();
                            }
                        }

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
