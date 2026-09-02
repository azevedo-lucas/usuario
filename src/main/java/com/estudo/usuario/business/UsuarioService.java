package com.estudo.usuario.business;

import com.estudo.usuario.business.converter.UsuarioConverter;
import com.estudo.usuario.business.dto.UsuarioDTO;
import com.estudo.usuario.infrastructure.entity.Usuario;
import com.estudo.usuario.infrastructure.exception.ConflictException;
import com.estudo.usuario.infrastructure.exception.ResourceNotFoundException;
import com.estudo.usuario.infrastructure.repository.UsuarioRepository;
import com.estudo.usuario.infrastructure.security.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UsuarioDTO salvaUsuario(UsuarioDTO usuarioDTO) {
        emailExiste(usuarioDTO.getEmail());
        usuarioDTO.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
        Usuario usuario = usuarioConverter.paraUsuario(usuarioDTO);
        return usuarioConverter.paraUsuarioDTO(usuarioRepository.save(usuario));

        // Lógica para salvar o usuário no banco de dados
        // Aqui você pode converter o DTO para a entidade e chamar o repositório para salvar
        // Retorna o DTO salvo (ou atualizado)
    }

    public void emailExiste(String email) {
        try {
            boolean existe = verificaEmailExistente(email);
            if (existe) {
                throw new ConflictException("Email já cadastrado" + email);
            }
        } catch (ConflictException e) {
            throw new ConflictException("Email já cadastrado" + e.getCause());
        }
    }

    public boolean verificaEmailExistente(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("Email não encontrado" + email));
    }

    @Transactional
    public void deletaUsuarioPorEmail(String email) {
        usuarioRepository.deleteByEmail(email);
    }

    public UsuarioDTO atualizaDadosUsuario(String token, UsuarioDTO dto){
    // Aqui buscamos o email do usuário a partir do token (tirar a obrigatoriedade do email)
    String email = jwtUtil.extrairEmailToken(token.substring(7));

    // Aqui verificamos se a senha do usuário foi alterada, caso tenha sido alterada, vamos criptografar a nova senha
    dto.setSenha(dto.getSenha() != null ? passwordEncoder.encode(dto.getSenha()) : null);

    // Busca os dados do usuario no banco de dados
    Usuario usuarioEntity = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Email não localizado: " + email));

    // Mesclou os dados que recebemos na requisição DTO com os dados de banco de dados, e retornou um novo objeto Usuario
    Usuario usuario = usuarioConverter.updateUsuario(dto, usuarioEntity);

    // SAlvamos os dados do usuário no banco de dados e retornamos o usuarioDTO atualizado
    return usuarioConverter.paraUsuarioDTO(usuarioRepository.save(usuario));


    }

}
