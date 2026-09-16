# BloodConnect — Product Requirements Document

**Course:** 2CEIT5PE18 — Mobile Application Development (Semester V, CE/IT/CE-AI)  
**Faculty:** Prof. Hiten M Sadani, Ganpat University  
**Author:** Dev Patel  
**Presentation deadline:** On or before 21/09/2026  
**Submission deadline:** 21/09/2026, 8:00 AM (Google Form)

---

## 1. Overview

**One-line pitch:** A real-time blood donor connection app that helps users find available donors, send blood requests, and track responses across devices.

**Problem:** Finding a suitable blood donor during an emergency can be difficult when relying on phone contacts, WhatsApp groups, or static donor lists. These methods are difficult to search and do not provide a proper way to track donor availability or request status.

**Solution:** BloodConnect provides a centralized Android application where users can create donor profiles, search donors by blood group, city, or name, check availability, and send blood requests. Donors can receive, accept, or decline requests while requesters can track their request status.

---

## 2. Goals

- Make blood donor discovery simple and organized
- Allow users to search donors by name, city, and blood group
- Allow donors to manage their availability
- Provide a complete blood request and response workflow
- Synchronize donor and request information in real time
- Prevent duplicate pending and self-requests
- Use Firebase authentication and database security
- Deliver a functional Android application for the course

---

## 3. Non-Goals (out of scope)

- Online payments or financial transactions
- Medical diagnosis or medical advice
- Hospital or blood-bank management
- In-app messaging/chat
- Automatic donor identity verification
- GPS-based live donor tracking
- Push notifications in the current version
- iOS or web application

---

## 4. Target Users

- People looking for blood donors
- People willing to donate blood
- Users who want to manage their donor availability
- Students and general users who need a simple donor-search platform

The same user can act as both a requester and a donor.

---

## 5. Key Decisions Log

| Area | Decision | Rationale |
|---|---|---|
| Authentication | Firebase Email/Password | Simple and secure user authentication |
| Database | Cloud Firestore | Cloud storage with real-time synchronization |
| User identity | Firebase UID | Unique identity for every authenticated user |
| Request storage | Separate `requests` collection | Keeps donor profiles and requests separate |
| Search | Name, city and blood group | Helps users quickly find suitable donors |
| Availability | Stored in user profile | Allows availability to be updated across devices |
| Request status | Pending / Accepted / Declined | Simple and clear request lifecycle |
| Security | Firestore Security Rules | Prevents unauthorized database changes |
| UI | Native Android Views | Suitable for the course and Android development |

---

## 6. Tech Stack

- **Language:** Kotlin
- **UI:** Android Views
- **Layouts:** ConstraintLayout
- **Lists:** RecyclerView
- **UI Components:** Material Components
- **Authentication:** Firebase Authentication
- **Database:** Cloud Firestore
- **Async Operations:** Kotlin Coroutines
- **IDE:** Android Studio
- **Version Control:** Git & GitHub

---

## 7. Data Model

### `users` collection

| Field | Type | Description |
|---|---|---|
| uid | String | Firebase Authentication UID |
| name | String | User's name |
| phone | String | Contact number |
| bloodGroup | String | Blood group |
| city | String | User's city |
| isAvailable | Boolean | Donor availability |
| lastDonationDate | Timestamp | Previous donation date |
| createdAt | Timestamp | Account creation time |

### `requests` collection

| Field | Type | Description |
|---|---|---|
| requesterId | String | Requester's UID |
| requesterName | String | Requester's name |
| donorId | String | Donor's UID |
| donorName | String | Donor's name |
| bloodGroup | String | Required blood group |
| status | String | Pending / Accepted / Declined |
| createdAt | Timestamp | Request creation time |

---

## 8. Screens

1. **Login** — Email/password authentication
2. **Sign Up** — User and donor information
3. **Dashboard** — Search, statistics and donor list
4. **Donor Detail** — Donor information and request action
5. **My Profile** — Profile information and availability
6. **My Requests** — Track sent blood requests
7. **Incoming Requests** — Donors receive and respond to requests

---

## 9. Core Workflow

```text
User A
  ↓
Search Donor
  ↓
View Donor Details
  ↓
Send Blood Request
  ↓
Firestore
  ↓
User B
  ↓
Incoming Request
  ↓
Accept / Decline
  ↓
Firestore
  ↓
User A
  ↓
My Requests
  ↓
Updated Status

Request lifecycle:

Pending → Accepted
        → Declined

10. Security & Validation

Firebase Authentication protects user accounts
Firestore Security Rules control database access
Users can modify only their own profiles
Only the receiving donor can accept or decline a request
Request status can only change from pending to accepted or declined
Duplicate pending requests are prevented
Self-requests are prevented
Signup fields are validated before account creation

11. Success Criteria

Users can successfully register and log in
Donors can create and manage their profiles
Donors can be searched by name, city and blood group
Availability changes are reflected correctly
Blood requests can be sent successfully
Donors can accept or decline requests
Requesters can see updated request status
Firebase security rules prevent unauthorized operations
The complete workflow works across multiple accounts/devices

12. Future Work

Push notifications for new blood requests
Map-based nearby donor discovery
SOS emergency broadcast
Hospital and blood-bank accounts
Donation history
Donor contribution statistics
Multi-language support
Dark-mode support