package group.tonight.electricityfeehelper.dao;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;


import java.io.Serializable;

/**
 * Created by liyiwei on 2018/2/20.
 */
@Entity
public class User implements Serializable {
    private static final long serialVersionUID = -4794237852963304362L;
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String userId;//用户编号
    private String userName;//用户名称
    private String userPhone;//联系方式
    private String powerLineId;//抄表段编号，原来的serialId
    private String powerLineName;//抄表段名称
    private String meterReadingDay;//抄表例日
    private String meterReader;//抄表员
    private String measurementPointId;//计量点编号
    private String meterReadingId;//抄表序号，原来的positionId
    private String powerMeterId;//电能表编号
    private String powerValueType;//示数类型
    private String lastPowerValue;//上次示数
    private String currentPowerValue;//本次示数
    private String consumePowerValue;//抄见电量
    private String comprehensiveRatio;//综合倍率
    private String meterReadingNumber;//抄表位数
    private String exceptionTypes;//异常类型
    private String meterReadingStatus;//抄表状态
    private String powerSupplyId;//供电单位
    private String powerSupplyName;//供电所
    private String userAddress;//用电地址

    private double yingShouSum;
    private double shiShouSum;
    private double qianFeiSum;

    private long createTime;
    private long updateTime;
    private String remarks;//备注

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getPowerLineId() {
        return powerLineId;
    }

    public void setPowerLineId(String powerLineId) {
        this.powerLineId = powerLineId;
    }

    public String getPowerLineName() {
        return powerLineName;
    }

    public void setPowerLineName(String powerLineName) {
        this.powerLineName = powerLineName;
    }

    public String getMeterReadingDay() {
        return meterReadingDay;
    }

    public void setMeterReadingDay(String meterReadingDay) {
        this.meterReadingDay = meterReadingDay;
    }

    public String getMeterReader() {
        return meterReader;
    }

    public void setMeterReader(String meterReader) {
        this.meterReader = meterReader;
    }

    public String getMeasurementPointId() {
        return measurementPointId;
    }

    public void setMeasurementPointId(String measurementPointId) {
        this.measurementPointId = measurementPointId;
    }

    public String getMeterReadingId() {
        return meterReadingId;
    }

    public void setMeterReadingId(String meterReadingId) {
        this.meterReadingId = meterReadingId;
    }

    public String getPowerMeterId() {
        return powerMeterId;
    }

    public void setPowerMeterId(String powerMeterId) {
        this.powerMeterId = powerMeterId;
    }

    public String getPowerValueType() {
        return powerValueType;
    }

    public void setPowerValueType(String powerValueType) {
        this.powerValueType = powerValueType;
    }

    public String getLastPowerValue() {
        return lastPowerValue;
    }

    public void setLastPowerValue(String lastPowerValue) {
        this.lastPowerValue = lastPowerValue;
    }

    public String getCurrentPowerValue() {
        return currentPowerValue;
    }

    public void setCurrentPowerValue(String currentPowerValue) {
        this.currentPowerValue = currentPowerValue;
    }

    public String getConsumePowerValue() {
        return consumePowerValue;
    }

    public void setConsumePowerValue(String consumePowerValue) {
        this.consumePowerValue = consumePowerValue;
    }

    public String getComprehensiveRatio() {
        return comprehensiveRatio;
    }

    public void setComprehensiveRatio(String comprehensiveRatio) {
        this.comprehensiveRatio = comprehensiveRatio;
    }

    public String getMeterReadingNumber() {
        return meterReadingNumber;
    }

    public void setMeterReadingNumber(String meterReadingNumber) {
        this.meterReadingNumber = meterReadingNumber;
    }

    public String getExceptionTypes() {
        return exceptionTypes;
    }

    public void setExceptionTypes(String exceptionTypes) {
        this.exceptionTypes = exceptionTypes;
    }

    public String getMeterReadingStatus() {
        return meterReadingStatus;
    }

    public void setMeterReadingStatus(String meterReadingStatus) {
        this.meterReadingStatus = meterReadingStatus;
    }

    public String getPowerSupplyId() {
        return powerSupplyId;
    }

    public void setPowerSupplyId(String powerSupplyId) {
        this.powerSupplyId = powerSupplyId;
    }

    public String getPowerSupplyName() {
        return powerSupplyName;
    }

    public void setPowerSupplyName(String powerSupplyName) {
        this.powerSupplyName = powerSupplyName;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public double getYingShouSum() {
        return yingShouSum;
    }

    public void setYingShouSum(double yingShouSum) {
        this.yingShouSum = yingShouSum;
    }

    public double getShiShouSum() {
        return shiShouSum;
    }

    public void setShiShouSum(double shiShouSum) {
        this.shiShouSum = shiShouSum;
    }

    public double getQianFeiSum() {
        return qianFeiSum;
    }

    public void setQianFeiSum(double qianFeiSum) {
        this.qianFeiSum = qianFeiSum;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
