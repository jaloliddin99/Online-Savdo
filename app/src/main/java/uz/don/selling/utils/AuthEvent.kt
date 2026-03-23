package uz.don.selling.utils

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object AuthEvent {
    private val _unauthorizedFlow = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val unauthorizedFlow = _unauthorizedFlow.asSharedFlow()

    fun emitUnauthorized() {
        _unauthorizedFlow.tryEmit(Unit)
    }
}
