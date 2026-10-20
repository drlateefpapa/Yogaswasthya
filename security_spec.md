# Security Specification — PranaVeda Yoga Therapy

## 1. Data Invariants
1. **User Isolation & Multi-Tenancy**: Every document in `/users/{userId}`, `/users/{userId}/recovery_logs/{logId}`, and `/users/{userId}/practice_sessions/{sessionId}` belongs exclusively to `userId`. Only an authenticated user with `request.auth.uid == userId` and `resource.data.userId == request.auth.uid` can read, query (`list`), create, update, or delete their records.
2. **Strict Schema & Bounds Validation**: Every `create` and `update` operation must pass `isValidPatientProfile(incoming())`, `isValidRecoveryLog(incoming())`, or `isValidPracticeSession(incoming())` with strict key allowlists (`hasAll` and `hasOnly`), string length bounds (`.size()`), numeric range bounds (`0..10`, `1..10`, `0..240`, etc.), and bounded lists (`size() <= 10` or `size() <= 30`).
3. **Immutable Ownership & Creation Timestamps**: `userId` and `createdAt` cannot be modified on `update` (`incoming().userId == existing().userId && incoming().createdAt == existing().createdAt`).
4. **Server Timestamp Enforcement**: `createdAt` and `updatedAt` must be valid Firestore `timestamp` objects with `<= request.time`.

## 2. The "Dirty Dozen" Payloads
1. **Unauthenticated Profile Read**: `unauthDb.doc("users/alice_123").get()` -> Reject (`request.auth != null`).
2. **Cross-User Profile Read (PII/Health Isolation)**: Bob reading `users/alice_123` -> Reject (`request.auth.uid == userId`).
3. **Identity Spoofing on Create**: Alice writing `userId: "bob_456"` inside `users/alice_123/recovery_logs/log_1` -> Reject (`incoming().userId == request.auth.uid`).
4. **Shadow Update / Ghost Field Injection**: Adding `isDoctorVerified: true` to a recovery log -> Reject via `hasOnly()`.
5. **Value Poisoning (Out-of-Bounds Pain Score)**: Writing `painLevel: 999` or `painLevel: "high"` -> Reject via `isValidRecoveryLog()`.
6. **Denial of Wallet (Oversized Notes String)**: Writing a 5,000-char `notes` string -> Reject (`data.notes.size() <= 500`).
7. **Unbounded Array Injection**: Writing 50 items in `completedPractices` -> Reject (`data.completedPractices.size() <= 30`).
8. **Immortal Field Mutation**: Updating `createdAt` or `userId` on an existing `RecoveryLog` -> Reject (`incoming().createdAt == existing().createdAt`).
9. **Future Timestamp Spoofing**: Writing a timestamp far in the future (`> request.time`) -> Reject (`<= request.time`).
10. **Invalid Enum Category in PracticeSession**: Writing `category: "Gym"` instead of `["Asana", "Pranayama", "Dhyana"]` -> Reject.
11. **Unfiltered Collection Query (Query Enforcer)**: Querying `recovery_logs` without matching `userId` or across another user's path -> Reject (`resource.data.userId == request.auth.uid`).
12. **Required Field Deletion via Update**: Deleting `conditionId` or `painLevel` during partial update -> Reject (`isValidRecoveryLog(incoming())` checks post-write state).
