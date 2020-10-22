package net.pwnhub;

/**
 * 格式化,保留两位小数。
 */
public class FormatTemperature {

    public static String formateRate(String rateStr) {
        if (rateStr.contains(".")) {
            // 获取小数点的位置
            int num = 0;
            num = rateStr.indexOf(".");

            String dianAfter = rateStr.substring(0, num + 1);
            String afterData = rateStr.replace(dianAfter, "");

            return rateStr.substring(0, num) + "." + afterData.substring(0, 1);
        } else {
            String result = rateStr == "1"?"100":rateStr;
            return result;
        }
    }
}
