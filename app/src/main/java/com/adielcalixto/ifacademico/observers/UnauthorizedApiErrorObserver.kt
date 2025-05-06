package com.adielcalixto.ifacademico.observers

class UnauthorizedApiErrorObserver {
    private val _subscribers = arrayListOf<UnauthorizedApiErrorSubscriber>()

    fun notifyError() {
        _subscribers.map { it.onUnauthorizedError() }
    }

    fun subscribe(subscriber: UnauthorizedApiErrorSubscriber) {
        _subscribers.add(subscriber)
    }
}