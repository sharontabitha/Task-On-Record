package com.taskonrecord.data.db

import android.content.Context
import androidx.room.*
import com.taskonrecord.data.model.Category
import com.taskonrecord.data.model.HouseType
import com.taskonrecord.data.model.Task
import com.taskonrecord.data.model.ResidentType
import com.taskonrecord.data.model.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Database(
    entities = [Category::class, Task::class, Note::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao
    abstract fun taskDao(): TaskDao
    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "taskonrecord.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(prepopulateCallback(context))
                    .build()
                    .also { INSTANCE = it }
            }
        }

        /**
         * Seeds the DB with 3 default category houses on first launch.
         */
        private fun prepopulateCallback(context: Context) = object : RoomDatabase.Callback() {
            override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                super.onCreate(db)
                // Seed defaults via coroutine after creation
                GlobalScope.launch(
                    Dispatchers.IO
                ) {
                    val database = getInstance(context)
                    database.categoryDao().insertCategory(
                        Category(name = "College", type = HouseType.COLLEGE, description = "Study tasks & assignments")
                    )
                    database.categoryDao().insertCategory(
                        Category(name = "Home Chores", type = HouseType.HOME, description = "Household tasks")
                    )
                    database.categoryDao().insertCategory(
                        Category(name = "My Garden", type = HouseType.GARDEN, description = "Personal goals & hobbies")
                    )
                }
            }
        }
    }
}

/**
 * Type converters for Room to store enums as strings.
 */
class Converters {
    @TypeConverter fun fromHouseType(value: HouseType): String = value.name
    @TypeConverter fun toHouseType(value: String): HouseType = HouseType.valueOf(value)

    @TypeConverter fun fromResidentType(value: ResidentType): String = value.name
    @TypeConverter fun toResidentType(value: String): ResidentType = ResidentType.valueOf(value)

    @TypeConverter fun fromPriority(value: com.taskonrecord.data.model.Priority): String = value.name
    @TypeConverter fun toPriority(value: String): com.taskonrecord.data.model.Priority =
        com.taskonrecord.data.model.Priority.valueOf(value)

    @TypeConverter fun fromTaskStatus(value: com.taskonrecord.data.model.TaskStatus): String = value.name
    @TypeConverter fun toTaskStatus(value: String): com.taskonrecord.data.model.TaskStatus =
        com.taskonrecord.data.model.TaskStatus.valueOf(value)
}
