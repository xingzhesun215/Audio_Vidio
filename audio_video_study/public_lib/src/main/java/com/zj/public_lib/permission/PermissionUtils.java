package com.zj.public_lib.permission;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * author: sun
 * desc:
 */

public class PermissionUtils {

    private static final String TAG = PermissionUtils.class.getCanonicalName();

    public interface PermissionCallbacks extends ActivityCompat.OnRequestPermissionsResultCallback {

        void onPermissionGranted(int requestCode, List<String> perms);

        void onPermissionDenied(int requestCode, List<String> perms);

    }

    public static boolean hasPermissions(Context context, String... perms) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        for (String perm : perms) {
            boolean hasPerm = (ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED);
            if (!hasPerm) {
                return false;
            }
        }

        return true;
    }


    public static void requestPermissions(final Object object, final int requestCode, final String... perms) {
        checkCallingObjectSuitability(object);

        executePermissionsRequest(object, perms, requestCode);
    }



    @TargetApi(23)
    static void executePermissionsRequest(Object object, String[] perms, int requestCode) {
        checkCallingObjectSuitability(object);
        if (object instanceof Activity) {
            ActivityCompat.requestPermissions((Activity) object, perms, requestCode);
        } else if (object instanceof Fragment) {
            ((Fragment) object).requestPermissions(perms, requestCode);
        } else if (object instanceof android.app.Fragment) {
            ((android.app.Fragment) object).requestPermissions(perms, requestCode);
        }
    }

    public static void onRequestPermissionsResult(int requestCode, String[] perms, int[] grantResults, Object... receivers) {
        ArrayList<String> granted = new ArrayList<>();
        ArrayList<String> denied = new ArrayList<>();
        for (int i = 0; i < perms.length; i++) {
            String perm = perms[i];
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                granted.add(perm);
            } else {
                denied.add(perm);
            }
        }

        for (Object object : receivers) {
            if (!granted.isEmpty()) {
                if (object instanceof PermissionCallbacks) {
                    ((PermissionCallbacks) object).onPermissionGranted(requestCode, granted);
                }
            }

            if (!denied.isEmpty()) {
                if (object instanceof PermissionCallbacks) {
                    ((PermissionCallbacks) object).onPermissionDenied(requestCode, denied);
                }
            }

            // 所有权限都已授予
            if (!granted.isEmpty() && denied.isEmpty()) {
                runAnnotatedMethods(object, requestCode);
            }
        }
    }

    /**
     * 解析注解，执行被注解的方法
     *
     * @param object
     * @param requestCode
     */
    private static void runAnnotatedMethods(Object object, int requestCode) {
        Class clazz = object.getClass();

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(AfterPermissionGranted.class)) {
                AfterPermissionGranted ann = method.getAnnotation(AfterPermissionGranted.class);
                if (ann.value() == requestCode) {
                    if (method.getParameterTypes().length > 0) {
                        Log.e("ME", "Cannot execute method " + method.getName() + " because it is non-void method and/or has input parameters.");
                        break;
                    }

                    try {
                        if (!method.isAccessible()) {
                            method.setAccessible(true);
                        }
                        method.invoke(object);
                    } catch (IllegalAccessException e) {
                        Log.e(TAG, "runDefaultMethod: IllegalAccessException", e);
                    } catch (InvocationTargetException e) {
                        Log.e(TAG, "runDefaultMethod: InvocationTargetException", e);
                    }
                }
            }
        }
    }

    @TargetApi(23)
    private static boolean shouldShowRequestPermissionRationale(Object object, String perm) {
        if (object instanceof Activity) {
            return ActivityCompat.shouldShowRequestPermissionRationale((Activity) object, perm);
        } else if (object instanceof Fragment) {
            return ((Fragment) object).shouldShowRequestPermissionRationale(perm);
        } else if (object instanceof android.app.Fragment) {
            return ((android.app.Fragment) object).shouldShowRequestPermissionRationale(perm);
        } else {
            return false;
        }
    }

    @TargetApi(11)
    private static Activity getActivity(Object object) {
        if (object instanceof Activity) {
            return ((Activity) object);
        } else if (object instanceof Fragment) {
            return ((Fragment) object).getActivity();
        } else if (object instanceof android.app.Fragment) {
            return ((android.app.Fragment) object).getActivity();
        } else {
            return null;
        }
    }

    private static FragmentManager getSupportFragmentManager(Object object) {
        if (object instanceof FragmentActivity) {
            return ((FragmentActivity) object).getSupportFragmentManager();
        } else if (object instanceof Fragment) {
            return ((Fragment) object).getChildFragmentManager();
        }
        return null;
    }

    private static android.app.FragmentManager getFragmentManager(Object object) {
        if (object instanceof Activity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                return ((Activity) object).getFragmentManager();
            }
        } else if (object instanceof android.app.Fragment) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                return ((android.app.Fragment) object).getChildFragmentManager();
            } else {
                return ((android.app.Fragment) object).getFragmentManager();
            }
        }
        return null;
    }

    /**
     * 是否勾选了 "不再询问"
     *
     * @param object
     * @param deniedPermissions
     * @return
     */
    public static boolean somePermissionsPermanentlyDenied(@NonNull Object object,
                                                           @NonNull List<String> deniedPermissions) {
        for (String deniedPermission : deniedPermissions) {
            if (permissionPermanentlyDenied(object, deniedPermission)) {
                return true;
            }
        }

        return false;
    }

    public static boolean permissionPermanentlyDenied(@NonNull Object object,
                                                      @NonNull String deniedPermission) {
        return !shouldShowRequestPermissionRationale(object, deniedPermission);
    }





    private static void checkCallingObjectSuitability(Object object) {
        if (object == null) {
            throw new NullPointerException("Activity or Fragment should not be null");
        }

        boolean isActivity = object instanceof Activity;
        boolean isSupportFragment = object instanceof Fragment;
        boolean isAppFragment = object instanceof android.app.Fragment;
        boolean isMinSdkM = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;

        if (!(isSupportFragment || isActivity || (isAppFragment && isMinSdkM))) {
            if (isAppFragment) {
                throw new IllegalArgumentException(
                        "Target SDK needs to be greater than 23 if caller is android.app.Fragment");
            } else {
                throw new IllegalArgumentException(
                        "Caller must be an Activity or a Fragment");
            }
        }
    }

    public static String notifyMessage(List<String> permissions) {
        if (permissions == null || permissions.size() == 0) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        sb.append("没有使用");
        for (int i = 0; i < permissions.size(); i++) {
            String permission = permissions.get(i);
            if (!sb.toString().contains("sd") && (permission.contains("WRITE_EXTERNAL_STORAGE") || permission.contains("READ_EXTERNAL_STORAGE"))) {
                sb.append("读写sd卡、");
            }
            if (permission.contains("CAMERA")) {
                sb.append("摄像头、");
            }
            if (permission.contains("LOCATION")) {
                sb.append("定位、");
            }
        }
        return sb.substring(0, sb.length() - 1) + "的权限,请在权限管理中开启";
    }
}
