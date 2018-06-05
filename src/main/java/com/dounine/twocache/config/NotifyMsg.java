package com.dounine.twocache.config;

import java.io.Serializable;

public class NotifyMsg implements Serializable {
    private NotifyType notifyType;
    private String cacheName;
    private String node;
    private Object key;
    private Object result;
    public NotifyMsg(){}
    public NotifyMsg(NotifyType notifyType,String node,Object key,Object result){
        this.node = node;
        this.notifyType = notifyType;
        this.key = key;
        this.result = result;
    }

    public NotifyType getNotifyType() {
        return notifyType;
    }

    public void setNotifyType(NotifyType notifyType) {
        this.notifyType = notifyType;
    }

    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public Object getKey() {
        return key;
    }

    public void setKey(Object key) {
        this.key = key;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
