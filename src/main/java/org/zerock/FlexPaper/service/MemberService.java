package org.zerock.FlexPaper.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.zerock.FlexPaper.dto.FPUserDTO;

public interface MemberService extends UserDetailsService {
    void registerUser(String username, String password);
    FPUserDTO getUserDetails(String username);
}
