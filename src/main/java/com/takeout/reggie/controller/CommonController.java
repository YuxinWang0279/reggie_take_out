package com.takeout.reggie.controller;

import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerOutput;
import com.takeout.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.path}")
    private String BasePath;

    /**
     * 文件上传
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) throws IOException {
        log.info("接收到上传文件,{}",file.getName());
        //文件为临时文件，存储在server中，如果不进行转存，在程序结束后文件将会被删除

        //1. 利用UUID对文件重新命名
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf('.'));
        String filename = UUID.randomUUID().toString() + suffix;
        //2. 转存文件
        // check BasePath whether exists
        File dirs = new File(BasePath);
        if(!dirs.exists()) dirs.mkdirs();

        file.transferTo(new File(BasePath + filename));
        return R.success(filename);
    }

    /**
     * 文件下载，与上传不同，下载主要利用输入流和输出流来实现
     * @param name
     * @param response
     * @return
     */
    @GetMapping("/download")
    public R<String> download(String name, HttpServletResponse response) throws IOException {
        //1. 获取文件输入流
        FileInputStream fileInputStream = new FileInputStream(BasePath+name);
        //2. 获取输出流
        // 设置输出流类型
        response.setContentType("image/jpeg");
        ServletOutputStream outputStream = response.getOutputStream();
        //3. 从输入流读取并写入输出流
        byte[] bytes = new byte[1024];
        int len=0;
        while((len = fileInputStream.read(bytes))!=-1){
            outputStream.write(bytes,0,len);
            outputStream.flush();
        }
        return R.success("下载成功");
    }
}
