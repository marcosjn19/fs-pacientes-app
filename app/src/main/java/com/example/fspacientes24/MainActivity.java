package com.example.fspacientes24;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

import apipaciente.ApiResponse;
import apipaciente.ApiServicePacientes;
import apipaciente.Retrofit;
import apipaciente.Paciente;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {
    VistaPaciente vistaPaciente;
    private Button botonAnadir;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        botonAnadir = findViewById(R.id.bntnuevopaciente);
        botonAnadir.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                anadirProductoPopup(MainActivity.this);
            }
        });
        reloadPacientes();
    }

    private void reloadPacientes ( ) {
        ApiServicePacientes apiService = Retrofit.getRetrofitInstance().create(ApiServicePacientes.class);
        Call<List<Paciente>> call = apiService.getPacientes();
        LinearLayout scrollView = findViewById(R.id.scrollContainer);
        scrollView.removeAllViews();
        call.enqueue(new Callback<List<Paciente>>() {
            @Override
            public void onResponse(Call<List<Paciente>> call, Response<List<Paciente>> response) {
                if ( response.isSuccessful() ) {
                    List<Paciente> pacientesRespuesta = response.body();
                    for (Paciente i:
                            pacientesRespuesta) {
                        vistaPaciente = new VistaPaciente(getBaseContext());

                        vistaPaciente.setDatos(i.getNombre(), i.getApellido(), i.getEdad()+"", i.getSexo());
                        vistaPaciente.eliminar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                eliminarPaciente ( i.id );
                            }
                        });

                        vistaPaciente.editar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                editarPopup(MainActivity.this, i.id);
                            }
                        });
                        scrollView.addView(vistaPaciente);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Paciente>> call, Throwable t) {
                showPopup(MainActivity.this, t.getMessage());            }
        });
    }

    public void anadirProductoPopup ( Context context ) {
        Dialog anadirPopup = new Dialog(context);
        anadirPopup.setContentView(R.layout.layout_nuevopaciente); // XML del popup de productos

        EditText tfNombre = anadirPopup.findViewById(R.id.tfnombre);
        EditText tfApellidos =  anadirPopup.findViewById(R.id.tfapellidos);
        EditText tfEdad =  anadirPopup.findViewById(R.id.tfedad);
        EditText tfSexo =  anadirPopup.findViewById(R.id.tfsexo);

        Button botonAnadir =  anadirPopup.findViewById(R.id.btnanadirpaciente);
        Button btncancelar =  anadirPopup.findViewById(R.id.btncancelar);

        btncancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {anadirPopup.dismiss();}
        });

        botonAnadir.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Validar cada campo con expresiones regulares

                String nombre = tfNombre.getText().toString().trim();
                String apellidos = tfApellidos.getText().toString().trim();
                String edadStr = tfEdad.getText().toString().trim();
                String sexo = tfSexo.getText().toString().trim();

                // Expresiones regulares para cada campo
                String regexNombre = "^[a-zA-Z]+$"; // Solo letras
                String regexApellidos = "^[a-zA-Z]+$"; // Solo letras
                String regexEdad = "^[0-9]{1,2}$"; // Solo números de 1 a 2 dígitos
                String regexSexo = "^(F|M)$"; // Masculino o Femenino

                boolean isValid = true;

                // Validar el campo nombre
                if (!nombre.matches(regexNombre)) {
                    Toast.makeText(context, "Nombre no válido", Toast.LENGTH_SHORT).show();
                    isValid = false;
                }

                // Validar el campo apellidos
                if (!apellidos.matches(regexApellidos)) {
                    Toast.makeText(context, "Apellidos no válidos", Toast.LENGTH_SHORT).show();
                    isValid = false;
                }

                // Validar el campo edad
                if (!edadStr.matches(regexEdad)) {
                    Toast.makeText(context, "Edad no válida", Toast.LENGTH_SHORT).show();
                    isValid = false;
                }

                // Validar el campo sexo
                if (!sexo.matches(regexSexo)) {
                    Toast.makeText(context, "Sexo no válido", Toast.LENGTH_SHORT).show();
                    isValid = false;
                }

                if ( isValid ) {
                    Paciente paciente = new Paciente();
                    paciente.setNombre(nombre); paciente.setApellido(apellidos); paciente.setSexo(sexo); paciente.setEdad(Integer.parseInt(edadStr));
                    storePaciente(paciente);
                    anadirPopup.dismiss();
                }
            }
        });
        anadirPopup.show();
    }

    public void getPaciente(int id, final OnPacienteLoadedListener listener) {
        ApiServicePacientes apiServicePacientes = Retrofit.getRetrofitInstance().create(ApiServicePacientes.class);
        Call<Paciente> call = apiServicePacientes.getPaciente(id);
        call.enqueue(new Callback<Paciente>() {
            @Override
            public void onResponse(Call<Paciente> call, Response<Paciente> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onPacienteLoaded(response.body()); // Pasa el paciente al callback
                } else {
                    listener.onError("Error al cargar paciente");
                }
            }

            @Override
            public void onFailure(Call<Paciente> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        });
    }

    public interface OnPacienteLoadedListener {
        void onPacienteLoaded(Paciente paciente);
        void onError(String errorMessage);
    }

    public void editarPopup(Context context, int id) {
        Dialog anadirPopup = new Dialog(context);
        anadirPopup.setContentView(R.layout.layout_nuevopaciente); // XML del popup de productos

        EditText tfNombre = anadirPopup.findViewById(R.id.tfnombre);
        EditText tfApellidos =  anadirPopup.findViewById(R.id.tfapellidos);
        EditText tfEdad =  anadirPopup.findViewById(R.id.tfedad);
        EditText tfSexo =  anadirPopup.findViewById(R.id.tfsexo);

        // Llamada asíncrona para obtener el paciente
        getPaciente(id, new OnPacienteLoadedListener() {
            @Override
            public void onPacienteLoaded(Paciente paciente) {
                tfNombre.setText(paciente.getNombre());
                tfApellidos.setText(paciente.getApellido());
                tfEdad.setText(String.valueOf(paciente.getEdad()));
                tfSexo.setText(paciente.getSexo());
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(context, "Error al cargar datos del paciente: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        Button botonAnadir = anadirPopup.findViewById(R.id.btnanadirpaciente);
        Button btncancelar = anadirPopup.findViewById(R.id.btncancelar);

        btncancelar.setOnClickListener(view -> anadirPopup.dismiss());

        botonAnadir.setOnClickListener(view -> {
            // Validar cada campo con expresiones regulares

            String nombre = tfNombre.getText().toString().trim();
            String apellidos = tfApellidos.getText().toString().trim();
            String edadStr = tfEdad.getText().toString().trim();
            String sexo = tfSexo.getText().toString().trim();

            // Expresiones regulares para cada campo
            String regexNombre = "^[a-zA-Z]+$"; // Solo letras
            String regexApellidos = "^[a-zA-Z]+$"; // Solo letras
            String regexEdad = "^[0-9]{1,2}$"; // Solo números de 1 a 2 dígitos
            String regexSexo = "^(F|M)$"; // Masculino o Femenino

            boolean isValid = true;

            // Validar el campo nombre
            if (!nombre.matches(regexNombre)) {
                Toast.makeText(context, "Nombre no válido", Toast.LENGTH_SHORT).show();
                isValid = false;
            }

            // Validar el campo apellidos
            if (!apellidos.matches(regexApellidos)) {
                Toast.makeText(context, "Apellidos no válidos", Toast.LENGTH_SHORT).show();
                isValid = false;
            }

            // Validar el campo edad
            if (!edadStr.matches(regexEdad)) {
                Toast.makeText(context, "Edad no válida", Toast.LENGTH_SHORT).show();
                isValid = false;
            }

            // Validar el campo sexo
            if (!sexo.matches(regexSexo)) {
                Toast.makeText(context, "Sexo no válido", Toast.LENGTH_SHORT).show();
                isValid = false;
            }

            if ( isValid ) {
                Paciente paciente = new Paciente();
                paciente.setNombre(nombre); paciente.setApellido(apellidos); paciente.setSexo(sexo); paciente.setEdad(Integer.parseInt(edadStr));
                updatePaciente(id, paciente);
                anadirPopup.dismiss();
            }
        });

        anadirPopup.show();
    }

    public void updatePaciente(int id, Paciente paciente ) {
        ApiServicePacientes apiService = Retrofit.getRetrofitInstance().create(ApiServicePacientes.class);
        Call<ApiResponse> call = apiService.updatePaciente(id, paciente);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                reloadPacientes();
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {

            }
        });
    }

    public void storePaciente ( Paciente paciente ) {
        ApiServicePacientes apiService = Retrofit.getRetrofitInstance().create(ApiServicePacientes.class);
        Call<ApiResponse> call = apiService.storePaciente(paciente);
        call.enqueue(new Callback<ApiResponse>() {

            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                reloadPacientes();
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "ERROR " + t.getMessage(), Toast.LENGTH_SHORT);
            }
        });
    }

    public void eliminarPaciente ( int id ) {
        ApiServicePacientes apiServicePacientes = Retrofit.getRetrofitInstance().create(ApiServicePacientes.class);
        Call<ApiResponse> call = apiServicePacientes.eliminarPaciente(id);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Toast.makeText(MainActivity.this, "Paciente eliminado", Toast.LENGTH_SHORT);
                reloadPacientes();
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {

            }
        });
    }

    public void showPopup(Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Información") // Título del popup
                .setMessage(message)     // Mensaje a mostrar
                .setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss()) // Botón para cerrar
                .show();
    }
}