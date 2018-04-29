package com.webatron.rakesh.muzic;

/**
 * Created by rakesh on 7/2/18.
 */

public class Constants {
    public interface ACTION {
        public static String MAIN_ACTION = "com.webatron.rakesh.muzic.action.main";
        public static String PREV_ACTION = "com.webatron.rakesh.muzic.action.previous";
        public static String PLAY_ACTION = "com.webatron.rakesh.muzic.action.play";
        public static String NEXT_ACTION = "com.webatron.rakesh.muzic.action.next";
        public static String STOP_ACTION = "com.webatron.rakesh,muzic.action.stop";
        public static String PLAYPAUSE_ACTION = "com.webatron.rakesh,muzic.action.playpause";
        public static String STARTFOREGROUND_ACTION = "com.webatron.rakesh.muzic.action.startforeground";
        public static String STOPFOREGROUND_ACTION = "com.webatron.rakesh.muzic.action.stopforeground";
    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }
}
