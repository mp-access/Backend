package ch.uzh.ifi.access.course.util;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class Utils {
    private static AtomicLong counter = new AtomicLong();

    // TODO: This should only be used by testing code NOT production
    public String getID(){
        return UUID.randomUUID().toString();
    }

    public String getID(String s){
        return UUID.nameUUIDFromBytes(s.getBytes()).toString();
    }
}
