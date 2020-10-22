package net.pwnhub; /**
 * @author hd
 * @create 2020-09-12 9:42
 */

import org.apache.commons.codec.binary.Base64;
import sun.misc.BASE64Decoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletRequest;
import java.util.*;

public class EncryptUtil {

    public static final String KEY = "woshiyigetiancai";
    private static final String ALGORITHMSTR = "AES/ECB/PKCS5Padding";

    public static String base64Encode(byte[] bytes) {
        return Base64.encodeBase64String(bytes);
    }

    public static byte[] base64Decode(String base64Code) throws Exception {
        return new BASE64Decoder().decodeBuffer(base64Code);
    }

    public static byte[] aesEncryptToBytes(String content, String encryptKey) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128);
        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptKey.getBytes(), "AES"));

        return cipher.doFinal(content.getBytes("utf-8"));
    }

    public static String aesEncrypt(String content, String encryptKey) throws Exception {
        return base64Encode(aesEncryptToBytes(content, encryptKey));
    }

    public static String aesDecryptByBytes(byte[] encryptBytes, String decryptKey) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128);

        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(decryptKey.getBytes(), "AES"));
        byte[] decryptBytes = cipher.doFinal(encryptBytes);

        return new String(decryptBytes);
    }

    public static String aesDecrypt(String encryptStr, String decryptKey) throws Exception {
        return aesDecryptByBytes(base64Decode(encryptStr), decryptKey);
    }

    /**
     * 获取request 中的参数，以map形式返回
     *
     * @param request
     * @return
     */
    public static Map<String, Object> getParamMap(ServletRequest request) {
        //Assert.notNull(request,"参数不能为空");
        Map<String, Object> map = new HashMap<>();
        Enumeration<String> en = request.getParameterNames();
        while (en.hasMoreElements()) {
            String name = en.nextElement();
            String[] values = request.getParameterValues(name);
            if (values == null || values.length == 0) {
                continue;
            }
            String value = values[0];
            if (value != null) {
                map.put(name, value);
            }
        }
        return map;
    }

    /**
     * 把数组所有元素，按字母排序，然后按照“参数=参数值”的模式用“&”字符拼接成字符串
     *
     * @param params 需要签名的参数
     * @return 签名的字符串
     */
    public static String createLinkString(Map<String, String> params) {
        /**
         * 拼接字符串参数
         */
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);
        StringBuilder signStr = new StringBuilder();
        for (String key : keys) {
            if (StringUtils.isEmpty(params.get(key).toString())) {
                continue;
            }
            signStr.append(key).append("=").append(params.get(key)).append("&");
        }
        if (signStr.length() > 0) {
            return signStr.deleteCharAt(signStr.length() - 1).toString();
        } else {
            return signStr.deleteCharAt(signStr.length()).toString();
        }
    }

    /**
     * 将请求参数进行加密。
     *
     * @param map 所有请求参数
     * @return
     */
    private static String getChuangyi(Map<String, String> map) throws Exception {
        // 把数组所有元素，按字母排序，然后按照“参数=参数值”的模式用“&”字符拼接成字符串（例如：studentId=4197&token=256966686126630990）
        String str = EncryptUtil.createLinkString(map);
        // AES 加密
        String sign = EncryptUtil.aesEncrypt(str, KEY);
        // MD5 摘要加密
        return Md5Utils.hash(sign);
    }


    /**
     * 测试
     */
    public static void main(String[] args) throws Exception {

        System.out.println("密钥：" + KEY);
        /*
         * 我的个人测试
         * 测试接口
         * 测试接口：/api/s/getMenuListDing
         * 请求参数：studentId=4197&token=256966686126630990
         * 请求头chuangyi参数值：fa0a25f857c61b61790387cd71cc9a99（这个值是前端计算出来传的，然后后端在更具请求参数再计算一次进行对比）
         */
        System.out.println("==================== 我的测试 ====================");
        // chuangyi请求头参数
        String front = "0ea72b1b3cf6901edc3c08d61455e905";

        // 后端加密
        // 请求参数字符串
        String str = "address=浙江省宁波市镇海区明海南路255号靠近公元世家1期&areaType=2&lat=29.924016556734&lng=121.62618231496&qrcodeColor=0&signType=3&signTypeStatus=1&studentId=1416&temperature=36.8&token=260001191325682126&uuid=ff44674550cde8a44ad2a9ba21f22d57";
        // 加密key（最顶上的key值）
        String key = "woshiyigetiancai";
        // AES 加密
        String sign = EncryptUtil.aesEncrypt(str, key);
        // MD5 摘要加密
        sign = Md5Utils.hash(sign);
        // 加密后值
        System.out.println("抓包值：" + front);
        System.out.println("加密后的值：" + sign);

        // 两者是否相等（不相等则拦截）
        System.out.println(sign.equals(front));
    }


}