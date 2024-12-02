package com.example.fspacientes24;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class VistaPaciente extends LinearLayout {

    private TextView nombre;
    private TextView apellidos;
    private TextView edad;
    private TextView sexo;

    public Button eliminar;
    public Button editar;

    public VistaPaciente(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.layout_paciente, this, true);

        this.nombre = findViewById(R.id.nombre);
        this.apellidos = findViewById(R.id.apellidos);
        this.edad = findViewById(R.id.edad);
        this.sexo = findViewById(R.id.sexo);
        this.eliminar = findViewById(R.id.btneliminar);
        this.editar = findViewById(R.id.btneditar);
    }

    // Método para asignar los valores de los datos
    public void setDatos(String nombre, String apellidos, String edad, String sexo) {
        this.nombre.setText(nombre);
        this.apellidos.setText(apellidos);
        this.edad.setText(edad);
        this.sexo.setText(sexo);
    }
}
