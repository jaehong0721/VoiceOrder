package com.rena21c.voiceorder.network;

import java.io.IOException;

/**
 * 인터넷이 연결되지 않은 상태에서 발생시키는 오류
 */
public class NoConnectivityException extends IOException {
    @Override public String getMessage() {
        return "No connectivity exception";
    }
}
