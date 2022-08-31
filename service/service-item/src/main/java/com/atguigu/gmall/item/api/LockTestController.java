package com.atguigu.gmall.item.api;


import com.atguigu.gmall.common.result.Result;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@RequestMapping("/lock")
@RestController
public class LockTestController {

    @Autowired
    StringRedisTemplate redisTemplate;

//    ReentrantLock lock = new ReentrantLock();



    @Resource
    RedissonClient redissonClient;

    int  i = 0;

    //闭锁
    @GetMapping("/longzhu")
    public Result shoujilongzhu(){

        RCountDownLatch latch = redissonClient.getCountDownLatch("sl-lock");
        latch.countDown();
        return Result.ok("收集到1颗");
    }

   //神龙
    @GetMapping("/shenlong")
    public Result shenlong() throws InterruptedException {
        RCountDownLatch latch = redissonClient.getCountDownLatch("sl-lock");
        latch.trySetCount(7); //设置数量


        latch.await(); //等待
        return Result.ok("神龙 来了....");
    }



 //读写锁
    @GetMapping("/rw/write")
    public Result readWriteValue() throws InterruptedException {
        //1、拿到读写锁
        RReadWriteLock lock = redissonClient.getReadWriteLock("rw-lock");

        //2、获取到写锁
        RLock rLock = lock.writeLock();

        //加写锁
        rLock.lock();
        //业务正在改数据
        Thread.sleep(20000);
        i = 888;

        rLock.unlock();

        return Result.ok();
    }
  //读写锁读数据
    @GetMapping("/rw/read")
    public Result readValue(){

        //1、拿到读写锁
        RReadWriteLock lock = redissonClient.getReadWriteLock("rw-lock");

        RLock rLock = lock.readLock();
        rLock.lock();
        int x = i;
        rLock.unlock();
        return Result.ok(x);
    }




    @GetMapping("/common")
    public Result redissonLock() throws InterruptedException {
        //名字相同就代表同一把锁
        //1、得到一把锁
        RLock lock = redissonClient.getLock("lock-hello");//获取一个普通的锁；可重入锁
        //2、加锁

//        lock.lock(10,TimeUnit.SECONDS);
        lock.lock(); //阻塞式加锁，非要等到锁。默认30s的过期时间

        System.out.println("拿到锁");
        //执行业务逻辑
        Thread.sleep(5000);
        System.out.println("执行结束");
        //3、解锁
        lock.unlock();



        return Result.ok();
    }




}
