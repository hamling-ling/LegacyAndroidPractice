import com.example.veryoldapp.QueueConsumer
import java.util.concurrent.locks.Condition

/**
 * 送信サンプル
 */
class MessageSender: QueueConsumer<Command>() {

    protected val lock = ReentrantLock()
    private val cond: Condition = lock.newCondition()

    onAckNack() {
    }

    onError() {
    }

    fun onCancel() {
        lock.withLock {
            isCanceled = true
            breakAwait()
        }
    }

    open fun consume(param: T):CommandResult {
        lock.withLock {
            // 実行
            send()

            // レスポンス待ち
            if (awaitSignal(timeoutMs)) {
                if(!isCanceled) {
                    // レスポンス受信結果を返却
                    createExecutedResult()
                } else {
                    // コマンドキャンセル結果を返却
                    createCanceledResult()
                }
            } else {
                // 失敗(タイムアウト)を返却
                createTimeoutResult()
            }
        }
    }

    private fun awaitSignal(timeoutMs: Long = 10000) : Boolean {
        while(!isOperatedBreak) {
            if(!cond.await(timeoutMs, TimeUnit.MILLISECONDS)) {
                // timeout
                return false
            } else {
                if(!isOperatedBreak) {
                    Log.w(TAG, "$deviceName sprious wakeup! for $deviceName command await")
                }
            }
        }
        return true
    }
}

