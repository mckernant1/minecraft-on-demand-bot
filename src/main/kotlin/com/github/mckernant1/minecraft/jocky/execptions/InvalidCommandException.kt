package com.github.mckernant1.minecraft.jocky.execptions


class InvalidCommandException(
    message: String,
    cause: Throwable? = null,
    enableSuppression: Boolean = false,
    writableStackTrace: Boolean = false
) : Exception(message, cause, enableSuppression, writableStackTrace) {

}
