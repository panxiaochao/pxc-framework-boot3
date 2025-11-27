/*
 * Copyright © 2025-2026 Lypxc (545685602@qq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.panxiaochao.boot3.weixin.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 微信用户
 * </p>
 *
 * @author Lypxc
 * @since 2024-04-02
 * @version 1.0
 */
@Getter
@Setter
public class WxUser implements Serializable {

    private static final long serialVersionUID = 1L;

    private String openid;

    private String appid;

    private String phone;

    private String nickname;

    private int sex;

    private String city;

    private String province;

    private String headImgUrl;

    @JsonProperty("subscribe_time")
    private Date subscribeTime;

    private boolean subscribe;

    private String unionid;

    private String remark;

    private Long[] tagidList;

    private String subscribeScene;

    private String qrSceneStr;

    public WxUser() {
    }

    public WxUser(String openid) {
        this.openid = openid;
    }

    public WxUser(WxMpUser wxMpUser, String appid) {
        this.openid = wxMpUser.getOpenId();
        this.appid = appid;
        this.subscribe = wxMpUser.getSubscribe();
        if (wxMpUser.getSubscribe()) {
            this.nickname = wxMpUser.getNickname();
            this.headImgUrl = wxMpUser.getHeadImgUrl();
            this.subscribeTime = new Date(wxMpUser.getSubscribeTime() * 1000);
            this.unionid = wxMpUser.getUnionId();
            this.remark = wxMpUser.getRemark();
            this.tagidList = wxMpUser.getTagIds();
            this.subscribeScene = wxMpUser.getSubscribeScene();
            String qrScene = wxMpUser.getQrScene();
            this.qrSceneStr = !StringUtils.hasText(qrScene) ? wxMpUser.getQrSceneStr() : qrScene;
        }
    }

    public WxUser(WxOAuth2UserInfo wxMpUser, String appid) {
        this.openid = wxMpUser.getOpenid();
        this.appid = appid;
        this.subscribe = wxMpUser.getNickname() != null;
        if (this.subscribe) {
            this.nickname = wxMpUser.getNickname();
            this.headImgUrl = wxMpUser.getHeadImgUrl();
            this.unionid = wxMpUser.getUnionId();
        }
    }

}
