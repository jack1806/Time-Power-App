package com.example.android.timepower;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.android.timepower.contract.intentContractClass;
import com.example.android.timepower.contract.sharedPrefContractClass;
import com.example.android.timepower.custom.objects.sharedPrefLinker;
import com.example.android.timepower.custom.objects.timeTable;
import com.example.android.timepower.custom.objects.timeTableElement;
import com.google.gson.Gson;
import com.tomerrosenfeld.customanalogclockview.CustomAnalogClock;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditElementActivity extends AppCompatActivity {

    EditText mHeader;
    EditText mSubHeader;
    TextView mFromTime,mFromDisplay;
    TextView mToTime,mToDisplay;
    Button mAddButton;
    Button mDeleteButton;
    int mStartTimeInt;
    int mEndTimeInt;
    int mStartTimeHour;
    int mStartTimeMinute;
    int mEndTimeHour;
    int mEndTimeMinute;
    timeTableElement mCurrentElement;
    Intent mIntent;
    SharedPreferences mPreferences;
    CustomAnalogClock startClock,endClock;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_element);
        mIntent = getIntent();
        Gson gson = new Gson();
        mCurrentElement = gson.fromJson(mIntent.getStringExtra(intentContractClass.adapterRecycler_To_EditElement_Data),timeTableElement.class);
        mHeader = (EditText)findViewById(R.id.edit_header);
        mSubHeader = (EditText) findViewById(R.id.edit_sub_title);
        startClock = (CustomAnalogClock) findViewById(R.id.from_time_display);
        endClock = (CustomAnalogClock) findViewById(R.id.end_time_display);
        mPreferences = getApplicationContext().getSharedPreferences(sharedPrefContractClass.SHARED_PREF_NAME,
                sharedPrefContractClass.SHARED_PREF_MODE_PRIVATE);
        mFromTime = (TextView) findViewById(R.id.edit_start_time);
        mFromDisplay = (TextView)findViewById(R.id.start_time_show);
        startClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setStartTime();
            }
        });
        mToTime = (TextView) findViewById(R.id.end_end_time);
        mToDisplay = (TextView)findViewById(R.id.end_time_show);
        endClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEndTime();
            }
        });

        mHeader.setText(mCurrentElement.getHeader());
        mSubHeader.setText(mCurrentElement.getSubHeader());
        Calendar initStartClock = Calendar.getInstance();
        Calendar initEndClock = Calendar.getInstance();
        initStartClock.set(Calendar.HOUR_OF_DAY,mCurrentElement.getStartTime()/60);
        initStartClock.set(Calendar.MINUTE,mCurrentElement.getStartTime()%60);
        initEndClock.set(Calendar.HOUR_OF_DAY,mCurrentElement.getEndTime()/60);
        initEndClock.set(Calendar.MINUTE,mCurrentElement.getEndTime()%60);

        startClock.setTime(initStartClock);
        endClock.setTime(initEndClock);

        mStartTimeHour = mCurrentElement.getStartTime()/60;
        mStartTimeMinute = mCurrentElement.getStartTime()%60;
        mEndTimeHour = mCurrentElement.getEndTime()/60;
        mEndTimeMinute = mCurrentElement.getEndTime()%60;

        SimpleDateFormat timeFmtOut = new SimpleDateFormat("hh:mm aa");
        mFromDisplay.setText(timeFmtOut.format(initStartClock.getTime()));
        mToDisplay.setText(timeFmtOut.format(initEndClock.getTime()));

        mStartTimeInt = mCurrentElement.getStartTime();
        mEndTimeInt = mCurrentElement.getEndTime();

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
                    element.setDay(mCurrentElement.getDay());
                    timeTable table = prefLinker.getTimeTable(mPreferences);
                    if (table.update(mCurrentElement.getDay(), mCurrentElement, element)) {
                        table.setUpdatedOnInt(Calendar.getInstance().getTimeInMillis());
                        Toast.makeText(getApplicationContext(), "DONE !", Toast.LENGTH_SHORT).show();
                        String dataJSON = prefLinker.getString(table);
                        Log.d("Element : \n", dataJSON);
                        SharedPreferences.Editor editor = mPreferences.edit();
                        editor.putString(sharedPrefContractClass.SHARED_PREF_TIME_TABLE_DATA,
                                dataJSON);
                        editor.commit();
                        if(!table.sync(getApplicationContext()))
                            prefLinker.setLogin(true,mPreferences);
                        startActivity(new Intent(getApplicationContext(), EditTimeTable.class));
                        finish();
                    } else
                        Toast.makeText(getApplicationContext(), "Something Went wrong !", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mDeleteButton = (Button)findViewById(R.id.edit_delete_button);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPrefLinker prefLinker = new sharedPrefLinker();
                timeTable table = prefLinker.getTimeTable(mPreferences);
                if(table.delete(mCurrentElement)) {
                    table.setUpdatedOnInt(Calendar.getInstance().getTimeInMillis());
                    Toast.makeText(getApplicationContext(), "DONE !", Toast.LENGTH_SHORT).show();
                    String dataJSON = prefLinker.getString(table);
                    Log.d("Element : \n",dataJSON);
                    SharedPreferences.Editor editor = mPreferences.edit();
                    editor.putString(sharedPrefContractClass.SHARED_PREF_TIME_TABLE_DATA,
                            dataJSON);
                    editor.commit();
                    if(table.sync(getApplicationContext()))
                        prefLinker.setLogin(true,mPreferences);
                    startActivity(new Intent(getApplicationContext(),EditTimeTable.class));
                    finish();
                }
                else
                    Toast.makeText(getApplicationContext(), "Something Went wrong !", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setStartTime() {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(EditElementActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
                startClock.setTime(msettedTime);
                mStartTimeHour = selectedHour;
                mStartTimeMinute = selectedMinute;
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
        mTimePicker = new TimePickerDialog(EditElementActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
                mEndTimeHour = selectedHour;
                mEndTimeMinute = selectedMinute;
                msettedTime.set(Calendar.HOUR_OF_DAY,selectedHour);
                msettedTime.set(Calendar.MINUTE,selectedMinute);
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
        Log.d("jack", "validation: "+mStartTimeInt+"=="+mCurrentElement.getStartTime());
        Log.d("jack", "validation: "+mEndTimeInt+"=="+mCurrentElement.getEndTime());
        Log.d("jack", "validation: "+mHeader.getText().toString()+"=="+mCurrentElement.getHeader());
        Log.d("jack", "validation: "+mSubHeader.getText().toString()+"=="+mCurrentElement.getSubHeader());
        if(mStartTimeInt == mCurrentElement.getStartTime()
                && mEndTimeInt == mCurrentElement.getEndTime()
                && mHeader.getText().toString().equals(mCurrentElement.getHeader())
                && mSubHeader.getText().toString().equals(mCurrentElement.getSubHeader())){
            Toast.makeText(getBaseContext(), "No Changes.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(mStartTimeInt >= mEndTimeInt){
            Toast.makeText(getBaseContext(), "Start time should be before end time .", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),EditTimeTable.class));
        finish();
        super.onBackPressed();
    }
}
