package com.webank.wedatasphere.qualitis.rule.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * @author v_gaojiedeng@webank.com
 */
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@Table(name = "qualitis_alarm_arguments_execution_parameters")
public class AlarmArgumentsExecutionParameters {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "alarm_event")
    private Integer alarmEvent;

    @Column(name = "alarm_level")
    private Integer alarmLevel;

    @Column(name = "alarm_receiver")
    private String alarmReceiver;

    @ManyToOne
    @JsonIgnore
    private ExecutionParameters executionParameters;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAlarmEvent() {
        return alarmEvent;
    }

    public void setAlarmEvent(Integer alarmEvent) {
        this.alarmEvent = alarmEvent;
    }

    public Integer getAlarmLevel() {
        return alarmLevel;
    }

    public void setAlarmLevel(Integer alarmLevel) {
        this.alarmLevel = alarmLevel;
    }

    public String getAlarmReceiver() {
        return alarmReceiver;
    }

    public void setAlarmReceiver(String alarmReceiver) {
        this.alarmReceiver = alarmReceiver;
    }

    public void setExecutionParameters(ExecutionParameters executionParameters) {
        this.executionParameters = executionParameters;
    }

    public ExecutionParameters getExecutionParameters() {
        return executionParameters;
    }
}
