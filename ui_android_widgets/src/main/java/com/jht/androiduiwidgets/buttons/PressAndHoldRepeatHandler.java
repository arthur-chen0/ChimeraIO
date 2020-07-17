package com.jht.androiduiwidgets.buttons;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Handles press and hold logic for certain buttons in the system. The press and hold behavior is
 * on press, immediately increment. If held for 500 ms then increment 10x per second until released
 * or the maximum number of increments are achieved.
 */
@SuppressWarnings("WeakerAccess")
public class PressAndHoldRepeatHandler {
    private Timer mHoldTimer = null;
    private int mNumPresses = 0;

    // Maximum presses
    private int mMaxPresses = -1;

    // Callback when to simulate clicks.
    private IOnClick onClick;

    public interface IOnClick {
        void onClick();
    }

    /**
     * Default constructor using the default timings.  No max presses.
     *
     * @param onClick Callback when the press and hold fires an event.
     */
    public PressAndHoldRepeatHandler(IOnClick onClick) {
        init(onClick, mMaxPresses);
    }

    /**
     * Constructor that sets the maximum presses.
     *
     * @param onClick    Callback when the press and hold fires an event.
     * @param maxPresses Maximum presses during a press and hold event.
     */
    public PressAndHoldRepeatHandler(IOnClick onClick, int maxPresses) {
        init(onClick, maxPresses);
    }

    /**
     * Initialize the timer.
     *
     * @param onClick        Callback when the press and hold fires an event.
     * @param maxPresses     Maximum presses during a press and hold event.
     */
    private void init(final IOnClick onClick, int maxPresses) {
        this.onClick = onClick;
        this.mMaxPresses = maxPresses;
    }

    /**
     * We have a press event.  Start the timer and send the first event.
     */
    public void start() {
        mNumPresses = 1;
        if (onClick != null) {
            onClick.onClick();
        }
        setHoldTimer();
    }

    /**
     * We have a release event.  Stop the timers.
     */
    public void stop() {
        stopHoldTimer();
    }

    /**
     * Set the number of maximum presses.
     *
     * @param maxPresses Maximum number of presses for a press and hold event.
     */
    public void setMaxPresses(int maxPresses) {
        this.mMaxPresses = maxPresses;
    }

    /**
     * Set the on click programmatically.
     *
     * @param onClick The onclick event.
     */
    public void setOnClick(IOnClick onClick) {
        this.onClick = onClick;
    }

    private void setHoldTimer() {
        stopHoldTimer();

        // Create timer and schedule increment task
        mHoldTimer = new Timer();
        mHoldTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Send event to trigger increment logic
                mNumPresses++;
                if (onClick != null) onClick.onClick();

                if (mMaxPresses > 0 && mNumPresses >= mMaxPresses) {
                    stopHoldTimer();
                }
            }
        }, 500, 100);
    }

    private void stopHoldTimer() {
        if (mHoldTimer != null) {
            mHoldTimer.cancel();
            mHoldTimer = null;
        }
    }
}
