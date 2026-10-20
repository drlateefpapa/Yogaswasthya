const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (
  process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085"
).split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

function validProfilePayload(uid = ALICE_UID) {
  const now = new Date();
  return {
    userId: uid,
    displayName: "Aarav Sharma",
    age: 42,
    heightCm: 170,
    weightKg: 72,
    knownDiseasesOrMedications: "Cervical Spondylitis, Vitamin D3 60000 IU",
    emailId: "aarav.sharma@example.com",
    phoneNumber: "+91 98765 43210",
    primaryConditionId: "cervical_lumbar_spondylitis",
    activeConditionIds: ["cervical_lumbar_spondylitis", "anxiety"],
    baselinePainScore: 7,
    baselineMobilityScore: 4,
    baselineStressScore: 6,
    dietaryPreference: "Sattvic Anti-Inflammatory",
    dailyGoalMinutes: 30,
    createdAt: now,
    updatedAt: now,
  };
}

function validRecoveryLogPayload(uid = ALICE_UID) {
  const now = new Date();
  return {
    userId: uid,
    conditionId: "cervical_lumbar_spondylitis",
    dateLabel: "2026-10-20",
    painLevel: 4,
    stiffnessLevel: 3,
    stressLevel: 3,
    mobilityScore: 7,
    sleepQuality: 8,
    asanaMinutes: 20,
    pranayamaMinutes: 10,
    dhyanaMinutes: 10,
    completedPractices: ["bhujangasana", "nadi_shodhana", "yoga_nidra"],
    dietAdherenceScore: 90,
    waterIntakeGlasses: 8,
    notes: "Neck stiffness reduced significantly after Bhujangasana and Nadi Shodhana.",
    createdAt: now,
    updatedAt: now,
  };
}

function validSessionPayload(uid = ALICE_UID) {
  const now = new Date();
  return {
    userId: uid,
    conditionId: "cervical_lumbar_spondylitis",
    practiceId: "bhujangasana",
    practiceTitle: "Bhujangasana (Gentle Cobra Pose)",
    category: "Asana",
    durationMinutes: 15,
    reliefRating: 5,
    createdAt: now,
    updatedAt: now,
  };
}

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

// --- DIRTY DOZEN & SECURITY BOUNDS TESTS ---

test("1. Unauthenticated user cannot read profile or recovery logs", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(
    unauthDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("recovery_logs")
      .get()
  );
});

test("2. Authenticated user can create and read their own PatientProfile", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set(validProfilePayload(ALICE_UID))
  );
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).get());
});

test("3. Cross-user isolation: Bob cannot read or write Alice's profile or logs", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("recovery_logs")
      .doc("log_1")
      .set(validRecoveryLogPayload(ALICE_UID))
  );

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(
    bobDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("recovery_logs")
      .doc("log_1")
      .get()
  );
  await assertFails(
    bobDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("recovery_logs")
      .where("userId", "==", ALICE_UID)
      .get()
  );
});

test("4. Identity spoofing: Alice cannot create a log with Bob's userId", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("recovery_logs")
      .doc("log_spoof")
      .set(validRecoveryLogPayload(BOB_UID))
  );
});

test("5. Shadow update / ghost field injection is rejected", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("recovery_logs")
      .doc("log_ghost")
      .set({
        ...validRecoveryLogPayload(ALICE_UID),
        isDoctorVerified: true,
      })
  );
});

test("6. Value poisoning: out-of-bounds painLevel or invalid category is rejected", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("recovery_logs")
      .doc("log_bad_pain")
      .set({
        ...validRecoveryLogPayload(ALICE_UID),
        painLevel: 99,
      })
  );

  await assertFails(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("practice_sessions")
      .doc("session_bad_cat")
      .set({
        ...validSessionPayload(ALICE_UID),
        category: "Weightlifting",
      })
  );
});

test("7. Client query alignment: owner can query recovery_logs and practice_sessions with userId filter", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("recovery_logs")
      .doc("log_1")
      .set(validRecoveryLogPayload(ALICE_UID))
  );
  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("practice_sessions")
      .doc("sess_1")
      .set(validSessionPayload(ALICE_UID))
  );

  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("recovery_logs")
      .where("userId", "==", ALICE_UID)
      .get()
  );
  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("practice_sessions")
      .where("userId", "==", ALICE_UID)
      .get()
  );
});
