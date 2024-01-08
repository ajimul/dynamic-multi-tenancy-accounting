package com.accounting.rest.multitenant.security;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.ApplicationScope;

import com.accounting.rest.multitenant.constant.UserStatus;
import com.accounting.rest.multitenant.dto.AuthResponse;
import com.accounting.rest.multitenant.dto.UserLoginDTO;
import com.accounting.rest.multitenant.mastertenant.config.DBContextHolder;
import com.accounting.rest.multitenant.mastertenant.entity.MasterTenant;
import com.accounting.rest.multitenant.mastertenant.services.MasterTenantService;
import com.accounting.rest.multitenant.util.JwtTokenUtil;

/**
 * @author Md. Amran Hossain
 */
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController implements Serializable {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);

	private Map<String, String> mapValue = new HashMap<>();
	private Map<String, String> userDbMap = new HashMap<>();

	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	@Autowired
	MasterTenantService masterTenantService;

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<?> userLogin(@RequestBody @NotNull UserLoginDTO userLoginDTO) throws AuthenticationException {

		LOGGER.info("userLogin() method call...");
		if (null == userLoginDTO.getusername() || userLoginDTO.getusername().isEmpty()) {

			return new ResponseEntity<>("User name is required", HttpStatus.BAD_REQUEST);

		}

		// set database parameter
		MasterTenant masterTenant = masterTenantService.findByClientId(userLoginDTO.getTenantOrClientId());

		if (null == masterTenant || masterTenant.getStatus().toUpperCase().equals(UserStatus.INACTIVE)) {
			throw new RuntimeException("Please contact service provider.");
		}

		// Entry Client Wise value dbName store into bean.
		loadCurrentDatabaseInstance(masterTenant.getDbName(), userLoginDTO.getusername());
		final Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(userLoginDTO.getusername(), userLoginDTO.getPassword()));

		if (true) {
		}

		SecurityContextHolder.getContext().setAuthentication(authentication);

		UserDetails userDetails = (UserDetails) authentication.getPrincipal();

		final String jwt = jwtTokenUtil.generateToken(userDetails.getUsername(),
				String.valueOf(userLoginDTO.getTenantOrClientId()));

//		final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
//
//		final String jwt = jwtTokenUtil.generateToken(userDetails);
		
		
		// Map the value into applicationScope bean
		setMetaDataAfterLogin();
		return ResponseEntity.ok(new AuthResponse ( jwt));
	}

	private void loadCurrentDatabaseInstance(String databaseName, String userName) {
		DBContextHolder.setCurrentDb(databaseName);
		mapValue.put(userName, databaseName);
	}

	@Bean(name = "userTenantInfo")
	@ApplicationScope
	public UserTenantInformation setMetaDataAfterLogin() {
		UserTenantInformation tenantInformation = new UserTenantInformation();
		if (mapValue.size() > 0) {
			for (String key : mapValue.keySet()) {
				if (null == userDbMap.get(key)) {
					// Here Assign putAll due to all time one come.
					userDbMap.putAll(mapValue);
				} else {
					userDbMap.put(key, mapValue.get(key));
				}
			}
			mapValue = new HashMap<>();
		}
		tenantInformation.setMap(userDbMap);
		return tenantInformation;
	}
}
