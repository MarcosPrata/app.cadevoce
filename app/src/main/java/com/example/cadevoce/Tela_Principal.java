package com.example.cadevoce;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import com.android.volley.Response;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Tela_Principal extends AppCompatActivity implements OnMapReadyCallback {
    Context context;
    private GoogleMap mMap;
    LocationManager locationManager;
    ArrayList<Usuario> usuarios = new ArrayList<>();
    String email = "mhpjunior3@gmail.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_principal);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Tela_Principal.this, "Clicou", Toast.LENGTH_SHORT).show();
            }
        });

        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Snackbar sn = Snackbar.make(v, "Parece que você está sem internet...", Snackbar.LENGTH_LONG);
                sn.setAction("Tentar novamente", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(Tela_Principal.this, "teste", Toast.LENGTH_SHORT).show();
                    }
                }).setActionTextColor(getResources().getColor(R.color.colorPrimary));
                sn.show();
                return true;
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            }
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            mMap = googleMap;
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    //mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                    //mMap.moveCamera(CameraUpdateFactory.zoomTo(17));
                    return false;
                }
            });

            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1,
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            Servidor.Sync_data(email, String.valueOf(latitude), String.valueOf(longitude), context, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        JSONArray grupos = response.getJSONArray("grupos");
                                        for (int i = 0; i < grupos.length(); i++) {
                                            JSONObject grupo = grupos.getJSONObject(i);
                                            JSONArray participantes = grupo.getJSONArray(grupo.names().getString(0));
                                            for(int j=0; j<participantes.length();j++){
                                                JSONObject participante = participantes.getJSONObject(j);
                                                String nome = participante.getString("nome");
                                                String email_participante = participante.getString("email");
                                                JSONObject localizacao = participante.getJSONObject("localizacao");
                                                double latitude = localizacao.getDouble("lat");
                                                double longitude = localizacao.getDouble("lon");
                                                Usuario oCaraTaLa = null;
                                                boolean ehTuMan = email_participante.equals(email);
                                                for(int k=0; k<usuarios.size(); k++){
                                                    if(usuarios.get(k).email.equals(email_participante)){
                                                        oCaraTaLa = usuarios.get(k);
                                                    }
                                                }
                                                if(oCaraTaLa!=null){
                                                    oCaraTaLa.marcador.setPosition(new LatLng(latitude, longitude));
                                                    if(ehTuMan){
                                                        oCaraTaLa.marcador.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.location2));
                                                    }
                                                }else{
                                                    Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(nome));
                                                    Usuario usuario = new Usuario(marker,email_participante,nome);
                                                    usuarios.add(usuario);
                                                    if(ehTuMan){
                                                        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.images));
                                                    }
                                                }
                                            }
                                        }
                                    }catch (JSONException e){

                                    }
                                }
                            });
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {

                        }

                        @Override
                        public void onProviderEnabled(String provider) {

                        }

                        @Override
                        public void onProviderDisabled(String provider) {

                        }
                    });
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 0: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }
                return;
            }
        }
    }

    class Usuario{
        Marker marcador;
        String email;
        String nome;

        public Usuario(Marker marcador, String email, String nome){
            this.marcador = marcador;
            this.email = email;
            this.nome = nome;
        }
    }
}
