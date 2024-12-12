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

package com.webank.wedatasphere.qualitis.rule.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.webank.wedatasphere.qualitis.entity.User;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * @author allenzhou
 */
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@Table(name = "qualitis_rule_datasource_count", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "datasource_name"}))
public class RuleDataSourceCount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "datasource_name", columnDefinition = "MEDIUMTEXT")
    private String datasourceName;
    @Column(name = "datasource_count")
    private Integer datasourceCount;

    @Column(name = "user_id")
    private Long userId;

    public RuleDataSourceCount() {
    }

    public RuleDataSourceCount(String datasourceName, Long userId) {
        this.datasourceName = datasourceName;
        this.userId = userId;
        this.datasourceCount = 1;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDatasourceName() {
        return datasourceName;
    }

    public void setDatasourceName(String datasourceName) {
        this.datasourceName = datasourceName;
    }

    public Integer getDatasourceCount() {
        return datasourceCount;
    }

    public void setDatasourceCount(Integer datasourceCount) {
        this.datasourceCount = datasourceCount;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        RuleDataSourceCount that = (RuleDataSourceCount) o;
        return Objects.equals(datasourceName + userId, that.datasourceName + that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(datasourceName + userId);
    }

}
