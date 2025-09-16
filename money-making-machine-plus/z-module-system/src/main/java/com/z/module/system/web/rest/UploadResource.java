package com.z.module.system.web.rest;

import com.z.module.system.domain.Upload;
import com.z.module.system.repository.UploadRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;

@Tag(name = "文件上传API")
@RestController
@RequestMapping("/system")
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class UploadResource {

    private final UploadRepository uploadRepository;

    public UploadResource(UploadRepository roleRepository) {
        this.uploadRepository = roleRepository;
    }

    /**
     * {@code POST  /admin/uploads}  : Creates a new role.
     * <p>
     * Creates a new role if the login and email are not already used, and sends an
     * mail with an activation link.
     * The role needs to be activated on creation.
     *
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new role, or with
     * status {@code 400 (Bad Request)} if the login or email is already in use.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Operation(description = "新增文件上传")
    @PostMapping("/uploads")
    public Upload createUpload(@RequestParam("f")MultipartFile file) throws URISyntaxException, IOException {
        // 获取文件信息转
        Upload upload = new Upload();
        byte[] bytes = file.getBytes();
        upload.setValue(bytes);
        return uploadRepository.save(upload);
    }
}
