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

package de.ph1b.audiobook.model

import rx.Observable
import timber.log.Timber
import java.io.File
import java.util.*

/**
 * Created by ph1b on 06/01/16.
 */
class FolderObserver(private val toObserve: File) {

    init {
        check(toObserve.isDirectory)
    }

    fun startObserving() {
        val contents = ArrayList<File>()
        scanInnerLayers(toObserve, contents, { isDirectory })
        contents.add(toObserve)
        Timber.i("contents=$contents")
    }

    private fun File.contents(): Observable<File> {
        return Observable.from(listFiles()?.asList() ?: Collections.emptyList())
    }

    private fun scanInnerLayers(nextFile: File, gatheredFiles: ArrayList<File>, filePredicate: File.() -> Boolean) {
        nextFile.contents()
                .filter { it.filePredicate() }
                .subscribe {
                    gatheredFiles.add(it)
                    scanInnerLayers(it, gatheredFiles, filePredicate)
                }
    }
}