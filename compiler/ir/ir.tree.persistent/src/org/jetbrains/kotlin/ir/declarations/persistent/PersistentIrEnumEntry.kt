/*
 * Copyright 2010-2016 JetBrains s.r.o.
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

package org.jetbrains.kotlin.ir.declarations.persistent

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.ir.ObsoleteDescriptorBasedAPI
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrEnumEntry
import org.jetbrains.kotlin.ir.declarations.persistent.carriers.EnumEntryCarrier
import org.jetbrains.kotlin.ir.expressions.IrExpressionBody
import org.jetbrains.kotlin.ir.symbols.IrEnumEntrySymbol
import org.jetbrains.kotlin.name.Name

internal class PersistentIrEnumEntry(
    startOffset: Int,
    endOffset: Int,
    origin: IrDeclarationOrigin,
    override val symbol: IrEnumEntrySymbol,
    override val name: Name
) : PersistentIrDeclarationBase<EnumEntryCarrier>(startOffset, endOffset, origin),
    IrEnumEntry,
    EnumEntryCarrier {

    init {
        symbol.bind(this)
    }

    @ObsoleteDescriptorBasedAPI
    override val descriptor: ClassDescriptor
        get() = symbol.descriptor

    override var correspondingClassField: IrClass? = null

    override var correspondingClass: IrClass?
        get() = getCarrier().correspondingClassField
        set(v) {
            if (correspondingClass !== v) {
                setCarrier().correspondingClassField = v
            }
        }

    override var initializerExpressionField: IrExpressionBody? = null

    override var initializerExpression: IrExpressionBody?
        get() = getCarrier().initializerExpressionField
        set(v) {
            if (initializerExpression !== v) {
                if (v is PersistentIrBodyBase<*>) {
                    v.container = this
                }
                setCarrier().initializerExpressionField = v
            }
        }
}
