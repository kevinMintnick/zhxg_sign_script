package net.pwnhub;

import okhttp3.*;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.security.krb5.internal.PAData;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {
    /**
     * @url https://fyyx.cunminss.com/api/s/temperatureSign
     * @param studentId 学生ID(可能存在越权漏洞)
     * DEFAULT:1416
     * @param lat 纬度
     * DEFAULT:29.922508123809
     * @param lng 经度
     * DEFAULT:121.62696705251
     * @param areaType 区域类型(1-学府东 2-学府西)
     * DEFAULT:2
     * @param temperature 体温
     * DEFAULT:随机数[36.1,36.9]
     * @param signType 签到类型(1-早上 2-午间 3-晚上)
     * DEFAULT:1,2,3(根据系统时间传递)
     * @param signTypeStatus 签到类型状态(1-正常 2-超时？待验证)
     * DEFAULT:1
     * @param address 地址
     * DEFAULT:浙江省宁波市江北区明海南路101号靠近浙江纺织服装职业技术学院生活区
     * @param qrcodeColor 二维码颜色?
     * DEFAULT:0
     * @header: token:260001191325682126(不确定生命周期)
     */

    /**
     * 参数配置区
     */
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    /**
     private static String userName = "(某不知名CTF选手)";
     private static final String studentId = "1416";
     private static final String stu_token = "260001191325682126";
     private static final String student_uuid = "ff44674550cde8a44ad2a9ba21f22d57";
     private static final String SERVER_AK = "SCU99919T9ba41e35e126d6f83c63703e87dce7a35ed2621d362a5";//Server酱AK
     private static final boolean enabled_ServerPush = true;
     **/

//    /**
//     * lcx配置
//     private static final String userName = "(lcx)";
//     private static final String studentId = "594";
//     private static final String stu_token = "124314473126280507";
//     private static final String student_uuid = "3f0ca9e171fe4a979e309d8b930df006";
//     private static final String SERVER_AK = "SCU99919T9ba41e35e126d6f83c63703e87dce7a35ed2621d362a5";//Server酱AK
//     private static final boolean enabled_ServerPush = true;
//     */


    private static final String userName = "(某不知名CTF选手)";
    private static final String studentId = "1416";
    private static final String stu_token = "260001191325682126";
    private static final String student_uuid = "ff44674550cde8a44ad2a9ba21f22d57";
    private static final String SERVER_AK = "SCU99919T9ba41e35e126d6f83c63703e87dce7a35ed2621d362a5";//Server酱AK
    private static final boolean enabled_ServerPush = true;
     private static int failed_count = 0;
     private static final int FAILED_NUMBER = 100;

    //https://sc.ftqq.com/SCU99919T9ba41e35e126d6f83c63703e87dce7a35ed2621d362a5.send
    public static void main(String[] args) throws IOException, ParseException, InterruptedException {
        // 智慧学工自动化签到脚本1.0
        // 挂后台 监测当前时间 执行脚本 获取返回结果 记录日志
        // 如果成功置flag为true,且今天不再执行。判断LastSign的day是否少于今天,如果少于今天则置flag为false;


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");

        String LastSignTime_morning = "1970-01-01";//早上最后一次执行签到的时间
//        String LastSignTime_noon = "1970-01-01";//午间最后一次执行签到的时间
        String LastSignTime_night = "1970-01-01";//晚上最后一次执行签到的时间

        //0-成功 500-失败
        Integer code_morning = -1;//早上签到的响应码
//        Integer code_noon = -1;//午间签到的响应码
        Integer code_night = -1;//晚上签到的响应码

        Boolean SignFlag_morning = false;//早上签到的FLAG
//        Boolean SignFlag_noon = false;//午间签到的FLAG
        Boolean SignFlag_night = false;//晚上签到的FLAG
        HashMap<String, String> resp = new HashMap<String, String>();
        logger.warn("=============欢迎使用=============");
        logger.warn("============ 版本号2.5.0[2020.09.13] ============");
        logger.warn("Author:胖哈勃实验室 - 某不知名的CTF选手");
        logger.warn("studentId:" + studentId);
        logger.warn("student_token:" + stu_token);
        logger.warn("student_uuid:" + student_uuid);
        logger.warn("enabled_Server_push:" + enabled_ServerPush);
        logger.warn("==================================");
        while (true) {
            try {
                Date now = df.parse(df.format(new Date()));//用于判断时间范围
                String systemDate = df2.format(new Date());//获取系统时间

                //System.out.println("系统时间:" + SystemTime);

                //sdf.parse(ss.getTime()).compareTo(sdf.parse(currentTime)) < 0


                //判断错误次数是否达到十次
                if (failed_count >= FAILED_NUMBER) {
                    logger.warn("【警告】错误次数达到限定值，程序脚本停止运行。请迅速排查!");
                    if (enabled_ServerPush) {
                        HttpUtils.anniePost(SERVER_AK,
                                "钉钉体温签到提醒 ， 警告",
                                "错误次数达到限定值，程序脚本停止运行。请迅速排查!");
                    }
                    return;
                }

                if (df2.parse(systemDate).compareTo(df2.parse(LastSignTime_morning)) > 0 && SignFlag_morning) {
                    SignFlag_morning = false;
                    logger.warn("[签到标记重置]重置早上的签到标记");
                    failed_count = 0;
                }
//                if (df2.parse(systemDate).compareTo(df2.parse(LastSignTime_noon)) > 0 && SignFlag_noon) {
//                    SignFlag_noon = false;
//                    logger.warn("重置午间的签到标记");
//                }
                if (df2.parse(systemDate).compareTo(df2.parse(LastSignTime_night)) > 0 && SignFlag_night) {
                    SignFlag_night = false;
                    logger.warn("[签到标记重置]重置晚上的签到标记");
                    failed_count = 0;
                }
                //if (!SignFlag_morning && belongCalendar(now, df.parse("06:00"), df.parse("18:30"))) {
                if (!SignFlag_morning && belongCalendar(now, df.parse("06:30"), df.parse("08:30"))) {
                    logger.warn("[计划任务](" + new Date().toString() + ")" + ":开始签到");
                    //System.out.println("[计划任务(" + new Date() + ")" + ":开始签到");
                    String message = doPostTemperatureSign("1");//开始早上签到
                    JSONObject obj = JSONObject.parseObject(message);
                    String msg = obj.getString("msg");
                    if (msg.contains("签到成功")) {
                        LastSignTime_morning = df2.format(new Date());
                        //发送Server酱消息
                        //https://sc.ftqq.com/SCU99919T9ba41e35e126d6f83c63703e87dce7a35ed2621d362a5.send
                    } else {
                        LastSignTime_morning = obj.getString("msg").substring(0, 10);//存储签到结果
                        if (LastSignTime_morning.contains("非签到时间")) {
                            LastSignTime_morning = df2.format(new Date());
                            SignFlag_morning = true;//早上签到成功
                        }
                    }
                    code_morning = Integer.parseInt(obj.getString("code"));//存储响应码 0为成功,500为失败。
                    logger.warn("接口返回:{}", message);//输出接口返回结果
                    //System.out.println(message);//签到结果的JSON串

                    if (code_morning == 0 || message.contains("您已签到!") || message.contains("签到成功")) {
                        logger.warn("早上签到 - 最后签到日期:" + LastSignTime_morning + ",响应码:" + code_morning + ("(successfully)"));
                        logger.warn("====================================");
                        if (enabled_ServerPush) {
                            HttpUtils.anniePost(SERVER_AK,
                                    "钉钉体温签到提醒 ， 签到成功。",
                                    "签到成功!" + userName + "\n\n" + "接口返回:\n\n" + message);
                        }
                        // System.err.println("早上签到 - 最后签到日期:" + LastSignTime_morning + ",响应码:" + code_morning + ("(successfully)"));
                        //System.out.println("====================================");
                        SignFlag_morning = true;//早上签到成功
                    } else {
                        logger.warn("早上签到 - 最后签到日期:" + LastSignTime_morning + ",响应码:" + code_morning + ("(failed)"));
                        logger.warn("====================================");
                        if (enabled_ServerPush) {
                            HttpUtils.anniePost(SERVER_AK,
                                    "钉钉体温签到提醒 ， 签到失败。",
                                    "签到失败,请注意!!" + userName + "\n\n" + "接口返回:\n\n" + message);
                        }
                        SignFlag_morning = false;
                        //防止遇到失败一直Server酱发信 错误次数达到十次就停止工作
                        failed_count++;
                    }
                    /**
                     * 屏蔽午间签到
                     */
//                } else if (!SignFlag_noon && belongCalendar(now, df.parse("11:30"), df.parse("13:30"))) {
//                    logger.warn("[计划任务(" + new Date() + ")" + ":开始签到");
//                    String message = doPostTemperatureSign("2");//开始午间签到
//                    JSONObject obj = JSONObject.parseObject(message);
//                    String msg = obj.getString("msg");
//                    if (msg.contains("签到成功")) {
//                        LastSignTime_noon = df2.format(new Date());
//                    } else {
//                        LastSignTime_noon = obj.getString("msg").substring(0, 10);//存储签到结果
//                    }
//                    code_noon = Integer.parseInt(obj.getString("code"));//存储响应码 0为成功,500为失败。
//                    logger.warn(message);//签到结果的JSON串
//                    if (code_noon == 0 || message.contains("您已签到!") || message.contains("签到成功")) {
//                        logger.warn("午间签到 - 最后签到日期:" + LastSignTime_noon + ",响应码:" + code_noon + ("(successfully)"));
//                        logger.warn("====================================");
//                        SignFlag_noon = true;//午间签到成功
//                    } else {
//                        logger.warn("午间签到 - 最后签到日期:" + LastSignTime_noon + ",响应码:" + code_noon + ("(failed)"));
//                        logger.warn("====================================");
//                        SignFlag_noon = false;
//                    }
                } else if (!SignFlag_night && belongCalendar(now, df.parse("21:00"), df.parse("23:00"))) {
                    logger.warn("[计划任务](" + new Date().toString() + ")" + ":开始签到");
                    String message = doPostTemperatureSign("3");//开始晚上签到
                    JSONObject obj = JSONObject.parseObject(message);
                    String msg = obj.getString("msg");
                    if (msg.contains("签到成功")) {
                        LastSignTime_night = df2.format(new Date());
                    } else {
                        LastSignTime_night = obj.getString("msg").substring(0, 10);//存储签到结果
                        if (LastSignTime_night.contains("非签到时间")) {
                            LastSignTime_night = df2.format(new Date());
                            SignFlag_night = true;//晚上签到成功
                        }
                    }
                    code_night = Integer.parseInt(obj.getString("code"));//存储响应码 0为成功,500为失败。
                    logger.warn("接口返回:{}", message);//签到结果的JSON串
                    if (code_night == 0 || message.contains("您已签到!") || message.contains("签到成功")) {
                        logger.warn("晚上签到 - 最后签到日期:" + LastSignTime_night + ",响应码:" + code_night + ("(successfully)"));
                        logger.warn("====================================");
                        if (enabled_ServerPush) {
                            HttpUtils.anniePost(SERVER_AK,
                                    "钉钉体温签到提醒 ， 签到成功。",
                                    "签到成功!" + userName + "\n\n" + "接口返回:\n\n" + message);
                        }
                        SignFlag_night = true;//晚上签到成功
                    } else {
                        logger.warn("晚上签到 - 最后签到日期:" + LastSignTime_night + ",响应码:" + code_night + ("(failed)"));
                        logger.warn("====================================");
                        if (enabled_ServerPush) {
                            HttpUtils.anniePost(SERVER_AK,
                                    "钉钉体温签到提醒 ， 签到失败。",
                                    "签到失败,请注意!!" + userName + "\n\n" + "接口返回:\n\n" + message);
                        }
                        SignFlag_night = false;
                        //防止遇到失败一直Server酱发信 错误次数达到十次就停止工作
                        failed_count++;
                    }
                }
                resp.put("date", sdf.format(new Date()));
                resp.put("LastSignTime_morning", LastSignTime_morning);
                resp.put("code_morning", code_morning.toString());
                resp.put("SignFlag_morning", SignFlag_morning.toString());

//                resp.put("LastSignTime_noon", LastSignTime_noon);
//                resp.put("code_noon", code_noon.toString());
//                resp.put("SignFlag_noon", SignFlag_noon.toString());

                resp.put("LastSignTime_night", LastSignTime_night);
                resp.put("code_night", code_night.toString());
                resp.put("SignFlag_night", SignFlag_night.toString());

                String output = JSONObject.toJSONString(resp);
                logger.info(output);
                logger.info("==========================");
                sleepThread();
            } catch (InterruptedException e) {
                sleepThread();
                e.printStackTrace();
            } catch (Exception e) {
                logger.info("抛出了未知异常……");
                e.printStackTrace();
            }
        }
    }

    /**
     * 判断时间段
     *
     * @param nowTime
     * @param beginTime
     * @param endTime
     * @return
     */
    public static boolean belongCalendar(Date nowTime, Date beginTime,
                                         Date endTime) {
        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);
        Calendar begin = Calendar.getInstance();
        begin.setTime(beginTime);
        Calendar end = Calendar.getInstance();
        end.setTime(endTime);
        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }

    public static String doPostTemperatureSign(String signType) throws IOException, InterruptedException {
        //随机生成一个温度
        double temperature = (36.1 + ((36.9 - 36.1) * new Random().nextDouble()));
        //格式化温度
        String newTemperature = FormatTemperature.formateRate(Double.toString(temperature));
        //随机地址
        String address[] = {
                "浙江省宁波市江北区明海南路101号靠近浙江纺织服装职业技术学院生活区",
                "浙江省宁波市江北区浙江纺织服装职业技术学院生活区",
                "浙江省宁波市镇海区明海南路255号靠近公园世家1期"
        };
        String lat = RandomLonLatUtils.randomLonLat(21, 36, 115, 129, "Lat");
        String lng = RandomLonLatUtils.randomLonLat(21, 36, 115, 129, "Lng");
        //随机uuid
        String uuid = UuidUtils.getUuid().substring(0, 30);
        Random r = new Random();
        int radomNum = r.nextInt(address.length);

        OkHttpClient client = new OkHttpClient();
        try {
            //参数封装到HashMap

            Map<String, String> paramsMap = new HashMap<>(9);
            paramsMap.put("studentId", studentId);
            paramsMap.put("lat", lat);
            paramsMap.put("lng", lng);
            paramsMap.put("areaType", "2");
            paramsMap.put("temperature", newTemperature);
            paramsMap.put("signType", signType);
            paramsMap.put("signTypeStatus", "1");
            paramsMap.put("address", address[radomNum]);
            paramsMap.put("qrcodeColor", "0");
            paramsMap.put("uuid", student_uuid);

            FormBody.Builder builder = new FormBody.Builder();
            for (String key : paramsMap.keySet()) {
                //追加表单信息
                builder.add(key, paramsMap.get(key));
            }

            RequestBody formBody = builder.build();
            Request request = new Request.Builder()
                    .post(formBody)
                    .url("https://fyyx.cunminss.com/api/s/temperatureSign")//接口地址
                    .header("token", stu_token)//用户token
                    .header("User-Agent", "Mozilla/5.0 (Linux; U; Android 10; zh-CN; GM1910 Build/QKQ1.190825.002)" +
                            " AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/69.0.3497.100 UWS/3.22.0.30 Mobile Safari/537.36 AliApp(DingTalk/5.1.6) " +
                            "com.alibaba.android.rimet/13241367 Channel/700159 language/zh-CN UT4Aplus/0.2.25 colorScheme/light")
                    .header("chuangyi", calcChuangyiValue(paramsMap))//计算chuangyi值
                    .header("chaofeng","ni_shi_yi_ge_chui_zi_de_tian_cai")
                    .build();
            logger.warn("temperature:{}", newTemperature);
            logger.warn("address:{}", address[radomNum]);
            logger.warn("lat:{}", lat);
            logger.warn("lng:{}", lng);
            //logger.warn("uuid:{}",uuid);
            Response response = client.newCall(request).execute();
            return Objects.requireNonNull(response.body()).string();
        } catch (UnknownHostException e) {
            logger.warn("网络连接失败...");
            e.printStackTrace();
            //防止遇到失败一直Server酱发信 错误次数达到十次就停止工作
            failed_count++;
            if (enabled_ServerPush) {
                HttpUtils.anniePost(SERVER_AK, "钉钉体温签到提醒 ， 警告", "无法连接对应接口服务器，请排查!");
            }
            sleepThread();
        } catch (Exception e) {
            logger.warn("发生异常……");
            e.printStackTrace();
            //防止遇到失败一直Server酱发信 错误次数达到十次就停止工作
            failed_count++;
            if (enabled_ServerPush) {
                HttpUtils.anniePost(SERVER_AK, "钉钉体温签到提醒 ， 警告", "签到服务发生了不可预知的错误，请排查!");
            }
            sleepThread();
        }
        //防止遇到失败一直Server酱发信 错误次数达到十次就停止工作
        failed_count++;
        if (enabled_ServerPush) {
            HttpUtils.anniePost(SERVER_AK, "钉钉体温签到提醒 ， 警告", "签到服务发生了不可预知的错误，请排查!");
        }
        return "未知错误";
    }

    public static void sleepThread() throws InterruptedException {
        Random r = new Random();
        int randomNum = r.nextInt(300) % (300 - 60 + 1) + 60;
        logger.warn("[延时系统]延时" + randomNum + "秒后继续下一次监控任务");
        Thread.sleep(randomNum * 1000); //随机延迟
    }

    public static String calcChuangyiValue(Map<String, String> params) throws Exception {
        String encodeStr = EncryptUtil.createLinkString(params);
        logger.info("编码后的参数串:{}", encodeStr);
        // AES 加密
        String sign = EncryptUtil.aesEncrypt(encodeStr, EncryptUtil.KEY);
        // MD5 摘要加密
        sign = Md5Utils.hash(sign);
        logger.info("加密后的摘要值:{}(chuangyi)", sign);
        return sign;
    }
}

