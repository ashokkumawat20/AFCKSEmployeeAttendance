package in.afckstechnologies.employeeattendance.adapter;


import android.content.Context;

import android.content.SharedPreferences;

import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;


import java.util.List;


import in.afckstechnologies.employeeattendance.R;
import in.afckstechnologies.employeeattendance.models.EmployeeAttendanceDAO;


public class EmployeeAttendanceListAdpter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    List<EmployeeAttendanceDAO> data;
    EmployeeAttendanceDAO current;


    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;


    // create constructor to innitilize context and data sent from MainActivity
    public EmployeeAttendanceListAdpter(Context context, List<EmployeeAttendanceDAO> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        preferences = context.getSharedPreferences("Prefrence", Context.MODE_PRIVATE);
        prefEditor = preferences.edit();
    }

    // Inflate the layout when viewholder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_employee_attendance_details, parent, false);
        EmployeeAttendanceListAdpter.MyHolder holder = new EmployeeAttendanceListAdpter.MyHolder(view);
        return holder;
    }

    // Bind data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final int pos = position;
        // Get current position of item in recyclerview to bind data and assign values from list
        final EmployeeAttendanceListAdpter.MyHolder myHolder = (EmployeeAttendanceListAdpter.MyHolder) holder;
        current = data.get(position);

        myHolder.view_Address.setText(current.getAddress());
        myHolder.view_Address.setTag(position);

    }


    // return total item from List
    @Override
    public int getItemCount() {
        return data.size();
    }


    class MyHolder extends RecyclerView.ViewHolder {

        TextView view_Address;


        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            view_Address = (TextView) itemView.findViewById(R.id.view_Address);

        }

    }


}
