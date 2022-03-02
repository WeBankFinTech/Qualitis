package com.webank.wedatasphere.qualitis.rule.util;

import com.webank.wedatasphere.qualitis.rule.constant.FileOutputUnitEnum;

/**
 * @author allenzhou
 */
public class UnitTransfer {
    public static double alarmconfigToTaskResult(double taskResult, String alarmConfigUnit, String taskResultUnit) {
        double res = 0;
        if (taskResultUnit.equals(FileOutputUnitEnum.TB.getMessage())) {
            taskResult = taskResult * 1024 * 1024 * 1024 * 1024;
            res = chooseUnit(taskResult, alarmConfigUnit);
        } else if (taskResultUnit.equals(FileOutputUnitEnum.GB.getMessage())) {
            taskResult = taskResult * 1024 * 1024 * 1024;
            res = chooseUnit(taskResult, alarmConfigUnit);
        } else if (taskResultUnit.equals(FileOutputUnitEnum.MB.getMessage())) {
            taskResult = taskResult * 1024 * 1024;
            res = chooseUnit(taskResult, alarmConfigUnit);
        } else if (taskResultUnit.equals(FileOutputUnitEnum.KB.getMessage())) {
            taskResult = taskResult * 1024;
            res = chooseUnit(taskResult, alarmConfigUnit);
        } else if (taskResultUnit.equals(FileOutputUnitEnum.B.getMessage())) {
            res = chooseUnit(taskResult, alarmConfigUnit);
        } else {

        }
        return res;
    }
    
    public static double chooseUnit(double input, String unit) {
        double output = 0;
        if (unit.equals(FileOutputUnitEnum.TB.getMessage())) {
            output = input / 1024 / 1024 / 1024 / 1024;
        } else if (unit.equals(FileOutputUnitEnum.GB.getMessage())) {
            output = input  / 1024 / 1024 / 1024;
        } else if (unit.equals(FileOutputUnitEnum.MB.getMessage())) {
            output = input  / 1024 / 1024;
        } else if (unit.equals(FileOutputUnitEnum.KB.getMessage())) {
            output = input  / 1024;
        } else if (unit.equals(FileOutputUnitEnum.B.getMessage())) {
            output = input;
        } else {

        }
        return output;
    }
}
