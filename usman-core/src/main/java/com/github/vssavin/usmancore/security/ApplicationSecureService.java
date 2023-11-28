package com.github.vssavin.usmancore.security;

import com.github.vssavin.jcrypt.osplatform.OSPlatformCrypt;
import org.springframework.stereotype.Service;

/**
 * Provides a service that uses a platform-specific (OS dependent) encryption algorithm.
 *
 * @author vssavin on 28.11.2023
 */
@Service
class ApplicationSecureService extends OSPlatformCrypt {

}
