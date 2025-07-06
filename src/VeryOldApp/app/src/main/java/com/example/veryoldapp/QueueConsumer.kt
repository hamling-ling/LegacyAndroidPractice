package com.example.veryoldapp


import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.withLock

/**
 * シングルスレッドでキューを順に処理する
 */
abstract class QueueConsumer<T> : IterativeRunnable<QueueConsumerContext<T>>() {

    /**
     * QueueConsumer が取り扱うデータをまとめたもの
     */
    override val ctx = QueueConsumerContext<T>()

    /**
     * 明示的に ctx.queueCond が signal されたのか
     * sprious wakeup が起きたのかを見分ける用。
     */
    private var isEnqueueSignaled = AtomicBoolean(false)

    /**
     * 親クラスから何度も呼ばれる処理
     * 待機しなければ busy loop になるので注意
     */
    override fun iterativeFunc(userData: QueueConsumerContext<T>) {
        val item: T?
        ctx.queueLock.withLock {
            if(shouldStop()) {
                return
            }

            if(ctx.queue.isEmpty()) {
                // enqueue を無期限待機
                waitForEnqueue()
                return // 次に iterativeFunc() が呼ばれて処理される
            }

            item = ctx.queue.poll()
        }

        consume(item!!)
    }

    /**
     * データをキューに追加する。
     */
    open fun enqueue(item: T) {
        ctx.queueLock.withLock {
            ctx.queue.offer(item)
            breakEnqueueWait()
        }
    }

    /**
     * スレッド停止処理開始
     * 終了待は 本 Runnable を駆動する thread で join すること。
     */
    override fun stop() {
        // 親クラスの停止フラグを立てる
        super.stop()

        ctx.queueLock.withLock {
            breakEnqueueWait()
        }
    }

    /**
     * キューから出したアイテムを処理
     */
    abstract fun consume(item: T)

    /**
     * enqueue() が呼ばれるまで待機
     * lock の中から呼ぶこと
     */
    private fun waitForEnqueue() {
        while(!isEnqueueSignaled.get()) {
            ctx.queueCond.await()
        }
        // 元に戻しておく
        isEnqueueSignaled.set(false)
    }

    /**
     * enqueue() 待ちを解除
     * Lock の中から呼ぶこと
     */
    private fun breakEnqueueWait() {
        isEnqueueSignaled.set(true)
        ctx.queueCond.signal()
    }
}
