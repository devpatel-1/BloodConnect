# BloodConnect 🩸

> **Find a donor. Send a request. Save a life.**

BloodConnect is a native Android application designed to connect people who need blood with available blood donors in real time.

The project was developed for the **Mobile Application Development (2CEIT5PE18)** course at **Ganpat University**.

## 🎯 Objective

Finding a suitable blood donor during an emergency can be difficult when relying on phone contacts, WhatsApp groups, or static donor lists.

BloodConnect provides a centralized platform where users can register as donors, search for suitable donors, check their availability, and send blood requests directly through the application.

## ✨ Features

### 🔐 Authentication
- Email/password signup and login using Firebase Authentication
- Password recovery
- Secure logout

### 🩸 Donor Discovery
- Create donor profile with name, blood group, phone and city
- Real-time donor list using Cloud Firestore
- Search donors by **name, city, or blood group**
- Dashboard statistics with donor and blood-group counts
- Current user is excluded from the donor list

### 🟢 Availability
- Donors can mark themselves as available/unavailable
- Availability is stored in Firestore
- 90-day donation interval is considered when determining donor availability
- Unavailable donors cannot receive new blood requests

### 📨 Blood Requests
- Send a blood request to an available donor
- Prevent duplicate pending requests
- Prevent users from requesting themselves
- Donors can view incoming requests
- Donors can **Accept** or **Decline** requests
- Requesters can track request status through **My Requests**
- Request status updates in real time

### 🔒 Security & Reliability
- Firebase Authentication for user identity
- Firestore Security Rules for authorized database access
- Input validation on signup
- Loading states and error handling
- Real-time Firestore listeners with lifecycle management

## 🛠️ Tech Stack

| Technology | Usage |
|---|---|
| **Kotlin** | Android development |
| **Android Studio** | Development environment |
| **Android Views** | UI development |
| **ConstraintLayout** | Screen layouts |
| **RecyclerView** | Donor & request lists |
| **Material Components** | UI components |
| **Firebase Authentication** | User authentication |
| **Cloud Firestore** | Real-time database |
| **Kotlin Coroutines** | Asynchronous operations |
| **Git & GitHub** | Version control |

## 🏗️ Project Architecture

The project follows a simple layered structure:


## 📱 Screenshots

## 📱 Screenshots

| Login | Sign Up | Dashboard |
|---|---|---|
| ![Login](screenshots/1.png) | ![Sign Up](screenshots/2.png) | ![Dashboard](screenshots/3.png) |

| My Requests | My Profile |
|---|---|
| ![My Requests](screenshots/4.png) | ![My Profile](screenshots/5.png) |

```text
com.dev.bloodconnect/
│
├── data/
│   ├── User.kt
│   └── Request.kt
│
├── repository/
│   ├── AuthRepository.kt
│   ├── DonorRepository.kt
│   └── RequestRepository.kt
│
└── ui/
    ├── LoginActivity.kt
    ├── SignUpActivity.kt
    ├── DashboardActivity.kt
    ├── DonorDetailActivity.kt
    ├── ProfileActivity.kt
    ├── MyRequestsActivity.kt
    ├── IncomingRequestsActivity.kt
    ├── DonorAdapter.kt
    ├── RequestAdapter.kt
    └── IncomingRequestAdapter.kt

Data Flow

UI
 ↓
Repository
 ↓
Firebase
 ├── Authentication
 └── Firestore

☁️ Firestore Structure

users/{uid}
 ├── name
 ├── phone
 ├── bloodGroup
 ├── city
 ├── isAvailable
 ├── lastDonationDate
 └── createdAt

requests/{requestId}
 ├── requesterId
 ├── requesterName
 ├── donorId
 ├── donorName
 ├── bloodGroup
 ├── status
 └── createdAt

Request lifecycle:

Pending → Accepted
        → Declined


🧪 Tested Workflow

The complete donor-request workflow has been tested across two accounts:

Account A
    ↓
Send Blood Request
    ↓
Account B
    ↓
Incoming Request
    ↓
Accept / Decline
    ↓
Firestore
    ↓
Account A
    ↓
My Requests
    ↓
Updated Status

🚀 Setup
Clone the repository.
Open the project in Android Studio.
Create a Firebase project.
Enable Email/Password Authentication.
Create a Cloud Firestore Database.
Add google-services.json to the app/ directory.
Sync Gradle.
Build and run the application.
🔮 Future Improvements
🔔 Push notifications for new blood requests
🗺️ Map-based nearby donor discovery
🚨 SOS emergency broadcast
🏥 Hospital/organization accounts
📊 Donation history
🌙 Dark mode
🌐 Multi-language support
🎓 Academic Information

Project: BloodConnect
Course: Mobile Application Development (2CEIT5PE18)
University: Ganpat University
Platform: Android
Language: Kotlin

👨‍💻 Developer

Dev Patel
Computer Engineering — Ganpat University

<p align="center"> Made with ❤️ using Kotlin & Firebase </p> ```