package com.marketplace.backend.controller;

import com.marketplace.backend.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
public class FileController {

    private final FileStorageService fileStorageService;

    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('ARTISTA', 'ADMIN')")
    public ResponseEntity<Map<String, String>> uploadProductoFile(
            @RequestParam("file") MultipartFile file) {
        
        log.info("Subiendo archivo de producto: {}", file.getOriginalFilename());
        String fileName = fileStorageService.storeFile(file, "productos");
        
        Map<String, String> response = new HashMap<>();
        response.put("fileName", fileName);
        response.put("filePath", "productos/" + fileName);
        response.put("fileUrl", "/api/files/download/productos/" + fileName);
        
        log.info("Archivo guardado: productos/{}", fileName);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/upload/miniatura")
    @PreAuthorize("hasAnyRole('ARTISTA', 'ADMIN')")
    public ResponseEntity<Map<String, String>> uploadMiniatura(
            @RequestParam("file") MultipartFile file) {
        
        log.info("Subiendo miniatura: {}", file.getOriginalFilename());
        String fileName = fileStorageService.storeFile(file, "miniaturas");
        
        Map<String, String> response = new HashMap<>();
        response.put("fileName", fileName);
        response.put("filePath", "miniaturas/" + fileName);
        response.put("fileUrl", "/api/files/download/miniaturas/" + fileName);
        
        log.info("Miniatura guardada: miniaturas/{}", fileName);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/upload/preview")
    @PreAuthorize("hasAnyRole('ARTISTA', 'ADMIN')")
    public ResponseEntity<Map<String, String>> uploadPreview(
            @RequestParam("file") MultipartFile file) {
        
        log.info("Subiendo preview: {}", file.getOriginalFilename());
        String fileName = fileStorageService.storeFile(file, "previews");
        
        Map<String, String> response = new HashMap<>();
        response.put("fileName", fileName);
        response.put("filePath", "previews/" + fileName);
        response.put("fileUrl", "/api/files/download/previews/" + fileName);
        
        log.info("Preview guardado: previews/{}", fileName);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/download/{subfolder}/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable String subfolder,
            @PathVariable String fileName) {
        
        log.info("Solicitando descarga: {}/{}", subfolder, fileName);
        
        try {
            Resource resource = fileStorageService.loadFileAsResource(subfolder + "/" + fileName);
            
            // Determinar el tipo de contenido
            String contentType = determineContentType(fileName);
            
            log.info("Archivo encontrado, enviando con content-type: {}", contentType);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
                    
        } catch (Exception e) {
            log.error("Error al descargar archivo: {}/{}", subfolder, fileName, e);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{subfolder}/{fileName:.+}")
    @PreAuthorize("hasAnyRole('ARTISTA', 'ADMIN')")
    public ResponseEntity<Void> deleteFile(
            @PathVariable String subfolder,
            @PathVariable String fileName) {
        
        log.info("Eliminando archivo: {}/{}", subfolder, fileName);
        fileStorageService.deleteFile(subfolder + "/" + fileName);
        return ResponseEntity.noContent().build();
    }

    private String determineContentType(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        
        return switch (extension) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "webp" -> "image/webp";
            case "mp3" -> "audio/mpeg";
            case "wav" -> "audio/wav";
            case "mp4" -> "video/mp4";
            case "pdf" -> "application/pdf";
            default -> "application/octet-stream";
        };
    }
}