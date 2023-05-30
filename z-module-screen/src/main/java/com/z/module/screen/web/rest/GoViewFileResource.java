package com.z.module.screen.web.rest;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;

import com.z.framework.common.web.rest.vm.ResponseData;
import com.z.module.screen.config.ScreenProperties;
import com.z.module.screen.domain.GoViewFileDO;
import com.z.module.screen.repository.GoViewFileRepository;
import com.z.module.screen.web.vo.GoViewFileVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.StrUtil;

@Tag(name = "文件上传")
@RestController
@RequestMapping("/file")
@Slf4j
public class GoViewFileResource {


    private final ScreenProperties screenProperties;

    private final GoViewFileRepository goViewFileRepository;

    public GoViewFileResource(ScreenProperties screenProperties, GoViewFileRepository goViewFileRepository) {
        this.screenProperties = screenProperties;
        this.goViewFileRepository = goViewFileRepository;
    }

    /**
     * 删除文件
     *
     * @param ids
     * @return
     */
    @Operation(description = "删除")
    @DeleteMapping("/remove")
    public ResponseEntity<ResponseData<Boolean>> remove(String ids) {
        final List<Long> collect = Arrays.stream(ids.split(",")).map(s -> Long.parseLong(s)).collect(Collectors.toList());
        goViewFileRepository.deleteAllByIdIn(collect);
        return ResponseData.ok(true);
    }


    @Operation(description = "修改")
    @PutMapping("/update")
    public ResponseEntity<ResponseData<Boolean>> update(Long id, @RequestBody MultipartFile object) throws IllegalStateException, IOException {
        final Optional<GoViewFileDO> byId = goViewFileRepository.findById(id);
        if (byId.isPresent()) {
            final GoViewFileDO goViewFileDO = byId.get();
            String fileUrl =
                    goViewFileDO.getAbsolutePath() + goViewFileDO.getRelativePath() + File.separator + goViewFileDO.getFileName();
            object.transferTo(new File(fileUrl));
            return ResponseData.ok(true);
        } else {
            return ResponseData.fail();
        }
    }

    /**
     * 上传文件
     *
     * @param object 文件流对象
     * @return
     * @throws Exception
     */
    @PostMapping("/upload")
    public ResponseEntity<ResponseData<GoViewFileVO>> upload(@RequestBody MultipartFile object) throws IOException {
        String fileName = object.getOriginalFilename();
        //默认文件格式
        String mediaKey = "";
        Long filesize = object.getSize();
        String suffixName = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
        //文件名字
        String fileSuffixName = mediaKey + suffixName;

        GoViewFileDO viewFileDO = new GoViewFileDO();
        viewFileDO.setFileName(fileSuffixName);
        viewFileDO.setFileSize(Integer.parseInt(String.valueOf(filesize)));
        viewFileDO.setFileSuffix(suffixName);
        String filepath = DateUtil.formatDate(new Date());
        viewFileDO.setRelativePath(filepath);
        String uploadType = screenProperties.getGoView().getUploadType();
        viewFileDO.setVirtualKey(uploadType);
        final String uploadPath = screenProperties.getGoView().getPath().getUpload();
        viewFileDO.setAbsolutePath(uploadPath);
        goViewFileRepository.save(viewFileDO);

        File desc = getAbsoluteFile(uploadPath + File.separator + filepath, fileSuffixName);
        object.transferTo(desc);
        GoViewFileVO goViewFileVO = BeanUtil.copyProperties(viewFileDO, GoViewFileVO.class);
        goViewFileVO.setFileurl(screenProperties.getGoView().getHttpUrl() + viewFileDO.getVirtualKey() + "/" + viewFileDO.getRelativePath() + "/" + viewFileDO.getFileName());
        return ResponseData.ok(goViewFileVO);
    }


    /**
     * Base64字符串转成图片
     *
     * @param base64str
     * @throws IOException
     */
    @PostMapping("/uploadbase64")
    public synchronized ResponseEntity<ResponseData<Object>> uploadBase64(String base64str) throws IOException {
        if (StrUtil.isNotBlank(base64str)) {
            String suffixName = ".png";
            String mediaKey = UUID.randomUUID().toString();
            String fileSuffixName = mediaKey + suffixName;
            GoViewFileDO viewFileDO = new GoViewFileDO();
            viewFileDO.setFileName(fileSuffixName);
            viewFileDO.setFileSuffix(suffixName);
            String filepath = DateUtil.formatDate(new Date());
            viewFileDO.setRelativePath(filepath);
            String uploadType = screenProperties.getGoView().getUploadType();
            viewFileDO.setVirtualKey(uploadType);
            final String uploadPath = screenProperties.getGoView().getPath().getUpload();
            viewFileDO.setAbsolutePath(uploadPath);
            File desc = getAbsoluteFile(uploadPath + File.separator + filepath, fileSuffixName);
            File file = null;
            try {
                file = Base64.decodeToFile(base64str, desc);
            } catch (Exception e) {
                log.error("错误base64： {}", base64str);
                log.error("上传失败", e);
            }
            viewFileDO.setFileSize(Integer.parseInt(String.valueOf(file.length())));
            goViewFileRepository.save(viewFileDO);
            GoViewFileVO goViewFileVO = BeanUtil.copyProperties(viewFileDO, GoViewFileVO.class);
            goViewFileVO.setFileurl(screenProperties.getGoView().getHttpUrl() + viewFileDO.getVirtualKey() + "/" + viewFileDO.getRelativePath() + "/" + viewFileDO.getFileName());
            return ResponseData.ok(goViewFileVO);
        }
        return ResponseData.fail();

    }


    /**
     * 定制方法
     * 根据关键字与相对路径获取文件内容
     *
     * @param key 访问关键字
     * @param relativePath 相对路径+文件名字
     * @return
     */
    @PostMapping("/getFileText")
    public ResponseEntity<ResponseData<String>> getFileText(String key, String relativePath) {
        final String uploadPath = screenProperties.getGoView().getPath().getUpload();
        String fileUrl = uploadPath + relativePath;
        try {
            String text = FileUtil.readUtf8String(fileUrl);
            return ResponseData.ok(text);
        } catch (IORuntimeException e) {
            return ResponseData.fail("没有该文件");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


//    /**
//     * 定制方法
//     * 根据关键字与相对路径获取文件内容
//     *
//     * @param key 访问关键字
//     * @param rpf 相对路径+文件名字
//     * @return
//     * @throws IOException
//     */
//    @PostMapping("/getFileText302")
//    public void getFileText302(String key, String relativePath, HttpServletResponse response) throws IOException {
//        String str = v2Config.getHttpurl() + key + "/" + relativePath;
//        response.sendRedirect(str);
//
//    }
//
//
//    /**
//     * 覆盖上传文件 key与指定路径
//     *
//     * @param object     文件流对象
//     * @param bucketName 桶名
//     * @return
//     * @throws Exception
//     */
//    @PostMapping("/coverupload")
//    public AjaxResult coverupload(@RequestBody MultipartFile object, String key, String relativePath) throws IOException {
//
//        String fileName = object.getOriginalFilename();
//        String suffixName = v2Config.getDefaultFormat();
//        Long filesize = object.getSize();
//        //文件名字
//        String fileSuffixName = "";
//        if (fileName.lastIndexOf(".") != -1) {//有后缀
//            suffixName = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
//            //mediaKey=MD5.create().digestHex(fileName);
//            //mediaKey=SnowflakeIdWorker.getUUID();
//            fileSuffixName = relativePath.substring(relativePath.lastIndexOf("/") + 1, relativePath.length());
//        } else {//无后缀
//            //取得唯一id
//            //mediaKey = MD5.create().digestHex(fileName+suffixName);
//            //mediaKey=SnowflakeIdWorker.getUUID();
//            //fileSuffixName=mediaKey+suffixName;
//        }
//        String virtualKey = key;
//        String absolutePath = v2Config.getXnljmap().get(key).replace("file:", "");
//        GoViewFileDO sysFile = new GoViewFileDO();
//        sysFile.setId(SnowflakeIdWorker.getUUID());
//        sysFile.setFileName(fileSuffixName);
//        sysFile.setFileSize(Integer.parseInt(filesize + ""));
//        sysFile.setFileSuffix(suffixName);
//        sysFile.setCreateTime(DateUtil.formatLocalDateTime(LocalDateTime.now()));
//        String filepath = relativePath.substring(0, relativePath.lastIndexOf("/"));
//        sysFile.setRelativePath(filepath);
//        sysFile.setVirtualKey(virtualKey);
//        sysFile.setAbsolutePath(absolutePath);
//        goViewFileRepository.saveOrUpdate(sysFile);
//        File desc = getAbsoluteFile(absolutePath + filepath, fileSuffixName);
//        object.transferTo(desc);
//        GoViewFileDOVo sysFileVo = BeanUtil.copyProperties(sysFile, GoViewFileDOVo.class);
//        sysFileVo.setFileurl(v2Config.getHttpurl() + sysFile.getVirtualKey() + "/" + sysFile.getRelativePath() + "/" + sysFile.getFileName());
//        return AjaxResult.successData(200, sysFileVo);
//    }
//
//
//    /**
//     * 根据文件id查询文件信息json
//     *
//     * @param id
//     * @return
//     */
//    @GetMapping("/getFileid/{id}")
//    public AjaxResult getFileid(@PathVariable("id") String id) {
//        GoViewFileDO sysFile = goViewFileRepository.getById(id);
//        if (sysFile != null) {
//            GoViewFileDOVo sysFileVo = BeanUtil.copyProperties(sysFile, GoViewFileDOVo.class);
//            sysFileVo.setFileurl(v2Config.getHttpurl() + sysFile.getVirtualKey() + "/" + sysFile.getRelativePath() +
//                    "/" + sysFile.getFileName());
//            return AjaxResult.successData(200, sysFileVo);
//        }
//        return AjaxResult.error("没有该文件");
//
//    }
//
//    /**
//     * 根据文件id 302跳转到绝对地址
//     *
//     * @param id
//     * @param response
//     * @throws IOException
//     */
//    @GetMapping("/getFileid/302/{id}")
//    public void getFileid302(@PathVariable("id") String id, HttpServletResponse response) throws IOException {
//        GoViewFileDO sysFile = goViewFileRepository.getById(id);
//        if (sysFile != null) {
//            String str =
//                    v2Config.getHttpurl() + sysFile.getVirtualKey() + "/" + sysFile.getRelativePath() + "/" + sysFile.getFileName();
//            response.sendRedirect(str);
//        }
//    }


    /**
     * 分页查询
     *
     * @param current
     * @param size
     * @return
     */
    @GetMapping("/list")
    public Object list(long current, long size) {
        final PageRequest page = PageRequest.of(Integer.parseInt(String.valueOf(current)), (int) size);
        final Page<GoViewFileDO> all = goViewFileRepository.findAll(page);
        return ResponseData.ok(all);
    }


    /**
     * 获取map中第一个非空数据key
     *
     * @param <K> Key的类型
     * @param <V> Value的类型
     * @param map 数据源
     * @return 返回的值
     */
    public static <K, V> K getFirstNotNull(Map<K, V> map) {
        K obj = null;
        for (Entry<K, V> entry : map.entrySet()) {
            obj = entry.getKey();
            if (obj != null) {
                break;
            }
        }
        return obj;
    }


    public static File getAbsoluteFile(String uploadDir, String filename) throws IOException {
        File desc = new File(uploadDir + File.separator + filename);

        if (!desc.getParentFile().exists()) {
            desc.getParentFile().mkdirs();
        }
        if (!desc.exists()) {
            desc.createNewFile();
        }
        return desc;
    }


    /**
     * 获取上传文件的md5
     *
     * @param file
     * @return
     * @throws IOException
     */
    public String getMd5(MultipartFile file) {
        try {
            //获取文件的byte信息
            byte[] uploadBytes = file.getBytes();
            // 拿到一个MD5转换器
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digest = md5.digest(uploadBytes);
            //转换为16进制
            return new BigInteger(1, digest).toString(16);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }
}
