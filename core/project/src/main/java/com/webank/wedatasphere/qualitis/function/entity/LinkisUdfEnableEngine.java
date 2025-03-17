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

package com.webank.wedatasphere.qualitis.function.entity;

import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Id;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author allenzhou
 */
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@Table(name = "qualitis_linkis_udf_enable_engine")
public class LinkisUdfEnableEngine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "enable_engine_code")
    private Integer enableEngineCode;

    @ManyToOne
    @JsonIgnore
    private LinkisUdf linkisUdf;

    public LinkisUdfEnableEngine() {
    }

    public LinkisUdfEnableEngine(LinkisUdf linkisUdfInDb, Integer engineCode) {
        this.enableEngineCode = engineCode;
        this.linkisUdf = linkisUdfInDb;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getEnableEngineCode() {
        return enableEngineCode;
    }

    public void setEnableEngineCode(Integer enableEngineCode) {
        this.enableEngineCode = enableEngineCode;
    }

    public LinkisUdf getLinkisUdf() {
        return linkisUdf;
    }

    public void setLinkisUdf(LinkisUdf linkisUdf) {
        this.linkisUdf = linkisUdf;
    }
}
