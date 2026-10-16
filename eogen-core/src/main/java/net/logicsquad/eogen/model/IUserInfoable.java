package net.logicsquad.eogen.model;

import java.util.Map;

import net.logicsquad.eogen.utils.NotificationMap;

public interface IUserInfoable {
	public void setUserInfo(Map<Object, Object> _userInfo);

	public void setUserInfo(Map<Object, Object> _userInfo, boolean _fireEvents);

	public NotificationMap<Object, Object> getUserInfo();
	
	public void userInfoChanged(String path, Object oldValue, Object newValue);
}
