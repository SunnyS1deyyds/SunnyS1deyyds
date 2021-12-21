package com.tanhua.test;

import com.baidu.aip.face.AipFace;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

public class AipTest {
    //设置APPID/AK/SK
    public static final String APP_ID = "25384207";
    public static final String API_KEY = "64anEHUyNwtY9dwr9tpQFDem";
    public static final String SECRET_KEY = "Vrro5EW3WS8paEpCsYSHw6CiYNF93Sn5";

    private AipFace client;

    @Before
    public void init() {
        // 初始化一个AipFace
        client = new AipFace(APP_ID, API_KEY, SECRET_KEY);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);

    }

    @Test
    public void test() {
        String imageUrl1 = "https://xuzy-tanhua-test.oss-cn-beijing.aliyuncs.com/2021/12/21/620639bb-f776-47d5-9aa8-1bae6c86bd79.jpg";
        boolean result1 = detect(imageUrl1);
        String imageUrl2 = "https://xuzy-tanhua-test.oss-cn-beijing.aliyuncs.com/2021/12/21/0b1fade4-5aaf-440f-8033-c433650a83af.jpg";
        boolean result2 = detect(imageUrl2);

        System.out.println(result1);
        System.out.println(result2);
    }

    public boolean detect(String imageUrl) {
        // 调用接口
        String imageType = "URL";

        HashMap<String, String> options = new HashMap<>();
        options.put("face_field", "age");
        options.put("max_face_num", "2");
        options.put("face_type", "LIVE");
        options.put("liveness_control", "NONE");

        // 人脸检测
        JSONObject res = client.detect(imageUrl, imageType, options);
        //System.out.println(res.toString(2));
        String error_code = res.get("error_code").toString();

        return "0".equals(error_code);
    }

    //public static void main(String[] args) {
    //    AipTest aipTest = new AipTest()
    //}
}