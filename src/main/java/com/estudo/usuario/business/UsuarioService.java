package com.estudo.usuario.business;

import com.estudo.usuario.business.converter.UsuarioConverter;
import com.estudo.usuario.business.dto.UsuarioDTO;
import com.estudo.usuario.infrastructure.entity.Usuario;
import com.estudo.usuario.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;


    public UsuarioDTO salvaUsuario(UsuarioDTO usuarioDTO) {
        Usuario usuario = usuarioConverter.paraUsuario(usuarioDTO);
        return usuarioConverter.paraUsuarioDTO(usuarioRepository.save(usuario));

        // Lógica para salvar o usuário no banco de dados
        // Aqui você pode converter o DTO para a entidade e chamar o repositório para salvar
         // Retorna o DTO salvo (ou atualizado)
    }
}
