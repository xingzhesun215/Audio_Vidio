package com.zj.public_lib.utils;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by Administrator on 2016/3/18.
 */
public class TimeUtil {
    /**
     * 获取时分的工具类
     *
     * @param context
     * @param curHour
     * @param curMinute
     * @param litsener
     */
    public static void getTimeAction(Context context, int curHour, int curMinute, final onGetTimeCommitListener litsener) {
        TimePickerDialog time = new TimePickerDialog(context,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view,
                                          int hourOfDay, int minute) {
                        litsener.getTime(hourOfDay, minute, hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay, minute < 10 ? "0" + minute : "" + minute);
                    }
                }, curHour, curMinute, true);
        time.show();
    }

    /**
     * 获取日期的工具类
     *
     * @param context
     * @param curYear
     * @param curMonth
     * @param curDay
     */
    public static void getDateAction(Context context, int curYear, int curMonth, int curDay, final onGetDataCommitListener listener) {
        DatePickerDialog datePicker = new DatePickerDialog(
                context, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear, int dayOfMonth) {
                listener.getData(year, monthOfYear + 1, dayOfMonth);
            }
        }, curYear, curMonth - 1, curDay);
        datePicker.show();
    }

    /**
     * 当前时间为默认
     *
     * @param context
     * @param listener
     */
    public static void getDateActionByCurTime(Context context, final onGetDataCommitListener listener) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePicker = new DatePickerDialog(
                context, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear, int dayOfMonth) {
                listener.getData(year, monthOfYear + 1, dayOfMonth);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
        datePicker.show();
    }

    /**
     * 获得时间 时 分
     */
    public interface onGetTimeCommitListener {
        void getTime(int getHour, int getMinute, String getHourStr, String getMinuteStr);
    }

    /**
     * 获得时间 年 月 日
     */
    public interface onGetDataCommitListener {
        void getData(int getYear, int getMonth, int getData);
    }
}
