# BarangayConnect

**A Web-Based Guided Dispute Resolution and Case Management System for Barangays**

Project Increment 1 implements the User Accounts and Authentication module from the approved SRS (Feature 1, FR-001 to FR-009).

## Increment 1 features

- Resident self-registration with required-field validation and duplicate email/mobile checks.
- BCrypt password hashing (cost factor 12); passwords are never returned by API responses.
- Login using email address or Philippine mobile number.
- JWT bearer authentication with a 30-minute token lifetime.
- Server-side session records for logout revocation and 30-minute inactivity checks.
- Five failed login attempts trigger a 15-minute account lock.
- Backend role-based authorization for Resident, Barangay Secretary, Punong Barangay, Lupon Member, and System Administrator.
- Role-specific dashboard landing pages.
- Administrator-only official account creation, account activation/deactivation, user listing, and audit-log viewing.
- Profile retrieval and profile/password update API endpoints.
- Audit records for registration, successful/failed login, logout, official account creation, and account status changes.

## Technology

- Frontend: React, Vite, React Router, Lucide
- Backend: Java 17, Spring Boot 3.5.6, Spring Security, Spring Data JPA
- Authentication: JWT (JJWT) and BCrypt
- Database: Supabase-hosted PostgreSQL
- Database connectivity: PostgreSQL JDBC driver through Supabase Session Pooler (SSL required)
- API: REST / JSON

## Prerequisites

- Java 17+
- Maven 3.9+
- Node.js 20.19+ and npm
- A Supabase project with its PostgreSQL database

## 1. Create the Supabase project

1. Open the [Supabase Dashboard](https://supabase.com/dashboard).
2. Create a project named `BarangayConnect`.
3. Choose Singapore as the region when available (or the closest available region to your users).
4. Save the database password securely. It is different from your Supabase account password.
5. The current application connects from Spring Boot directly to PostgreSQL using JDBC. It does not use the Supabase Data API or a Supabase frontend client. You can leave Data API access and automatic RLS options disabled for this initial backend-only database access pattern.
6. Wait until the project finishes provisioning.

## 2. Get the database connection string

1. Open your BarangayConnect project in Supabase.
2. Click **Connect**.
3. Select **Session pooler** as the connection method.
4. Select the **JDBC** format if the dashboard offers a format selector.
5. Copy the connection string. Use the actual host, port, username, and project reference shown by Supabase; do not type example placeholders.
6. Confirm the URL uses port `5432` and includes `sslmode=require`.

Use Session Pooler for a typical IPv4-only local Windows network. The direct database connection may require IPv6 or an IPv4 add-on. Do not use Transaction Pooler (port 6543) for this Spring Boot/JPA configuration.

The connection string should have this general JDBC shape (illustrative only):

```text
jdbc:postgresql://POOLER_HOST:5432/postgres?user=postgres.PROJECT_REF&password=YOUR_DATABASE_PASSWORD&sslmode=require
```

If your password contains URL-reserved characters such as `&`, `#`, `?`, or spaces, URL-encode those characters in the connection string. Do not paste your real connection string into GitHub, source code, screenshots, or public chats.

## 3. Configure and run the backend (Windows PowerShell)

Open PowerShell in the `backend` folder. Replace the example values with your own secure values. Set `SUPABASE_DB_URL` to the complete JDBC string copied from Supabase Connect:

```powershell
$env:SUPABASE_DB_URL="jdbc:postgresql://YOUR_POOLER_HOST:5432/postgres?user=postgres.YOUR_PROJECT_REF&password=YOUR_DATABASE_PASSWORD&sslmode=require"
$env:JWT_SECRET="replace-with-a-random-secret-at-least-32-characters"
$env:FRONTEND_ORIGIN="http://localhost:5173"
$env:BOOTSTRAP_ADMIN_EMAIL="admin@barangayconnect.local"
$env:BOOTSTRAP_ADMIN_PASSWORD="ChooseAUniqueStrongPassword123!"
$env:BOOTSTRAP_ADMIN_NAME="BarangayConnect Administrator"
$env:BOOTSTRAP_ADMIN_MOBILE="09999999999"
mvn spring-boot:run
```

The bootstrap administrator is created only when both bootstrap email and password are configured and that email does not already exist. Keep all credentials and JWT secrets out of Git. Do not use sample credentials in a deployed environment.

Backend: `http://localhost:8080`  
Health check: `GET http://localhost:8080/api/v1/health`

The backend uses Hibernate schema update for this development increment. On first successful connection, it creates the application's tables in the Supabase PostgreSQL database. Do not use `ddl-auto=update` as a production migration strategy; use reviewed database migrations before production.

## 4. Run the frontend

Open a second terminal in `frontend`:

```powershell
npm install
npm run dev
```

Frontend: `http://localhost:5173`

The React application calls the Spring Boot API. It must not contain the Supabase database password or the JDBC connection string.

## API endpoints

| Method | Endpoint                          | Access               | Purpose                     |
| ------ | --------------------------------- | -------------------- | --------------------------- |
| POST   | `/api/v1/auth/register`           | Public               | Register resident           |
| POST   | `/api/v1/auth/login`              | Public               | Login with email/mobile     |
| GET    | `/api/v1/auth/me`                 | Authenticated        | Get current account         |
| POST   | `/api/v1/auth/logout`             | Authenticated        | Revoke current session      |
| PUT    | `/api/v1/auth/profile`            | Authenticated        | Update profile              |
| PUT    | `/api/v1/auth/password`           | Authenticated        | Change password             |
| GET    | `/api/v1/admin/users`             | System Administrator | List accounts               |
| POST   | `/api/v1/admin/officials`         | System Administrator | Create official account     |
| PATCH  | `/api/v1/admin/users/{id}/status` | System Administrator | Activate/deactivate account |
| GET    | `/api/v1/admin/audit-logs`        | System Administrator | View audit records          |

Protected endpoints require `Authorization: Bearer <token>`.

### Resident registration JSON

```json
{
  "fullName": "Juan Dela Cruz",
  "address": "Purok 1, Barangay Sample, Cebu City",
  "mobileNumber": "09123456789",
  "email": "juan@example.com",
  "password": "ChangeMe123"
}
```

Public registration always assigns `RESIDENT`; clients cannot self-assign privileged roles. Official roles are created by an authenticated System Administrator.

### Login JSON

```json
{
  "identifier": "juan@example.com",
  "password": "ChangeMe123"
}
```

## Role access

| Role                 | Workspace                                     |
| -------------------- | --------------------------------------------- |
| RESIDENT             | Resident dashboard                            |
| BARANGAY_SECRETARY   | Secretary dashboard                           |
| PUNONG_BARANGAY      | Punong Barangay dashboard                     |
| LUPON_MEMBER         | Lupon Member dashboard                        |
| SYSTEM_ADMINISTRATOR | Administration dashboard and account controls |

Backend authorization is enforced independently of frontend route protection.

## Security notes

- Use HTTPS in deployment and a strong, unique JWT secret.
- Keep the Supabase database password and complete JDBC URL only in backend environment variables or a secrets manager.
- Never commit local `.env` files, credentials, API keys, or production data.
- The current backend connects as a trusted database role and does not expose the database directly to the browser. If the Supabase Data API or a Supabase client is introduced later, enable RLS and write explicit least-privilege policies before exposing tables.
- Configure database migrations and `ddl-auto=validate` before production.
- This coursework MVP stores the bearer token in browser local storage. A production release should consider HttpOnly, Secure, SameSite cookies and corresponding CSRF protections.
- SMS OTP registration and password reset (FR-008 and FR-009) are Should Have items in the SRS and are not implemented in this increment.

## Verification checklist

1. Confirm the backend starts and connects to the Supabase database.
2. Confirm the application tables appear in Supabase Table Editor.
3. Register a resident with valid details.
4. Register the same email/mobile again and confirm duplicate rejection.
5. Confirm the database stores a BCrypt hash, not the original password.
6. Login with email and then mobile number.
7. Logout and verify the old token cannot access protected endpoints.
8. Submit five incorrect passwords and verify the 15-minute lock.
9. Login as bootstrap administrator and create official accounts for each official role.
10. Confirm a resident receives HTTP 403 on administrator endpoints.
11. Deactivate a user and verify protected access is denied.
12. Review audit logs for account and authentication events.

## Scope note

This increment establishes the authentication and role foundation. Guided case intake, complaint filing, jurisdiction verification, hearing management, settlement, documents, and reports are subsequent increments.

## Project Increment 2: User Profile and Password Management

The `increment-2-profile-password` branch extends the Increment 1 account foundation with:

- Authenticated users can view and update only their own name, address, and Philippine mobile number. Email remains read-only as the account identifier.
- Profile updates validate required values and reject duplicate mobile numbers.
- Change password requires the current password, a new password between 8 and 72 characters, and matching confirmation. Reusing the current password is rejected.
- Successful password changes update the BCrypt hash and revoke all active sessions, requiring a fresh login.
- Forgot password sends a 15-minute, single-use reset link to the registered email address. Only a SHA-256 hash of the random token is stored in PostgreSQL.
- Reset password checks token validity, expiry, single-use status, password length, and confirmation. Completing a reset changes the BCrypt hash and revokes all active sessions.
- Password reset requests return a generic response for unknown email addresses to avoid account enumeration.

### Increment 2 API

| Method | Endpoint                          | Access        | Purpose                    |
| ------ | --------------------------------- | ------------- | -------------------------- |
| GET    | `/api/v1/profile`                 | Authenticated | View own profile           |
| PUT    | `/api/v1/profile`                 | Authenticated | Update own profile         |
| PUT    | `/api/v1/profile/password`        | Authenticated | Change password            |
| POST   | `/api/v1/profile/forgot-password` | Public        | Request reset email        |
| POST   | `/api/v1/profile/reset-password`  | Public        | Complete reset using token |

### Configure SMTP for password recovery

Password recovery requires an SMTP account. Configure these backend environment variables before testing the forgot-password flow:

```powershell
$env:SMTP_HOST="smtp.example.com"
$env:SMTP_PORT="587"
$env:SMTP_USERNAME="your-smtp-username"
$env:SMTP_PASSWORD="your-smtp-password"
$env:SMTP_FROM="no-reply@your-verified-domain.com"
$env:FRONTEND_URL="http://localhost:5173"
```

Use credentials from a real SMTP provider or institutional email service. Do not commit SMTP credentials. The forgot-password endpoint returns HTTP 503 when SMTP is not configured. The reset token expires after 15 minutes and is single-use.

### Increment 2 verification checklist

1. Sign in with a test account and open **My profile & password**.
2. Confirm the profile displays the authenticated account's details.
3. Update the name, address, and mobile number; verify changes in Supabase.
4. Attempt to use another account's mobile number and confirm the update is rejected.
5. Change the password with an incorrect current password and confirm rejection.
6. Submit mismatched new-password confirmation and confirm rejection.
7. Change the password successfully and confirm the old session is revoked.
8. Sign in using the new password and confirm the old password no longer works.
9. Request a password reset for a registered email and confirm the email arrives.
10. Use the reset link once, then confirm a second use is rejected.
11. Wait beyond 15 minutes or test an expired token and confirm reset is rejected.
12. Confirm reset tokens are stored as hashes and password hashes are BCrypt.

Runtime testing requires the Supabase connection and SMTP variables to be configured locally. Do not use real personal accounts or passwords as submission test credentials.
