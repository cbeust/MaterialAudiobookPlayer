/*
 * This file is part of Material Audiobook Player.
 *
 * Material Audiobook Player is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * Material Audiobook Player is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Material Audiobook Player. If not, see <http://www.gnu.org/licenses/>.
 * /licenses/>.
 */

package de.ph1b.audiobook.persistence

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import timber.log.Timber
import java.util.*
import javax.inject.Inject

/**
 * Helper class that manages the underlying the database.
 *
 * @author Paul Woitaschek
 */
class InternalDb
@Inject constructor(context: Context)
: SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        BookTable.onCreate(db)
        ChapterTable.onCreate(db)
        BookmarkTable.onCreate(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        try {
            val upgradeHelper = DataBaseUpgradeHelper(db)
            upgradeHelper.upgrade(oldVersion)
        } catch (e: InvalidPropertiesFormatException) {
            Timber.e(e, "Error at upgrade")
            BookTable.dropTableIfExists(db)
            ChapterTable.dropTableIfExists(db)
            BookmarkTable.dropTableIfExists(db)
            onCreate(db)
        }
    }

    companion object {

        private val DATABASE_VERSION = 33
        private val DATABASE_NAME = "autoBookDB"
    }
}
