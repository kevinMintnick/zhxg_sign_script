package net.pwnhub;

import java.util.UUID;

public class UuidUtils {
    public static String getUuid(){
        UUID uuid = UUID.randomUUID();
        String uuidStr =  uuid.toString();
        uuidStr = uuidStr.replace("-", "");
        return uuidStr;
    }
}
