package group.tonight.electricityfeehelper.crud;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import group.tonight.electricityfeehelper.dao.User;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<User> userLists);

    @Update
    void update(User... users);

    @Query("SELECT * FROM user WHERE id=:id")
    LiveData<User> loadLiveDataUser(int id);

    @Query("SELECT * FROM user")
    List<User> loadAll();

    @Query("SELECT * FROM user")
    LiveData<List<User>> loadAllLiveData();

    @Query("select * from User " +
            "where " +
            "userId like '%' || :userId || '%'" +
            "or " +
            "userName like '%' || :userName || '%'" +
            "or " +
            "powerMeterId  like '%' || :powerMeterId || '%'" +
            "or " +
            "userPhone  like '%' || :userPhone || '%'" +
            "limit 50")
    LiveData<List<User>> searchUser(String userId, String userName, String powerMeterId, String userPhone);


}
