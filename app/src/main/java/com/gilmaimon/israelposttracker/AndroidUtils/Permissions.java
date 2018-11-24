package com.gilmaimon.israelposttracker.AndroidUtils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class Permissions {

    public interface PermissionCallback {
        void permissionGranted(String permission);
        void permissionDenied(String permission);
    }

    public static class OnRequestPermissionHandler {
        private final String permission;
        private final int requestCode;
        private PermissionCallback callback;

        OnRequestPermissionHandler(String permission, int requestCode, PermissionCallback callback) {
            this.permission = permission;
            this.requestCode = requestCode;
            this.callback = callback;
        }

        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            if(requestCode == this.requestCode) {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callback.permissionGranted(permission);
                } else {
                    callback.permissionDenied(permission);
                }
            }
        }
    }

    private static int requestCodeFromString(String str) {
        return Math.abs(str.hashCode()) % (1 << 16);
    }

    public static OnRequestPermissionHandler RequirePermission(Activity context,
                                                               String permission,
                                                               PermissionCallback callback) {
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            callback.permissionGranted(permission);
        } else {
            ActivityCompat.requestPermissions(
                    context,
                    new String[]{permission},
                    requestCodeFromString(permission)
            );
        }

        return new OnRequestPermissionHandler(permission, requestCodeFromString(permission), callback);
    }
}
