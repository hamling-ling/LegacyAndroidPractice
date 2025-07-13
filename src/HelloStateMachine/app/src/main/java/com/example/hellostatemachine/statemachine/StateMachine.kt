package com.example.hellostatemachine.statemachine

import java.util.concurrent.locks.ReentrantLock


// ステートマシンの実装
class StateMachine<T>(private val _states: Map<String,State<T>>) {

    private var _currentState: State<T>
    private val _lock = ReentrantLock()

    val currentStateName: String
        get() = synchronized(_lock) { _currentState.name }

    init {
        _currentState = _states["initial"]!!
    }

    // イベントを処理して状態遷移
    fun processEvent(event: String) {
        synchronized(_lock) {
            val transition = _currentState.transitions[event]
            transition?.param?.let { transition.action?.invoke(it) }
            _currentState = _states[transition?.nextStateName]!!
        }
    }
}
