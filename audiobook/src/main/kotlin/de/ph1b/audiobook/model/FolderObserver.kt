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

import android.os.FileObserver
import de.ph1b.audiobook.utils.FileRecognition
import rx.Observable
import rx.subjects.PublishSubject
import timber.log.Timber
import java.io.File
import java.io.FileFilter
import java.util.*

/**
 * Created by ph1b on 06/01/16.
 */
class FolderObserver(private val toObserve: File) {

    init {
        check(toObserve.isDirectory)
    }

    private val internalObservers = ArrayList<FileObserver>()
    private val notifySubject = PublishSubject.create<Unit>()

    fun startObserving() {

        // first collect all directories
        val contents = ArrayList<File>()
        contents.add(toObserve)
        scanInnerLayers(toObserve, contents, FileFilter { it.isDirectory })
        Timber.i("contents=$contents")

        // then get all files
        val containingFiles = ArrayList<File>()
        contents.forEach {
            scanInnerLayers(it, containingFiles, FileRecognition.folderAndMusicFilter)
        }
        Timber.i("containingFiles=$containingFiles")

        // monitor the top folder as well
        containingFiles.add(toObserve)

        // set up FileObservers for each directory
        containingFiles.forEach {
            val fileObserver = object : FileObserver(it.absolutePath) {
                override fun onEvent(event: Int, path: String?) {
                    when (event) {
                        FileObserver.CREATE,
                        FileObserver.DELETE, FileObserver.DELETE_SELF,
                        FileObserver.MOVED_FROM, FileObserver.MOVED_TO, FileObserver.MOVE_SELF,
                        FileObserver.MODIFY -> {
                            if (path != "materialaudiobookplayer.log") {
                                // only notify if its a music file or folder
                                if (path == null || FileRecognition.folderAndMusicFilter.accept(File(it, path))) {
                                    Timber.i("onEvent fired with $event and $path. Calling subject now.")
                                    notifySubject.onNext(Unit)
                                }
                            }
                        }
                    }
                }
            }
            Timber.i("Added observer for $it")
            fileObserver.startWatching()
            internalObservers.add(fileObserver)
        }
    }

    private fun File.contents(filter: FileFilter): Observable<File> {
        return Observable.from(listFiles(filter)?.asList() ?: Collections.emptyList())
    }

    private fun scanInnerLayers(nextFile: File, gatheredFiles: ArrayList<File>, filter: FileFilter) {
        nextFile.contents(filter)
                .subscribe {
                    gatheredFiles.add(it)
                    scanInnerLayers(it, gatheredFiles, filter)
                }
    }
}