package com.guitar.security;

import com.guitar.entity.SysUser;
import com.guitar.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final SysUserMapper sysUserMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = sysUserMapper.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }
        return new CustomUserDetails(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getRole(),
                Boolean.TRUE.equals(user.getEnabled())
        );
    }
}
