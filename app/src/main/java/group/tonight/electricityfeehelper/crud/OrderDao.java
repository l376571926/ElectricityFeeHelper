package group.tonight.electricityfeehelper.crud;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import group.tonight.electricityfeehelper.dao.Order;
import group.tonight.electricityfeehelper.dao.User;

@Dao
public interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Order... order);

    @Update
    void update(Order order);

    @Query("SELECT * FROM `order` WHERE id=:id")
    Order load(int id);

    @Query("SELECT * FROM `Order` WHERE uid=:uid")
    List<Order> loadOrderByUid(int uid);


    @Query("SELECT * FROM `Order` WHERE uid=:uid AND orderDate=:orderDate")
    Order loadOrderByUidAndOrderDate(int uid, String orderDate);
}
