package com.chrsrns.codingencyclopedia

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Entity
data class User(
    @PrimaryKey val email: String,
    @ColumnInfo val name: String,
    @ColumnInfo val profilePhoto: String = ""
)

@Dao
interface UserDao {
    @Query("SELECT * FROM user WHERE email LIKE :email AND name LIKE :name LIMIT 1")
    fun findByEmailAndName(email: String, name: String): Flow<User?>

    @Query("SELECT * FROM user WHERE email LIKE :email LIMIT 1")
    fun findByEmail(email: String): Flow<User?>

    @Upsert
    suspend fun insertAll(vararg users: User)

    @Query("DELETE from user")
    suspend fun deleteAll()
}

@Database(entities = [User::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            synchronized(this) {
                return INSTANCE ?: synchronized(this) {
                    Room.databaseBuilder(context, AppDatabase::class.java, "item_database")
                        .fallbackToDestructiveMigration()
                        .build()
                        .also { INSTANCE = it }
                }

            }
        }
    }
}

