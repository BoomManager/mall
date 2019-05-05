package com.macro.mall.bo;

import com.macro.mall.model.UmsAdmin;
import com.macro.mall.model.UmsPermission;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SpringSecurity需要的用户详情
 * Created by macro on 2018/4/26.
 */
public class AdminUserDetails implements UserDetails {

    private UmsAdmin umsAdmin;

    private List<UmsPermission> permissionList;//许可列表

    public AdminUserDetails(UmsAdmin umsAdmin,List<UmsPermission> permissionList) {
        this.umsAdmin = umsAdmin;
        this.permissionList = permissionList;
    }

    /**
     * 得到用户权限
     * @return
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //返回当前用户的权限
        return permissionList.stream().filter(permission -> permission.getValue()!=null)
                .map(permission ->new SimpleGrantedAuthority(permission.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return umsAdmin.getPassword();
    }

    @Override
    public String getUsername() {
        return umsAdmin.getUsername();
    }

    /**
     * 账户不合法失效
     * @return
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 账户不合法锁定
     * @return
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /***
     * 资格不合法失效
     * @return
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 可实现
     * @return
     */
    @Override
    public boolean isEnabled() {
        return umsAdmin.getStatus().equals(1);
    }
}
