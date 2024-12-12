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

package com.webank.wedatasphere.qualitis.rule.dao;

import com.webank.wedatasphere.qualitis.entity.Department;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateDepartment;
import java.util.List;

/**
 * @author allenzhou
 */
public interface TemplateDepartmentDao {

    /**
     * Save DepartmentTemplate
     *
     * @param templateDepartment
     * @return
     */
    TemplateDepartment saveDepartmentTemplate(TemplateDepartment templateDepartment);

    /**
     * Delete DepartmentTemplate
     * @param templateDepartment
     */
    void delete(TemplateDepartment templateDepartment);

    /**
     * Find by template.
     * @return
     * @param templateInDb
     */
    TemplateDepartment findByTemplate(Template templateInDb);

    /**
     * Find templates by department.
     * @param department
     * @return
     */
    List<Template> findTemplates(Department department);
}
