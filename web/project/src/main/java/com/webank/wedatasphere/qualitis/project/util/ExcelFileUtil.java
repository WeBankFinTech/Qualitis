package com.webank.wedatasphere.qualitis.project.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * @author v_minminghe@webank.com
 * @date 2024-10-31 15:12
 * @description
 */
public class ExcelFileUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelFileUtil.class);

    public static void validateAndCreate(File directory, String fileName) throws IOException {
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File excel = new File(directory, fileName);
        if (!excel.exists()) {
            boolean newFile = excel.createNewFile();
            if (!newFile) {
                LOGGER.error("{&FAILED_TO_CREATE_NEW_FILE}");
            }
        }
    }
}
