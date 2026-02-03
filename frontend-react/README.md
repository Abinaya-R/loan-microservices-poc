# Loan Microservices Frontend (React)

## What This App Does
- Provides a UI to test the backend microservices.
- Covers user auth, account operations, and loan payments.
- Uses Chakra UI for layout and Formik + Yup for forms and validation.

## Requirements
- Node.js 18+
- Backend services running:
  - User Service: `http://localhost:8091`
  - Account Service: `http://localhost:8092`
  - Payment Service: `http://localhost:8093`

## Setup
1. Install dependencies:
```bash
npm install
```
2. Start the app:
```bash
npm run dev
```
3. Open the app at the URL shown in the terminal (usually `http://localhost:5173`).

## Environment Configuration
Edit `.env` if your backend ports differ:
```
VITE_USER_SERVICE_URL=http://localhost:8091
VITE_ACCOUNT_SERVICE_URL=http://localhost:8092
VITE_PAYMENT_SERVICE_URL=http://localhost:8093
```

## Typical Demo Flow
1. Register a user.
2. Login and copy the JWT token (it auto-fills the token box).
3. Create a deposit account for the user.
4. Credit or debit the account.
5. Create a loan account and make a loan payment.

## Key Files
- `src/App.jsx`: UI forms and handlers.
- `src/api/http.js`: shared fetch helpers.
- `src/api/users.js`: User Service calls.
- `src/api/accounts.js`: Account Service calls.
- `src/api/payments.js`: Payment Service calls.
