package com.example.hellostatemachine.statemachine

import java.util.concurrent.locks.ReentrantLock


// ステートマシンの実装
class StateMachine<T>(private val _stateMachineDef:StateMachineDefinition<T>) {

    companion object {
        const val INITIAL_STATE_NAME = "initial"
    }

    private var _currentState: State<T>
    private val _lock = ReentrantLock()

    val currentStateName: String
        get() = synchronized(_lock) { _currentState.name }

    init {
        _currentState = _stateMachineDef["initial"]!!
    }

    // イベントを処理して状態遷移
    fun processEvent(event: String) {
        synchronized(_lock) {
            _currentState.transitions[event]?.let { transition ->
                transition.param?.let { transition.action?.invoke(it) }
                _stateMachineDef[transition.nextStateName]?.let { nextState ->
                    _currentState = nextState
                }
            }
        }
    }
}
