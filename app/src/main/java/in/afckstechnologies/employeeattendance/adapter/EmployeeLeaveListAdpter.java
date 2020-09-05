package in.afckstechnologies.employeeattendance.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import in.afckstechnologies.employeeattendance.R;
import in.afckstechnologies.employeeattendance.models.EmployeeLeaveDAO;
import in.afckstechnologies.employeeattendance.utils.Config;
import in.afckstechnologies.employeeattendance.utils.FeesListener;
import in.afckstechnologies.employeeattendance.utils.WebClient;


public class EmployeeLeaveListAdpter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    List<EmployeeLeaveDAO> data;
    EmployeeLeaveDAO current;
    int currentPos = 0;
    String id1;
    String centerId;
    int ID;

    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;

    ProgressDialog mProgressDialog;
    private JSONObject jsonLeadObj;
    JSONArray jsonArray;
    String deleteLeaveRespone = "";
    boolean status;
    String message = "";
    String msg = "";
    String id = "";

    static FeesListener mListener;

    // create constructor to innitilize context and data sent from MainActivity
    public EmployeeLeaveListAdpter(Context context, List<EmployeeLeaveDAO> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        preferences = context.getSharedPreferences("Prefrence", Context.MODE_PRIVATE);
        prefEditor = preferences.edit();
    }

    // Inflate the layout when viewholder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_employee_leave_details, parent, false);
        EmployeeLeaveListAdpter.MyHolder holder = new EmployeeLeaveListAdpter.MyHolder(view);
        return holder;
    }

    // Bind data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final int pos = position;
        // Get current position of item in recyclerview to bind data and assign values from list
        EmployeeLeaveListAdpter.MyHolder myHolder = (EmployeeLeaveListAdpter.MyHolder) holder;
        current = data.get(position);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        final String date = format.format(cal.getTime());
        myHolder.reasonDate.setText(current.getLeave_date());
        myHolder.reasonDate.setTag(position);
        myHolder.reasonRemarks.setText(current.getLeave_remarks());
        myHolder.reasonRemarks.setTag(position);
        myHolder.deleteLeave.setTag(position);

        if (current.getLeave_use_status().equals("1")) {
            myHolder.deleteLeave.setVisibility(View.VISIBLE);
        }

        if (current.getLeave_use_status().equals("0")) {
            myHolder.deleteLeave.setVisibility(View.GONE);
        }

        myHolder.deleteLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ID = (Integer) v.getTag();
                Log.e("", "list Id" + ID);
                current = data.get(ID);
                id = current.getId();
              //  Toast.makeText(context, current.getId(), Toast.LENGTH_LONG).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Do you want to delete Leave ?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                new deleteLeaveEmployee().execute();

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();
                            }
                        });

                //Creating dialog box
                AlertDialog alert = builder.create();
                //Setting the title manually
                alert.setTitle("Deleting Leave");
                alert.show();
            }
        });

    }


    // return total item from List
    @Override
    public int getItemCount() {
        return data.size();
    }


    class MyHolder extends RecyclerView.ViewHolder {

        TextView reasonDate, reasonRemarks;
        ImageView deleteLeave;


        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            reasonDate = (TextView) itemView.findViewById(R.id.reasonDate);
            reasonRemarks = (TextView) itemView.findViewById(R.id.reasonRemarks);
            deleteLeave = (ImageView) itemView.findViewById(R.id.deleteLeave);

        }

    }


    private class deleteLeaveEmployee extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(context);
            // Set progressdialog title
            mProgressDialog.setTitle("Please Wait...");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            //mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            jsonLeadObj = new JSONObject() {
                {
                    try {
                        put("id", id);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("json exception", "json exception" + e);
                    }
                }
            };
            WebClient serviceAccess = new WebClient();


            Log.i("json", "json" + jsonLeadObj);
            deleteLeaveRespone = serviceAccess.SendHttpPost(Config.URL_DELETEEMPLOYEELEAVE, jsonLeadObj);
            Log.i("resp", "deleteLeaveRespone" + deleteLeaveRespone);


            if (deleteLeaveRespone.compareTo("") != 0) {
                if (isJSONValid(deleteLeaveRespone)) {


                    try {

                        JSONObject jsonObject = new JSONObject(deleteLeaveRespone);
                        status = jsonObject.getBoolean("status");
                        msg = jsonObject.getString("message");
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                } else {


                    Toast.makeText(context, "Please check your webservice", Toast.LENGTH_LONG).show();


                }
            } else {

                Toast.makeText(context, "Please check your network connection.", Toast.LENGTH_LONG).show();

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            if (status) {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();

                mListener.messageReceived(message);
                // Close the progressdialog
                mProgressDialog.dismiss();

            } else {
                // Close the progressdialog
                mProgressDialog.dismiss();

            }
        }
    }

    public static void bindListener(FeesListener listener) {
        mListener = listener;
    }

    //
    protected boolean isJSONValid(String callReoprtResponse2) {
        // TODO Auto-generated method stub
        try {
            new JSONObject(callReoprtResponse2);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(callReoprtResponse2);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }


}
