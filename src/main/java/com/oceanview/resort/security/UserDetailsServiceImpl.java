package com.oceanview.resort.security;

import com.oceanview.resort.model.User;
import com.oceanview.resort.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;

/**
 * Custom {@link UserDetailsService} implementation that loads user data
 * from the database and maps it to Spring Security's {@link UserDetails}.
 *
 * <p>The user's {@link com.oceanview.resort.model.enums.UserRole} is
 * converted to a {@code ROLE_<role>} {@link GrantedAuthority}.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Loads a user by username for Spring Security authentication.
     *
     * @param username the username to look up
     * @return a fully populated {@link UserDetails} object
     * @throws UsernameNotFoundException if no user is found or account is inactive
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user by username: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with username: " + username));

        if (!user.isActive()) {
            throw new UsernameNotFoundException("User account is deactivated: " + username);
        }

        Collection<? extends GrantedAuthority> authorities =
                Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isActive(),   // enabled
                true,              // accountNonExpired
                true,              // credentialsNonExpired
                true,              // accountNonLocked
                authorities);
    }
}
