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

import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.Visibility
import org.jetbrains.kotlin.ir.ObsoleteDescriptorBasedAPI
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.declarations.persistent.carriers.PropertyCarrier
import org.jetbrains.kotlin.ir.descriptors.WrappedPropertyDescriptor
import org.jetbrains.kotlin.ir.symbols.IrPropertySymbol
import org.jetbrains.kotlin.name.Name

internal abstract class PersistentIrPropertyCommon(
    startOffset: Int,
    endOffset: Int,
    origin: IrDeclarationOrigin,
    override val name: Name,
    override var visibility: Visibility,
    override val isVar: Boolean,
    override val isConst: Boolean,
    override val isLateinit: Boolean,
    override val isDelegated: Boolean,
    override val isExternal: Boolean,
    override val isExpect: Boolean,
) : PersistentIrDeclarationBase<PropertyCarrier>(startOffset, endOffset, origin),
    IrProperty,
    PropertyCarrier {

    override var backingFieldField: IrField? = null

    override var backingField: IrField?
        get() = getCarrier().backingFieldField
        set(v) {
            if (backingField !== v) {
                setCarrier().backingFieldField = v
            }
        }

    override var getterField: IrSimpleFunction? = null

    override var getter: IrSimpleFunction?
        get() = getCarrier().getterField
        set(v) {
            if (getter !== v) {
                setCarrier().getterField = v
            }
        }

    override var setterField: IrSimpleFunction? = null

    override var setter: IrSimpleFunction?
        get() = getCarrier().setterField
        set(v) {
            if (setter !== v) {
                setCarrier().setterField = v
            }
        }

    override var metadataField: MetadataSource? = null

    override var metadata: MetadataSource?
        get() = getCarrier().metadataField
        set(v) {
            if (metadata !== v) {
                setCarrier().metadataField = v
            }
        }
}

internal class PersistentIrProperty(
    startOffset: Int,
    endOffset: Int,
    origin: IrDeclarationOrigin,
    override val symbol: IrPropertySymbol,
    name: Name,
    visibility: Visibility,
    override val modality: Modality,
    isVar: Boolean,
    isConst: Boolean,
    isLateinit: Boolean,
    isDelegated: Boolean,
    isExternal: Boolean,
    isExpect: Boolean = false,
    override val isFakeOverride: Boolean = origin == IrDeclarationOrigin.FAKE_OVERRIDE,
) : PersistentIrPropertyCommon(
    startOffset, endOffset, origin, name, visibility, isVar, isConst, isLateinit, isDelegated, isExternal, isExpect,
) {
    init {
        symbol.bind(this)
    }

    @ObsoleteDescriptorBasedAPI
    override val descriptor: PropertyDescriptor
        get() = symbol.descriptor
}

internal class PersistentIrFakeOverrideProperty(
    startOffset: Int,
    endOffset: Int,
    origin: IrDeclarationOrigin,
    name: Name,
    visibility: Visibility,
    override var modality: Modality,
    isVar: Boolean,
    isConst: Boolean,
    isLateinit: Boolean,
    isDelegated: Boolean,
    isExternal: Boolean,
    isExpect: Boolean,
) : PersistentIrPropertyCommon(
    startOffset, endOffset, origin, name, visibility, isVar, isConst, isLateinit,
    isDelegated, isExternal, isExpect
), IrFakeOverrideProperty {
    override val isFakeOverride: Boolean
        get() = true

    private var _symbol: IrPropertySymbol? = null

    override val symbol: IrPropertySymbol
        get() = _symbol ?: error("$this has not acquired a symbol yet")

    @ObsoleteDescriptorBasedAPI
    override val descriptor
        get() = _symbol?.descriptor ?: WrappedPropertyDescriptor()

    @OptIn(ObsoleteDescriptorBasedAPI::class)
    override fun acquireSymbol(symbol: IrPropertySymbol): IrProperty {
        assert(_symbol == null) { "$this already has symbol _symbol" }
        _symbol = symbol
        symbol.bind(this)
        (symbol.descriptor as? WrappedPropertyDescriptor)?.bind(this)
        return this
    }
}
