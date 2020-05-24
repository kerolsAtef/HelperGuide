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

public class addLevel extends AppCompatActivity {


    FirebaseAuth firebaseAuth ;
    BottomNavigationView bottomNavigationView ;
    TextView chName;
    ListView listView ;
    SharedPreferences sharedPreferences;
    DatabaseReference databaseReference,databaseReference2;
    FirebaseDatabase firebaseDatabase;
    int CH_id,index;
    String name;
    ArrayAdapter<String>adapter;
    AlertDialog.Builder alert;
    ArrayList<String> levelName=new ArrayList<>();
    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_level);
        mAdView=findViewById(R.id.levelAdView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        chName =(TextView)findViewById(R.id.levelChurchName);
        Bundle extras =getIntent().getExtras();
        if (extras!=null)
        {
           chName.setText(extras.getString("churchName"));
           CH_id=extras.getInt("church_id");
        }
        databaseReference=firebaseDatabase.getInstance().getReference().
                child(firebaseAuth.getInstance().getUid()).child("church"+(CH_id+1));
        firebaseAuth =FirebaseAuth.getInstance();


        readFireWriteShared();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
            // Start ListView
                listView=(ListView)findViewById(R.id.levelList);
                adapter=new ArrayAdapter<String>(getApplicationContext(),R.layout.church_list_row,levelName);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        int myPosition = position;
                        if (listView.getItemAtPosition(myPosition)!="تم حذفه") {
                            Intent intent = new Intent(addLevel.this, AddPerson.class);
                            intent.putExtra("levelName", levelName.get(myPosition));
                            intent.putExtra("church_id", CH_id);
                            intent.putExtra("level_id", myPosition);
                            startActivity(intent);
                        }
                        }
                });
                listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, final long id) {
                        final int myPosition=position;

                       if (listView.getItemAtPosition(myPosition)!="تم حذفه") {
                           alert = new AlertDialog.Builder(addLevel.this);
                           alert.setTitle("حذف");
                           alert.setMessage("هل تريد تأكيد الحذف ؟ ");
                           alert.setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialog, int which) {
                                   deletePerson("level" + (myPosition + 1));
                                   adapter.notifyDataSetChanged();
                                   Intent intent =new Intent(addLevel.this,addLevel.class);
                                   intent.putExtra("churchName",chName.getText().toString());
                                   intent.putExtra("church_id",CH_id);
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
                        return true;
                    }
                });


                final int size =readshared();
                for (index=0 ; index<size ; index++) {

                    databaseReference.child("level"+(index+1)).child("level_name")
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        String value = String.valueOf(dataSnapshot.getValue());
                                        levelName.add(value);
                                        adapter.notifyDataSetChanged();
                                    }
                                    else
                                        levelName.add("تم حذفه");
                                    adapter.notifyDataSetChanged();

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }

                // End ListView


                // Start bottom menu

                bottomNavigationView=(BottomNavigationView)findViewById(R.id.bottomMenuL);

                bottomNavigationView.setOnNavigationItemSelectedListener(
                        new BottomNavigationView.OnNavigationItemSelectedListener()
                        {
                            @Override
                            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                                switch (menuItem.getItemId())
                                {
                                    case R.id.logOutMenu:
                                        firebaseAuth.signOut();
                                        Intent intent=new Intent(addLevel.this,Login.class);
                                        startActivity(intent);
                                        finish();
                                        break;
                                    case R.id.aboutUsMenu:
                                        alert = new AlertDialog.Builder(addLevel.this);
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
                                        alert=new AlertDialog.Builder(addLevel.this);
                                        alert.setTitle("أضافة مرحلة ");
                                        final EditText church_name =new EditText(addLevel.this);
                                        church_name.setInputType(InputType.TYPE_CLASS_TEXT);
                                        alert.setView(church_name);
                                        alert.setPositiveButton("أضافه", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                name=church_name.getText().toString();
                                                if (name.isEmpty())
                                                {
                                                    Toast.makeText(getApplicationContext(),"يرجي كتابة أسم المرحلة",Toast.LENGTH_LONG).show();
                                                }
                                                else
                                                {
                                                    HashMap k=new HashMap<>();
                                                    k.put("level_name",name);
                                                    k.put("personsNum",0);
                                                    writeToFirebase(k);
                                                    levelName.add(name);
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
    private void writeToFirebase(HashMap<String,String>hash)
    {
        int size =readshared();
        databaseReference.child("level"+(size+1)).setValue(hash)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                            writeShared(readshared()+1);
                        }
                });

    }
    private int readshared()
    {
       readFireWriteShared();
               sharedPreferences = getSharedPreferences("church", Context.MODE_PRIVATE);
               return sharedPreferences.getInt("level_name", 0);
}
    public void readFireWriteShared()
    {
        databaseReference.child("levelsNum").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {
                    //Toast.makeText(getApplicationContext(),String.valueOf(dataSnapshot.getValue()),Toast.LENGTH_SHORT).show();
                    sharedPreferences = getSharedPreferences("church", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("level_name", Integer.parseInt(String.valueOf(dataSnapshot.getValue())));
                    editor.apply();
                }

                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void deletePerson(String child)
    {
        databaseReference2=firebaseDatabase.getInstance().getReference().
                child(firebaseAuth.getInstance().getUid()).child("church"+(CH_id+1)).child(child);
        databaseReference2.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful())
                {
                    Toast.makeText(getApplicationContext(), "تم الحذف بنجاح", Toast.LENGTH_SHORT).show();


                }
            }
        });

    }

    private void writeShared(int num)
    {
        databaseReference.child("levelsNum").setValue(num);
        readFireWriteShared();
    }



}
