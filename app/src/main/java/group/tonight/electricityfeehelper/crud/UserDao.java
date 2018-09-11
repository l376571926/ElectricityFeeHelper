package group.tonight.electricityfeehelper.crud;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.Cursor;

import java.util.List;

import group.tonight.electricityfeehelper.dao.User;

@Dao
public interface UserDao {

    /**
     * ASC  升序
     * DESC 降序
     *
     * @return
     */
    @Query("SELECT * FROM user ORDER BY updateTime DESC")
    List<User> getAllUsers();

    @Query("SELECT * FROM user WHERE id=:id")
    User getUser(int id);

    @Query("SELECT * FROM user")
    Cursor getUserCursor();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User... users);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<User> userLists);

    @Update
    void update(User... users);

    @Delete
    void delete(User... users);

//    @Query("SELECT * FROM user WHERE age=:age")
//    List<User> getUsersByAge(int age);

//    @Query("SELECT * FROM user WHERE age=:age LIMIT :max")
//    List<User> getUsersByAge(int max, int... age);

//    @Query("SELECT * FROM user WHERE User.email == :email ")
//    List<User> findUserByEmail(String email);

    @Query("SELECT * FROM user WHERE id=:id")
    User load(int id);

    @Query("SELECT * FROM user WHERE id=:id")
    LiveData<User> loadLiveDataUser(int id);

    @Query("SELECT * FROM user")
    List<User> loadAll();

    @Query("SELECT * FROM user")
    LiveData<List<User>> loadAllLiveData();

    @Query("SELECT * FROM USER WHERE userId=:userId")
    User loadUserByUserId(String userId);

    @Query("SELECT * FROM User WHERE qianFeiSum != 0")
    List<User> loadOrderListNotEq0();

    @Query("select * from User " +
            "where " +
            "userId like '%' ||  :userId || '%' " +
            "or " +
            "userName like '%' ||  :userName || '%' " +
            "and " +
            "qianFeiSum != 0 " +
            "limit 50")
    LiveData<List<User>> search1(String userId, String userName);

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
