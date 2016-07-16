
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

public final class ContextSwitchTest {
	static final int RUNS = 3;
	static final int ITERATES = 1000000;
	static AtomicReference turn = new AtomicReference();

	static final class WorkerThread extends Thread {
		volatile Thread other;
		volatile int nparks;

		public void run() {
			final AtomicReference t = turn;
			final Thread other = this.other;
			if (turn == null || other == null)
				throw new NullPointerException();
			int p = 0;
			for (int i = 0; i < ITERATES; ++i) {
				while (!t.compareAndSet(other, this)) {
					LockSupport.park();
					++p;
				}
				LockSupport.unpark(other);
			}
			LockSupport.unpark(other);
			nparks = p;
			System.out.println("parks: " + p);

		}
	}

	static void test() throws Exception {
		WorkerThread a = new WorkerThread();
		WorkerThread b = new WorkerThread();
		a.other = b;
		b.other = a;
		turn.set(a);
		long startTime = System.nanoTime();
		a.start();
		b.start();
		a.join();
		b.join();
		long endTime = System.nanoTime();
		int parkNum = a.nparks + b.nparks;
		System.out.println("Average time: " + ((endTime - startTime) / parkNum)
				+ "ns");
	}

	public static void main(String[] args) throws Exception {
		for (int i = 0; i < RUNS; i++) {
			test();
		}
	}
}

/**
引起上下文切换的原因大概有以下几种:
1. 当前执行任务的时间片用完之后, 系统CPU正常调度下一个任务 
2. 当前执行任务碰到IO阻塞, 调度器将挂起此任务, 继续下一任务 
3. 多个任务抢占锁资源, 当前任务没有抢到,被调度器挂起, 继续下一任务 
4. 用户代码挂起当前任务, 让出CPU时间 
5. 硬件中断
*/
