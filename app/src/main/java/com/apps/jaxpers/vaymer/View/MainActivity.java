package com.apps.jaxpers.vaymer.View;




import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.apps.jaxpers.vaymer.Apdapters.VehicleAdapter;
import com.apps.jaxpers.vaymer.Data.DataVehiclesUser;

import com.apps.jaxpers.vaymer.Model.RestricionesCiudades;
import com.apps.jaxpers.vaymer.Model.ciudades;
import com.apps.jaxpers.vaymer.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity  {

    private static final String TAG = "Ma";
    private static final String PARTICULARES = "_id_R_001";
    private static final String PUBLICOS = "_id_R_002";
    private String ciudadItemn ;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private DataVehiclesUser dbHelper;
    private VehicleAdapter vehicleAdapter;
    private DatabaseReference databaseReference;
    private List<String> lciudades;
    private ArrayAdapter<String> adapterCiudades;
    private ciudades ciudades;
    @BindView(R.id.ciudades)
    Spinner spinnerCiudades;
    @BindView(R.id.digitos_particulares)
    TextView digitoParticulares;
    @BindView(R.id.digito_publicos)
    TextView digitoPublico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_vehicles);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(),LinearLayoutManager.HORIZONTAL));
        mLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        lciudades =  new ArrayList<>();
        adapterCiudades = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,lciudades);
        spinnerCiudades.setAdapter(adapterCiudades);
        selectSpinner();
        getCiudades();
        dataVehicles();


        Button opennewdialog = (Button)findViewById(R.id.add_new_vehicle);
        opennewdialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),NewVehicle.class);
                startActivity(intent);

            }
        });

    }
    public void getCiudades(){
        databaseReference.child("ciudades").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lciudades.clear();
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    ciudades  = dataSnapshot1.getValue(ciudades.class);
                    lciudades.add(ciudades.getNombre());
                }
                adapterCiudades.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }
    public void getIdCiudad(String ciudad)
    {

        databaseReference = FirebaseDatabase.getInstance().getReference("ciudades");
        databaseReference.orderByChild("nombre").equalTo(ciudad).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ciudades  = dataSnapshot.getValue(ciudades.class);
                getDigitosPublicos(ciudades.getId());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getDigitosPublicos(String id_ciudad){

        id_ciudad = id_ciudad+PUBLICOS+"p";
        Calendar calendar = Calendar.getInstance();
        final String day = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));

        databaseReference = FirebaseDatabase.getInstance().getReference("RestricionCiudades");
        databaseReference.orderByChild(id_ciudad).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
               //RestricionesCiudades restricionesCiudades = dataSnapshot.getValue(RestricionesCiudades.class);
                Log.e(TAG, "onChildAdded: "+ dataSnapshot.child("lunes").getValue(String.class));
                digitoParticulares.setText("");
                digitoParticulares.setText(dataSnapshot.child("lunes").getValue(String.class));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void selectSpinner()
    {

        spinnerCiudades.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
               ciudadItemn = adapterView.getItemAtPosition(i).toString();
                getIdCiudad(ciudadItemn);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }












    @Override
    protected void onRestart() {
        super.onRestart();
        dataVehicles();
        Log.e(TAG, "onRestart: " );
    }

    @Override
    protected void onPause() {
        super.onPause();
        dataVehicles();
    }

    @Override
    protected void onResume() {
        super.onResume();
        dataVehicles();
    }

    public void dataVehicles(){
        dbHelper = new DataVehiclesUser(this);
        vehicleAdapter = new VehicleAdapter(dbHelper.vehicleList(),getApplicationContext(),mRecyclerView);
        mRecyclerView.setAdapter(vehicleAdapter);
    }

    @OnClick(R.id.openAlarm)
    public void openTime(){

        DialogFragment dialogFragment = new DialogAlarm();
        dialogFragment.show(getSupportFragmentManager(),"DialogAlarm");

    }

}
