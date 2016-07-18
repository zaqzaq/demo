package cn.freshz.demo.thread;

/**
 * 演示 CPU 指令重排序(happen-before)
 */
public class ReOrderTest {
	public static void main(String[] args) {
		while (!Thread.currentThread().isInterrupted()) {
			final VolatileSample2 vs2 = new VolatileSample2();
			final Thread w = new Thread(){
				public void run() {
					vs2.writer();
				}
			};

			final Thread r = new Thread(){
				public void run() {
					vs2.reader();
				}
			};
			r.start();
			w.start();
			new Thread(){
				public void run() {
					try {
						w.join();
						r.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					//指令重排的后果
					if (vs2.result.equals("x=0,v=true")) {
						System.out.println(this.getName() + " " + vs2.result);
						System.exit(0);
					}
				}
			}.start();

			Thread.yield();
		}
	}
}

class VolatileSample2 {
	int x = 0;
	volatile boolean v = false;
	String result;

	public void writer() {
		x = 42;				//第1步
		v = true;			//第2步
							//则单线程中不会出现 v为true时 x为0
	}

	public void reader() {
		result = "x=" + x + ",v=" + v;
		//result = "v=" + v + ",x=" + x;
	}

}
