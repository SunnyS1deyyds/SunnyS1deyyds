package com.tanhua.config.template;

import com.baidu.aip.face.AipFace;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;

public class AipTemplate {

    @Autowired
    private AipFace client;

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
}
