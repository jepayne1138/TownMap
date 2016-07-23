package com.jepaynedev.townmap;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.gms.common.SignInButton;

/**
 * Created by James Payne on 7/17/2016.
 * jepayne1138@gmail.com
 */
public class SignInDialogFragment extends DialogFragment implements View.OnClickListener {

    /* The activity that creates an instance of this dialog fragment must
         * implement this interface in order to receive event callbacks.
         * Each method passes the DialogFragment in case the host needs to query it. */
    public interface SignInDialogListener {
        public void onGoogleSignInClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    SignInDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (SignInDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement SignInDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this.getActivity());
        dialogBuilder.setTitle(R.string.dialog_sign_in_title);

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and get the dialog view
        // Pass null as the parent view because its going in the dialog layout
        View dialogView = inflater.inflate(R.layout.dialog_sign_in, null);
        SignInButton signInButton = (SignInButton) dialogView.findViewById(
                R.id.google_sign_in_button);
        signInButton.setOnClickListener(this);

        // Set the layout for the dialog
        dialogBuilder.setView(dialogView)
        // Add action buttons
            .setNegativeButton(R.string.dialog_sign_in_negative_button,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mListener.onDialogNegativeClick(SignInDialogFragment.this);
                        }
                    }
            );

        return dialogBuilder.create();
    }

    private void googleSignIn() {
        // The sign in with Boogle button was clicked
        // Attempt to sign in with Google and call the interface onGoogleSignInClick method
        mListener.onGoogleSignInClick(this);

        // Close the dialog
        this.dismiss();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.google_sign_in_button:
                googleSignIn();
                return;
            default:
                break;
        }
        // There should be no unhandled cases
        throw new IllegalArgumentException("No handler for the given view: " + view.toString());
    }
}
