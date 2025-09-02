package com.ccpay.common.audit;

import com.ccpay.common.utils.SecurityUtils;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditorAwareImpl implements AuditorAware<String> {
    
    @Override
    public Optional<String> getCurrentAuditor() {
        return SecurityUtils.getCurrentUsername()
                .or(() -> Optional.of("system"));
    }
}