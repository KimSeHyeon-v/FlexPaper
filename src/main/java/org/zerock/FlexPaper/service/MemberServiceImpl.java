package org.zerock.FlexPaper.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.FlexPaper.domain.FPUser;
import org.zerock.FlexPaper.dto.BundleDTO;
import org.zerock.FlexPaper.dto.FPUserDTO;
import org.zerock.FlexPaper.repository.FPUserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final FPUserRepository fpUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void registerUser(String username, String rawPassword) {
        if (fpUserRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("이미 존재하는 사용자입니다.");
        }

        String encodedPassword = passwordEncoder.encode(rawPassword);
        FPUser user = FPUser.builder()
                .username(username)
                .password(encodedPassword)
                .role("USER")
                .build();

        fpUserRepository.save(user);
    }

    @Override
    public FPUser loadUserByUsername(String username) throws UsernameNotFoundException {
        // Retrieve user from the database
        return fpUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
    }

    @Transactional(readOnly = true)  // 읽기 전용 트랜잭션
    @Override
    public FPUserDTO getUserDetails(String username) {
        FPUser user = fpUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + username));

        List<BundleDTO> bundleDTOs = user.getBundles().stream()
                .map(bundle -> BundleDTO.builder()
                        .bno(bundle.getId())
                        .title(bundle.getTitle())
                        .description(bundle.getDescription())
                        .build())
                .collect(Collectors.toList());

        return FPUserDTO.builder()
                .username(user.getUsername())
                .bundles(bundleDTOs)
                .build();
    }
}
