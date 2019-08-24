/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.mrs.api.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.mrs.Person;
import org.mrs.Privilege;
import org.mrs.PrivilegeListener;
import org.mrs.Role;
import org.mrs.User;
import org.mrs.annotation.Authorized;
import org.mrs.annotation.Logging;
import org.mrs.api.APIAuthenticationException;
import org.mrs.api.APIException;
import org.mrs.api.AdministrationService;
import org.mrs.api.CannotDeleteRoleWithChildrenException;
import org.mrs.api.InvalidActivationKeyException;
import org.mrs.api.UserService;
import org.mrs.api.context.Context;
import org.mrs.api.db.DAOException;
import org.mrs.api.db.LoginCredential;
import org.mrs.api.db.UserDAO;
import org.mrs.messagesource.MessageSourceService;
import org.mrs.notification.MessageException;
import org.mrs.patient.impl.LuhnIdentifierValidator;
import org.mrs.util.OpenmrsConstants;
import org.mrs.util.OpenmrsUtil;
import org.mrs.util.PrivilegeConstants;
import org.mrs.util.RoleConstants;
import org.mrs.util.Security;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation of the user service. This class should not be used on its own. The current
 * OpenMRS implementation should be fetched from the Context
 * 
 * @see org.mrs.api.UserService
 * @see org.mrs.api.context.Context
 */
@Transactional
public class UserServiceImpl extends BaseOpenmrsService implements UserService {
	
	private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
	
	protected UserDAO dao;
	
	private static final int MAX_VALID_TIME = 12*60*60*1000; //Period of 12 hours
	private static final int MIN_VALID_TIME = 60*1000; //Period of 1 minute
	private static final int DEFAULT_VALID_TIME = 10*60*1000; //Default time of 10 minute
	
	@Autowired(required = false)
	List<PrivilegeListener> privilegeListeners;
	
	public UserServiceImpl() {
	}
	
	public void setUserDAO(UserDAO dao) {
		this.dao = dao;
	}
	
	/**
	 * @return the validTime for which the password reset activation key will be valid
	 */
	private int getValidTime() {
		String validTimeGp = Context.getAdministrationService()
		        .getGlobalProperty(OpenmrsConstants.GP_PASSWORD_RESET_VALIDTIME);
		final int validTime = StringUtils.isBlank(validTimeGp) ? DEFAULT_VALID_TIME : Integer.parseInt(validTimeGp);
		//if valid time is less that a minute or greater than 12hrs reset valid time to 1 minutes else set it to the required time.
		return (validTime < MIN_VALID_TIME) || (validTime > MAX_VALID_TIME) ? DEFAULT_VALID_TIME : validTime;
	}
	
	/**
	 * @see org.mrs.api.UserService#createUser(org.mrs.User, java.lang.String)
	 */
	@Override
	public User createUser(User user, String password) throws APIException {
		if (user.getUserId() != null) {
			throw new APIException("This method can be used for only creating new users");
		}
		
		Context.requirePrivilege(PrivilegeConstants.ADD_USERS);
		
		checkPrivileges(user);
		
		// if a password wasn't supplied, throw an error
		if (password == null || password.length() < 1) {
			throw new APIException("User.creating.password.required", (Object[]) null);
		}
		
		if (hasDuplicateUsername(user)) {
			throw new DAOException("Username " + user.getUsername() + " or system id " + user.getSystemId()
			        + " is already in use.");
		}
		
		// TODO Check required fields for user!!
		OpenmrsUtil.validatePassword(user.getUsername(), password, user.getSystemId());
		
		return dao.saveUser(user, password);
	}
	
	/**
	 * @see org.mrs.api.UserService#getUser(java.lang.Integer)
	 */
	@Override
	@Transactional(readOnly = true)
	public User getUser(Integer userId) throws APIException {
		return dao.getUser(userId);
	}
	
	/**
	 * @see org.mrs.api.UserService#getUserByUsername(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public User getUserByUsername(String username) throws APIException {
		return dao.getUserByUsername(username);
	}
	
	/**
	 * @see org.mrs.api.UserService#hasDuplicateUsername(org.mrs.User)
	 */
	@Override
	@Transactional(readOnly = true)
	public boolean hasDuplicateUsername(User user) throws APIException {
		return dao.hasDuplicateUsername(user.getUsername(), user.getSystemId(), user.getUserId());
	}
	
	/**
	 * @see org.mrs.api.UserService#getUsersByRole(org.mrs.Role)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<User> getUsersByRole(Role role) throws APIException {
		List<Role> roles = new ArrayList<>();
		roles.add(role);
		
		return Context.getUserService().getUsers(null, roles, false);
	}
	
	/**
	 * @see org.mrs.api.UserService#saveUser(org.mrs.User)
	 */
	@Override
	@CacheEvict(value = "userSearchLocales", allEntries = true)
	public User saveUser(User user) throws APIException {
		if (user.getUserId() == null) {
			throw new APIException("This method can be called only to update existing users");
		}
		
		Context.requirePrivilege(PrivilegeConstants.EDIT_USERS);
		
		checkPrivileges(user);
		
		if (hasDuplicateUsername(user)) {
			throw new DAOException("Username " + user.getUsername() + " or system id " + user.getSystemId()
			        + " is already in use.");
		}
		
		return dao.saveUser(user, null);
	}
	
	/**
	 * @see org.mrs.api.UserService#voidUser(org.mrs.User, java.lang.String)
	 */
	public User voidUser(User user, String reason) throws APIException {
		return Context.getUserService().retireUser(user, reason);
	}
	
	/**
	 * @see org.mrs.api.UserService#retireUser(org.mrs.User, java.lang.String)
	 */
	@Override
	public User retireUser(User user, String reason) throws APIException {
		user.setRetired(true);
		user.setRetireReason(reason);
		user.setRetiredBy(Context.getAuthenticatedUser());
		user.setDateRetired(new Date());
		
		return saveUser(user);
	}
	
	/**
	 * @see org.mrs.api.UserService#unvoidUser(org.mrs.User)
	 */
	public User unvoidUser(User user) throws APIException {
		return Context.getUserService().unretireUser(user);
	}
	
	/**
	 * @see org.mrs.api.UserService#unretireUser(org.mrs.User)
	 */
	@Override
	public User unretireUser(User user) throws APIException {
		user.setRetired(false);
		user.setRetireReason(null);
		user.setRetiredBy(null);
		user.setDateRetired(null);
		
		return saveUser(user);
	}
	
	/**
	 * @see org.mrs.api.UserService#getAllUsers()
	 */
	@Override
	@Transactional(readOnly = true)
	public List<User> getAllUsers() throws APIException {
		return dao.getAllUsers();
	}

	/**
	 * @see org.mrs.api.UserService#getAllPrivileges()
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Privilege> getAllPrivileges() throws APIException {
		return dao.getAllPrivileges();
	}
	
	/**
	 * @see org.mrs.api.UserService#getPrivilege(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public Privilege getPrivilege(String p) throws APIException {
		return dao.getPrivilege(p);
	}
	
	/**
	 * @see org.mrs.api.UserService#purgePrivilege(org.mrs.Privilege)
	 */
	@Override
	public void purgePrivilege(Privilege privilege) throws APIException {
		if (OpenmrsUtil.getCorePrivileges().keySet().contains(privilege.getPrivilege())) {
			throw new APIException("Privilege.cannot.delete.core", (Object[]) null);
		}
		
		dao.deletePrivilege(privilege);
	}
	
	/**
	 * @see org.mrs.api.UserService#savePrivilege(org.mrs.Privilege)
	 */
	@Override
	public Privilege savePrivilege(Privilege privilege) throws APIException {
		return dao.savePrivilege(privilege);
	}

	/**
	 * @see org.mrs.api.UserService#getAllRoles()
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Role> getAllRoles() throws APIException {
		return dao.getAllRoles();
	}
	
	/**
	 * @see org.mrs.api.UserService#getRole(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public Role getRole(String r) throws APIException {
		return dao.getRole(r);
	}
	
	/**
	 * @see org.mrs.api.UserService#purgeRole(org.mrs.Role)
	 */
	@Override
	public void purgeRole(Role role) throws APIException {
		if (role == null || role.getRole() == null) {
			return;
		}
		
		if (OpenmrsUtil.getCoreRoles().keySet().contains(role.getRole())) {
			throw new APIException("Role.cannot.delete.core", (Object[]) null);
		}
		
		if (role.hasChildRoles()) {
			throw new CannotDeleteRoleWithChildrenException();
		}
		
		dao.deleteRole(role);
	}
	
	/**
	 * @see org.mrs.api.UserService#saveRole(org.mrs.Role)
	 */
	@Override
	public Role saveRole(Role role) throws APIException {
		// make sure one of the parents of this role isn't itself...this would
		// cause an infinite loop
		if (role.getAllParentRoles().contains(role)) {
			throw new APIException("Role.cannot.inherit.descendant", (Object[]) null);
		}
		
		checkPrivileges(role);
		
		return dao.saveRole(role);
	}
	
	/**
	 * @see org.mrs.api.UserService#changePassword(java.lang.String, java.lang.String)
	 */
	@Override
	public void changePassword(String pw, String pw2) throws APIException {
		User user = Context.getAuthenticatedUser();
		
		changePassword(user, pw, pw2);
	}
	
	/**
	 * @see org.mrs.api.UserService#changeHashedPassword(User, String, String)
	 */
	@Override
	public void changeHashedPassword(User user, String hashedPassword, String salt) throws APIException {
		dao.changeHashedPassword(user, hashedPassword, salt);
	}
	
	/**
	 * @see org.mrs.api.UserService#changeQuestionAnswer(User, String, String)
	 */
	@Override
	public void changeQuestionAnswer(User u, String question, String answer) throws APIException {
		dao.changeQuestionAnswer(u, question, answer);
	}
	
	/**
	 * @see org.mrs.api.UserService#changeQuestionAnswer(java.lang.String, java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public void changeQuestionAnswer(String pw, String q, String a) {
		dao.changeQuestionAnswer(pw, q, a);
	}
	
	/**
	 * @see org.mrs.api.UserService#isSecretAnswer(org.mrs.User, java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public boolean isSecretAnswer(User u, String answer) {
		return dao.isSecretAnswer(u, answer);
	}
	
	/**
	 * @see org.mrs.api.UserService#getUsersByName(java.lang.String, java.lang.String, boolean)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<User> getUsersByName(String givenName, String familyName, boolean includeVoided) throws APIException {
		return dao.getUsersByName(givenName, familyName, includeVoided);
	}
	
	/**
	 * @see org.mrs.api.UserService#getUsersByPerson(org.mrs.Person, boolean)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<User> getUsersByPerson(Person person, boolean includeRetired) throws APIException {
		return dao.getUsersByPerson(person, includeRetired);
	}
	
	/**
	 * @see org.mrs.api.UserService#getUsers(java.lang.String, java.util.List, boolean)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<User> getUsers(String nameSearch, List<Role> roles, boolean includeVoided) throws APIException {
		return Context.getUserService().getUsers(nameSearch, roles, includeVoided, null, null);
	}
	
	/**
	 * Convenience method to check if the authenticated user has all privileges they are giving out
	 * 
	 * @param new user that has privileges
	 */
	private void checkPrivileges(User user) {
		Collection<Role> roles = user.getAllRoles();
		List<String> requiredPrivs = new ArrayList<>();
		
		for (Role r : roles) {
			if (r.getRole().equals(RoleConstants.SUPERUSER)
			        && !Context.hasPrivilege(PrivilegeConstants.ASSIGN_SYSTEM_DEVELOPER_ROLE)) {
				throw new APIException("User.you.must.have.role", new Object[] { RoleConstants.SUPERUSER });
			}
			if (r.getPrivileges() != null) {
				for (Privilege p : r.getPrivileges()) {
					if (!Context.hasPrivilege(p.getPrivilege())) {
						requiredPrivs.add(p.getPrivilege());
					}
				}
			}
		}
		
		if (requiredPrivs.size() == 1) {
			throw new APIException("User.you.must.have.privilege", new Object[] { requiredPrivs.get(0) });
		} else if (requiredPrivs.size() > 1) {
			StringBuilder txt = new StringBuilder("You must have the following privileges in order to assign them: ");
			for (String s : requiredPrivs) {
				txt.append(s).append(", ");
			}
			throw new APIException(txt.substring(0, txt.length() - 2));
		}
	}
	
	/**
	 * @see org.mrs.api.UserService#setUserProperty(User, String, String)
	 */
	@Override
	public User setUserProperty(User user, String key, String value) {
		if (user != null) {
			if (!Context.hasPrivilege(PrivilegeConstants.EDIT_USERS) && !user.equals(Context.getAuthenticatedUser())) {
				throw new APIException("you.are.not.authorized.change.properties", new Object[] { user.getUserId() });
			}
			
			user.setUserProperty(key, value);
			try {
				Context.addProxyPrivilege(PrivilegeConstants.EDIT_USERS);
				Context.getUserService().saveUser(user);
			}
			finally {
				Context.removeProxyPrivilege(PrivilegeConstants.EDIT_USERS);
			}
		}
		
		return user;
	}
	
	/**
	 * @see org.mrs.api.UserService#removeUserProperty(org.mrs.User, java.lang.String)
	 */
	@Override
	public User removeUserProperty(User user, String key) {
		if (user != null) {
			
			// if the current user isn't allowed to edit users and
			// the user being edited is not the current user, throw an
			// exception
			if (!Context.hasPrivilege(PrivilegeConstants.EDIT_USERS) && !user.equals(Context.getAuthenticatedUser())) {
				throw new APIException("you.are.not.authorized.change.properties", new Object[] { user.getUserId() });
			}
			
			user.removeUserProperty(key);
			
			try {
				Context.addProxyPrivilege(PrivilegeConstants.EDIT_USERS);
				Context.getUserService().saveUser(user);
			}
			finally {
				Context.removeProxyPrivilege(PrivilegeConstants.EDIT_USERS);
			}
		}
		
		return user;
	}
	
	/**
	 * Generates system ids based on the following algorithm scheme: user_id-check digit
	 * 
	 * @see org.mrs.api.UserService#generateSystemId()
	 */
	@Override
	@Transactional(readOnly = true)
	public String generateSystemId() {
		// Hardcoding Luhn algorithm since all existing mrs user ids have
		// had check digits generated this way.
		LuhnIdentifierValidator liv = new LuhnIdentifierValidator();
		
		String systemId;
		Integer offset = 0;
		do {
			// generate and increment the system id if necessary
			Integer generatedId = dao.generateSystemId() + offset++;
			
			systemId = generatedId.toString();
			
			try {
				systemId = liv.getValidIdentifier(systemId);
			}
			catch (Exception e) {
				log.error("error getting check digit", e);
				return systemId;
			}
			
			// loop until we find a system id that no one has 
		} while (dao.hasDuplicateUsername(null, systemId, null));
		
		return systemId;
	}
	
	/**
	 * @see org.mrs.api.UserService#purgeUser(org.mrs.User)
	 */
	@Override
	public void purgeUser(User user) throws APIException {
		dao.deleteUser(user);
	}
	
	/**
	 * @see org.mrs.api.UserService#purgeUser(org.mrs.User, boolean)
	 */
	@Override
	public void purgeUser(User user, boolean cascade) throws APIException {
		if (cascade) {
			throw new APIException("cascade.do.not.think", (Object[]) null);
		}
		
		dao.deleteUser(user);
	}
	
	/**
	 * Convenience method to check if the authenticated user has all privileges they are giving out
	 * to the new role
	 * 
	 * @param new user that has privileges
	 */
	private void checkPrivileges(Role role) {
		Collection<Privilege> privileges = role.getPrivileges();
		
		if (privileges != null) {
			for (Privilege p : privileges) {
				if (!Context.hasPrivilege(p.getPrivilege())) {
					throw new APIAuthenticationException("Privilege required: " + p);
				}
			}
		}
	}
	
	/**
	 * @see org.mrs.api.UserService#getPrivilegeByUuid(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public Privilege getPrivilegeByUuid(String uuid) throws APIException {
		return dao.getPrivilegeByUuid(uuid);
	}
	
	/**
	 * @see org.mrs.api.UserService#getRoleByUuid(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public Role getRoleByUuid(String uuid) throws APIException {
		return dao.getRoleByUuid(uuid);
	}
	
	/**
	 * @see org.mrs.api.UserService#getUserByUuid(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public User getUserByUuid(String uuid) throws APIException {
		return dao.getUserByUuid(uuid);
	}
	
	/**
	 * @see UserService#getCountOfUsers(String, List, boolean)
	 */
	@Override
	@Transactional(readOnly = true)
	public Integer getCountOfUsers(String name, List<Role> roles, boolean includeRetired) {
		if (name != null) {
			name = StringUtils.replace(name,", ", " ");
		}
		
		// if the authenticated role is in the list of searched roles, then all
		// persons should be searched
		Role authRole = getRole(RoleConstants.AUTHENTICATED);
		if (roles.contains(authRole)) {
			return dao.getCountOfUsers(name, new ArrayList<>(), includeRetired);
		}
		
		return dao.getCountOfUsers(name, roles, includeRetired);
	}
	
	/**
	 * @see UserService#getUsers(String, List, boolean, Integer, Integer)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<User> getUsers(String name, List<Role> roles, boolean includeRetired, Integer start, Integer length)
	        throws APIException {
		if (name != null) {
			name = StringUtils.replace(name,", ", " ");
		}
		
		if (roles == null) {
			roles = new ArrayList<>();
		}

		// if the authenticated role is in the list of searched roles, then all
		// persons should be searched
		Role authRole = getRole(RoleConstants.AUTHENTICATED);
		if (roles.contains(authRole)) {
			return dao.getUsers(name, new ArrayList<>(), includeRetired, start, length);
		}

		// add the requested roles and all child roles for consideration
		Set<Role> allRoles = new HashSet<>();
		for (Role r : roles) {
			allRoles.add(r);
			allRoles.addAll(r.getAllChildRoles());
		}
		
		return dao.getUsers(name, new ArrayList<>(allRoles), includeRetired, start, length);
	}
	
	@Override
	public User saveUserProperty(String key, String value) {
		User user = Context.getAuthenticatedUser();
		if (user == null) {
			throw new APIException("no.authenticated.user.found", (Object[]) null);
		}
		user.setUserProperty(key, value);
		return dao.saveUser(user, null);
	}
	
	@Override
	public User saveUserProperties(Map<String, String> properties) {
		User user = Context.getAuthenticatedUser();
		if (user == null) {
			throw new APIException("no.authenticated.user.found", (Object[]) null);
		}
		user.getUserProperties().clear();
		for (Map.Entry<String, String> entry : properties.entrySet()) {
			user.setUserProperty(entry.getKey(), entry.getValue());
		}
		return dao.saveUser(user, null);
	}
	
	/**
	 * @see UserService#changePassword(User, String, String)
	 */
	@Override
	@Authorized(PrivilegeConstants.EDIT_USER_PASSWORDS)
	@Logging(ignoredArgumentIndexes = { 1, 2 })
	public void changePassword(User user, String oldPassword, String newPassword) throws APIException {
		if (user.getUserId() == null) {
			throw new APIException("user.must.exist", (Object[]) null);
		}
		if (oldPassword == null) {
			if (!Context.hasPrivilege(PrivilegeConstants.EDIT_USER_PASSWORDS)) {
				throw new APIException("null.old.password.privilege.required", (Object[]) null);
			}
		} else if (!dao.getLoginCredential(user).checkPassword(oldPassword)) {
			throw new APIException("old.password.not.correct", (Object[]) null);
		}
	
		updatePassword(user, newPassword);
	}

	private void updatePassword(User user, String newPassword) {
		OpenmrsUtil.validatePassword(user.getUsername(), newPassword, user.getSystemId());
		dao.changePassword(user, newPassword);
	}

	@Override
	public void changePassword(User user, String newPassword) throws APIException {
		updatePassword(user, newPassword);
	}

	@Override
	public void changePasswordUsingSecretAnswer(String secretAnswer, String pw) throws APIException {
		User user = Context.getAuthenticatedUser();
		if(!isSecretAnswer(user, secretAnswer)) {
			throw new APIException("secret.answer.not.correct", (Object[]) null);
		}
		updatePassword(user, pw);
	}

	@Override
    public String getSecretQuestion(User user) throws APIException {
		if (user.getUserId() != null) {
			LoginCredential loginCredential = dao.getLoginCredential(user);
			return loginCredential.getSecretQuestion();
		} else {
			return null;
		}
    }
	
	/**
	 * @see org.mrs.api.UserService#getUserByUsernameOrEmail(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public User getUserByUsernameOrEmail(String usernameOrEmail) {
		if (StringUtils.isNotBlank(usernameOrEmail)) {
			User user = dao.getUserByEmail(usernameOrEmail);
			if (user == null) {
				return getUserByUsername(usernameOrEmail);
			}
			return user;
		}
		throw new APIException("error.usernameOrEmail.notNullOrBlank", (Object[]) null);
	}
	
	/**
	 * @see org.mrs.api.UserService#getUserByActivationKey(java.lang.String)
	 * 
	 */
	@Override
	@Transactional(readOnly = true)
	public User getUserByActivationKey(String activationKey) {
		LoginCredential loginCred = dao.getLoginCredentialByActivationKey(activationKey);
		if (loginCred != null) {
			String[] credTokens = loginCred.getActivationKey().split(":");
			if (System.currentTimeMillis() <= Long.parseLong(credTokens[1])) {
				return getUser(loginCred.getUserId());
			}
		}
		return null;
	}
	
	/**
	 * @throws APIException
	 * @throws MessageException
	 * @see org.mrs.api.UserService#setUserActivationKey(org.mrs.User)
	 */
	@Override
	public User setUserActivationKey(User user) throws MessageException {
		String token = RandomStringUtils.randomAlphanumeric(20);
		long time = System.currentTimeMillis() + getValidTime();
		String hashedKey = Security.encodeString(token);
		String activationKey = hashedKey + ":" + time;
		LoginCredential credentials = dao.getLoginCredential(user);
		credentials.setActivationKey(activationKey);	
		dao.setUserActivationKey(credentials);	
		
		MessageSourceService messages = Context.getMessageSourceService();
		AdministrationService adminService = Context.getAdministrationService();
		String link = adminService.getGlobalProperty(OpenmrsConstants.GP_HOST_URL)
		        .replace("{activationKey}", token);
		String msg = messages.getMessage("mail.passwordreset.content").replace("{name}", user.getUsername())
		        .replace("{link}", link)
		        .replace("{time}", String.valueOf(getValidTime() / 60000));
		Context.getMessageService().sendMessage(user.getEmail(),
		    adminService.getGlobalProperty("mail.from"),
		    messages.getMessage("mail.passwordreset.subject"), msg);
		
		return user;
	}
	
	/**
	 * @see org.mrs.api.UserService#changeUserPasswordUsingActivationKey(String, String);
	 */
	@Override
	public void changePasswordUsingActivationKey(String activationKey, String newPassword) {
		User user = getUserByActivationKey(activationKey);
		if (user == null) {
			throw new InvalidActivationKeyException("activation.key.not.correct");
		}
		updatePassword(user, newPassword);
	}
}
