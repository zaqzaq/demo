package com.alipay.titan.dcc.dal.entity;

import java.util.concurrent.atomic.AtomicInteger;
/**
*Ticket锁主要解决的是访问顺序的问题，主要的问题是在多核cpu上  ,顺序 公平  锁
* 
* 每次都要查询一个serviceNum 服务号，影响性能（必须要到主内存读取，并阻止其他cpu修改）。
*/
public class TicketLock {
    private AtomicInteger                     serviceNum = new AtomicInteger(); // 服务号
    private AtomicInteger                     ticketNum  = new AtomicInteger(); // 排队号
    private static final ThreadLocal<Integer> LOCAL      = new ThreadLocal<Integer>();

    public void lock() {
        // 首先原子性地获得一个排队号
        int myticket = ticketNum.getAndIncrement();
        LOCAL.set(myticket);
         // 只要当前服务号不是自己的就不断轮询
        while (myticket != serviceNum.get()) {
        }

    }

    public void unlock() {
        int myticket = LOCAL.get();
        // 只有当前线程拥有者才能释放锁
        serviceNum.compareAndSet(myticket, myticket + 1);
    }
}
