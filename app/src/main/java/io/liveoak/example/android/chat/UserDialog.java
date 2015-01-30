package io.liveoak.example.android.chat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by mwringe on 19/01/15.
 */
public class UserDialog extends DialogFragment implements View.OnClickListener {


    private Listener listener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            listener = (Listener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.getClass().getSimpleName() + " must implement UserDialog.Listener.");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View userDialogView = getActivity().getLayoutInflater().inflate(R.layout.userdialog, null);

        builder.setView(userDialogView);
        Dialog dialog = builder.create();

        Button cancelButton = (Button) userDialogView.findViewById(R.id.userdialog_cancel_button);
        Button logoutButton = (Button) userDialogView.findViewById(R.id.userdialog_logout_button);

        cancelButton.setOnClickListener(this);
        logoutButton.setOnClickListener(this);

        return dialog;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.userdialog_logout_button) {
            listener.onLogout();
            this.dismiss();
        } else {
            this.dismiss();
        }
    }

    public interface Listener {
        public void onLogout();
    }
}
