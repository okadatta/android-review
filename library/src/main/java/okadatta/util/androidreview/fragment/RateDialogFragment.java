package okadatta.util.androidreview.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import okadatta.util.androidreview.R;
import okadatta.util.androidreview.ReviewRequestManager;
import okadatta.util.androidreview.firebase.FirebaseAnalyticsLogger;

public class RateDialogFragment extends DialogFragment {

    private String dialogTitle;
    private String rateButtonText;
    private String cancelButtonText;
    private String atLeastOneStar;
    private int rateThreshold;

    public RateDialogFragment() {
        super();
        ReviewRequestManager manager = ReviewRequestManager.getInstance();
        dialogTitle = manager.getRateDialogTitle();
        rateButtonText = manager.getRateButtonText();
        cancelButtonText = manager.getRateCancelButtonText();
        atLeastOneStar = manager.getRateAtLeastOneStar();
        rateThreshold = manager.getRateThreshold();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(dialogTitle);

        // Set rate button
        builder.setPositiveButton(rateButtonText, null);

        // Set cancel button
        builder.setNegativeButton(cancelButtonText, null);

        // Set rating view
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        builder.setView(inflater.inflate(R.layout.rate, null));

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
                        // Display different dialogs depending on the rating
                        float stars = ((RatingBar) (view.getRootView().findViewById(R.id.ratingBar)) ).getRating();

                        if (stars >= rateThreshold) {
                            // Send log to firebase
                            logger.logEvent("RateRequestDialog : rateButton", Float.toString(stars));

                            // Close dialog
                            dismiss();

                            // Request review in app store
                            ReviewDialogFragment fragment = new ReviewDialogFragment();
                            fragment.show(getFragmentManager(), "review");

                        } else if (stars == 0) {
                            // Show toast
                            Toast.makeText(getActivity(), atLeastOneStar, Toast.LENGTH_SHORT).show();

                        } else {
                            // Send log to firebase
                            logger.logEvent("RateRequestDialog : rateButton", Float.toString(stars));

                            // Close dialog
                            dismiss();

                            // Request sending feedback
                            FeedbackDialogFragment fragment = new FeedbackDialogFragment();
                            fragment.show(getFragmentManager(), "feedback");

                        }
                    }
                });

                ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                    FirebaseAnalytics firebaseAnalytics = ReviewRequestManager.getInstance(getActivity()).getFirebaseAnalytics();
                    FirebaseAnalyticsLogger logger = new FirebaseAnalyticsLogger(firebaseAnalytics);

                    @Override
                    public void onClick(View view) {
                        // Send log to firebase
                        logger.logEvent("RateRequestDialog : cancelButton");

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
