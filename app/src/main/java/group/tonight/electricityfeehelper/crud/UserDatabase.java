package group.tonight.electricityfeehelper.crud;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import group.tonight.electricityfeehelper.dao.Order;
import group.tonight.electricityfeehelper.dao.User;

@Database(entities = {User.class, Order.class}, version = 1)
public abstract class UserDatabase extends RoomDatabase {
    private static final String DB_NAME = "UserDatabase.db";

    public abstract UserDao getUserDao();

    private static UserDatabase INSTANCE;

    public static void init(final Context context) {
        if (INSTANCE == null) {
            synchronized (UserDatabase.class) {
                if (INSTANCE == null) {
                    // Create database here
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            UserDatabase.class, DB_NAME)
                            .build();
                }
            }
        }
    }

    public static UserDatabase get() {
        return INSTANCE;
    }
}
