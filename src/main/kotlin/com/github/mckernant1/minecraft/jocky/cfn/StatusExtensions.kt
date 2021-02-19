package com.github.mckernant1.minecraft.jocky.cfn

import software.amazon.awssdk.services.cloudformation.model.StackStatus

fun StackStatus.isSuccess(): Boolean = this == StackStatus.CREATE_COMPLETE

fun StackStatus.isFailure(): Boolean =
    this == StackStatus.CREATE_FAILED ||
            this == StackStatus.ROLLBACK_FAILED ||
            this == StackStatus.DELETE_FAILED
