package com.imagina.demo.service;

import com.imagina.demo.exception.EmailInvalidoException;
import com.imagina.demo.model.Cliente;
import com.imagina.demo.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class ClienteService {

    private static final Pattern EMAIL_VALIDO = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    public Cliente crear(Cliente cliente) {
        validarEmail(cliente.getEmail());
        return clienteRepository.save(cliente);
    }

    public void eliminar(Long id) {
        clienteRepository.deleteById(id);
    }

    private void validarEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new EmailInvalidoException("email", "El email es obligatorio");
        }
        if (!EMAIL_VALIDO.matcher(email).matches()) {
            throw new EmailInvalidoException("email",
                    "El email no tiene un formato válido (debe ser tipo usuario@dominio.com, sin espacios)");
        }
    }
}
