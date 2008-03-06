package com.jbidwatcher.util.queue;

/*
 * Copyright (c) 2000-2007, CyberFOX Software, Inc. All Rights Reserved.
 *
 * Developed by mrs (Morgan Schweers)
 */

/**
 * @file   TimerHandler.java
 * @author Morgan Schweers <cyberfox@users.sourceforge.net>
 * @date   Sat Oct 12 18:42:10 2002
 * 
 * @brief  Several operations need 'regular wakeups', which is what this class provides.
 * 
 * 
 */

/*!@class TimerHandler
 *
 * @brief Provides once-a-second callbacks to classes that extend it.
 *
 */

public class TimerHandler extends Thread {
  private static final int ALMOST_A_SECOND = 990;
  private WakeupProcess _toWake = null;
  private volatile boolean _remainAsleep = false;
  private long _sleep_ms = ALMOST_A_SECOND;

  public interface WakeupProcess {
    boolean check();
  }

  public TimerHandler(WakeupProcess inWake, long sleeptime) {
    _toWake = inWake;
    _sleep_ms = sleeptime;
  }

  public TimerHandler(WakeupProcess inWake) {
    _toWake = inWake;
  }

  public void pause() { _remainAsleep = true; }
  public void unpause() { _remainAsleep = false; }
  public boolean isPaused() { return _remainAsleep; }

  public void run() {
    boolean interrupted = false;
    while(!interrupted) {
      try {
        sleep(_sleep_ms);
        if(!_remainAsleep) {
            _toWake.check();
        }
      } catch(InterruptedException ignored) {
        interrupted = true;
      } catch(Exception e) {
          com.jbidwatcher.util.config.ErrorManagement.handleException("Exception during the check() operation of " + _toWake.getClass().toString(), e);
      } catch(Error e) {
        //  This is more sketchy...
        com.jbidwatcher.util.config.ErrorManagement.handleException("Serious error, consider dying.", e);
      }
    }
  }
}