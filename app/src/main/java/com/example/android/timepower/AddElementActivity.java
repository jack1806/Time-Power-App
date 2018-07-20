package com.example.android.timepower;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AnalogClock;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.example.android.timepower.contract.intentContractClass;
import com.example.android.timepower.contract.sharedPrefContractClass;
import com.example.android.timepower.custom.adapters.AddSlotRecyclerViewAdapter;
import com.example.android.timepower.custom.objects.addSlot;
import com.example.android.timepower.custom.objects.timeTable;
import com.example.android.timepower.custom.objects.timeTableElement;
import com.example.android.timepower.custom.objects.sharedPrefLinker;
import com.example.android.timepower.interfaceClass.SlotDeleteListener;
import com.google.gson.Gson;
import com.tomerrosenfeld.customanalogclockview.CustomAnalogClock;


public class AddElementActivity extends AppCompatActivity {

    EditText mHeader,mSubHeader;
    TextView mFromTime,mFromDisplay,mToTime,mToDisplay,emptySlot;
    ImageButton backButton;
    int mStartTimeInt = 0,mEndTimeInt = 0,mStartTimeHour,mStartTimeMinute,mEndTimeHour,mEndTimeMinute;
    String mCurrentDay;
    Intent mIntent;
    SharedPreferences mPreferences;
    CustomAnalogClock startClock,endClock;
    FloatingActionButton mAdditem,mDone,mCancel;
    RecyclerView mSlotRecyclerView;
    List<timeTableElement> addSlots = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_element);
        Calendar mcurrentTime = Calendar.getInstance();
        mStartTimeHour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        mStartTimeMinute = mcurrentTime.get(Calendar.MINUTE);
        mEndTimeHour = mStartTimeHour;
        mEndTimeMinute = mStartTimeMinute;
        mStartTimeInt = mStartTimeHour*60 + mStartTimeMinute ;
        mEndTimeInt = mEndTimeHour*60 + mEndTimeMinute;
        mIntent = getIntent();
        mCurrentDay = mIntent.getStringExtra(intentContractClass.editTimeTable_To_AddElement_Day);
        mHeader = (EditText)findViewById(R.id.add_header);
        mSubHeader = (EditText) findViewById(R.id.add_sub_title);
//        backButton = (ImageButton) findViewById(R.id.back_button);
//        backButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(AddElementActivity.this, EditTimeTable.class));
//                finish();
//            }
//        });
        mAdditem = (FloatingActionButton) findViewById(R.id.element_add_button);
        mAdditem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validation_1())
                    addSlotItem();
            }
        });
        mCancel = (FloatingActionButton) findViewById(R.id.element_cancel_button);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AddElementActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AddElementActivity.this, EditTimeTable.class));
                finish();
            }
        });
        mDone = (FloatingActionButton) findViewById(R.id.element_done_button);
        mDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddElementActivity.this, EditTimeTable.class));
                finish();
            }
        });
        mSlotRecyclerView = (RecyclerView) findViewById(R.id.add_slot_recycler_view);
        emptySlot = (TextView) findViewById(R.id.slot_empty);
//        startClock = (CustomAnalogClock) findViewById(R.id.from_time_display);
//        endClock = (CustomAnalogClock) findViewById(R.id.end_time_display);
        mPreferences = getApplicationContext().getSharedPreferences(sharedPrefContractClass.SHARED_PREF_NAME,
                sharedPrefContractClass.SHARED_PREF_MODE_PRIVATE);
//        mFromTime = (TextView) findViewById(R.id.add_start_time); Toast.makeText(AddElementActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
//        mFromDisplay = (TextView)findViewById(R.id.start_time_show);
//        startClock.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                setStartTime();
//            }
//        });
//        mToTime = (TextView) findViewById(R.id.add_end_time);
//        mToDisplay = (TextView)findViewById(R.id.end_time_show);
//        endClock.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                setEndTime();
//            }
//        });
//        mAddButton = (ImageButton)findViewById(R.id.element_add_button);
//        mAddButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(AddElementActivity.this, EditTimeTable.class));
//                finish();
//            }
//        });
//        mAddButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (validation()) {
//                    sharedPrefLinker prefLinker = new sharedPrefLinker();
//                    timeTableElement element = new timeTableElement(mHeader.getText().toString(),
//                            mSubHeader.getText().toString(),
//                            mStartTimeInt, mEndTimeInt);
//                    element.setFromTimeType(mFromDisplay.getText().toString().split(" ")[1]);
//                    element.setToTimeType(mToDisplay.getText().toString().split(" ")[1]);
//                    element.setDay(mCurrentDay);
//                    timeTable table = prefLinker.getTimeTable(mPreferences);
//                    timeTableElement result = table.addElement(mCurrentDay, element);
//                    if (result == null) {
//                        table.setUpdatedOnInt(Calendar.getInstance().getTimeInMillis());
//                        String dataJSON = prefLinker.getString(table);
//                        Log.d("Element : \n", dataJSON);
//                        SharedPreferences.Editor editor = mPreferences.edit();
//                        editor.putString(sharedPrefContractClass.SHARED_PREF_TIME_TABLE_DATA,
//                                dataJSON);
//                        editor.commit();
//                        if(!table.sync(getApplicationContext()))
//                            prefLinker.setLogin(true,mPreferences);
//                        //onBackPressed();
//                        Toast.makeText(getApplicationContext(), "DONE !", Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(AddElementActivity.this, EditTimeTable.class));
//                        finish();
//                    } else
//                        Toast.makeText(getApplicationContext(), "Clashed !", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }

//    private void setStartTime() {
//        TimePickerDialog mTimePicker;
//        mTimePicker = new TimePickerDialog(AddElementActivity.this, new TimePickerDialog.OnTimeSetListener() {
//            @Override
//            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
//                Calendar msettedTime = Calendar.getInstance();
//                String time = selectedHour + ":" + selectedMinute;
//                SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");
//                mStartTimeInt = selectedHour*60 + selectedMinute ;
//                Date date = null;
//                try {
//                    date = fmt.parse(time);
//                } catch (ParseException e) {
//
//                    e.printStackTrace();
//                }
//                SimpleDateFormat fmtOut = new SimpleDateFormat("hh:mm aa");
//                String formattedTime = fmtOut.format(date);
//                msettedTime.set(Calendar.HOUR_OF_DAY,selectedHour);
//                msettedTime.set(Calendar.MINUTE,selectedMinute);
//                mStartTimeHour = selectedHour;
//                mStartTimeMinute = selectedMinute;
//                startClock.setTime(msettedTime);
//                mFromDisplay.setText(formattedTime);
//            }
//        }, mStartTimeHour, mStartTimeMinute, false);//Yes 24 hour time
//        mTimePicker.setTitle("Select Time");
//        mTimePicker.show();
//    }
//
//    private void setEndTime() {
//        Calendar mcurrentTime = Calendar.getInstance();
//
//        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
//        int minute = mcurrentTime.get(Calendar.MINUTE);
//        TimePickerDialog mTimePicker;
//        mTimePicker = new TimePickerDialog(AddElementActivity.this, new TimePickerDialog.OnTimeSetListener() {
//            @Override
//            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
//                Calendar msettedTime = Calendar.getInstance();
//                String time = selectedHour + ":" + selectedMinute;
//                SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");
//                mEndTimeInt = selectedHour*60 + selectedMinute;
//                Date date = null;
//                try {
//                    date = fmt.parse(time);
//                } catch (ParseException e) {
//
//                    e.printStackTrace();
//                }
//                SimpleDateFormat fmtOut = new SimpleDateFormat("hh:mm aa");
//                String formattedTime = fmtOut.format(date);
//                msettedTime.set(Calendar.HOUR_OF_DAY,selectedHour);
//                msettedTime.set(Calendar.MINUTE,selectedMinute);
//                mEndTimeHour = selectedHour;
//                mEndTimeMinute = selectedMinute;
//                endClock.setTime(msettedTime);
//                mToDisplay.setText(formattedTime);
//            }
//        }, mEndTimeHour, mEndTimeMinute, false);//Yes 24 hour time
//        mTimePicker.setTitle("Select Time");
//        mTimePicker.show();
//
//    }
//
//    public boolean validation(){
//        if(TextUtils.isEmpty(mHeader.getText().toString())){
//            mHeader.setError("Please Enter some Heading");
//            mHeader.requestFocus();
//            return false;
//        }
//        if(mStartTimeInt==0){
//            Toast.makeText(getBaseContext(), "Enter Starting Time.", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        if(mEndTimeInt==0){
//            Toast.makeText(getBaseContext(), "Enter Ending Time.", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        if(mStartTimeInt>=mEndTimeInt){
//            Toast.makeText(getBaseContext(), "Start time should be before end time .", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        return true;
//    }

    public void addSlotItem(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final addSlot slot = new addSlot();
        final CharSequence items[] = new CharSequence[]{"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};
        slot.setDay(items[0].toString());
        builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                slot.setDay(items[i].toString());
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("DAY SELECTED", "onClick: "+slot.getDay());
                setStartTime(slot);
            }
        });
        builder.setTitle("Which Day?");
        builder.show();
        Log.d("EOF :", "addSlotItem: reached");
    }

    private void setStartTime(final addSlot slot) {
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
                slot.setFrom(formattedTime);
                slot.setStart(mStartTimeInt);
                setEndTime(slot);
            }
        }, mStartTimeHour, mStartTimeMinute, false);//Yes 24 hour time
        mTimePicker.setTitle("Starting time");
        mTimePicker.setCancelable(false);
        mTimePicker.show();
    }

    private void setEndTime(final addSlot slot) {
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
                slot.setEnd(mEndTimeInt);
                slot.setTo(formattedTime);
                if(validation_2()){
                    finalAdd(slot);
                }
            }
        }, mEndTimeHour, mEndTimeMinute, false);//Yes 24 hour time
        mTimePicker.setTitle("Ending time");
        mTimePicker.show();
    }

    public void finalAdd(addSlot slot) {
        Gson gson = new Gson();
        Log.d("ADDED SLOT", "finalAdd: " + gson.toJson(slot));
        final sharedPrefLinker prefLinker = new sharedPrefLinker();
        timeTableElement element = new timeTableElement(mHeader.getText().toString(),
                mSubHeader.getText().toString(),
                slot.getStart(), slot.getEnd());
        element.setFromTimeType(slot.getFrom().split(" ")[1]);
        element.setToTimeType(slot.getTo().split(" ")[1]);
        element.setDay(slot.getDay());
        final timeTable table = prefLinker.getTimeTable(mPreferences);
        timeTableElement result = table.addElement(slot.getDay().substring(0, 3), element);
        if (result == null) {
            table.setUpdatedOnInt(Calendar.getInstance().getTimeInMillis());
            String dataJSON = prefLinker.getString(table);
            Log.d("Element : \n", dataJSON);
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putString(sharedPrefContractClass.SHARED_PREF_TIME_TABLE_DATA,
                    dataJSON);
            editor.commit();
            if (!table.sync(getApplicationContext()))
                prefLinker.setLogin(true, mPreferences);
            //onBackPressed();
            Toast.makeText(getApplicationContext(), "DONE !", Toast.LENGTH_SHORT).show();
            addSlots.add(element);
            if(addSlots.size()!=0 && emptySlot.getVisibility()!=View.INVISIBLE) {
                mDone.setVisibility(View.VISIBLE);
                mCancel.setVisibility(View.INVISIBLE);
                emptySlot.setVisibility(View.INVISIBLE);
            }
            AddSlotRecyclerViewAdapter addSlotRecyclerViewAdapter =
                    new AddSlotRecyclerViewAdapter(addSlots,this,new SlotDeleteListener() {
                                @Override
                                public void onDelete(timeTableElement timeTableElement, int position) {
                                    if(table.delete(timeTableElement)){
                                        addSlots.remove(position);
                                        if(addSlots.size()==0){
                                            mDone.setVisibility(View.INVISIBLE);
                                            mCancel.setVisibility(View.VISIBLE);
                                            emptySlot.setVisibility(View.VISIBLE);
                                        }
                                        table.setUpdatedOnInt(Calendar.getInstance().getTimeInMillis());
                                        String dataJSON = prefLinker.getString(table);
                                        Log.d("Element : \n", dataJSON);
                                        SharedPreferences.Editor editor = mPreferences.edit();
                                        editor.putString(sharedPrefContractClass.SHARED_PREF_TIME_TABLE_DATA,
                                                dataJSON);
                                        editor.commit();
                                        if (!table.sync(getApplicationContext()))
                                            prefLinker.setLogin(true, mPreferences);
                                        mSlotRecyclerView.setAdapter(
                                                new AddSlotRecyclerViewAdapter(addSlots,getApplicationContext(),this));
                                        mSlotRecyclerView.invalidate();
                                    }
                                }
                            });
            LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                    LinearLayoutManager.VERTICAL,false);
            mSlotRecyclerView.setLayoutManager(layoutManager);
            mSlotRecyclerView.setAdapter(addSlotRecyclerViewAdapter);
        }else{
            Toast.makeText(this, "CLASHED !!", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean validation_1(){
        if(TextUtils.isEmpty(mHeader.getText().toString())){
            mHeader.setError("Please Enter some Heading");
            mHeader.requestFocus();
            return false;
        }
        if(TextUtils.isEmpty(mSubHeader.getText().toString())){
            mSubHeader.setError("Please Enter some Heading");
            mSubHeader.requestFocus();
            return false;
        }
        return true;
    }

    public boolean validation_2(){
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
