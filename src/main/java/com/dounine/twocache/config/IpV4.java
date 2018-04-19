package com.dounine.twocache.config;

import java.net.Inet4Address;
import java.net.UnknownHostException;

public final class IpV4 {
    private static String node;
    static {
        try {
            node = Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
    public static final String get(){
        return node;
    }
}
