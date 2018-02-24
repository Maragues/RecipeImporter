package com.maragues.planner.ui.utils;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import com.maragues.planner_kotlin.R;


/**
 * Created by miguel on 28/11/14.
 */
public class ProgressFragmentDialog extends DialogFragment {

  public static final String TAG = ProgressFragmentDialog.class.getSimpleName();

  public static ProgressFragmentDialog newInstance() {
    return new ProgressFragmentDialog();
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    Dialog dialog = new Dialog(getActivity());
    dialog.setCancelable(false);
    dialog.setCanceledOnTouchOutside(false);
    dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);

    View view = new ProgressBar(getActivity());
    view.setId(R.id.progressbar);
    // layout to display
    dialog.setContentView(view);

    // set color transpartent
    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    // Disable the back button
    DialogInterface.OnKeyListener keyListener = (dialog1, keyCode, event) -> keyCode
        == KeyEvent.KEYCODE_BACK;
    dialog.setOnKeyListener(keyListener);

    return dialog;
  }
}
