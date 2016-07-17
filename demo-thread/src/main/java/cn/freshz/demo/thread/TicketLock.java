package com.alipay.titan.dcc.dal.entity;

import java.util.concurrent.atomic.AtomicInteger;
/**
*Ticket锁主要解决的是访问顺序的问题，主要的问题是在多核cpu上  ,顺序 公平  锁
*/
public class TicketLock {
    private AtomicInteger                     serviceNum = new AtomicInteger();
    private AtomicInteger                     ticketNum  = new AtomicInteger();
    private static final ThreadLocal<Integer> LOCAL      = new ThreadLocal<Integer>();

    public void lock() {
        int myticket = ticketNum.getAndIncrement();
        LOCAL.set(myticket);
        while (myticket != serviceNum.get()) {
        }

    }

    public void unlock() {
        int myticket = LOCAL.get();
        serviceNum.compareAndSet(myticket, myticket + 1);
    }
}
