package com.example.android.timepower;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AnalogClock;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.example.android.timepower.contract.intentContractClass;
import com.example.android.timepower.contract.sharedPrefContractClass;
import com.example.android.timepower.custom.objects.timeTable;
import com.example.android.timepower.custom.objects.timeTableElement;
import com.example.android.timepower.custom.objects.sharedPrefLinker;
import com.tomerrosenfeld.customanalogclockview.CustomAnalogClock;


public class AddElementActivity extends AppCompatActivity {

    EditText mHeader;
    EditText mSubHeader;
    TextView mFromTime,mFromDisplay;
    TextView mToTime,mToDisplay;
    Button mAddButton;
    int mStartTimeInt = 0;
    int mEndTimeInt = 0;
    int mStartTimeHour;
    int mStartTimeMinute;
    int mEndTimeHour;
    int mEndTimeMinute;
    String mCurrentDay;
    Intent mIntent;
    SharedPreferences mPreferences;
    CustomAnalogClock startClock,endClock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_element);
        Calendar mcurrentTime = Calendar.getInstance();
        mStartTimeHour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        mStartTimeMinute = mcurrentTime.get(Calendar.MINUTE);
        mEndTimeHour = mStartTimeHour;
        mEndTimeMinute = mStartTimeMinute;
        mIntent = getIntent();
        mCurrentDay = mIntent.getStringExtra(intentContractClass.editTimeTable_To_AddElement_Day);
        mHeader = (EditText)findViewById(R.id.add_header);
        mSubHeader = (EditText) findViewById(R.id.add_sub_title);
        startClock = (CustomAnalogClock) findViewById(R.id.from_time_display);
        endClock = (CustomAnalogClock) findViewById(R.id.end_time_display);
        mPreferences = getApplicationContext().getSharedPreferences(sharedPrefContractClass.SHARED_PREF_NAME,
                sharedPrefContractClass.SHARED_PREF_MODE_PRIVATE);
        mFromTime = (TextView) findViewById(R.id.add_start_time);
        mFromDisplay = (TextView)findViewById(R.id.start_time_show);
        startClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setStartTime();
            }
        });
        mToTime = (TextView) findViewById(R.id.add_end_time);
        mToDisplay = (TextView)findViewById(R.id.end_time_show);
        endClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEndTime();
            }
        });
        mAddButton = (Button)findViewById(R.id.element_add_button);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validation()) {
                    sharedPrefLinker prefLinker = new sharedPrefLinker();
                    timeTableElement element = new timeTableElement(mHeader.getText().toString(),
                            mSubHeader.getText().toString(),
                            mStartTimeInt, mEndTimeInt);
                    element.setFromTimeType(mFromDisplay.getText().toString().split(" ")[1]);
                    element.setToTimeType(mToDisplay.getText().toString().split(" ")[1]);
                    element.setDay(mCurrentDay);
                    timeTable table = prefLinker.getTimeTable(mPreferences);
                    timeTableElement result = table.addElement(mCurrentDay, element);
                    if (result == null) {
                        table.setUpdatedOnInt(Calendar.getInstance().getTimeInMillis());
                        String dataJSON = prefLinker.getString(table);
                        Log.d("Element : \n", dataJSON);
                        SharedPreferences.Editor editor = mPreferences.edit();
                        editor.putString(sharedPrefContractClass.SHARED_PREF_TIME_TABLE_DATA,
                                dataJSON);
                        editor.commit();
                        if(!table.sync(getApplicationContext()))
                            prefLinker.setLogin(true,mPreferences);
                        //onBackPressed();
                        Toast.makeText(getApplicationContext(), "DONE !", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AddElementActivity.this, EditTimeTable.class));
                        finish();
                    } else
                        Toast.makeText(getApplicationContext(), "Clashed !", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setStartTime() {
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(AddElementActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                Calendar msettedTime = Calendar.getInstance();
                String time = selectedHour + ":" + selectedMinute;
                SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");
                mStartTimeInt = selectedHour*60 + selectedMinute ;
                Date date = null;
                try {
                    date = fmt.parse(time);
                } catch (ParseException e) {

                    e.printStackTrace();
                }
                SimpleDateFormat fmtOut = new SimpleDateFormat("hh:mm aa");
                String formattedTime = fmtOut.format(date);
                msettedTime.set(Calendar.HOUR_OF_DAY,selectedHour);
                msettedTime.set(Calendar.MINUTE,selectedMinute);
                mStartTimeHour = selectedHour;
                mStartTimeMinute = selectedMinute;
                startClock.setTime(msettedTime);
                mFromDisplay.setText(formattedTime);
            }
        }, mStartTimeHour, mStartTimeMinute, false);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    private void setEndTime() {
        Calendar mcurrentTime = Calendar.getInstance();

        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(AddElementActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                Calendar msettedTime = Calendar.getInstance();
                String time = selectedHour + ":" + selectedMinute;
                SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");
                mEndTimeInt = selectedHour*60 + selectedMinute;
                Date date = null;
                try {
                    date = fmt.parse(time);
                } catch (ParseException e) {

                    e.printStackTrace();
                }
                SimpleDateFormat fmtOut = new SimpleDateFormat("hh:mm aa");
                String formattedTime = fmtOut.format(date);
                msettedTime.set(Calendar.HOUR_OF_DAY,selectedHour);
                msettedTime.set(Calendar.MINUTE,selectedMinute);
                mEndTimeHour = selectedHour;
                mEndTimeMinute = selectedMinute;
                endClock.setTime(msettedTime);
                mToDisplay.setText(formattedTime);
            }
        }, mEndTimeHour, mEndTimeMinute, false);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();

    }

    public boolean validation(){
        if(TextUtils.isEmpty(mHeader.getText().toString())){
            mHeader.setError("Please Enter some Heading");
            mHeader.requestFocus();
            return false;
        }
        if(mStartTimeInt==0){
            Toast.makeText(getBaseContext(), "Enter Starting Time.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(mEndTimeInt==0){
            Toast.makeText(getBaseContext(), "Enter Ending Time.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(mStartTimeInt>=mEndTimeInt){
            Toast.makeText(getBaseContext(), "Start time should be before end time .", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(AddElementActivity.this,EditTimeTable.class));
        finish();
        super.onBackPressed();
    }
}
