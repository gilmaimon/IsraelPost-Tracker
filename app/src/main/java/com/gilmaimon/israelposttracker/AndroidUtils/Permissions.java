package com.gilmaimon.israelposttracker.AndroidUtils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.Arrays;

public class Permissions {

    public interface PermissionCallback {
        void permissionGranted(String[] permissions);
        void permissionDenied(String[] permissions);
    }

    public static class OnRequestPermissionHandler {
        private final String[] requestedPermissions;
        private final int requestCode;
        private PermissionCallback callback;

        OnRequestPermissionHandler(String[] requestedPermissions, int requestCode, PermissionCallback callback) {
            this.requestedPermissions = requestedPermissions;
            this.requestCode = requestCode;
            this.callback = callback;
        }

        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            if(requestCode == this.requestCode) {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callback.permissionGranted(this.requestedPermissions);
                } else {
                    callback.permissionDenied(this.requestedPermissions);
                }
            }
        }
    }

    private static int requestCodeFromString(String[] strs) {
        return Math.abs(Arrays.hashCode(strs)) % (1 << 16);
    }

    public static OnRequestPermissionHandler RequirePermission(Activity context,
                                                               String[] permissions,
                                                               PermissionCallback callback) {
        OnRequestPermissionHandler handler = new OnRequestPermissionHandler(
                permissions,
                requestCodeFromString(permissions),
                callback
        );

        for(String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        context,
                        permissions,
                        requestCodeFromString(permissions)
                );
                return handler;
            }
        }

        callback.permissionGranted(permissions);
        return handler;
    }
}
