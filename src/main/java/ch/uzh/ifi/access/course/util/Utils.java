package ch.uzh.ifi.access.course.util;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class Utils {
    public static final boolean SIMPLE_UUID = true;
    private static AtomicLong counter = new AtomicLong();

    public String getID(){
        if(SIMPLE_UUID){
            return Long.toString(counter.getAndAdd(1));
        }else{
            return UUID.randomUUID().toString();
        }
    }

    public String getID(String s){
        return UUID.nameUUIDFromBytes(s.getBytes()).toString();
    }
}
