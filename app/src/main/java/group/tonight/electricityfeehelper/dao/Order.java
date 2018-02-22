package group.tonight.electricityfeehelper.dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;

/**
 * Created by liyiwei on 2018/2/21.
 */
@Entity
public class Order implements Serializable {
    private static final long serialVersionUID = 6550423647636643872L;
    @Id
    private Long id;//自身id
    private Long uid;//外键关联id，也就是user表中的id

    private String orderDate;//年月
    private double yingShou;//应收电费
    private double shiShou;//实收电费
    private double qianFei;//欠费金额
    private double yingShouWeiYue;//应收违约金
    private double shiShouWeiYue;//实收违约金
    private double qianJiaoWeiYue;//欠交违约金
    private String orderStatus;//电费类别

    private String remarks;//备注

    private long createTime;
    private long updateTime;
    @Generated(hash = 1282803358)
    public Order(Long id, Long uid, String orderDate, double yingShou,
            double shiShou, double qianFei, double yingShouWeiYue,
            double shiShouWeiYue, double qianJiaoWeiYue, String orderStatus,
            String remarks, long createTime, long updateTime) {
        this.id = id;
        this.uid = uid;
        this.orderDate = orderDate;
        this.yingShou = yingShou;
        this.shiShou = shiShou;
        this.qianFei = qianFei;
        this.yingShouWeiYue = yingShouWeiYue;
        this.shiShouWeiYue = shiShouWeiYue;
        this.qianJiaoWeiYue = qianJiaoWeiYue;
        this.orderStatus = orderStatus;
        this.remarks = remarks;
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
    public String getOrderDate() {
        return this.orderDate;
    }
    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
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
    public double getYingShouWeiYue() {
        return this.yingShouWeiYue;
    }
    public void setYingShouWeiYue(double yingShouWeiYue) {
        this.yingShouWeiYue = yingShouWeiYue;
    }
    public double getShiShouWeiYue() {
        return this.shiShouWeiYue;
    }
    public void setShiShouWeiYue(double shiShouWeiYue) {
        this.shiShouWeiYue = shiShouWeiYue;
    }
    public double getQianJiaoWeiYue() {
        return this.qianJiaoWeiYue;
    }
    public void setQianJiaoWeiYue(double qianJiaoWeiYue) {
        this.qianJiaoWeiYue = qianJiaoWeiYue;
    }
    public String getOrderStatus() {
        return this.orderStatus;
    }
    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
    public String getRemarks() {
        return this.remarks;
    }
    public void setRemarks(String remarks) {
        this.remarks = remarks;
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
