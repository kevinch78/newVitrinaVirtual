package com.vitrina.vitrinaVirtual.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.vitrina.vitrinaVirtual.domain.dto.SavedOutfitDto;
import com.vitrina.vitrinaVirtual.domain.service.SavedOutfitService;
import com.vitrina.vitrinaVirtual.infraestructura.crud_interface.ClienteCrudRepository;
import com.vitrina.vitrinaVirtual.infraestructura.crud_interface.UsuarioCrudRepository;
import com.vitrina.vitrinaVirtual.infraestructura.entity.Cliente;
import com.vitrina.vitrinaVirtual.infraestructura.entity.Usuario;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/outfits")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class SavedOutfitController {

    private final SavedOutfitService outfitService;
    private final UsuarioCrudRepository usuarioCrudRepository;
    private final ClienteCrudRepository clienteCrudRepository;

    /**
     * 💾 Guardar un nuevo outfit
     * POST /api/outfits
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_CLIENT', 'ROLE_ADMIN')")
    public ResponseEntity<?> saveOutfit(
            @RequestBody SavedOutfitDto outfitDto,
            Authentication authentication) {
        try {
            log.info("📥 Request para guardar outfit de: {}", authentication.getName());
            
            // Obtener clientId del usuario autenticado
            Long clientId = getClientIdFromAuth(authentication);
            
            // Guardar outfit
            SavedOutfitDto savedOutfit = outfitService.saveOutfit(outfitDto, clientId);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(savedOutfit);
        } catch (Exception e) {
            log.error("❌ Error al guardar outfit: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al guardar el outfit: " + e.getMessage());
        }
    }

    /**
     * 📦 Obtener todos los outfits del usuario
     * GET /api/outfits
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_CLIENT', 'ROLE_ADMIN')")
    public ResponseEntity<?> getUserOutfits(Authentication authentication) {
        try {
            log.info("📥 Request para obtener outfits de: {}", authentication.getName());
            
            Long clientId = getClientIdFromAuth(authentication);
            
            List<SavedOutfitDto> outfits = outfitService.getUserOutfits(clientId);
            
            return ResponseEntity.ok(outfits);
        } catch (Exception e) {
            log.error("❌ Error al obtener outfits: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener outfits: " + e.getMessage());
        }
    }

    /**
     * 🔍 Obtener un outfit específico por ID
     * GET /api/outfits/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_CLIENT', 'ROLE_ADMIN')")
    public ResponseEntity<?> getOutfitById(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            log.info("📥 Request para obtener outfit {} de: {}", id, authentication.getName());
            
            Long clientId = getClientIdFromAuth(authentication);
            
            SavedOutfitDto outfit = outfitService.getOutfitById(id, clientId);
            
            return ResponseEntity.ok(outfit);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("❌ Error al obtener outfit: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener outfit: " + e.getMessage());
        }
    }

    /**
     * 📝 Actualizar un outfit
     * PUT /api/outfits/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_CLIENT', 'ROLE_ADMIN')")
    public ResponseEntity<?> updateOutfit(
            @PathVariable Long id,
            @RequestBody SavedOutfitDto outfitDto,
            Authentication authentication) {
        try {
            log.info("📥 Request para actualizar outfit {} de: {}", id, authentication.getName());
            
            Long clientId = getClientIdFromAuth(authentication);
            
            SavedOutfitDto updatedOutfit = outfitService.updateOutfit(id, outfitDto, clientId);
            
            return ResponseEntity.ok(updatedOutfit);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("❌ Error al actualizar outfit: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar outfit: " + e.getMessage());
        }
    }

    /**
     * 🗑️ Eliminar un outfit
     * DELETE /api/outfits/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_CLIENT', 'ROLE_ADMIN')")
    public ResponseEntity<?> deleteOutfit(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            log.info("📥 Request para eliminar outfit {} de: {}", id, authentication.getName());
            
            Long clientId = getClientIdFromAuth(authentication);
            
            outfitService.deleteOutfit(id, clientId);
            
            return ResponseEntity.ok().body("Outfit eliminado correctamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("❌ Error al eliminar outfit: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar outfit: " + e.getMessage());
        }
    }

    // ==================== HELPER METHODS ====================

    /**
     * Obtiene el clientId del usuario autenticado
     */
    private Long getClientIdFromAuth(Authentication authentication) {
        String email = authentication.getName();
        
        Usuario usuario = usuarioCrudRepository.findByCorreo(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        
        Cliente cliente = clienteCrudRepository.findByUsuario_IdUsuario(usuario.getIdUsuario())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Este usuario no tiene un perfil de cliente"));
        
        return cliente.getId();
    }
}