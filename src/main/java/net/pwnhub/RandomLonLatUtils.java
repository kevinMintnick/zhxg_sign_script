package net.pwnhub;

import java.math.BigDecimal;
import java.util.Random;

public class RandomLonLatUtils {
    /**
     * @Title: randomLonLat
     * @Description: 在矩形内随机生成经纬度
     * @param MinLon：最新经度  MaxLon： 最大经度   MinLat：最新纬度   MaxLat：最大纬度    type：设置返回经度还是纬度
     * @return
     * @throws
     */
    public static String randomLonLat(double MinLon, double MaxLon, double MinLat, double MaxLat, String type) {
        Random random = new Random();
        BigDecimal db = new BigDecimal(Math.random() * (MaxLon - MinLon) + MinLon);
        String lat = db.setScale(12, BigDecimal.ROUND_HALF_UP).toString();// 小数后6位
        db = new BigDecimal(Math.random() * (MaxLat - MinLat) + MinLat);
        String lng = db.setScale(11, BigDecimal.ROUND_HALF_UP).toString();
        String result = "Lat".equals(type)?lat:lng;
        return result;
    }
}
