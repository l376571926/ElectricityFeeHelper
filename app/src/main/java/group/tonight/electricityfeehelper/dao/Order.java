package group.tonight.electricityfeehelper.dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

import java.io.Serializable;

/**
 * Created by liyiwei on 2018/2/21.
 */
@Entity
public class Order implements Serializable {
    private static final long serialVersionUID = -5197766696251962980L;
    @Id
    private Long id;//自身id
    private Long uid;//外键关联id，也就是user表中的id
    private double yingShou;
    private double shiShou;
    private double qianFei;
    private String remarks;//备注
    private String orderDate;
    private long createTime;
    private long updateTime;
    @Generated(hash = 1166992264)
    public Order(Long id, Long uid, double yingShou, double shiShou, double qianFei,
            String remarks, String orderDate, long createTime, long updateTime) {
        this.id = id;
        this.uid = uid;
        this.yingShou = yingShou;
        this.shiShou = shiShou;
        this.qianFei = qianFei;
        this.remarks = remarks;
        this.orderDate = orderDate;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }
    @Generated(hash = 1105174599)
    public Order() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getUid() {
        return this.uid;
    }
    public void setUid(Long uid) {
        this.uid = uid;
    }
    public double getYingShou() {
        return this.yingShou;
    }
    public void setYingShou(double yingShou) {
        this.yingShou = yingShou;
    }
    public double getShiShou() {
        return this.shiShou;
    }
    public void setShiShou(double shiShou) {
        this.shiShou = shiShou;
    }
    public double getQianFei() {
        return this.qianFei;
    }
    public void setQianFei(double qianFei) {
        this.qianFei = qianFei;
    }
    public String getRemarks() {
        return this.remarks;
    }
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    public String getOrderDate() {
        return this.orderDate;
    }
    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
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
}
