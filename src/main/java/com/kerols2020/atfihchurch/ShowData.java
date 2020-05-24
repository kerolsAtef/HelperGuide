package com.kerols2020.atfihchurch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ShowData extends AppCompatActivity {
    FirebaseAuth firebaseAuth ;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    Bundle extras;
    int CH_id,LE_id,PE_id;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    TextView age ,birthDay,churchLevel,schoolLevel,father,server,fatherOfService,gender;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_data);

        //bannner

        mAdView=findViewById(R.id.showAdView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // interitstial
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-1669182856365767/2683722440");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        age=(TextView)findViewById(R.id.SAge);
        birthDay=(TextView)findViewById(R.id.SBirthday);
        churchLevel=(TextView)findViewById(R.id.SChurchLevel);
        schoolLevel=(TextView)findViewById(R.id.SSchoolLevel);
        father=(TextView)findViewById(R.id.SFather);
        server=(TextView)findViewById(R.id.SServer);
        fatherOfService=(TextView)findViewById(R.id.SFService);
        gender=(TextView)findViewById(R.id.SGender);
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


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                ShowData("السن   ","age",age);
                ShowData("تاريخ الميلاد  ","date",birthDay);
                ShowData("النوع  ","gender",gender);
                ShowData("الخادم  ","server",server);
                ShowData("أب الأعتراف  ","father",father);
                ShowData("رئيس الخدمة  ","father_of_service",fatherOfService);
                ShowData("المرحلة الدراسية  ","school_level",schoolLevel);
                ShowData("المرحلة الكنسية  ","church_level",churchLevel);
            }
        },2000);
    }

    private void ShowData(final String description,String child, final TextView textView)
    {
       databaseReference.child(child).addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if (dataSnapshot.exists())
                       textView.setText(description+String.valueOf(dataSnapshot.getValue()));
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       }) ;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
        finish();
    }
}
