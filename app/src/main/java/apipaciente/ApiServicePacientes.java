package apipaciente;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiServicePacientes {
    @GET("api/pacientes")
    Call<List<Paciente>> getPacientes();

    @POST("api/pacientes")
    Call<ApiResponse> storePaciente(@Body Paciente paciente);

    @DELETE("api/pacientes/{id}")
    Call<ApiResponse> eliminarPaciente(@Path("id") int id);

    @GET("api/pacientes/{id}")
    Call<Paciente> getPaciente(@Path("id") int id);

    @PUT("api/pacientes/{id}")
    Call<ApiResponse> updatePaciente(@Path("id") int id, @Body Paciente paciente );
}
