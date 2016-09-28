package cnic.sdc.videorecorder;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * 操作确认框
 */
public abstract class ConfirmDialog{

    private AlertDialog.Builder builder;

    public ConfirmDialog(Context context, String title, String msg, String ok_text, String no_text){
        this.builder = new AlertDialog.Builder(context);
        this.builder.setTitle(title);
        this.builder.setMessage(msg);
        this.builder.setPositiveButton(ok_text, new OkDialogListener(this));
        this.builder.setNegativeButton(no_text, new NoDialogListener(this));
    }

    public void show(){
        this.builder.create().show();
    }

    public abstract void ok_action();
    public abstract void no_action();


    private class OkDialogListener implements DialogInterface.OnClickListener{

        private ConfirmDialog _dialog=null;

        public OkDialogListener(ConfirmDialog dialog){
            this._dialog = dialog;
        }

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            this._dialog.ok_action();
            dialogInterface.dismiss();
        }
    }

    private class NoDialogListener implements DialogInterface.OnClickListener{

        private ConfirmDialog _dialog=null;

        public NoDialogListener(ConfirmDialog dialog){
            this._dialog = dialog;
        }

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            this._dialog.no_action();
            dialogInterface.dismiss();
        }
    }

}
