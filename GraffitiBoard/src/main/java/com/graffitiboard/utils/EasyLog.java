package com.graffitiboard.utils;

import android.util.Log;

/**
 * Created by antiy2015 on 2017/4/5.AntiyLog
 */
public class EasyLog {
    private static final String DEFAULT_TAG = "EasyLog";
    private  static String TAG = DEFAULT_TAG;
    public static void init(String tag){
        EasyLog.TAG = tag;
    }

    private static boolean allowD = true;
    private static boolean allowI = true;
    private static boolean allowE = true;
    private static boolean allowW = true;
    private static boolean allowV = true;

    public static void setEnableLogD(boolean enable){
        EasyLog.allowD = enable;
    }

    public static void setEnableLogI(boolean enable){
        EasyLog.allowI = enable;
    }

    public static void setEnableLogE(boolean enable){
        EasyLog.allowE = enable;
    }

    public static void setEnableLogW(boolean enable){
        EasyLog.allowW = enable;
    }

    public static void setEnableLogV(boolean enable){
        EasyLog.allowV = enable;
    }

    public static void setAllLogEnable(boolean enable){
        EasyLog.setEnableLogD(enable);
        EasyLog.setEnableLogI(enable);
        EasyLog.setEnableLogE(enable);
        EasyLog.setEnableLogW(enable);
        EasyLog.setEnableLogV(enable);
    }

    public static void d(String log){
        if (!allowD){
            return;
        }
        String tag = generateTag(getCallerStackTraceElement());
        Log.d(TAG, tag+log);
    }

    public static void i(String log){
        if (!allowI){
            return;
        }
        String tag = generateTag(getCallerStackTraceElement());
        Log.i(TAG, tag+log);
    }

    public static void e(String log){
        if (!allowE){
            return;
        }
        String tag = generateTag(getCallerStackTraceElement());
        Log.e(TAG, tag+log);
    }

    public static void w(String log){
        if (!allowW){
            return;
        }
        String tag = generateTag(getCallerStackTraceElement());
        Log.w(TAG, tag+log);
    }

    public static void v(String log){
        if (!allowV){
            return;
        }
        String tag = generateTag(getCallerStackTraceElement());
        Log.v(TAG, tag+log);
    }

    private static String generateTag(StackTraceElement caller) {
        String tag = "%s.%s(Line:%d)::"; // 占位符
        String callerClazzName = caller.getClassName(); // 获取到类名
        callerClazzName = callerClazzName.substring(callerClazzName
                .lastIndexOf(".") + 1);
        String methodName = caller.getMethodName();
        int lineNumber = caller.getLineNumber();
        return String.format(tag, callerClazzName, methodName,lineNumber);
    }

    private static StackTraceElement getCallerStackTraceElement() {
        return Thread.currentThread().getStackTrace()[4];
    }
}
