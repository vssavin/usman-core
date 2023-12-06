package com.github.vssavin.usmancore.config;

import java.util.List;

/**
 * A container for storing permission paths.
 *
 * @author vssavin on 06.12.2023
 */
public interface PermissionPathsContainer {

	List<String> getPermissionPaths(Permission permission);

}
