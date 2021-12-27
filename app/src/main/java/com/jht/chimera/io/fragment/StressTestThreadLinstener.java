package com.jht.chimera.io.fragment;

import java.sql.Struct;

public interface StressTestThreadLinstener {

    enum Level {
        COMPLETE,
        STOP
    }
    void onFinishLinstener(Level level);
    void updateTxtViewLinstener(String txt);
}
