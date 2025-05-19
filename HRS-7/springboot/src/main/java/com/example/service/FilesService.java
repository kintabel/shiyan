package com.example.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import com.example.common.enums.ResultCodeEnum;
import com.example.entity.Account;
import com.example.entity.DiskFiles;
import com.example.exception.CustomException;
import com.example.mapper.FilesMapper;
import com.example.utils.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

@Slf4j
@Service
public class FilesService {

    @Value("${server.port:9090}")
    private String port;

    @Value("${ip:localhost}")
    private String ip;
    private static final String filePath = System.getProperty("user.dir") + "/disk/";
    @Resource
    private FilesMapper filesMapper;

    public void preview(String fileName, HttpServletResponse response) {
        DiskFiles files = filesMapper.selectByName(fileName);
        if (files == null) {
            throw new CustomException(ResultCodeEnum.FILE_NOT_FOUND);
        }
        ArrayList<String> typeList = CollUtil.newArrayList("jpg", "jpeg", "png", "pdf", "gif");
        if (!typeList.contains(files.getType())) {
            throw new CustomException(ResultCodeEnum.TYPE_NOT_SUPPORT);
        }
        OutputStream os;
        try {
            String file = files.getFile();
            String flag = file.substring(file.lastIndexOf("/"));
            response.addHeader("Content-Disposition", "inline;filename=" + URLEncoder.encode(files.getName(), "UTF-8"));
            byte[] bytes = FileUtil.readBytes(filePath + flag);
            os = response.getOutputStream();
            os.write(bytes);
            os.flush();
            os.close();
        } catch (Exception e) {
            System.out.println("文件下载失败");
        }
    }

    public void add(MultipartFile file, String name) {
        DiskFiles files = new DiskFiles();
        String now = DateUtil.now();
        files.setCreateTime(now);
        Account currentUser = TokenUtils.getCurrentUser();
        files.setUserId(currentUser.getId());
        //对文件的处理
        String originalFilename = file.getOriginalFilename();//获取文件原始名称
        files.setName(originalFilename);

        String extName = FileUtil.extName(originalFilename);//获取文件后缀
        files.setType(extName);

        long flag = System.currentTimeMillis();
        String fileName = flag + "-" + originalFilename; //文件存储在磁盘的文件名
        files.setDiskName(fileName);
        //判断文件路径是否存在
        if (!FileUtil.exist(filePath)) {
            //不存在创建一个文件目录
            FileUtil.mkdir(filePath);
        }

        try {
            byte[] bytes = file.getBytes(); //byte
            double size = BigDecimal.valueOf(bytes.length).divide(BigDecimal.valueOf(1024), 3, RoundingMode.HALF_UP).doubleValue(); //四舍五入精确计算大小
            files.setSize(size);
            //文件上传
            file.transferTo(new File(filePath + fileName));
        } catch (Exception e) {
            log.error("文件上传错误", e);
        }
        String url = "http://" + ip + ":" + port + "/diskFiles/download/" + fileName;   //  "/diskFiles/download/"为文件下载接口

        files.setFile(url);
        System.out.println(files);
        filesMapper.insert(files);
    }


}
