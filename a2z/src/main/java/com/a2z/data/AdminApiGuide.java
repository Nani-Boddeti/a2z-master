package com.a2z.data;

/**
 * ADMIN API USAGE GUIDE
 *
 * 1. LOGIN ENDPOINT (Direct Username/Password - No OAuth2)
 *    POST /api/admin/login
 *
 *    Request Body:
 *    {
 *      "username": "admin",
 *      "password": "admin@12345"
 *    }
 *
 *    Response (Success - 200):
 *    {
 *      "success": true,
 *      "token": "<admin_token>",
 *      "username": "admin",
 *      "email": "admin@a2z.local",
 *      "scope": "app.write",
 *      "message": "Admin authenticated successfully",
 *      "timestamp": 1674567890123
 *    }
 *
 *    Response (Error - 401):
 *    {
 *      "success": false,
 *      "message": "Invalid username or password",
 *      "timestamp": 1674567890123
 *    }
 *
 * 2. CREATE NEW ADMIN USER ENDPOINT (Protected - Requires ROLE_ADMIN)
 *    POST /api/admin/create-user
 *    Authorization: Bearer <admin_token>
 *
 *    Request Body:
 *    {
 *      "username": "newadmin",
 *      "password": "SecurePass@123",
 *      "email": "newadmin@a2z.local",
 *      "firstName": "John",
 *      "lastName": "Doe",
 *      "phoneNumber": "+1234567890"
 *    }
 *
 *    Response (Success - 201):
 *    {
 *      "username": "newadmin",
 *      "email": "newadmin@a2z.local",
 *      "firstName": "John",
 *      "lastName": "Doe",
 *      "message": "Admin user created successfully",
 *      "success": true
 *    }
 *
 *    Response (Error - 400):
 *    {
 *      "username": "newadmin",
 *      "email": "newadmin@a2z.local",
 *      "firstName": "John",
 *      "lastName": "Doe",
 *      "message": "Username already exists",
 *      "success": false
 *    }
 *
 * 3. HEALTH CHECK ENDPOINT (Public)
 *    GET /api/admin/health
 *
 *    Response (200):
 *    "Admin API is running"
 *
 * IMPORTANT NOTES:
 * - Admin users created via InitAdminUser or API have ROLE_ADMIN
 * - Admin users are assigned to ADMIN_GROUP which grants app.write scope
 * - Admins login directly with username/password (NO OAuth2 authorization code flow)
 * - Regular users still use OAuth2 with app.read and app.write scopes
 * - App runs entirely on scopes, not Spring Security roles
 * - The token returned from /api/admin/login contains scope: "app.write"
 *
 * CREATING INITIAL ADMIN USER:
 * - On application startup, InitAdminUser component creates default admin if not exists
 * - Default credentials: username=admin, password=admin@12345
 * - Change default password in InitAdminUser.java ADMIN_PASSWORD constant
 *
 * DATABASE RELATIONSHIPS:
 * - Customer: Main user entity with role field
 * - UserGroup: Groups of users with assigned scopes/permissions
 * - Admin users belong to ADMIN_GROUP with app.write scope
 */
public class AdminApiGuide {
    // This class serves as inline documentation
    // Refer to the comments above for complete API usage instructions
}

