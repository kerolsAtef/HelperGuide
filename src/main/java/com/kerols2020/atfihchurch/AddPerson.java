package com.kerols2020.atfihchurch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class AddPerson extends AppCompatActivity {

    FirebaseAuth firebaseAuth ;
    SharedPreferences sharedPreferences;
    TextView LeName;
    BottomNavigationView bottomNavigationView ;
    ListView listView ;
    DatabaseReference databaseReference,databaseReference2;
    FirebaseDatabase firebaseDatabase;
    int index,LE_id,CH_id;
    ArrayList<String> personName=new ArrayList<>();
    ArrayAdapter<String> adapter;
    Bundle extras;
    String name;
    private AdView mAdView ;
    AlertDialog.Builder alert;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_person);
        firebaseAuth=FirebaseAuth.getInstance();
        mAdView=findViewById(R.id.personAdView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        LeName =(TextView)findViewById(R.id.personLevelName);
        extras =getIntent().getExtras();
        if (extras!=null)
        {
            try {
                LeName.setText(extras.getString("levelName"));
                CH_id=extras.getInt("church_id");
                LE_id=extras.getInt("level_id");

            }
            catch (Exception e)
            {

            }

        }
        databaseReference=firebaseDatabase.getInstance().getReference().
                child(firebaseAuth.getInstance().getUid()).child("church"+(CH_id+1)).child("level"+(LE_id+1));


        readFireWriteShared();
        // start ListView
new Handler().postDelayed(new Runnable() {
    @Override
    public void run() {
        listView=(ListView)findViewById(R.id.personList);
        adapter=new ArrayAdapter<String>(getApplicationContext(),R.layout.church_list_row,personName);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, final long id) {
                final int myPosition=position ;
                /*
                send 1-ch_id
                2-le_id
                3-person_id
                 */

                if(listView.getItemAtPosition(myPosition)!="تم حذفه") {
                    alert = new AlertDialog.Builder(AddPerson.this);
                    alert.setTitle("Options");
                    alert.setPositiveButton("تعديل", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(AddPerson.this, EditData.class);
                            intent.putExtra("church_id", CH_id);
                            intent.putExtra("level_id", LE_id);
                            intent.putExtra("person_id", myPosition);
                            startActivity(intent);
                        }
                    });
                    alert.setNegativeButton("عرض", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(AddPerson.this, ShowData.class);
                            intent.putExtra("church_id", CH_id);
                            intent.putExtra("level_id", LE_id);
                            intent.putExtra("person_id", myPosition);
                            startActivity(intent);
                        }
                    });
                    alert.setNeutralButton("حذف", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {


                            alert = new AlertDialog.Builder(AddPerson.this);
                            alert.setTitle("حذف");
                            alert.setMessage("هل تريد تأكيد الحذف ؟ " );
                            alert.setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deletePerson("person" + (myPosition + 1));
                                    adapter.notifyDataSetChanged();
                                    Intent intent=new Intent(AddPerson.this,AddPerson.class);
                                    intent.putExtra("levelName",LeName.getText().toString());
                                    intent.putExtra("church_id",CH_id);
                                    intent.putExtra("level_id",LE_id);
                                    startActivity(intent);
                                    finish();
                                }
                            });

                            alert.setNegativeButton("لا", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            alert.show();
                        }

                    });
                    alert.show();
                }
            }
        });
        final int size =readshared();
        for (index=0 ; index<size ; index++) {

            databaseReference.child("person"+(index+1)).child("person_name")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String value = String.valueOf(dataSnapshot.getValue());
                        personName.add(value);
                        adapter.notifyDataSetChanged();
                    }
                    else
                        personName.add("تم حذفه");
                            adapter.notifyDataSetChanged();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }



        // bottom menu

        bottomNavigationView=(BottomNavigationView)findViewById(R.id.bottomMenuP);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener()
                {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        switch (menuItem.getItemId())
                        {
                            case R.id.logOutMenu:
                                firebaseAuth.signOut();
                                Intent intent=new Intent(AddPerson.this,Login.class);
                                startActivity(intent);
                                finish();
                                break;
                            case R.id.aboutUsMenu:
                                alert = new AlertDialog.Builder(AddPerson.this);
                                alert.setTitle("About Us");
                                alert.setMessage("developed by Eng: kerols atef. \n\n send me on : kerolsatef2020@gmail.com.");
                                alert.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                alert.show();
                                break;
                            case R.id.changePasswordMenu:
                                Toast.makeText(getApplicationContext(),"change password",Toast.LENGTH_LONG).show();
                                break;
                            case R.id.Add:
                                alert=new AlertDialog.Builder(AddPerson.this);
                                alert.setTitle("أضافة عضو جديد ");
                                final EditText church_name =new EditText(AddPerson.this);
                                church_name.setInputType(InputType.TYPE_CLASS_TEXT);
                                alert.setView(church_name);
                                alert.setPositiveButton("أضافه", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                         name=church_name.getText().toString();
                                        if (name.isEmpty())
                                        {
                                            Toast.makeText(getApplicationContext(),"يرجي كتابة أسم العضو الجديد",Toast.LENGTH_LONG).show();
                                        }
                                        else {
                                            HashMap k = new HashMap<>();
                                            k.put("person_name", name);
                                            writeToFirebase(k);
                                            personName.add(name);
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                                alert.setNegativeButton("ألغاء", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        dialog.cancel();
                                    }
                                });
                                alert.show();
                                break;
                        }
                        return true;
                    }

                });


    }
    },2000);

    }

    private void deletePerson(String child)
    {
        databaseReference2=firebaseDatabase.getInstance().getReference().
                child(firebaseAuth.getInstance().getUid()).child("church"+(CH_id+1)).child("level"+(LE_id+1)).child(child);
        databaseReference2.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful())
                {
                    Toast.makeText(getApplicationContext(), "تم الحذف بنجاح", Toast.LENGTH_SHORT).show();


                }
                else
                    Toast.makeText(getApplicationContext(),"what", Toast.LENGTH_SHORT).show();

            }
        });

    }

    public void readFireWriteShared()
    {
        databaseReference.child("personsNum").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    sharedPreferences = getSharedPreferences("church", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("person_name", Integer.parseInt(String.valueOf(dataSnapshot.getValue())));
                    editor.apply();

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private int readshared()
    {

        readFireWriteShared();
                sharedPreferences = getSharedPreferences("church", Context.MODE_PRIVATE);
                return sharedPreferences.getInt("person_name", 0);
}
    private void writeShared(int num)
    {
        databaseReference.child("personsNum").setValue(num);
        readFireWriteShared();
    }
    private void writeToFirebase(HashMap<String,String>hash)
    {
        int size =readshared();
        databaseReference.child("person"+(size+1)).setValue(hash)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful())
                        {
                            Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                        }
                        else
                        {

                            writeShared(readshared()+1);

                        }
                    }
                });

    }

}