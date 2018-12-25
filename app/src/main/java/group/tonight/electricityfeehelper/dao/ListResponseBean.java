package group.tonight.electricityfeehelper.dao;

import java.io.Serializable;
import java.util.List;

import group.tonight.electricityfeehelper.model.BaseResponseBean;

public class ListResponseBean<T> extends BaseResponseBean implements Serializable {
    private List<T> data;

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}