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
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import java.util.ArrayList;
import java.util.HashMap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
public class MainActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth ;
    SharedPreferences sharedPreferences;
    FirebaseUser firebaseUser ;
    BottomNavigationView bottomNavigationView ;
    DatabaseReference databaseReference,databaseReference2;
    FirebaseDatabase firebaseDatabase;
    int index;
    ArrayList <String>CHName=new ArrayList<>();
    ArrayAdapter<String>adapter;
    ListView listV ;
    AlertDialog.Builder alert;
    String name;
    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkInternet();
        firebaseAuth=FirebaseAuth.getInstance();
        mAdView=findViewById(R.id.mainAdView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        if (firebaseAuth.getCurrentUser() != null) {
            databaseReference = firebaseDatabase.getInstance().getReference().child(firebaseAuth.getUid());

            readFireWriteShared();
            // start ListView
            listV = (ListView) findViewById(R.id.churchList);
            adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.church_list_row, CHName);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    listV.setAdapter(adapter);
                    listV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            int myPosition = position;
                            if (listV.getItemAtPosition(myPosition) != "تم حذفه") {
                                Intent intent = new Intent(MainActivity.this, addLevel.class);
                                intent.putExtra("churchName", CHName.get(myPosition));
                                intent.putExtra("church_id", myPosition);
                                startActivity(intent);
                            }
                        }
                    });

                    listV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                            final int myPosition = position;
                            // Toast.makeText(getApplicationContext(),String.valueOf(indexes.get(0)),Toast.LENGTH_SHORT).show();
                            if (listV.getItemAtPosition(myPosition) != "تم حذفه") {
                                alert = new AlertDialog.Builder(MainActivity.this);
                                alert.setTitle("حذف");
                                alert.setMessage("هل تريد تأكيد الحذف ؟ ");
                                alert.setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        deletePerson("church" + (myPosition + 1));
                                        adapter.notifyDataSetChanged();
                                        startActivity(new Intent(MainActivity.this, MainActivity.class));
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

                    final int size = readshared();
                    for (index = 0; index < size; index++) {

                        databaseReference.child("church" + (index + 1)).child("church_name").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    String value = String.valueOf(dataSnapshot.getValue());
                                    CHName.add(value);
                                    adapter.notifyDataSetChanged();
                                } else {
                                    CHName.add("تم حذفه");
                                    adapter.notifyDataSetChanged();
                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }


// End ListView

// Bottom navigation menu

                    bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomMenu);

                    bottomNavigationView.setOnNavigationItemSelectedListener(
                            new BottomNavigationView.OnNavigationItemSelectedListener() {
                                @Override
                                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                                    switch (menuItem.getItemId()) {
                                        case R.id.logOutMenu:
                                            firebaseAuth.signOut();
                                            Intent intent = new Intent(MainActivity.this, Login.class);
                                            startActivity(intent);
                                            finish();
                                            break;
                                        case R.id.aboutUsMenu:
                                            alert = new AlertDialog.Builder(MainActivity.this);
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
                                            alert = new AlertDialog.Builder(MainActivity.this);
                                            alert.setTitle("تغيير الرقم السري ");
                                            final EditText pass  = new EditText(MainActivity.this);
                                            pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                            pass.setHint("أدخل الرقم السري الجديد");
                                            alert.setView(pass);
                                            alert.setPositiveButton("تغيير", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                               String passWord=pass.getText().toString();
                                                   databaseReference.child("PassWord")
                                                   .setValue(passWord).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                       @Override
                                                       public void onComplete(@NonNull Task<Void> task) {
                                                       if (task.isSuccessful())
                                                           Toast.makeText(getApplicationContext(), "تم تغيير الرقم السري", Toast.LENGTH_SHORT).show();
                                                       else
                                                           Toast.makeText(MainActivity.this, "حدثت مشكلة يرجي الحاولة لاحقا", Toast.LENGTH_SHORT).show();
                                                       }

                                                   });


                                                }
                                            });
                                            alert.show();
                                            break;
                                        case R.id.Add:
                                            alert = new AlertDialog.Builder(MainActivity.this);
                                            alert.setTitle("أضافة كنيسة ");
                                            final EditText church_name = new EditText(MainActivity.this);
                                            church_name.setInputType(InputType.TYPE_CLASS_TEXT);
                                            alert.setView(church_name);
                                            alert.setPositiveButton("أضافه", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    name = church_name.getText().toString();
                                                    if (name.isEmpty()) {
                                                        Toast.makeText(getApplicationContext(), "يرجي كتابة أسم الكنيسة", Toast.LENGTH_LONG).show();
                                                    } else {
                                                        HashMap k = new HashMap<>();
                                                        k.put("church_name", name);
                                                        k.put("levelsNum", 0);
                                                        writeToFirebase(k);
                                                        CHName.add(name);
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
// End ListView

                }
            }, 2000);
        }
        else {
            start();
        }
    }

    private void start() {
        startActivity(new Intent(MainActivity.this, Login.class));
        finish();
    }

    private void writeToFirebase(HashMap<String,String>hash)
   {
       int size =readshared();
       databaseReference.child("church"+(size+1)).setValue(hash)
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
                                           adapter.notifyDataSetChanged();
                                       }
                               }
                           });

    //addChurchName();

   }

   private int readshared()
   {
       readFireWriteShared();
       sharedPreferences = getSharedPreferences("church", Context.MODE_PRIVATE);
       return sharedPreferences.getInt("church_name", 0);

   }
   public void readFireWriteShared()
   {
       databaseReference.child("churchesNum").addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

               sharedPreferences = getSharedPreferences("church", Context.MODE_PRIVATE);
               SharedPreferences.Editor editor = sharedPreferences.edit();
               editor.putInt("church_name",Integer.parseInt(String.valueOf(dataSnapshot.getValue())));
               editor.apply();
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });

   }

   private void writeShared(int num)
   {
       databaseReference.child("churchesNum").setValue(num);
      readFireWriteShared();
   }
    private void deletePerson(String child)
    {
        databaseReference2=firebaseDatabase.getInstance().getReference().
                child(firebaseAuth.getInstance().getUid()).child(child);
        databaseReference2.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful())
                {
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(), "تم الحذف بنجاح", Toast.LENGTH_SHORT).show();
                  //  writeShared(readshared()-1);

                }
                else
                    Toast.makeText(getApplicationContext(),"حدثت مشكلة أثناء الحذف !", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void checkInternet()
    {
        if (isNetworkAvailable())
        {

        }
        else
        {
            alert = new AlertDialog.Builder(MainActivity.this);
            alert.setTitle("تنبيه هام");
            alert.setMessage("تعذر الأتصال بالأنترنت");
            alert.setCancelable(false);
            alert.setNegativeButton("خروج", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    System.exit(0);

                }
            });
            alert.setPositiveButton("اعاده المحاولة", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    checkInternet();
                }
            });
            AlertDialog alertDialog=alert.create();
            alertDialog.show();

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
                   finish();
                System.exit(0);
                android.os.Process.killProcess(android.os.Process.myPid());

    }
}
