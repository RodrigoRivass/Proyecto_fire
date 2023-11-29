package com.fire.proyecto_fire;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    //declaro variables
    private EditText txtCodigo, txtNombre, txtDueño, txtDireccion;
    private ListView lista;
    private Spinner spMascota;

    //variable de la conexion de FireStore
    private FirebaseFirestore db;
    //Datos del Spinner
    String[] TiposMascotas = {"Perro","Gato","Pajaro"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // llamamos al metodo que cargara la lista
        CargarListaFirestore();
        //Inicializa Firestore
        db = FirebaseFirestore.getInstance();


        //Uno las variables cons los Id dek XML
        txtCodigo = findViewById(R.id.txtCodigo);
        txtNombre = findViewById(R.id.txtNombre);
        txtDueño = findViewById(R.id.txtDueño);
        txtDireccion = findViewById(R.id.txtDireccion);
        spMascota = findViewById(R.id.spMascota);
        lista = findViewById(R.id.lista);

        //poblar Spinner de los tipos de Mascotas
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, TiposMascotas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMascota.setAdapter(adapter);
    }
    //metodo enviar Datos
    public void enviarDatosFirestore(View view){
        //obtenemos los campos ingresados en el formulario
        String codigo = txtCodigo.getText().toString();
        String nombre = txtNombre.getText().toString();
        String dueño = txtDueño.getText().toString();
        String direccion = txtDireccion.getText().toString();
        String tipoMascota = spMascota.getSelectedItem().toString();

        //creamos un mapa con los datos a enviar
        Map<String, Object> mascota = new HashMap<>();
        mascota.put("codigo", codigo);
        mascota.put("nombre", nombre);
        mascota.put("dueño", dueño);
        mascota.put("direccion", direccion);
        mascota.put("tipoMascota", tipoMascota);

        //Enviamos los dato a Firestore
        db.collection("mascotas")
                .document(codigo)
                .set(mascota)
                .addOnSuccessListener(aVoid->{
                    Toast.makeText(MainActivity.this, "Datos enviados a Firestore correctamente", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e->{
                    Toast.makeText(MainActivity.this, "Error al enviar los datos a Firestore " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    //Boton que carga la Lista
    public void CargarLista(View view){
        CargarListaFirestore();
    }
    //metodo cargar Lista
    public void CargarListaFirestore(){
        //obtenemos la instacia de Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //Hacemos una consulta a la coleccion llamada mascotas
        db.collection("mascotas")
           .get()
           .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
               @Override
               public void onComplete(@NonNull Task<QuerySnapshot> task){
                   if (task.isSuccessful()) {
                       //si la consulta es exitosa, procesara la documentos obtenidos
                       //creando una lista para almacenar las cadenas de informacion de mascotas
                       List<String> listaMascotas = new ArrayList<>();

                       //recorre todos los datos obtenidos ordenandolos en la lista
                       for (QueryDocumentSnapshot document : task.getResult()){
                           String linea = "||" + document.getString("codigo")+ "||" +
                                   document.getString("nombre") + "||" +
                                   document.getString("dueño") + "||" +
                                   document.getString("direccion");
                           listaMascotas.add(linea);
                       }
                       //crear un ArrayAdapter con la lista de mascotas
                       //y establece el adaptador en el listView
                       ArrayAdapter<String> adaptador = new ArrayAdapter<>(MainActivity.this,
                               android.R.layout.simple_list_item_1, listaMascotas);
                       lista.setAdapter(adaptador);
                   } else {
                       //se imprimira en la consola si ahi errores al traer los datos
                       Log.e("TAG", "Erro al obtener datos de Firestore", task.getException());
                   }
               }
           });

    }
}