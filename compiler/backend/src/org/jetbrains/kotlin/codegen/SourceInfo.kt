/*
 * Copyright 2010-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.codegen

import org.jetbrains.kotlin.backend.common.CodegenUtil
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction

class SourceInfo(
    val fileEntryName: String,
    val pathOrCleanFQN: String,
    val linesInFile: Int,
    val sourceFileName: String? = fileEntryName
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SourceInfo

        if (fileEntryName != other.fileEntryName) return false
        if (pathOrCleanFQN != other.pathOrCleanFQN) return false
        if (linesInFile != other.linesInFile) return false
        if (sourceFileName != other.sourceFileName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = fileEntryName.hashCode()
        result = 31 * result + pathOrCleanFQN.hashCode()
        result = 31 * result + linesInFile
        result = 31 * result + (sourceFileName?.hashCode() ?: 0)
        return result
    }

    companion object {
        fun createFromPsi(element: KtElement?, internalClassName: String): SourceInfo {
            assert(element != null) { "Couldn't create source mapper for null element $internalClassName" }
            val lineNumbers = CodegenUtil.getLineNumberForElement(element!!.containingFile, true)
                ?: error("Couldn't extract line count in ${element.containingFile}")

            //TODO hack condition for package parts cleaning
            val isTopLevel = element is KtFile || (element is KtNamedFunction && element.getParent() is KtFile)
            val cleanedClassFqName = if (!isTopLevel) internalClassName else internalClassName.substringBefore('$')

            val fileName = element.containingKtFile.name
            return SourceInfo(fileName, cleanedClassFqName, lineNumbers, fileName)
        }

        fun createForIr(lineNumbers: Int, internalClassName: String, fileEntryName: String, sourceFileName: String?): SourceInfo {
            return SourceInfo(fileEntryName, internalClassName, lineNumbers, sourceFileName)
        }
    }
}

