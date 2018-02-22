package group.tonight.electricityfeehelper.dao;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;

import java.io.Serializable;
import java.util.List;

/**
 * Created by liyiwei on 2018/2/20.
 */
@Entity
public class User implements Serializable {
    private static final long serialVersionUID = -4794237852963304362L;
    @Id
    private Long id;

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

    @ToMany(referencedJoinProperty = "uid")
    private List<Order> orders;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 1507654846)
    private transient UserDao myDao;

    @Generated(hash = 345354198)
    public User(Long id, String userId, String userName, String userPhone,
            String powerLineId, String powerLineName, String meterReadingDay,
            String meterReader, String measurementPointId, String meterReadingId,
            String powerMeterId, String powerValueType, String lastPowerValue,
            String currentPowerValue, String consumePowerValue,
            String comprehensiveRatio, String meterReadingNumber,
            String exceptionTypes, String meterReadingStatus, String powerSupplyId,
            String powerSupplyName, String userAddress, double yingShouSum,
            double shiShouSum, double qianFeiSum, long createTime, long updateTime,
            String remarks) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.userPhone = userPhone;
        this.powerLineId = powerLineId;
        this.powerLineName = powerLineName;
        this.meterReadingDay = meterReadingDay;
        this.meterReader = meterReader;
        this.measurementPointId = measurementPointId;
        this.meterReadingId = meterReadingId;
        this.powerMeterId = powerMeterId;
        this.powerValueType = powerValueType;
        this.lastPowerValue = lastPowerValue;
        this.currentPowerValue = currentPowerValue;
        this.consumePowerValue = consumePowerValue;
        this.comprehensiveRatio = comprehensiveRatio;
        this.meterReadingNumber = meterReadingNumber;
        this.exceptionTypes = exceptionTypes;
        this.meterReadingStatus = meterReadingStatus;
        this.powerSupplyId = powerSupplyId;
        this.powerSupplyName = powerSupplyName;
        this.userAddress = userAddress;
        this.yingShouSum = yingShouSum;
        this.shiShouSum = shiShouSum;
        this.qianFeiSum = qianFeiSum;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.remarks = remarks;
    }

    @Generated(hash = 586692638)
    public User() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return this.userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getPowerLineId() {
        return this.powerLineId;
    }

    public void setPowerLineId(String powerLineId) {
        this.powerLineId = powerLineId;
    }

    public String getPowerLineName() {
        return this.powerLineName;
    }

    public void setPowerLineName(String powerLineName) {
        this.powerLineName = powerLineName;
    }

    public String getMeterReadingDay() {
        return this.meterReadingDay;
    }

    public void setMeterReadingDay(String meterReadingDay) {
        this.meterReadingDay = meterReadingDay;
    }

    public String getMeterReader() {
        return this.meterReader;
    }

    public void setMeterReader(String meterReader) {
        this.meterReader = meterReader;
    }

    public String getMeasurementPointId() {
        return this.measurementPointId;
    }

    public void setMeasurementPointId(String measurementPointId) {
        this.measurementPointId = measurementPointId;
    }

    public String getMeterReadingId() {
        return this.meterReadingId;
    }

    public void setMeterReadingId(String meterReadingId) {
        this.meterReadingId = meterReadingId;
    }

    public String getPowerMeterId() {
        return this.powerMeterId;
    }

    public void setPowerMeterId(String powerMeterId) {
        this.powerMeterId = powerMeterId;
    }

    public String getPowerValueType() {
        return this.powerValueType;
    }

    public void setPowerValueType(String powerValueType) {
        this.powerValueType = powerValueType;
    }

    public String getLastPowerValue() {
        return this.lastPowerValue;
    }

    public void setLastPowerValue(String lastPowerValue) {
        this.lastPowerValue = lastPowerValue;
    }

    public String getCurrentPowerValue() {
        return this.currentPowerValue;
    }

    public void setCurrentPowerValue(String currentPowerValue) {
        this.currentPowerValue = currentPowerValue;
    }

    public String getConsumePowerValue() {
        return this.consumePowerValue;
    }

    public void setConsumePowerValue(String consumePowerValue) {
        this.consumePowerValue = consumePowerValue;
    }

    public String getComprehensiveRatio() {
        return this.comprehensiveRatio;
    }

    public void setComprehensiveRatio(String comprehensiveRatio) {
        this.comprehensiveRatio = comprehensiveRatio;
    }

    public String getMeterReadingNumber() {
        return this.meterReadingNumber;
    }

    public void setMeterReadingNumber(String meterReadingNumber) {
        this.meterReadingNumber = meterReadingNumber;
    }

    public String getExceptionTypes() {
        return this.exceptionTypes;
    }

    public void setExceptionTypes(String exceptionTypes) {
        this.exceptionTypes = exceptionTypes;
    }

    public String getMeterReadingStatus() {
        return this.meterReadingStatus;
    }

    public void setMeterReadingStatus(String meterReadingStatus) {
        this.meterReadingStatus = meterReadingStatus;
    }

    public String getPowerSupplyId() {
        return this.powerSupplyId;
    }

    public void setPowerSupplyId(String powerSupplyId) {
        this.powerSupplyId = powerSupplyId;
    }

    public String getPowerSupplyName() {
        return this.powerSupplyName;
    }

    public void setPowerSupplyName(String powerSupplyName) {
        this.powerSupplyName = powerSupplyName;
    }

    public String getUserAddress() {
        return this.userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public double getYingShouSum() {
        return this.yingShouSum;
    }

    public void setYingShouSum(double yingShouSum) {
        this.yingShouSum = yingShouSum;
    }

    public double getShiShouSum() {
        return this.shiShouSum;
    }

    public void setShiShouSum(double shiShouSum) {
        this.shiShouSum = shiShouSum;
    }

    public double getQianFeiSum() {
        return this.qianFeiSum;
    }

    public void setQianFeiSum(double qianFeiSum) {
        this.qianFeiSum = qianFeiSum;
    }

    public long getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public String getRemarks() {
        return this.remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1238409544)
    public List<Order> getOrders() {
        if (orders == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            OrderDao targetDao = daoSession.getOrderDao();
            List<Order> ordersNew = targetDao._queryUser_Orders(id);
            synchronized (this) {
                if (orders == null) {
                    orders = ordersNew;
                }
            }
        }
        return orders;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1446109810)
    public synchronized void resetOrders() {
        orders = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 2059241980)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getUserDao() : null;
    }
    
}
