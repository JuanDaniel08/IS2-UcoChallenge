package co.edu.uco.ucochallenge.secondary.adapters.parameterscatalog;

import co.edu.uco.ucochallenge.secondary.ports.ParametersCatalog;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

// Adaptador que se comunica con el microservicio de parámetros (puerto 8080)
// usando HttpClient (sin RestTemplate)
@Component
public class ParametersCatalogAdapter implements ParametersCatalog {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ParametersCatalogAdapter() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    // Clase interna para mapear la respuesta JSON del microservicio
    private static class Parametro {
        private String clave;
        private String valor;

        public String getClave() {
            return clave;
        }

        public void setClave(String clave) {
            this.clave = clave;
        }

        public String getValor() {
            return valor;
        }

        public void setValor(String valor) {
            this.valor = valor;
        }
    }

    @Override
    public String getMessage() {
        String url = "http://localhost:8080/api/parametros/mensaje_bienvenida";

        try {
            // Construir la solicitud HTTP GET
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            // Enviar la solicitud y obtener la respuesta
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // Convertir el JSON a objeto Parametro
                Parametro parametro = objectMapper.readValue(response.body(), Parametro.class);

                if (parametro.getValor() != null) {
                    return "✅ Mensaje recibido desde el catálogo de parámetros: " + parametro.getValor();
                } else {
                    return "⚠️ El parámetro 'mensaje_bienvenida' no tiene valor.";
                }
            } else {
                return "⚠️ El microservicio de parámetros respondió con estado: " + response.statusCode();
            }

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt(); // buena práctica si se interrumpe el hilo
            return "❌ Error al obtener el mensaje desde el microservicio de parámetros: " + e.getMessage();
        }
    }
}
