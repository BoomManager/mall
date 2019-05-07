package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.UmsMember;
import com.macro.mall.portal.domain.MemberDetails;
import com.macro.mall.portal.service.UmsMemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 会员登录注册管理Controller
 * Created by macro on 2018/8/3.
 */
@Controller
@Api(tags = "UmsMemberController", description = "会员登录注册管理")
@RequestMapping("/sso")
public class UmsMemberController {
    @Autowired
    private UmsMemberService memberService;

    @ApiOperation("注册")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult register(@RequestParam String username,
                                 @RequestParam String password,
                                 @RequestParam String telephone,
                                 @RequestParam String authCode) {
        return memberService.register(username, password, telephone, authCode);
    }

    @ApiOperation("获取验证码")
    @RequestMapping(value = "/getAuthCode", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult getAuthCode(@RequestParam String telephone) {
        return memberService.generateAuthCode(telephone);
    }

    @ApiOperation("修改密码")
    @RequestMapping(value = "/updatePassword", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updatePassword(@RequestParam String telephone,
                                 @RequestParam String password,
                                 @RequestParam String authCode) {
        return memberService.updatePassword(telephone,password,authCode);
    }

    @ApiOperation("获取用户信息")
    @RequestMapping(value = "/getUserInfo", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<UmsMember> getUserInfo(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UmsMember member = memberService.getByUsername(username);
        if(member!=null){
            return CommonResult.success(member);
        }
        throw new UsernameNotFoundException("获取用户信息失败！");
    }

    @ApiOperation("修改用户信息")
    @RequestMapping(value = "/updateUserInfo", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateMemberInfo(@RequestBody UmsMember umsMember){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!username.isEmpty()){
            int i = memberService.updateMemberByUsername(username, umsMember);
            if(i != 1){
                return CommonResult.failed("修改用户信息失败！请重试！");
            }
        }
        return CommonResult.success("修改用户信息成功！！！");
    }
}
