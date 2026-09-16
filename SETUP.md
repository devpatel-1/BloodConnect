# BloodConnect — External Service Setup

You only have to do this once. It takes about 10–15 minutes.

BloodConnect depends on one external service:

| Service | Used for | Cost |
|---|---|---|
| **Firebase** | Authentication and Cloud Firestore database | Free (Spark plan) |

Work through Part 1, then Part 2 to verify the setup.

---

## Part 1 — Firebase

### 1.1 Create the Firebase project

1. Go to <https://console.firebase.google.com> and sign in with a Google account.
2. Click **Create a project** (or **Add project**).
3. Project name: `BloodConnect`.
4. On the Google Analytics step, Analytics can be turned off since BloodConnect does not use it.
5. Click **Create project** and wait for the project to finish.
6. Click **Continue**.

---

### 1.2 Enable Email/Password Authentication

1. In the Firebase Console, open **Build → Authentication**.
2. Click **Get started**.
3. Open the **Sign-in method** tab.
4. Select **Email/Password**.
5. Enable **Email/Password**.
6. Leave **Email link (passwordless sign-in)** disabled.
7. Click **Save**.

BloodConnect uses Firebase Authentication for:

- User registration
- Login
- Password recovery
- Logout

---

### 1.3 Create Cloud Firestore

1. In the Firebase Console, open **Build → Firestore Database**.
2. Click **Create database**.
3. Select a suitable database location, preferably a nearby region.
4. For initial development, select **Start in test mode**.
5. Click **Create/Enable**.

> ⚠️ **Important:** Test mode is only for development. Before final submission, make sure proper Firestore Security Rules are configured.

BloodConnect stores user profiles and blood-request information in Firestore.

---

### 1.4 Register the Android app

1. Open **Project settings** using the gear icon.
2. Scroll to **Your apps**.
3. Click the **Android** icon.
4. Enter the Android package name:

```text

com.dev.bloodconnect

This must exactly match the applicationId in app/build.gradle.kts.

App nickname: BloodConnect (optional).
SHA-1 can be left blank because the current version uses Email/Password authentication.
Click Register app.
1.5 Download google-services.json
Click Download google-services.json.
Place the downloaded file inside the app/ folder.

The final structure should be:

BloodConnect/
└── app/
    └── google-services.json
Open Android Studio.
Sync the project with Gradle.

⚠️ Do not upload your real google-services.json to a public GitHub repository if your project setup requires keeping Firebase configuration private. Check your repository's .gitignore before committing.

Part 2 — Firestore Security Rules

BloodConnect uses Firestore Security Rules to control access to user profiles and blood requests.

The final rules should allow:

Users
Authenticated users can read donor profiles.
A user can create or update only their own profile.
Users cannot delete profiles.
Requests
Authenticated users can read requests.
A user can create a request only as themselves.
Only the receiving donor can accept or decline a request.
A pending request can only change to accepted or declined.
Users cannot delete requests.

The rules used by BloodConnect are:

rules_version = '2';

service cloud.firestore {
  match /databases/{database}/documents {

    match /users/{userId} {
      allow read: if request.auth != null;
      allow create, update: if request.auth != null
                            && request.auth.uid == userId;
      allow delete: if false;
    }

    match /requests/{requestId} {
      allow read: if request.auth != null;

      allow create: if request.auth != null
                    && request.resource.data.requesterId == request.auth.uid;

      allow update: if request.auth != null
                    && resource.data.donorId == request.auth.uid
                    && resource.data.status == "pending"
                    && request.resource.data.status in ["accepted", "declined"]
                    && request.resource.data.diff(resource.data).affectedKeys()
                       .hasOnly(["status"]);

      allow delete: if false;
    }

    match /{document=**} {
      allow read, write: if false;
    }
  }
}

After adding the rules, click Publish.

Part 3 — Verify It Worked
Open the BloodConnect project in Android Studio.
Click File → Sync Project with Gradle Files.
Run the application on an Android device or emulator with internet access.
The app should open on the Login screen.
Create a test account using Sign Up.
Check Firebase Console → Authentication to verify that the account was created.
Check Firebase Console → Firestore Database to verify that a user document was created.
Create a second test account and test the donor request workflow.

The basic workflow should be:

Sign Up
   ↓
Firebase Authentication
   ↓
User Profile → Firestore
   ↓
Dashboard
   ↓
Search Donor
   ↓
Send Request
   ↓
Incoming Request
   ↓
Accept / Decline
   ↓
My Requests
Troubleshooting
"Firebase Authentication failed"

Check that Email/Password authentication is enabled under:

Firebase Console → Authentication → Sign-in method

"PERMISSION_DENIED"

Check the Firestore Security Rules.

Make sure the latest BloodConnect rules have been published.

"google-services.json is missing"

Make sure the file is located exactly at:

app/google-services.json

It should not be placed in the project root.

"Donors are not loading"

Check:

Device/emulator has internet access.
User is logged in.
Firestore database has been created.
Firestore Security Rules allow authenticated reads.
"Requests are not working"

Check:

Both users are logged in correctly.
The donor's isAvailable field is true.
Firestore Security Rules are published.
The request document contains the correct requesterId and donorId.
Firebase Collections

BloodConnect uses two main Firestore collections:

users/
    {uid}
        name
        phone
        bloodGroup
        city
        isAvailable
        lastDonationDate
        createdAt

requests/
    {requestId}
        requesterId
        requesterName
        donorId
        donorName
        bloodGroup
        status
        createdAt