package com.qcloud.qclib.rxbus

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.internal.functions.ObjectHelper
import io.reactivex.subjects.PublishSubject
import java.lang.reflect.Modifier
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet

/**
 * 类说明：RxJava实现事件总线(Event Bus)
 *          // 注册 Bus mEventBus = BusProvider.getInstance();
 *                  mEventBus.register(this)
 *          // 取消注册 mEventBus.unregister(this);
 *                     mEventBus = null;
 *          // 事件发送BusProvider.getInstance().post(RxBusEvent.newBuilder(R.id.hello).setObj("Hello rxBus").build());
 *          // 事件接收 mEventBus.registerSubscriber(this, mEventBus.obtainSubscriber(RxBusEvent.class, new Consumer<RxBusEvent>() {
 *                           @Override
 *                           public void accept(@NonNull RxBusEvent rxBusEvent) throws Exception {
 *
 *                           }
 *                       }));
 * Author: Kuzan
 * Date: 2018/1/9 13:44.
 */
class RxBus: Bus {
    private val observers = ConcurrentHashMap<Class<*>, CompositeDisposable>()
    private val subscribers = ConcurrentHashMap<Class<*>, CopyOnWriteArraySet<CustomSubscriber<*>>>()
    private val bus = PublishSubject.create<Any>().toSerialized()

    /**
     * 注册事件
     *
     * @param observer 监听者，任何对象
     * @throws IllegalArgumentException
     *
     * @time 2018/1/9 14:09
     */
    override fun register(@NonNull observer: Any) {
        ObjectHelper.requireNonNull(observer, "Observer to register must not be null.")
        val observerClass = observer.javaClass
        if (observers.putIfAbsent(observerClass, CompositeDisposable()) != null) {
            throw IllegalArgumentException("Observer has already been registered.")
        }

        val composite = observers[observerClass]
        val events = HashSet<Class<*>>()

        for (method in observerClass.declaredMethods) {
            if (method.isBridge || method.isSynthetic) {
                continue
            }
            if (!method.isAnnotationPresent(Subscribe::class.java)) {
                continue
            }
            val mod = method.modifiers
            if (Modifier.isStatic(mod) || !Modifier.isPublic(mod)) {
                throw IllegalArgumentException("Method " + method.name +
                        " has @Subscribe annotation must be public, non-static")
            }
            val params = method.parameterTypes
            if (params.size != 1) {
                throw IllegalArgumentException("Method " + method.name +
                        " has @Subscribe annotation must require a single argument")
            }
            val eventClass = params[0]
            if (eventClass.isInterface) {
                throw IllegalArgumentException("Event class must be on a concrete class type.")
            }
            if (!events.add(eventClass)) {
                throw IllegalArgumentException("Subscriber for " + eventClass.simpleName +
                        " has already been registered.")
            }
            composite?.add(bus.ofType(eventClass)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(AnnotatedSubscriber<Any>(observer, method)))
        }
    }

    /**
     * 获得监听者
     *
     * @param eventClass
     * @param receiver
     *
     * @time 2018/1/9 14:10
     */
    override fun <T: Any> obtainSubscriber(@NonNull eventClass: Class<T>, @NonNull receiver: Consumer<T>): CustomSubscriber<T> {
        ObjectHelper.requireNonNull(eventClass, "Event class must not be null.")
        if (eventClass.isInterface) {
            throw IllegalArgumentException("Event class must be on a concrete class type.")
        }
        ObjectHelper.requireNonNull(receiver, "Receiver must not be null.")
        return CustomSubscriber(eventClass, receiver)
    }

    /**
     * 接收事件，一般放在需要通知的页面里
     *
     * @param observer 监听事件，可以是所有对象
     * @param subscriber
     *
     * @time 2018/1/9 14:10
     */
    override fun <T : Any> registerSubscriber(@NonNull observer: Any, @NonNull subscriber: CustomSubscriber<T>) {
        ObjectHelper.requireNonNull(observer, "Observer to register must not be null.")
        ObjectHelper.requireNonNull(subscriber, "Subscriber to register must not be null.")

        subscribers.putIfAbsent(observer.javaClass, CopyOnWriteArraySet())
        val ss = subscribers[observer.javaClass]
        if (ss != null) {
            if (ss.contains(subscriber)) {
                throw IllegalArgumentException("Subscriber has already been registered.")
            } else {
                ss.add(subscriber)
            }
        }

        val observable = bus.ofType(subscriber.eventClass)
                .observeOn(if (subscriber.scheduler == null) AndroidSchedulers.mainThread() else subscriber.scheduler)
        val observerClass = observer.javaClass
        observers.putIfAbsent(observerClass, CompositeDisposable())
        val composite = observers[observerClass]
        composite?.add((if (subscriber.filter == null) observable else observable.filter(subscriber.filter)).subscribe(subscriber))
    }

    /**
     * 解除注册
     *
     * @throws IllegalArgumentException
     *
     * @time 2018/1/9 14:22
     */
    override fun unregister(@NonNull observer: Any) {
        ObjectHelper.requireNonNull(observer, "Observer to unregister must not be null.")
        val composite = observers[observer.javaClass]
        ObjectHelper.requireNonNull(composite, "Missing observer, it was registered?")
        composite?.dispose()
        observers.remove(observer.javaClass)
        
        val ss = subscribers[observer.javaClass]
        if (ss != null) {
            ss.clear()
            subscribers.remove(observer.javaClass)
        }
    }

    /**
     * 发送事件
     *
     * @param event 发送的事件，可以是任何对象
     *
     * @time 2018/1/9 14:27
     */
    override fun post(@NonNull event: Any) {
        ObjectHelper.requireNonNull(event, "Event must not be null.")
        bus.onNext(event)
    }
}