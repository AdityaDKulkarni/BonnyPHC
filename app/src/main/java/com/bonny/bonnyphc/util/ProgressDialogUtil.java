package com.bonny.bonnyphc.util;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * @author Aditya Kulkarni
 */

public class ProgressDialogUtil {

    public static ProgressDialog progressDialog(Context context, String message, boolean isCancellable){
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(isCancellable);
        progressDialog.setCanceledOnTouchOutside(isCancellable);
        return progressDialog;
    }
}
