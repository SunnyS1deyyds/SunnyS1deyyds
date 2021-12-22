package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.model.pojo.BlackList;
import com.itheima.model.pojo.UserInfo;
import com.tanhua.dubbo.mapper.BlackListMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService
public class BlackListApiImpl implements BlackListApi {

    @Autowired
    private BlackListMapper blackListMapper;

    @Override
    public Page<UserInfo> findByUserId(Long userId, int page, int pagesize) {
        //封装分页对象
        Page<UserInfo> pages = new Page(page, pagesize);

        //执行查询
        pages = blackListMapper.findByUserId(pages, userId);


        return pages;
    }

    @Override
    public void removeBlacklist(Long userId, Long uid) {
        blackListMapper.delete(
                Wrappers.lambdaQuery(BlackList.class)
                        .eq(BlackList::getUserId, userId)
                        .eq(BlackList::getBlackUserId, uid)
        );
    }
}
