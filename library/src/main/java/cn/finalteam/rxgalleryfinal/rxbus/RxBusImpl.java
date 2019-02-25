package cn.finalteam.rxgalleryfinal.rxbus;

import android.app.Activity;
import android.app.Fragment;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;


/**
 * Desction:
 * Author:pengjianbo  Dujinyang
 * Date:16/7/22 下午2:40
 */
public class RxBusImpl {
    static volatile RxBusImpl mIntance;
    private Subject<Object> subject = PublishSubject.create().toSerialized();

    private Map<String, CompositeDisposable> disposableMap = new HashMap<>();

    private Map<String, Map<String,CompositeDisposable>> disposableTagMap = new HashMap<>();

    public static final String DEFAULT_TAG = "default_tag";

    public static RxBusImpl getInstance() {
        if (mIntance == null) {
            synchronized (RxBusImpl.class) {
                if (mIntance == null) {
                    mIntance = new RxBusImpl();
                }
            }
        }
        return mIntance;
    }

    /**
     * 发送消息
     *
     * @param type
     */
    public void postEvent(Object type) {
        subject.onNext(type);
    }

    /**
     * @param type
     * @param <T>
     * @return
     * @see RxBusImpl#getObservable(Class, int)
     */
    private <T> Flowable<T> getObservable(Class<T> type) {
        return subject.toFlowable(BackpressureStrategy.BUFFER).ofType(type);
    }

    /**
     * 从subject中获取flowable数据流，并根据code进行过滤筛选
     * 只有消息类型为rxmessage并且rxmessage code为code的订阅才可能收到并处理
     *
     * @param type
     * @param eventCode
     * @param <T>
     * @return
     */
    private <T extends RxMessage> Flowable<T> getObservable(Class<T> type, int eventCode) {
        return subject.toFlowable(BackpressureStrategy.BUFFER).ofType(type).filter(eventType -> eventType.getCode() == eventCode);
    }

    private <T> Disposable addEventListener(Class<T> eventType, Consumer<T> consumer) {
        return getObservable(eventType).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);
    }

    /**
     * @param type
     * @param code
     * @param consumer
     * @param <T>
     * @return
     */
    private <T extends RxMessage> Disposable addEventListener(Class<T> type, int code, Consumer<T> consumer) {
        return getObservable(type, code).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);
    }



    /**
     * 只有订阅类型为rxmessage，且消息码为code才可以接收到消息
     *
     * @param type
     * @param code
     * @param consumer
     * @param <T>
     */
    public <T extends RxMessage> void addDispositeListner(Class<T> type, int code, Consumer<T> consumer) {
        Disposable disposable = addEventListener(type, code, consumer);
        addDisposite(consumer, disposable);
    }

    /**
     *
     * 有订阅类型为rxmessage，且消息码为code才可以接收到消息
     * @param type
     * @param tag
     * @param consumer
     * @param <T>
     */
    public <T extends RxMessage> void addDispositeListner(Class<T> type,Object tag, Consumer<T> consumer){
        Disposable disposable = addEventListener(type, consumer);
        addDisposite(consumer, tag,disposable);
    }

    /**
     * 所有参与订阅的只要订阅类型为rxmessage都会收到消息
     *
     * 如果有Activity或者Fragment实现了Consumer的话，
     * 会自动把Activity或者Fragment的hashCode作为tag
     * 便于和Lifecycle框架结合使用
     *
     * @param type
     * @param consumer
     * @param <T>
     */
    public <T extends RxMessage> void addDispositeListner(Class<T> type, Consumer<T> consumer) {
        int aClass = consumer.hashCode();
        if (consumer instanceof Activity || consumer instanceof Fragment || consumer instanceof android.support.v4.app.Fragment) {
            Disposable disposable = addEventListener(type, consumer);
            addDisposite(consumer, consumer, disposable);
        }else{
            Disposable disposable = addEventListener(type, consumer);
            addDisposite(consumer, DEFAULT_TAG, disposable);
        }
    }



    /**
     * 将
     *
     * @param type
     * @param disposable
     */
    private void addDisposite(Object type, Object tag, Disposable disposable) {

        disposableMap = disposableTagMap.get(String.valueOf(tag.hashCode()));

        if (disposableMap == null) {
            disposableMap = new HashMap<>();
        }
        String hashCode = String.valueOf(type.hashCode());
        CompositeDisposable compositeDisposable = disposableMap.get(hashCode);
        if (compositeDisposable != null) {
            compositeDisposable.add(disposable);
        } else {
            compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(disposable);
            disposableMap.put(hashCode, compositeDisposable);
        }
        disposableTagMap.put(String.valueOf(tag.hashCode()),disposableMap);
        disposableMap = null;
    }

    /**
     * 将
     *
     * @param type
     * @param disposable
     */
    private void addDisposite(Object type, Disposable disposable) {
        String hashCode = String.valueOf(type.hashCode());
        CompositeDisposable compositeDisposable = disposableMap.get(hashCode);
        if (compositeDisposable != null) {
            compositeDisposable.add(disposable);
        } else {
            compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(disposable);
            disposableMap.put(hashCode, compositeDisposable);
        }
    }

    public void unscribe(Object tag){
        unscribe(tag,null);
    }

    public <T extends RxMessage> void unscribe(Class<T> tag){
        unscribe(null,tag);
    }
    /**
     * 取消当前界面的订阅
     * 有tag,eventType为null时取消该tag下的所有订阅
     */
    public <T> void unscribe(Object tag,Class<T> eventType) {

        if (tag == null){
           disposableMap = disposableTagMap.get(DEFAULT_TAG);
        }else{
            disposableMap = disposableTagMap.get(String.valueOf(tag.hashCode()));
        }

        if (disposableMap == null){
            return;
        }

        if (eventType == null){
            Set<String> keys = disposableMap.keySet();
            if (keys == null){
                return;
            }
            for (String key : keys) {
                CompositeDisposable compositeDisposable = disposableMap.get(key);
                if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
                    compositeDisposable.dispose();//取消下游消息的接收
                }
            }
            disposableMap.clear();
            return;
        }
        String hashCode = String.valueOf(eventType.hashCode());
        CompositeDisposable compositeDisposable = disposableMap.get(hashCode);
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();//取消下游消息的接收
            disposableMap.remove(hashCode);//移除此次缓存
        }
    }

}
