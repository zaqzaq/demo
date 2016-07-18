package cn.freshz.demo.thread;

/**
 * 演示 valatitle 关键字
 */
public class VolatileExample extends Thread {
    //设置类静态变量,各线程访问这同一共享变量
    private static boolean flag = false;

    //无限循环,等待flag变为true时才跳出循环
    public void run() {
        while (!flag) {
            //System.out.println(123);
        }
    }

    public static void main(String[] args) throws Exception {
        new VolatileExample().start();
        //sleep的目的是等待线程启动完毕,也就是说进入run的无限循环体了
        Thread.sleep(100);
        flag = true;
    }
}

/**
 * 在计算机中，软件技术和硬件技术有一个共同的目标：在不改变程序执行结果的前提下，尽可能的开发并行度。
 * 编译器和处理器遵从这一目标，从happens- before的定义我们可以看出，JMM同样遵从这一目标
 */

/**
 * 并发专家建议我们远离它，尤其是在JDK6的synchronized关键字的性能被大幅优化之后，更是几乎没有使用它的场景
 *
 * 只有在对变量读取频率很高的情况下，虚拟机才不会及时回写主内存，而当频率没有达到虚拟机认为的高频率时，
 * 普通变量和volatile是同样的处理逻辑。如在每个循环中执行System.out.println(1)加大了读取变量的时间间隔，
 * 使虚拟机认为读取频率并不那么高，所以实现了和volatile的效果
 * 
 * -Xcomp  -Xint
 * “mixed mode”就表示混合模式。在混合模式中，部分函数会被解释执行，部分可能被编译执行。
 * 虚拟机决定函数是否需要编译执行的依据是判断该函数，是否为热点代码。
 * 如果函数的调用频率很高，被反复使用，那么就会被认为是热点，热点代码就会被编译执行。
 * 
 * 查看JIT编译结果
 * -XX:+UnlockDiagnosticVMOptions -XX:PrintAssemblyOptions=hsdis-print-bytes -XX:CompileCommand=print,*VolatileExample.run
 * 
 * volatile的效果在jdk1.2及之前很容易重现，但随着虚拟机的不断优化，如今的普通变量的可见性已经不是那么严重的问题了，
 * 这也是volatile如今确实不太有使用场景
 */

/**
 * volatile的原理和实现机制

 　　前面讲述了源于volatile关键字的一些使用，下面我们来探讨一下volatile到底如何保证可见性和禁止指令重排序的。

 　　下面这段话摘自《深入理解Java虚拟机》：

 　　“观察加入volatile关键字和没有加入volatile关键字时所生成的汇编代码发现，加入volatile关键字时，会多出一个lock前缀指令”

 　　lock前缀指令实际上相当于一个内存屏障（也成内存栅栏），内存屏障会提供3个功能：

 　　1）它确保指令重排序时不会把其后面的指令排到内存屏障之前的位置，也不会把前面的指令排到内存屏障的后面；即在执行到内存屏障这句指令时，在它前面的操作已经全部完成；

 　　2）它会强制将对缓存的修改操作立即写入主存；

 　　3）如果是写操作，它会导致其他CPU中对应的缓存行无效。
 */
/**
 * http://blog.csdn.net/wxwzy738/article/details/43238089
 * http://jiangzhengjun.iteye.com/blog/652532
 */
