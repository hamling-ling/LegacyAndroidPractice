package com.example.hellostatemachine.statemachine

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.runBlocking
import java.util.concurrent.locks.ReentrantLock


// ステートマシンの実装
class StateMachine<T>(
    private val _stateMachineDef:StateMachineDefinition<T>,
    private val _dispatcher: CoroutineDispatcher
) {

    companion object {
        const val INITIAL_STATE_NAME = "initial"
        const val TIMEOUT_EVENT_NAME = "timeout"
    }

    private var _parents = mutableListOf<State<T>>()
    private var _currentState: State<T> = trackDeepestFirstState(_stateMachineDef[INITIAL_STATE_NAME]!!, _parents)
    private val _lock = ReentrantLock()
    private val _timer = OneShotTimer(_dispatcher)

    val currentStateName: String
        get() = synchronized(_lock) { _currentState.name }

    val currentStateFullName: String
        get() = synchronized(_lock) {
            _parents.joinToString(".") { it.name } + "." + _currentState.name
        }

    // Process Event then Transit State
    fun processEvent(event: String) {
        synchronized(_lock) {
            // Cancel timer
            runBlocking { _timer.cancel() }

            findStateWithTransition(_currentState, _parents, event)?.let { stateParentPair ->
                val state = stateParentPair.first
                val parents = stateParentPair.second

                val transition = state[event]!!
                transition.action?.invoke(transition.param)

                findNextState(parents, transition.nextStateName)?.let { nextStateParentsPair ->
                    val nextState = nextStateParentsPair.first
                    val nextParents = nextStateParentsPair.second

                    _currentState = nextState
                    _parents = nextParents.toMutableList()

                    // Set timeout timer
                    _currentState.timeoutSec?.let {
                        runBlocking { _timer.start(it * 1000) {
                            processEvent(TIMEOUT_EVENT_NAME)
                        } }
                    }
                }
            }
        }
    }

    private fun trackDeepestFirstState(state: State<T>, parents: MutableList<State<T>>) : State<T> {
        return if(state.childrenList.isEmpty()) {
            return state
        } else {
            parents.add(state)
            trackDeepestFirstState(state.childrenList.first(), parents)
        }
    }

    private fun findStateWithTransition(state: State<T>, parents: List<State<T>>, eventName: String)
    : Pair<State<T>, List<State<T>>>? {
        var foundState: State<T>? = null
        if(state.transitions.containsKey(eventName)) {
            foundState = state
            return Pair(foundState, parents)
        }

        // Search Parent → Parent's brothers/sisters → Grandparent ...
        val nextStateParents = parents.toMutableList()
        run breaking@{
            parents.reversed().forEach() {
                if (it.transitions.containsKey(eventName)) {
                    foundState = it
                    return@breaking
                }
                nextStateParents.removeAt(nextStateParents.lastIndex)
            }
        }

        if(foundState != null) {
            return Pair(foundState!!, nextStateParents)
        }

        // Search top level states if none is found
        foundState = _stateMachineDef.states.find { it.transitions.containsKey(eventName) }
        if(foundState == null) {
            return null
        }

        return Pair(foundState!!, nextStateParents)
    }

    private fun findNextState(parents: List<State<T>>, nextStateName: String)
    : Pair<State<T>, List<State<T>>>?
    {
        // Found next State
        var nextState: State<T>? = null
        // Found next State's ancestors
        val tempNextParent = parents.toMutableList()

        // Search state's brothers/sisters -> parent -> parent brothers/sisters -> ...
        while (tempNextParent.size > 0) {
            // check parent
            if(tempNextParent.last().name == nextStateName) {
                nextState = tempNextParent.last()
                break
            }
            // check parent's brothers/sisters
            if(tempNextParent.last().children.containsKey(nextStateName)) {
                nextState = tempNextParent.last().children[nextStateName]
                break
            }
            // Go up 1 level
            tempNextParent.removeAt(tempNextParent.lastIndex)
        }

        // None is find, check for top level states
        if(nextState == null && tempNextParent.size == 0) {
            nextState = _stateMachineDef.states.find { it.name == nextStateName }
        }

        // If found、try to get deepest first state
        if(nextState != null) {
            nextState = trackDeepestFirstState(nextState, tempNextParent)
            return Pair(nextState, tempNextParent)
        }
        return null
    }
}
