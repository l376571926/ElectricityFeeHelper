package group.tonight.electricityfeehelper.dao;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

/**
 * Created by liyiwei on 2018/2/21.
 */
@Entity
public class Order implements Serializable {
    private static final long serialVersionUID = 6550423647636643872L;
    @PrimaryKey(autoGenerate = true)
    private int id;//自身id
    private int uid;//外键关联id，也就是user表中的id

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public double getYingShou() {
        return yingShou;
    }

    public void setYingShou(double yingShou) {
        this.yingShou = yingShou;
    }

    public double getShiShou() {
        return shiShou;
    }

    public void setShiShou(double shiShou) {
        this.shiShou = shiShou;
    }

    public double getQianFei() {
        return qianFei;
    }

    public void setQianFei(double qianFei) {
        this.qianFei = qianFei;
    }

    public double getYingShouWeiYue() {
        return yingShouWeiYue;
    }

    public void setYingShouWeiYue(double yingShouWeiYue) {
        this.yingShouWeiYue = yingShouWeiYue;
    }

    public double getShiShouWeiYue() {
        return shiShouWeiYue;
    }

    public void setShiShouWeiYue(double shiShouWeiYue) {
        this.shiShouWeiYue = shiShouWeiYue;
    }

    public double getQianJiaoWeiYue() {
        return qianJiaoWeiYue;
    }

    public void setQianJiaoWeiYue(double qianJiaoWeiYue) {
        this.qianJiaoWeiYue = qianJiaoWeiYue;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
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
}
