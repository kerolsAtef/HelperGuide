package com.kerols2020.atfihchurch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;

public class EditData extends AppCompatActivity {

    EditText name,age,churchLevel,schoolLevel,father,server,fatherOfServers;
    Button saveData;
    RadioGroup radioGroup;
    RadioButton selected ;
    TextView showBirthDay;
    String Date="" , gender="",personName;
    FirebaseAuth firebaseAuth ;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    Bundle extras;
    int CH_id,LE_id,PE_id;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    DatePickerDialog.OnDateSetListener dateSetListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_data);

        //banner

        mAdView=findViewById(R.id.editAdView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // inretitasial
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-1669182856365767/6535531068");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        firebaseAuth=FirebaseAuth.getInstance();
        extras =getIntent().getExtras();
        if (extras!=null)
        {
            CH_id=extras.getInt("church_id");
            LE_id=extras.getInt("level_id");
            PE_id=extras.getInt("person_id");

        }



        databaseReference=firebaseDatabase.getInstance().getReference().
                child(firebaseAuth.getInstance().getUid()).child("church"+(CH_id+1)).child("level"+(LE_id+1)).child("person"+(PE_id+1));

        // Start birth day
        showBirthDay=(TextView)findViewById(R.id.EbirthDay);
        final Calendar calendar =Calendar.getInstance();
        final int year=calendar.get(Calendar.YEAR);
        final int month=calendar.get(Calendar.MONTH);
        final int day=calendar.get(Calendar.DAY_OF_MONTH);
        showBirthDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog =new DatePickerDialog(EditData.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,dateSetListener,year,month,day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });
        dateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {

                month+=1;
                 String date =day+"/"+month+"/"+year ;
                showBirthDay.setText(date);
                Date=date;
            }
        };

     // End birthday show ;

        //start getting data
        name=(EditText)findViewById(R.id.EpersonName);
        age=(EditText)findViewById(R.id.Eage);
        churchLevel=(EditText)findViewById(R.id.ECharchLevel);
        schoolLevel=(EditText)findViewById(R.id.ESchoolLevel);
        father=(EditText)findViewById(R.id.Efather);
        server=(EditText)findViewById(R.id.Eservice);
        fatherOfServers=(EditText)findViewById(R.id.EFservice);

        // End getting data

       // save button
                            saveData=(Button)findViewById(R.id.saveData);
                           saveData.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (mInterstitialAd.isLoaded())
                                    {
                                        mInterstitialAd.show();
                                    }
                                    radioGroup=(RadioGroup)findViewById(R.id.Radiogroup);
                                    int option =radioGroup.getCheckedRadioButtonId();
                                    selected=(RadioButton)findViewById(option);
                                        try {
                                                gender=selected.getText().toString();
                                            }
                                        catch (Exception e)
                                            {

                                            }
                                     if (!name.getText().toString().isEmpty()) {
                                         // here will save above data plus gender and birth day
                                         HashMap k = new HashMap();
                                         k.put("person_name", name.getText().toString());
                                         k.put("age", age.getText().toString());
                                         k.put("church_level", churchLevel.getText().toString());
                                         k.put("school_level", schoolLevel.getText().toString());
                                         k.put("father", father.getText().toString());
                                         k.put("server", server.getText().toString());
                                         k.put("father_of_service", fatherOfServers.getText().toString());
                                         k.put("gender", gender);
                                         k.put("date", Date);
                                         databaseReference.setValue(k).addOnCompleteListener(new OnCompleteListener<Void>() {
                                             @Override
                                             public void onComplete(@NonNull Task<Void> task) {
                                                 if (task.isSuccessful()) {
                                                     Toast.makeText(getApplicationContext(), "تم أضافة العضو بنجاح", Toast.LENGTH_LONG).show();
                                                 } else {
                                                     Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                 }
                                             }
                                         });

                                         Intent intent = new Intent(EditData.this, AddPerson.class);
                                         intent.putExtra("church_id", CH_id);
                                         intent.putExtra("level_id", LE_id);

                                         startActivity(intent);
                                         finish();
                                     }
                                  else
                                         Toast.makeText(EditData.this, "يرجي كتابة أسم الشخص .", Toast.LENGTH_SHORT).show();
                        }
                    });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mInterstitialAd.isLoaded())
        {
            mInterstitialAd.show();
        }
    }
}
