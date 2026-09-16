# Software Requirements Specification — BloodConnect 🩸

**Course:** 2CEIT5PE18 — Mobile Application Development (Semester V, CE/IT/CE-AI)  
**Faculty:** Prof. Hiten M Sadani, Ganpat University  
**Author:** Dev Patel  
**Version:** 1.0  
**Standard followed:** IEEE 830

---

# 1. Introduction

## 1.1 Purpose

This document specifies the software requirements for BloodConnect, a native Android application that connects people who need blood with available blood donors. It describes the functional and non-functional requirements, system interfaces, database structure, security requirements, and overall system behavior.

The document is intended for development, testing, documentation, and faculty evaluation as part of the Mobile Application Development course.

## 1.2 Scope

BloodConnect is an Android application built using Kotlin and Firebase. It allows users to create donor profiles, search for donors by blood group, city, or name, check donor availability, and send blood requests.

Donors can receive incoming requests and accept or decline them. Requesters can track the status of their requests through the My Requests screen.

The application uses Firebase Authentication for user authentication and Cloud Firestore for cloud data storage and real-time synchronization.

## 1.3 Definitions, Acronyms, and Abbreviations

| Term | Definition |
|---|---|
| **MVP** | Minimum Viable Product |
| **UID** | Unique identifier assigned to a Firebase Authentication user |
| **Firestore** | Google Cloud Firestore NoSQL cloud database |
| **Firebase Auth** | Firebase Authentication service |
| **CRUD** | Create, Read, Update, Delete |
| **SRS** | Software Requirements Specification |

## 1.4 References

- Mobile Application Development course requirements — 2CEIT5PE18
- Firebase Authentication documentation
- Firebase Cloud Firestore documentation
- Android Developers documentation
- BloodConnect project source code

## 1.5 Overview

Section 2 describes the overall product, users, functions, constraints, and assumptions.

Section 3 defines the specific functional and non-functional requirements of BloodConnect.

Section 4 contains supporting appendices and system models.

---

# 2. Overall Description

## 2.1 Product Perspective

BloodConnect is a standalone Android application that communicates with Firebase services.

```text

Android Application
        |
        +----------------------+
        |                      |
        v                      v
Firebase Authentication    Cloud Firestore
        |                      |
        v                      v
    User Login            Users / Requests


System Interfaces
Firebase Authentication
Firebase Cloud Firestore
User Interface
Native Android Views
ConstraintLayout
RecyclerView
Material Components
Hardware Interfaces
Android smartphone/tablet
Internet connectivity
Software Interfaces
Android SDK
Firebase Authentication SDK
Firebase Firestore SDK
Kotlin Coroutines
Communications

All communication with Firebase services is performed through secure network connections.

2.2 Product Functions

BloodConnect provides the following major functions:

User registration and login
Donor profile creation
Donor availability management
Real-time donor listing
Search by name, city, and blood group
Donor details
Blood request creation
Incoming blood requests
Accepting or declining requests
Request status tracking
Duplicate request prevention
Self-request prevention
Firestore security controls
Logout

2.3 User Characteristics

The application is designed for general smartphone users who need blood or are willing to donate blood.

Users are expected to have:

Basic smartphone knowledge
Internet connectivity
A valid email address
A valid phone number

There are two functional roles within the application:

Requester — searches for donors and sends blood requests.
Donor — maintains availability and responds to incoming requests.

The same registered user can perform both roles.

2.4 Constraints

The application must run on Android.
The application must be developed using Kotlin.
Firebase is used as the backend infrastructure.
Internet connectivity is required for cloud operations.
Firebase Firestore Security Rules must restrict unauthorized database operations.
The application targets Android devices with minimum SDK 24.

2.5 Assumptions and Dependencies

Users have internet access.
Firebase Authentication and Firestore services are available.
Users provide accurate blood group and contact information.
Donors correctly maintain their availability status.
Firebase configuration is correctly added to the Android project.

3. Specific Requirements

3.1 External Interface Requirements
User Interface

The application provides the following main screens:

Login
Sign Up
Dashboard
Donor Details
Profile
My Requests
Incoming Requests

The application uses native Android UI components including Material input fields, buttons, RecyclerView, cards, and ConstraintLayout.

Software Interfaces
Firebase Authentication
Cloud Firestore
Android SDK
Kotlin Coroutines
Communications Interface

The application communicates with Firebase over secure network connections.

3.2 Functional Requirements

Authentication — FR-AUTH
ID	Requirement	Priority

FR-AUTH-001	The system shall allow a user to register using email and password.	M
FR-AUTH-002	The system shall collect name, phone, blood group, and city during registration.	M
FR-AUTH-003	The system shall validate user input before registration.	M
FR-AUTH-004	The system shall allow registered users to log in using email and password.	M
FR-AUTH-005	The system shall support password recovery through Firebase Authentication.	M
FR-AUTH-006	The system shall allow authenticated users to log out.	M

Donor Management — FR-DONOR
ID	Requirement	Priority

FR-DONOR-001	The system shall create a donor profile after successful registration.	M
FR-DONOR-002	The system shall store donor name, phone, blood group, city, and availability in Firestore.	M
FR-DONOR-003	The system shall display donors in a real-time donor list.	M
FR-DONOR-004	The system shall exclude the currently logged-in user from the donor list.	M
FR-DONOR-005	The system shall allow users to view donor details.	M
FR-DONOR-006	The system shall display the donor's availability status.	M

Search — FR-SEARCH
ID	Requirement	Priority

FR-SEARCH-001	The system shall allow users to search donors by name.	M
FR-SEARCH-002	The system shall allow users to search donors by city.	M
FR-SEARCH-003	The system shall allow users to search donors by blood group.	M
FR-SEARCH-004	The system shall normalize search input by ignoring unnecessary spaces and letter case.	S
FR-SEARCH-005	The system shall display an appropriate message when no donors match the search.	S
FR-SEARCH-006	The system shall display donor statistics including total donors and blood-group counts.	S

Availability — FR-AVAIL
ID	Requirement	Priority

FR-AVAIL-001	The system shall allow donors to mark themselves as available or unavailable.	M
FR-AVAIL-002	The system shall store donor availability in Firestore.	M
FR-AVAIL-003	The system shall prevent blood requests from being sent to unavailable donors.	M
FR-AVAIL-004	The system shall consider the donor's last donation date when determining availability.	M
FR-AVAIL-005	The system shall use a 90-day donation interval in the availability calculation.	M

Blood Requests — FR-REQ
ID	Requirement	Priority

FR-REQ-001	The system shall allow an authenticated user to send a blood request to an available donor.	M
FR-REQ-002	The system shall store requester and donor information with each request.	M
FR-REQ-003	The system shall assign a new request the status pending.	M
FR-REQ-004	The system shall prevent a user from sending a request to themselves.	M
FR-REQ-005	The system shall prevent duplicate pending requests between the same requester and donor.	M
FR-REQ-006	The system shall display incoming requests to the selected donor.	M
FR-REQ-007	The donor shall be able to accept a pending request.	M
FR-REQ-008	The donor shall be able to decline a pending request.	M
FR-REQ-009	The system shall update the request status after the donor responds.	M
FR-REQ-010	The requester shall be able to view sent requests and their current status.	M
FR-REQ-011	The system shall store the request creation timestamp.	M
FR-REQ-012	The system shall synchronize request updates in real time.	M

Security — FR-SEC
ID	Requirement	Priority

FR-SEC-001	The system shall require authentication for protected operations.	M
FR-SEC-002	A user shall only be able to modify their own user profile.	M
FR-SEC-003	A requester shall only create requests on their own behalf.	M
FR-SEC-004	Only the donor associated with a request shall be allowed to accept or decline it.	M
FR-SEC-005	Request status shall only be changed from pending to accepted or declined.	M
FR-SEC-006	Unauthorized database operations shall be denied by Firestore Security Rules.	M

3.3 Performance Requirements

Donor and request lists should load without noticeable delay under normal internet conditions.
Firestore snapshot listeners should provide real-time updates when data changes.
User interface operations should not be blocked while Firebase operations are running.
Loading indicators should be displayed during important asynchronous operations.

3.4 Design Constraints

The application shall be developed using Kotlin.
The application shall run on Android.
Android Views shall be used for the user interface.
Firebase Authentication shall be used for authentication.
Cloud Firestore shall be used for application data.
RecyclerView shall be used for displaying donor and request lists.
Firebase Security Rules shall be used to control database access.

3.5 Software System Attributes

Security

Firebase Authentication and Firestore Security Rules are used to protect user and request data.

Reliability

The application handles Firebase operation failures and displays appropriate error messages to the user.

Usability

The application uses familiar Android UI components and clear navigation.

Maintainability

The application separates UI, data models, and Firebase operations into different packages:

data/
repository/
ui/

This makes the code easier to maintain and extend.

3.6 Database Requirements

The system uses Cloud Firestore with two primary collections.

Users
users/{uid}

├── uid
├── name
├── phone
├── bloodGroup
├── city
├── isAvailable
├── lastDonationDate
└── createdAt
Requests
requests/{requestId}

├── requesterId
├── requesterName
├── donorId
├── donorName
├── bloodGroup
├── status
└── createdAt

Request status values:

pending
accepted
declined

4. Appendices

Appendix A — Glossary

Term	Meaning
Donor	A registered user willing to donate blood
Requester	A user requesting blood from a donor
Blood Request	A request sent from a requester to a donor
Availability	Indicates whether a donor is currently available
Firestore	Cloud NoSQL database used by BloodConnect
UID	Unique Firebase Authentication user identifier

Appendix B — System Flow

User
 ↓
Login / Signup
 ↓
Dashboard
 ↓
Search Donor
 ↓
Donor Details
 ↓
Send Request
 ↓
Firestore
 ↓
Incoming Requests
 ↓
Accept / Decline
 ↓
Firestore
 ↓
My Requests
 ↓
Updated Status

Appendix C — Request Lifecycle

              ┌─────────────┐
              │   Pending   │
              └──────┬──────┘
                     │
              ┌──────┴──────┐
              ↓             ↓
       ┌────────────┐ ┌────────────┐
       │  Accepted  │ │  Declined  │
       └────────────┘ └────────────┘

This SRS describes the current implemented scope of BloodConnect and should be updated if the project scope changes.


