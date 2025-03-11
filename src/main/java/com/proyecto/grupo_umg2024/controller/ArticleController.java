package com.proyecto.grupo_umg2024.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.grupo_umg2024.model.RoleUser;
import com.proyecto.grupo_umg2024.model.entity.Articles;
import com.proyecto.grupo_umg2024.model.entity.BaseResponse;
import com.proyecto.grupo_umg2024.model.entity.User;
import com.proyecto.grupo_umg2024.service.ArticleService;
import com.proyecto.grupo_umg2024.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/proyecto/")
@SuppressWarnings("rawtypes")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService service;
    private final UserService serviceUser;

    @GetMapping("user/articles")
    public List<Articles> getListArticles() {
        return service.getDataList();
    }

    @PostMapping("user/articles/editOrSave/{username}")
    public ResponseEntity<BaseResponse> postArticlesEditSave(@RequestBody Articles entity,
            @PathVariable String username) {
        try {
            User find = serviceUser.getFindUncleUsername(username);
            if (find != null) {
                if (entity.getId() != null) {
                    Articles articles = service.getFindUncle(entity.getId());
                    if (articles == null) {
                        entity.setUser(find);
                        entity.setDatePublish(LocalDateTime.now());
                        entity.setDateUpdate(LocalDateTime.now());
                        service.createOrUpdate(entity);

                        return ResponseEntity.ok(BaseResponse.builder()
                                .code("200")
                                .message("Se creo correctamente")
                                .entity(entity).build());
                    } else {
                        if ((find.getUsername().equals(articles.getUser().getUsername()))
                                || find.getRol().equals(RoleUser.ADMIN.name())) {
                            entity.setUser(articles.getUser());
                            entity.setDatePublish(articles.getDatePublish());
                            entity.setDateUpdate(LocalDateTime.now());

                            service.createOrUpdate(entity);
                            return ResponseEntity.ok(BaseResponse.builder()
                                    .code("200")
                                    .message("Se Actualizo Correctamente")
                                    .entity(entity).build());
                        } else {
                            return ResponseEntity
                                    .ok(BaseResponse.builder().code("400")
                                            .message("Este Usuario no puede Editar este Articulo")
                                            .build());
                        }

                    }
                } else {
                    entity.setUser(find);
                    entity.setDatePublish(LocalDateTime.now());
                    entity.setDateUpdate(LocalDateTime.now());
                    service.createOrUpdate(entity);

                    return ResponseEntity.ok(BaseResponse.builder()
                            .code("200")
                            .message("Se creo correctamente")
                            .entity(entity).build());
                }
            } else {
                return ResponseEntity
                        .ok(BaseResponse.builder().code("400").message("Surgio algo Inesperado, Revise sus datos")
                                .build());
            }

        } catch (Exception e) {
            return ResponseEntity
                    .ok(BaseResponse.builder().code("400").message("Surgio algo Inesperado, Revise sus datos").build());
        }
    }

    @DeleteMapping("user/articles/delete/{username}/{idArticle}")
    public ResponseEntity<BaseResponse> deleteArticle(@PathVariable Long idArticle, @PathVariable String username) {
        try {
            User finduser = serviceUser.getFindUncleUsername(username);
            if (finduser != null) {
                Articles find = service.getFindUncle(idArticle);
                if ((finduser.getUsername().equals(find.getUser().getUsername()))
                        || finduser.getRol().equals(RoleUser.ADMIN.name())) {
                    if (find != null) {
                        service.deleteFind(find);
                        return ResponseEntity.ok(BaseResponse.builder()
                                .code("200")
                                .message("Se elimino correctamente")
                                .entity(find).build());
                    }

                }
                return ResponseEntity.ok(BaseResponse.builder()
                        .code("400")
                        .message("No se puedo eliminar, Usuario no tiene permisos para eliminar este Articulo")
                        .entity(finduser).build());
            } else {
                return ResponseEntity
                        .ok(BaseResponse.builder().code("400").message("Surgio algo Inesperado, no se encontro usuario")
                                .build());
            }

        } catch (Exception e) {
            return ResponseEntity
                    .ok(BaseResponse.builder().code("400").message("Surgio algo Inesperado, Revise sus datos").build());
        }
    }

}
