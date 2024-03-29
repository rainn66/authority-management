package auth.core;

import auth.entity.Admin;
import auth.entity.Authority;
import auth.entity.Menu;
import auth.repository.AdminRepository;
import auth.repository.AuthorityRepository;
import auth.repository.MenuRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class InitAuthority {

    private final AuthorityRepository authorityRepository;

    private final MenuRepository menuRepository;

    private final AdminRepository adminRepository;

    @PostConstruct
    public void initAuthority() throws NoSuchAlgorithmException {

        //기본권한등록
        Authority 시스템관리자 = Authority.builder().authorityCd("AUTH003").authorityNm("시스템관리자").build();
        Authority 일반관리자 = Authority.builder().authorityCd("AUTH002").authorityNm("일반관리자").build();
        Authority 일반사용자 = Authority.builder().authorityCd("AUTH001").authorityNm("일반사용자").build();

        authorityRepository.save(시스템관리자);
        authorityRepository.save(일반관리자);
        authorityRepository.save(일반사용자);

        //초기메뉴등록
        Menu menu = Menu.builder().menuNm("메뉴관리")
                .menuOrder(1)
                .menuLink("/menu")
                .viewAuthority(일반관리자.getAuthorityCd())
                .saveAuthority(일반관리자.getAuthorityCd())
                .build();
        menuRepository.save(menu);

        Menu authority = Menu.builder().menuNm("권한관리")
                .menuOrder(2)
                //.menuLink("/authority/admin")
                .viewAuthority(일반관리자.getAuthorityCd())
                .saveAuthority(일반관리자.getAuthorityCd())
                .build();
        menuRepository.save(authority);

        Menu authorityAdmin = Menu.builder().menuNm("관리자/관리자권한관리")
                .menuOrder(1)
                .menuLink("/authority/admin")
                .viewAuthority(시스템관리자.getAuthorityCd())
                .saveAuthority(시스템관리자.getAuthorityCd())
                .parent(authority)
                .build();
        menuRepository.save(authorityAdmin);

        Menu authorityMenu = Menu.builder().menuNm("메뉴권한관리")
                .menuOrder(2)
                .menuLink("/authority/menu")
                .viewAuthority(일반관리자.getAuthorityCd())
                .saveAuthority(일반관리자.getAuthorityCd())
                .parent(authority)
                .build();
        menuRepository.save(authorityMenu);

        //초기관리자 등록(단방향 암호화)
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.reset();
        md.update("admin".getBytes());
        byte[] hashVale  = md.digest();
        String password = new String(Base64.encodeBase64(hashVale, true));

        Admin admin = Admin.builder().userId("admin")
                .userNm("최고관리자")
                .password(password)
                .lastLoginDt(LocalDateTime.now())
                .authority(시스템관리자).build();
        adminRepository.save(admin);
    }

}
