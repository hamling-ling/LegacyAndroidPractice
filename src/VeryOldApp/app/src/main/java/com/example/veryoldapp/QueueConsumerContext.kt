package com.example.veryoldapp

import java.util.*
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock

open class QueueConsumerContext<T> {
    val queue:Queue<T> = LinkedList<T>()
    val queueLock = ReentrantLock()
    val queueCond: Condition = queueLock.newCondition()
}
