package cn.finalteam.rxgalleryfinal.rxbus;

/**
 * @author sunshine
 * @version 1.0
 * @package com.jusfoun.xinpenchengedu.app
 * @date 2018/12/19 10:35
 */
public class RxMessage {
    public static final int LOGIN_CODE = 0X01;
    public static final int LOGOUT_CODE = LOGIN_CODE << 1;
    public static final int PAUSE_CODE = LOGIN_CODE << 2;
    public static final int APPS_CODE = LOGIN_CODE << 3;
    public static final int RESUME_CODE = LOGIN_CODE << 4;
    public static final int AUTH_CODE = RESUME_CODE << 4;
    public static final int UPDATE_USER_CODE = AUTH_CODE << 4;
    public static final int AUTH_CER_CODE = AUTH_CODE << 5;
    private int code;//消息码

    public RxMessage() {
    }

    private Object object;//可能存在的消息体，比如传递值,消息体的大小尽量不要超过1M

    public RxMessage(int code, Object object) {
        this.code = code;
        this.object = object;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    @Override
    public String toString() {
        return "RxMessage{" +
                "code=" + code +
                ", object=" + object +
                '}';
    }
}
