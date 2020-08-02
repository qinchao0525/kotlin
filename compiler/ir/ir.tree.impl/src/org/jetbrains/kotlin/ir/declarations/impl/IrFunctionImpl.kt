/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.declarations.impl

import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.Visibility
import org.jetbrains.kotlin.ir.ObsoleteDescriptorBasedAPI
import org.jetbrains.kotlin.ir.declarations.IrAttributeContainer
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrFakeOverrideFunction
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.descriptors.WrappedSimpleFunctionDescriptor
import org.jetbrains.kotlin.ir.symbols.IrPropertySymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.name.Name

abstract class IrFunctionCommonImpl(
    startOffset: Int,
    endOffset: Int,
    origin: IrDeclarationOrigin,
    name: Name,
    visibility: Visibility,
    returnType: IrType,
    isInline: Boolean,
    isExternal: Boolean,
    override val isTailrec: Boolean,
    override val isSuspend: Boolean,
    override val isOperator: Boolean,
    override val isInfix: Boolean,
    isExpect: Boolean,
) : IrFunctionBase(startOffset, endOffset, origin, name, visibility, isInline, isExternal, isExpect, returnType), IrSimpleFunction {
    override var overriddenSymbols: List<IrSimpleFunctionSymbol> = emptyList()

    @Suppress("LeakingThis")
    override var attributeOwnerId: IrAttributeContainer = this

    override var correspondingPropertySymbol: IrPropertySymbol? = null
}

class IrFunctionImpl(
    startOffset: Int,
    endOffset: Int,
    origin: IrDeclarationOrigin,
    override val symbol: IrSimpleFunctionSymbol,
    name: Name,
    visibility: Visibility,
    override val modality: Modality,
    returnType: IrType,
    isInline: Boolean,
    isExternal: Boolean,
    isTailrec: Boolean,
    isSuspend: Boolean,
    isOperator: Boolean,
    isInfix: Boolean,
    isExpect: Boolean,
    override val isFakeOverride: Boolean = origin == IrDeclarationOrigin.FAKE_OVERRIDE,
) : IrFunctionCommonImpl(
    startOffset, endOffset, origin, name, visibility, returnType, isInline,
    isExternal, isTailrec, isSuspend, isOperator, isInfix, isExpect,
) {
    @ObsoleteDescriptorBasedAPI
    override val descriptor: FunctionDescriptor
        get() = symbol.descriptor

    init {
        symbol.bind(this)
    }
}

class IrFakeOverrideFunctionImpl(
    startOffset: Int,
    endOffset: Int,
    origin: IrDeclarationOrigin,
    name: Name,
    override var visibility: Visibility,
    override var modality: Modality,
    returnType: IrType,
    isInline: Boolean,
    isExternal: Boolean,
    isTailrec: Boolean,
    isSuspend: Boolean,
    isOperator: Boolean,
    isInfix: Boolean,
    isExpect: Boolean,
) : IrFunctionCommonImpl(
    startOffset, endOffset, origin, name, visibility, returnType, isInline,
    isExternal, isTailrec, isSuspend, isOperator, isInfix, isExpect,
), IrFakeOverrideFunction {
    override val isFakeOverride: Boolean
        get() = true

    private var _symbol: IrSimpleFunctionSymbol? = null

    override val symbol: IrSimpleFunctionSymbol
        get() = _symbol ?: error("$this has not acquired a symbol yet")

    @ObsoleteDescriptorBasedAPI
    override val descriptor
        get() = _symbol?.descriptor ?: WrappedSimpleFunctionDescriptor()

    @OptIn(ObsoleteDescriptorBasedAPI::class)
    override fun acquireSymbol(symbol: IrSimpleFunctionSymbol): IrSimpleFunction {
        assert(_symbol == null) { "$this already has symbol _symbol" }
        _symbol = symbol
        symbol.bind(this)
        (symbol.descriptor as? WrappedSimpleFunctionDescriptor)?.bind(this)
        return this
    }
}
