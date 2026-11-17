package com.lacouf.rsbjwt.security;

import com.lacouf.rsbjwt.repository.UserAppRepository;
import com.lacouf.rsbjwt.model.UserApp;
import com.lacouf.rsbjwt.security.exception.AuthenticationException;
import com.lacouf.rsbjwt.security.exception.UserNotFoundException;
import com.lacouf.rsbjwt.security.exception.APIException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthProvider implements AuthenticationProvider{
	private final PasswordEncoder passwordEncoder;
	private final UserAppRepository userAppRepository;

	@Override
	public Authentication authenticate(Authentication authentication) {
		try {
			UserApp user = loadUserByEmail(authentication.getPrincipal().toString());
			validateAuthentication(authentication, user);
			return new UsernamePasswordAuthenticationToken(
				user.getEmail(),
				user.getPassword(),
				user.getAuthorities()
			);
		} catch (APIException e) {
			// AuthenticationProvider ne permet pas de propager une exception checked.
			throw new BadCredentialsException(e.getMessage(), e);
		}
	}

	@Override
	public boolean supports(Class<?> authentication){
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}

	private UserApp loadUserByEmail(String email) throws UserNotFoundException {
		return userAppRepository.findUserAppByEmail(email)
			.orElseThrow(UserNotFoundException::new);
	}

	private void validateAuthentication(Authentication authentication, UserApp user) throws AuthenticationException {
		if(!passwordEncoder.matches(authentication.getCredentials().toString(), user.getPassword()))
			throw new AuthenticationException(HttpStatus.FORBIDDEN, "Incorrect username or password");
	}
}
