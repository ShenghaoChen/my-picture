package com.csh.mypicturebackend.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csh.mypicturebackend.annotation.AuthCheck;
import com.csh.mypicturebackend.common.BaseResponse;
import com.csh.mypicturebackend.common.DeleteRequest;
import com.csh.mypicturebackend.common.ResultUtils;
import com.csh.mypicturebackend.constant.UserConstant;
import com.csh.mypicturebackend.exception.BusinessException;
import com.csh.mypicturebackend.exception.ErrorCode;
import com.csh.mypicturebackend.exception.ThrowUtils;
import com.csh.mypicturebackend.manager.CosManager;
import com.csh.mypicturebackend.model.dto.picture.PictureEditRequest;
import com.csh.mypicturebackend.model.dto.picture.PictureQueryRequest;
import com.csh.mypicturebackend.model.dto.picture.PictureUpdateRequest;
import com.csh.mypicturebackend.model.dto.picture.PictureUploadRequest;
import com.csh.mypicturebackend.model.entity.Picture;
import com.csh.mypicturebackend.model.entity.User;
import com.csh.mypicturebackend.model.vo.PictureVO;
import com.csh.mypicturebackend.service.PictureService;
import com.csh.mypicturebackend.service.UserService;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectInputStream;
import com.qcloud.cos.utils.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {

    @Resource
    private CosManager cosManager;
    @Resource
    private PictureService pictureService;
    @Resource
    private UserService userService;

    /**
     * 测试文件上传
     *
     * @param multipartFile
     * @return
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/test/upload")
    public BaseResponse<String> testUploadFile(@RequestPart("file") MultipartFile multipartFile) {
        // 文件目录
        String filename = multipartFile.getOriginalFilename();
        // 传入到对象存储中的路径
        String filepath = String.format("test/%s", filename);
        File file = null;
        try {
            // 在本地创建临时文件用于上传
            file = File.createTempFile(filepath, null);
            // 将需要上传的文件写入临时文件
            multipartFile.transferTo(file);
            // 上传文件
            cosManager.putObject(filepath, file);
            // 返回可访问地址
            return ResultUtils.success(filepath);
        } catch (Exception e) {
            log.error("file upload error, filepath = " + filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            if (file != null) {
                // 删除临时文件
                boolean delete = file.delete();
                if (!delete) {
                    log.error("file delete error, filepath = {}", filepath);
                }
            }
        }
    }

    /**
     * 测试文件下载
     *
     * @param filepath 文件路径
     * @param response 响应对象
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @GetMapping("/test/download/")
    public void testDownloadFile(String filepath, HttpServletResponse response) throws IOException {
        COSObjectInputStream cosObjectInput = null;
        try {
            COSObject cosObject = cosManager.getObject(filepath);
            cosObjectInput = cosObject.getObjectContent();
            // 处理下载到的流
            byte[] bytes = IOUtils.toByteArray(cosObjectInput);
            // 设置响应头
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + filepath);
            // 写入响应
            response.getOutputStream().write(bytes);
            response.getOutputStream().flush();
        } catch (Exception e) {
            log.error("file download error, filepath = " + filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "下载失败");
        } finally {
            if (cosObjectInput != null) {
                cosObjectInput.close();
            }
        }
    }



}
