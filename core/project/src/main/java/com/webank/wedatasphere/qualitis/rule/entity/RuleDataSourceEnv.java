package com.webank.wedatasphere.qualitis.rule.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.Objects;
import javax.persistence.*;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

/**
 * @author v_minminghe@webank.com
 * @date 2022-08-03 15:57
 * @description
 */
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@Table(name = "qualitis_rule_datasource_env")
public class RuleDataSourceEnv {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;
    @Column(name = "linkis_env_name", length = 100)
    private String envName;
    @Column(name = "linkis_env_id", length = 100)
    private Long envId;
    @Column(name = "db_and_table")
    private String dbAndTable;

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonIgnore
    private RuleDataSource ruleDataSource;

    public RuleDataSourceEnv() {
    }

    public RuleDataSourceEnv(Long linkisDataSourceId, String linkisDataSourceName, RuleDataSource newRuleDataSource) {
        this.envId = linkisDataSourceId;
        this.envName = linkisDataSourceName;
        this.ruleDataSource = newRuleDataSource;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEnvName() {
        return envName;
    }

    public void setEnvName(String envName) {
        this.envName = envName;
    }

    public Long getEnvId() {
        return envId;
    }

    public void setEnvId(Long envId) {
        this.envId = envId;
    }

    public String getDbAndTable() {
        return dbAndTable;
    }

    public void setDbAndTable(String dbAndTable) {
        this.dbAndTable = dbAndTable;
    }

    public RuleDataSource getRuleDataSource() {
        return ruleDataSource;
    }

    public void setRuleDataSource(RuleDataSource ruleDataSource) {
        this.ruleDataSource = ruleDataSource;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RuleDataSourceEnv that = (RuleDataSourceEnv) o;
        return id.equals(that.id) &&
            Objects.equals(envName, that.envName) &&
            envId.equals(that.envId) &&
            Objects.equals(dbAndTable, that.dbAndTable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, envName, envId, dbAndTable);
    }
}
