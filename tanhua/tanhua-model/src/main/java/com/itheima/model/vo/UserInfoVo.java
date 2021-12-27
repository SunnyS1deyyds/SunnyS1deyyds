package com.itheima.model.vo;

import com.itheima.model.pojo.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoVo implements Serializable {

    private Long id; //用户id
    private String nickname; //昵称
    private String avatar; //用户头像
    private String birthday; //生日
    private String gender; //性别
    private String age; //年龄
    private String city; //城市
    private String income; //收入
    private String education; //学历
    private String profession; //行业
    private Integer marriage; //婚姻状态

    public static UserInfoVo init(UserInfo userInfo) {
        UserInfoVo vo = new UserInfoVo();

        BeanUtils.copyProperties(userInfo, vo);
        if (userInfo.getAge() != null) {//age必须转为字符串
            vo.setAge(userInfo.getAge().toString());
        }

        return vo;
    }
}