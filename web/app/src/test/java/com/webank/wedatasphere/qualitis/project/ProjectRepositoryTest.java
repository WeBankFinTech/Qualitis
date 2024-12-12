/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.qualitis.project;

import com.webank.wedatasphere.qualitis.project.dao.repository.ProjectRepository;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import java.nio.charset.Charset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.Assert.*;

/**
 * @author v_wblwyan
 * @date 2018-11-20
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProjectRepositoryTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProjectRepositoryTest.class);
  @Autowired
  ProjectRepository repository;

  public void extract(String zipFilePath, String destDirectory) throws IOException {
    File destDir = new File(destDirectory);
    if (!destDir.exists()) {
      destDir.mkdir();
    }
    try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath), Charset.forName("UTF-8"))) {
      ZipEntry entry = zipIn.getNextEntry();
      while (entry != null) {
        String filePath = destDirectory + File.separator + entry.getName();
        if (!entry.isDirectory()) {
          extractFile(zipIn, filePath);
        } else {
          File dir = new File(filePath);
          dir.mkdir();
        }
        zipIn.closeEntry();
        entry = zipIn.getNextEntry();
      }
    }catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
    }

  }

  private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
    try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
      byte[] bytesIn = new byte[4096];
      int read = 0;
      while ((read = zipIn.read(bytesIn)) != -1) {
        bos.write(bytesIn, 0, read);
      }
    }

  }

  @Test
  public void testUnzipFiles() throws IOException {
    File zipFile = new File("D:\\share\\merge\\qualitis\\tmp\\allenzhou\\f136beeaf8314f4e8d5bcb016d45c152.zip");
    extract(zipFile.getPath(), zipFile.getParentFile().getPath());
//    // First, create zip file that points to the disk.
//    ZipFile zFile = new ZipFile(zipFile);
//    // Extract the file to the unzip directory
//    zFile.extractAll(zipFile.getParentFile().getPath());
//    List<FileHeader> headerList = zFile.getFileHeaders();
//    for (FileHeader fileHeader : headerList) {
//      if (!fileHeader.isDirectory()) {
//      }
//    }
  }

  @Test
  @Transactional
  public void test() {
    //保存是否成功
    Project entity = new Project();
    entity.setName("junitProject");
    entity.setCreateUser("setCreateUser");
    entity.setCreateUserFullName("setCreateUserFullName");
    entity.setDescription("setDescription");
    entity.setDepartment("setUserDepartment");
    Project saveEntity = repository.save(entity);
    assertTrue(saveEntity.getId() != 0);


    //保存到数据库的对象是否和保存的值一致
    List<Project> findByCreateUserList = repository.findByCreateUser(saveEntity.getCreateUser());
    assertTrue(findByCreateUserList.size() > 0);
    assertEquals(findByCreateUserList.get(0).getName(), saveEntity.getName());

    //根据username查询的数据库对象是否和保存的值一致
    Project findByNameUser = repository.findByName(saveEntity.getName());
    assertNotNull(findByNameUser);
    assertEquals(findByNameUser.getName(), saveEntity.getName());
  }

}
