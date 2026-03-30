//package com.jobassistant.jabackend.core.jpa.auditorAware;
//
//import com.jobassistant.jabackend.core.utils.SecurityUtils;
//import org.springframework.data.domain.AuditorAware;
//import org.springframework.stereotype.Component;
//
//import java.util.Optional;
//
//@Component
//public class AuditorAwareImpl implements AuditorAware<String> {
//
//    @Override
//    public Optional<String> getCurrentAuditor() {
//        try {
//            String userId = SecurityUtils.getUserId();
//
//            return Optional.of(userId != null ? userId : "SYSTEM");
//
//        } catch (Exception e) {
//            return Optional.of("SYSTEM");
//        }
//    }
//}
//ㅠ