package com.webank.wedatasphere.qualitis.service.impl;

import com.webank.wedatasphere.qualitis.config.LinkisConfig;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.service.FileService;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import com.webank.wedatasphere.qualitis.util.UuidGenerator;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import jodd.util.StringUtil;
import org.apache.commons.io.FileUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author allenzhou@webank.com
 * @date 2023/4/21 9:14
 */
@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private LinkisConfig linkisConfig;

    private static final Logger LOGGER = LoggerFactory.getLogger(FileServiceImpl.class);

    private HttpServletRequest httpServletRequest;

    public FileServiceImpl(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    public GeneralResponse<String> uploadFile(InputStream fileInputStream, FormDataContentDisposition fileDisposition, String loginUser)
        throws UnExpectedRequestException {
        String userName = StringUtil.isNotEmpty(loginUser)? loginUser : HttpUtils.getUserName(httpServletRequest);
        // Upload file will be stored locally, return file path.
        StringBuilder filePath = new StringBuilder();
        String fileName = fileDisposition.getFileName();
        LOGGER.info("{} start to upload file: {}", userName, fileName);

        System.setProperty("file.encoding", "UTF-8");

        filePath.append(linkisConfig.getUploadTmpPath()).append(File.separator).append(userName)
            .append(File.separator).append(UuidGenerator.generate()).append(File.separator).append(fileName);

        try {
            File targetFile = new File(filePath.toString());
            FileUtils.copyInputStreamToFile(fileInputStream, targetFile);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw new UnExpectedRequestException("Failed to store file locally.");
        }
        LOGGER.info("{} upload file finished: {}", userName, fileName);
        return new GeneralResponse<>("200", "{&SUCCEED_TO_UPLOAD_FILE}", filePath.toString());
    }
}
