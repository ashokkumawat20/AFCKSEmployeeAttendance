package in.afckstechnologies.employeeattendance.utils;

import java.util.ArrayList;

/**
 * Created by admin on 12/9/2016.
 */

public class Config {

    public static final String BASE_URL = "http://afckstechnologies.in/afckstest/api/";
    public static final String URL_AVAILABLEENQUIRYUSER = BASE_URL + "user/availableEnquiryUser";
    public static final String URL_UPDATEATTENDANCEDETAILS = BASE_URL + "user/updateattendancedetails";
    public static final String URL_GETAVAILABLEMOBILEDEVICES = BASE_URL + "user/getAvailableEMobileDeviceID";
    public static final String URL_ADDEMPLOYEEATTENDACE = BASE_URL + "user/addEmployeeAttendace";
    public static final String URL_GETALLEMPLOYEEATTENDANCEBYUSERID = BASE_URL + "user/getallEmployeeAttendanceByUserID";
    public static final String URL_AVAILABLERANGE = BASE_URL + "user/availableRange";
    public static final String URL_GETALLEMPLOYEEAMONTH = BASE_URL + "user/getallEmployeeAMonth";
    public static final String URL_GETUSERNAMEPASSSMS = BASE_URL + "user/getUserNamePassSMS";
    public static final String URL_ADDTEMPLOCATIONDATA = BASE_URL + "user/addTempLocationData";
    public static final String URL_CHECKEMPLOYEELOGINATTENDANCE = BASE_URL + "user/checkEmployeeLoginAttendance";
    public static final String URL_CHECKEMPLOYEELOGOUTATTENDANCE = BASE_URL + "user/checkEmployeeLogoutAttendance";
    public static final String URL_APPLYCURRENTDAYLEAVE = BASE_URL + "user/applyCurrentDayLeave";
    public static final String URL_APPLYLEAVEFORFUTURE = BASE_URL + "user/applyLeaveForFuture";
    public static final String URL_CHECKAVAILABLELEAVE = BASE_URL + "user/checkAvailableLeave";
    public static final String URL_GETALLEMPLOYEELEAVEBYUSERID = BASE_URL + "user/getallEmployeeLeaveByUserID";
    public static final String URL_DELETEEMPLOYEELEAVE = BASE_URL + "user/deleteEmployeeLeave";
    public static final String GETEMPLOYEELEAVESBYID = BASE_URL + "user/getEmployeeLeavesByID";
    public static final String URL_GETAVAILABLEROLES = BASE_URL + "user/getAvailableRoles";
    public static String DATA_MOVE_FROM_LOCATION = "";
    public static ArrayList<String> VALUE = new ArrayList<String>();
    // public static final String SMS_ORIGIN = "WAVARM";
    public static final String SMS_ORIGIN = "AFCKST";
}
