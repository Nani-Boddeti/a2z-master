
package com.a2z.data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * LoginResponse DTO for OAuth2 Authorization Code Flow
 * Returns authentication result, tokens, and user information to Angular frontend
 */
public class LoginResponse {

    // Response Status
    private boolean success;
    private String message;
    private int statusCode;
    private String errorCode;

    // OAuth2 Tokens
    private String authCode;
    private String accessToken;
    private String refreshToken;
    private String idToken;
    private String tokenType;
    private Long expiresIn;

    // User Information
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String userRole;
    private Long userId;

    // Navigation
    private String redirectUrl;
    private String nextStep;

    // Token Metadata
    private String scope;
    private String state;
    private LocalDateTime issuedAt;
    private LocalDateTime expiresAt;

    // Device/Session Information
    private String sessionId;
    private String deviceId;
    private String ipAddress;
    private boolean mfaRequired;
    private String mfaMethod;

    // Additional Data
    private Map<String, Object> additionalInfo;
    private String locale;
    private boolean rememberMeEnabled;

    // Constructors
    public LoginResponse() {
        this.additionalInfo = new HashMap<>();
        this.statusCode = 200;
        this.tokenType = "Bearer";
    }

    public LoginResponse(boolean success, String message) {
        this();
        this.success = success;
        this.message = message;
        this.statusCode = success ? 200 : 401;
    }

    public LoginResponse(boolean success, String message, String authCode, String accessToken, String refreshToken) {
        this();
        this.success = success;
        this.message = message;
        this.authCode = authCode;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.statusCode = success ? 200 : 401;
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getNextStep() {
        return nextStep;
    }

    public void setNextStep(String nextStep) {
        this.nextStep = nextStep;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(LocalDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public boolean isMfaRequired() {
        return mfaRequired;
    }

    public void setMfaRequired(boolean mfaRequired) {
        this.mfaRequired = mfaRequired;
    }

    public String getMfaMethod() {
        return mfaMethod;
    }

    public void setMfaMethod(String mfaMethod) {
        this.mfaMethod = mfaMethod;
    }

    public Map<String, Object> getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(Map<String, Object> additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public void addAdditionalInfo(String key, Object value) {
        this.additionalInfo.put(key, value);
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public boolean isRememberMeEnabled() {
        return rememberMeEnabled;
    }

    public void setRememberMeEnabled(boolean rememberMeEnabled) {
        this.rememberMeEnabled = rememberMeEnabled;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", statusCode=" + statusCode +
                ", username='" + username + '\'' +
                ", userRole='" + userRole + '\'' +
                ", tokenType='" + tokenType + '\'' +
                ", expiresIn=" + expiresIn +
                ", redirectUrl='" + redirectUrl + '\'' +
                ", mfaRequired=" + mfaRequired +
                ", rememberMeEnabled=" + rememberMeEnabled +
                '}';
    }
}