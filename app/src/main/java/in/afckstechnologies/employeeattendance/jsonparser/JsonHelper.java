package in.afckstechnologies.employeeattendance.jsonparser;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.afckstechnologies.employeeattendance.models.EmployeeAttendanceDAO;
import in.afckstechnologies.employeeattendance.models.EmployeeLeaveDAO;


public class JsonHelper {


    private ArrayList<EmployeeAttendanceDAO> employeeAttendanceDAOArrayList = new ArrayList<EmployeeAttendanceDAO>();
    private EmployeeAttendanceDAO employeeAttendanceDAO;

    private ArrayList<EmployeeLeaveDAO> leaveDAOArrayList = new ArrayList<EmployeeLeaveDAO>();
    private EmployeeLeaveDAO employeeLeaveDAO;

    //studentPaser
    public ArrayList<EmployeeAttendanceDAO> parseShowEmployeeAttendanceList(String rawLeadListResponse) {
        // TODO Auto-generated method stub
        Log.d("scheduleListResponse", rawLeadListResponse);
        try {
            JSONArray leadJsonObj = new JSONArray(rawLeadListResponse);

            for (int i = 0; i < leadJsonObj.length(); i++) {
                employeeAttendanceDAO = new EmployeeAttendanceDAO();
                JSONObject json_data = leadJsonObj.getJSONObject(i);
                employeeAttendanceDAO.setId(json_data.getString("id"));
                employeeAttendanceDAO.setFirst_name(json_data.getString("first_name"));
                employeeAttendanceDAO.setUser_id(json_data.getString("user_id"));
                employeeAttendanceDAO.setAddress(json_data.getString("TXT"));
                employeeAttendanceDAO.setDatetime(json_data.getString("datetime"));
                employeeAttendanceDAO.setLogout_location(json_data.getString("logout_location"));
                employeeAttendanceDAO.setMobile_no(json_data.getString("mobile_no"));
                employeeAttendanceDAO.setLogin_time(json_data.getString("login_time"));
                employeeAttendanceDAO.setLogout_time(json_data.getString("logout_time"));
                employeeAttendanceDAOArrayList.add(employeeAttendanceDAO);
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return employeeAttendanceDAOArrayList;
    }

    //userLeavePaser
    public ArrayList<EmployeeLeaveDAO> parseShowEmployeeLeaveList(String rawLeadListResponse) {
        // TODO Auto-generated method stub
        Log.d("scheduleListResponse", rawLeadListResponse);
        try {
            JSONArray leadJsonObj = new JSONArray(rawLeadListResponse);

            for (int i = 0; i < leadJsonObj.length(); i++) {
                employeeLeaveDAO = new EmployeeLeaveDAO();
                JSONObject json_data = leadJsonObj.getJSONObject(i);
                employeeLeaveDAO.setId(json_data.getString("id"));
                employeeLeaveDAO.setEmp_id(json_data.getString("emp_id"));
                employeeLeaveDAO.setApproval_status(json_data.getString("approval_status"));
                employeeLeaveDAO.setLeave_use_status(json_data.getString("leave_use_status"));
                employeeLeaveDAO.setLeave_date(json_data.getString("leave_date"));
                employeeLeaveDAO.setLeave_remarks(json_data.getString("leave_remarks"));
                employeeLeaveDAO.setDay(json_data.getString("day"));
                leaveDAOArrayList.add(employeeLeaveDAO);
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return leaveDAOArrayList;
    }

}
