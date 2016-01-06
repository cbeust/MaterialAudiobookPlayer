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
import rx.Observable
import rx.subscriptions.Subscriptions
import timber.log.Timber
import java.io.File
import java.io.FileFilter

/**
 * Created by ph1b on 06/01/16.
 */
object SingleFileObserverFactory {

    fun observeChanges(root: File, filter: FileFilter): Observable<Unit> {
        return Observable.create<Unit> {
            val fileObserver = object : FileObserver(root.absolutePath) {
                override fun onEvent(event: Int, path: String?) {
                    when (event) {
                        FileObserver.CREATE,
                        FileObserver.DELETE, FileObserver.DELETE_SELF,
                        FileObserver.MOVED_FROM, FileObserver.MOVED_TO, FileObserver.MOVE_SELF,
                        FileObserver.MODIFY -> {
                            // ignore our logfile.
                            if (path != "materialaudiobookplayer.log") {
                                // only notify if its a music file or folder
                                if (path == null || filter.accept(File(root, path))) {
                                    if (!it.isUnsubscribed) {
                                        Timber.i("onEvent fired with $event and $path. Calling subject now.")
                                        it.onNext(Unit)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // setup the file observer and stop watching when unsubscribed.
            fileObserver.startWatching()
            it.add(Subscriptions.create { fileObserver.stopWatching() })
        }
    }
}