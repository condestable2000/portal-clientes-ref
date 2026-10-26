package com.imagina.demo;

import com.imagina.demo.model.Cliente;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ClienteControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void crearYObtenerCliente() {
        Cliente nuevo = new Cliente("Ana Garcia", "ana@example.com");

        ResponseEntity<Cliente> creado = restTemplate.postForEntity("/clientes", nuevo, Cliente.class);
        assertThat(creado.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(creado.getBody()).isNotNull();
        assertThat(creado.getBody().getId()).isNotNull();

        ResponseEntity<Cliente> obtenido = restTemplate.getForEntity(
                "/clientes/" + creado.getBody().getId(), Cliente.class);
        assertThat(obtenido.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(obtenido.getBody().getNombre()).isEqualTo("Ana Garcia");
    }

    @ParameterizedTest
    @ValueSource(strings = {"sin-arroba.com", "ana@example.com ", " ana@example.com", "ana @example.com", "ana@sinpunto", "@example.com", ""})
    void crearClienteConEmailInvalidoDevuelve400YNoGuarda(String emailInvalido) {
        int antes = restTemplate.getForEntity("/clientes", Cliente[].class).getBody().length;

        ResponseEntity<Map> respuesta = restTemplate.postForEntity(
                "/clientes", new Cliente("Pepe Lopez", emailInvalido), Map.class);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(respuesta.getBody()).containsOnlyKeys("campo", "mensaje");
        assertThat(respuesta.getBody().get("campo")).isEqualTo("email");
        assertThat((String) respuesta.getBody().get("mensaje")).isNotBlank();

        int despues = restTemplate.getForEntity("/clientes", Cliente[].class).getBody().length;
        assertThat(despues).isEqualTo(antes);
    }

    @Test
    void crearClienteSinEmailDevuelve400() {
        ResponseEntity<Map> respuesta = restTemplate.postForEntity(
                "/clientes", new Cliente("Pepe Lopez", null), Map.class);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(respuesta.getBody().get("campo")).isEqualTo("email");
    }

    @Test
    void listarClientesDevuelveOk() {
        ResponseEntity<Cliente[]> respuesta = restTemplate.getForEntity("/clientes", Cliente[].class);
        assertThat(respuesta.getStatusCode().is2xxSuccessful()).isTrue();
    }
}
