package com.github.vssavin.usmancore.security.auth;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.stereotype.Service;

/**
 * Service bean with DefaultOAuth2UserService implementation of o2Auth.
 *
 * @author vssavin on 11.12.2023.
 */
@Service
public class UsmanOAuth2UserService extends DefaultOAuth2UserService {

}
