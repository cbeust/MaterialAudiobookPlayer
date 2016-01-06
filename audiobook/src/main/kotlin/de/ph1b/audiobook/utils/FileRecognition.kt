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

package de.ph1b.audiobook.utils

import android.os.Build
import com.google.common.collect.Lists
import com.google.common.io.Files
import java.io.File
import java.io.FileFilter
import java.util.*


/**
 * Class containing methods for recognizing different file types by their file ending.

 * @author Paul Woitaschek
 */
object FileRecognition {

    private val imageTypes = Arrays.asList("jpg", "jpeg", "png", "bmp")
    private val audioTypes: List<String>

    init {
        audioTypes = Lists.newArrayList("3gp",
                "aac",
                "awb",
                "flac",
                "imy",
                "m4a",
                "m4b",
                "mp4",
                "mid",
                "mkv",
                "mp3",
                "mp3package",
                "mxmf",
                "ogg",
                "oga",
                "ota",
                "rtttl",
                "rtx",
                "wav",
                "wma",
                "xmf"
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            audioTypes.add("opus")
        }
    }

    private fun File.extensionToLowerCase() = Files.getFileExtension(name)
            .toLowerCase()

    private fun File.isAudio(): Boolean {
        return audioTypes.contains(extensionToLowerCase())
    }

    val folderAndMusicFilter = FileFilter {
        it.isDirectory || it.isAudio()
    }
    val folderAndImagesFilter = FileFilter {
        it.isDirectory || imageTypes.contains(it.extensionToLowerCase())
    }
}