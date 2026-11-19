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
- Database: PostgreSQL 15+
- API: REST / JSON

## Prerequisites

Java 17+, Maven 3.9+, Node.js 20.19+ and npm, PostgreSQL 15+.

## 1. Create the database

Run in pgAdmin or psql:

```sql
CREATE DATABASE barangayconnect;
```

Hibernate creates the `user_accounts`, `login_sessions`, and `audit_logs` tables on first startup (development configuration).

## 2. Configure and run the backend

Open PowerShell in `backend`. Replace the example credentials:

```powershell
$env:DATABASE_URL="jdbc:postgresql://localhost:5432/barangayconnect"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="YOUR_POSTGRES_PASSWORD"
$env:JWT_SECRET="replace-with-a-random-secret-at-least-32-characters"
$env:FRONTEND_ORIGIN="http://localhost:5173"
$env:BOOTSTRAP_ADMIN_EMAIL="admin@barangayconnect.local"
$env:BOOTSTRAP_ADMIN_PASSWORD="ChangeThisAdminPassword123!"
$env:BOOTSTRAP_ADMIN_NAME="BarangayConnect Administrator"
$env:BOOTSTRAP_ADMIN_MOBILE="09999999999"
mvn spring-boot:run
```

The bootstrap administrator is created only when both bootstrap email and password are configured and that email does not already exist. Keep all credentials and JWT secrets out of Git. Do not use sample credentials in a deployed environment.

Backend: `http://localhost:8080`  
Health check: `GET http://localhost:8080/api/v1/health`

## 3. Run the frontend

Open a second terminal in `frontend`:

```powershell
npm install
npm run dev
```

Frontend: `http://localhost:5173`

For a remote backend, create a local `.env` file based on `.env.example` and set `VITE_API_URL` to the API base URL.

## API endpoints

| Method | Endpoint | Access | Purpose |
|---|---|---|---|
| POST | `/api/v1/auth/register` | Public | Register resident |
| POST | `/api/v1/auth/login` | Public | Login with email/mobile |
| GET | `/api/v1/auth/me` | Authenticated | Get current account |
| POST | `/api/v1/auth/logout` | Authenticated | Revoke current session |
| PUT | `/api/v1/auth/profile` | Authenticated | Update profile |
| PUT | `/api/v1/auth/password` | Authenticated | Change password |
| GET | `/api/v1/admin/users` | System Administrator | List accounts |
| POST | `/api/v1/admin/officials` | System Administrator | Create official account |
| PATCH | `/api/v1/admin/users/{id}/status` | System Administrator | Activate/deactivate account |
| GET | `/api/v1/admin/audit-logs` | System Administrator | View audit records |

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

| Role | Workspace |
|---|---|
| RESIDENT | Resident dashboard |
| BARANGAY_SECRETARY | Secretary dashboard |
| PUNONG_BARANGAY | Punong Barangay dashboard |
| LUPON_MEMBER | Lupon Member dashboard |
| SYSTEM_ADMINISTRATOR | Administration dashboard and account controls |

Backend authorization is enforced independently of frontend route protection.

## Security notes

- Use HTTPS in deployment and a strong, unique JWT secret.
- Use a dedicated PostgreSQL account with least-privilege permissions.
- Never commit local `.env` files, credentials, API keys, or production data.
- Configure database migrations and `ddl-auto=validate` before production.
- This coursework MVP stores the bearer token in browser local storage. A production release should consider HttpOnly, Secure, SameSite cookies and corresponding CSRF protections.
- SMS OTP registration and password reset (FR-008 and FR-009) are Should Have items in the SRS and are not implemented in this increment.

## Verification checklist

1. Register a resident with valid details.
2. Register the same email/mobile again and confirm duplicate rejection.
3. Confirm the database stores a BCrypt hash, not the original password.
4. Login with email and then mobile number.
5. Logout and verify the old token cannot access protected endpoints.
6. Submit five incorrect passwords and verify the 15-minute lock.
7. Login as bootstrap administrator and create official accounts for each official role.
8. Confirm a resident receives HTTP 403 on administrator endpoints.
9. Deactivate a user and verify protected access is denied.
10. Review audit logs for account and authentication events.

## Scope note

This increment establishes the authentication and role foundation. Guided case intake, complaint filing, jurisdiction verification, hearing management, settlement, documents, and reports are subsequent increments.
