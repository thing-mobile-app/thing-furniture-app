<div align="center">

<img src="assets/thing_logo_320.png" alt="thing. — Home Furniture Marketplace" width="320"/>

# Software Architecture Document

**Home Furniture Marketplace**

`SWE332 — Software Architecture` · `Altınbaş University` · `Spring 2026` · `Team thing.`

---

![Android](https://img.shields.io/badge/Android-3DDC84?style=flat-square&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=flat-square&logo=kotlin&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=flat-square&logo=firebase&logoColor=black)
![Architecture](https://img.shields.io/badge/Architecture-4%2B1-4A90E2?style=flat-square)
![Min SDK](https://img.shields.io/badge/Min_SDK-24-4A90E2?style=flat-square&logo=android&logoColor=white)
![Course](https://img.shields.io/badge/Course-SWE332-6C3483?style=flat-square)

</div>

---

## Change History

| Version | Date | Author | Description |
|---------|------|--------|-------------|
| 1.0 | March 2026 | Team thing. | Initial document created |
| 2.0 | April 2026 | Team thing. | Refined and expanded non-architectural sections, including Scope, References, Goals & Constraints, Size & Performance, and Quality attributes.|
| 3.0 | April 2026 | Team thing. / Hasan Açıkel | Added Process View including thread model, concurrency design, authentication and purchase flow sequence diagrams, and end-to-end activity diagram. |
| 4.0 | April 2026 | Team thing. / Hasan Açıkel | Added Physical View including deployment diagram, Firebase infrastructure, network communication, and permissions. |
| 5.0 | April 2026 | Team thing. / Samed Tevin | Added Development View including layered architecture, package diagram, component diagram, and navigation flow. |
---

## Table of Contents

1. [Scope](#1-scope)
2. [References](#2-references)
3. [Software Architecture](#3-software-architecture)
4. [Architectural Goals & Constraints](#4-architectural-goals--constraints)
5. [Logical View](#5-logical-view)
6. [Process View](#6-process-view)
7. [Development View](#7-development-view)
8. [Physical View](#8-physical-view)
9. [Scenarios (Use Case View)](#9-scenarios-use-case-view)
10. [Size & Performance](#10-size--performance)
11. [Quality](#11-quality)

**Appendices**
- [A — Acronyms & Abbreviations](#appendix-a--acronyms--abbreviations)
- [B — Definitions](#appendix-b--definitions)
- [C — Design Principles](#appendix-c--design-principles)

---

## 1. Scope

## Scope

**thing.** is a mobile-first Android application designed to provide a platform for browsing and purchasing high-quality home furniture products such as chairs, cupboards, tables, and accessories.

The system enables users to explore product listings, view detailed information, manage a shopping cart, and complete purchase operations.

The application emphasizes a simple, responsive, and user-friendly experience, supported by real-time data updates and modern Android design principles.

This document follows the 4+1 architectural view model proposed by Philippe Kruchten.
---

## 2. References

| ID | Title / Source | Link | Relevance |
|----|----------------|------|-----------|
| R1 | Android Developer Documentation — Activity & Fragment Lifecycle | [developer.android.com](https://developer.android.com/guide/components/activities/activity-lifecycle) | Navigation & lifecycle architecture |
| R2 | Firebase Documentation — Firestore | [firebase.google.com/docs/firestore](https://firebase.google.com/docs/firestore) | Primary database service |
| R3 | Firebase Documentation — Authentication | [firebase.google.com/docs/auth](https://firebase.google.com/docs/auth) | Identity management |
| R4 | Firebase Documentation — Storage | [firebase.google.com/docs/storage](https://firebase.google.com/docs/storage) | Image and media file storage |
| R5 | AndroidX Navigation Component Docs | [developer.android.com/guide/navigation](https://developer.android.com/guide/navigation) | In-app navigation graph |
| R6 | Material Design 3 Guidelines | [m3.material.io](https://m3.material.io) | UI component library |
| R7 | Firebase Crashlytics SDK Docs | [firebase.google.com/docs/crashlytics](https://firebase.google.com/docs/crashlytics) | Crash reporting integration |
| R8 | Glide Image Loading Library | [github.com/bumptech/glide](https://github.com/bumptech/glide) | Image caching and loading |
| R9 | Kotlin Coroutines Guide | [kotlinlang.org/docs/coroutines-guide.html](https://kotlinlang.org/docs/coroutines-guide.html) | Async background operations |
| R10 | AndroidX Lifecycle — ViewModel & LiveData | [developer.android.com/topic/libraries/architecture/viewmodel](https://developer.android.com/topic/libraries/architecture/viewmodel) | MVVM state management |
| R11 | 4+1 Architectural View Model — Wikipedia | [en.wikipedia.org/wiki/4%2B1_architectural_view_model](https://en.wikipedia.org/wiki/4%2B1_architectural_view_model) | Architecture viewpoint framework overview |
| R12 | Architectural Blueprints — The "4+1" View Model of Software Architecture, Philippe Kruchten | [cs.ubc.ca/~gregor/teaching/papers/4+1view-architecture.pdf](https://www.cs.ubc.ca/~gregor/teaching/papers/4+1view-architecture.pdf) | Primary architecture viewpoint framework |
| R13 | SWE332 Course Slides — Altinbas University, Spring 2026 | — | Architecture viewpoint framework |

---

## 3. Software Architecture

thing. is a **native Android application** following the **Single-Activity, Multi-Fragment** architectural pattern. One Activity host manages all buyer-facing screens.

### 3.1 High-Level System Overview

The system is structured around the **4+1 Architectural View Model** (R11, R12):

| View | Concern | Primary Stakeholders |
|------|---------|----------------------|
| **Logical** | Functionality — classes, domain model, state | Analysts, designers |
| **Process** | Runtime behaviour — concurrency, sequences, activities | Integrators, performance engineers |
| **Development** | Software organisation — packages, components | Developers, project managers |
| **Physical** | Deployment topology — nodes, connectors | System engineers, DevOps |
| **Scenarios** | Use cases — end-to-end walkthroughs | All stakeholders |
---
### 3.2 Architectural Style

The system follows a **client-cloud** architecture. The Android client contains all UI and local business logic, while Firebase acts as the serverless backend for data persistence, identity management, and file storage. There is no custom application server — all backend communication happens through Firebase SDKs embedded in the client.

Within the Android client, the **AndroidX Navigation Component** manages a single Fragment back stack per Activity host, keeping screen transitions decoupled and the back stack predictable.

---

## 4. Architectural Goals & Constraints
---
### 4.1 Goals

| Goal | Description |
|------|-------------|
| **Scalability** | Firebase Firestore scales horizontally — no server provisioning needed as user counts grow. |
| **Usability** | Material Design 3 delivers a familiar, accessible experience across Android versions (API 24+). |
| **Real-Time Updates** | Firestore live listeners push product and order changes to the UI without polling. |
| **Reliability** | Firebase Crashlytics captures uncaught exceptions in production from day one. |
| **Offline Tolerance** | Firestore's local cache allows read access during intermittent connectivity. |
| **Extensibility** | Architecture is designed to accommodate new merchant and admin features in future releases without restructuring the core. |
---
### 4.2 Constraints

| Constraint | Impact |
|------------|--------|
| **Android-only (API 24–35 — Android 7.0–15)** | No iOS or web client in v1.0; limits initial reach. |
| **Firebase lock-in** | All persistence, auth, and storage are Firebase-dependent; migration would be costly. |
| **No custom backend** | Business rules enforced client-side or via Firestore Security Rules. |
| **Google Play Services required** | Google Sign-In requires Play Services on device. |
| **Academic scope** | Package `com.example.*` marks this as a prototype build, not a production-signed release. |

---

## 5. Logical View
---

## 6. Process View
The process view deals with the dynamic aspects of the system — runtime concurrency, communication, and control flow.
---
### 6.1 Thread Model & Concurrency

thing. runs multiple concurrent execution contexts, each with a clearly defined responsibility. Understanding which work happens on which thread — and why — is essential to evaluating the system's responsiveness and correctness.

| Thread / Context | Responsibility | Parallelism Role |
|------------------|----------------|------------------|
| **Main (UI) Thread** | Fragment rendering, user input, Navigation Component transitions | Single-threaded; all UI mutations must occur here |
| **Firebase SDK Thread Pool** | Firestore network I/O, Auth token refresh, Storage transfers | Runs in parallel with the UI thread; results are posted back via callbacks or Tasks |
| **WorkManager / JobScheduler** | DataTransport batch jobs — Crashlytics upload, analytics | Background; scheduled by OS; runs concurrently with user sessions |
| **Kotlin Coroutines (ViewModel scope)** | Async wrappers over Firebase Tasks inside ViewModel lifecycles | Structured concurrency; coroutines are cancelled when ViewModel is cleared |

**Why Kotlin Coroutines?**

Firebase's Java SDK exposes asynchronous results via `Task<T>` callbacks. Coroutines allow ViewModels to `await()` these Tasks using `suspendCancellableCoroutine`, transforming callback-style code into linear, readable suspend functions — without blocking the Main thread. Coroutines launched in `viewModelScope` are automatically cancelled when the Fragment that owns the ViewModel is destroyed, preventing memory leaks and stale UI updates.

**Parallel operations in the purchase flow:**

During checkout, the following operations may run concurrently:
- Firestore's real-time snapshot listener (background thread) continuing to push catalogue updates while the user edits their address.
- The `placeOrder` coroutine writing the order document to Firestore (Firebase thread pool), while the Main thread renders the BillingFragment confirmation UI.
- Crashlytics DataTransport flushing any buffered telemetry via JobScheduler, independent of both.

This separation ensures that a slow network write to Firestore does not freeze the UI, and that background telemetry work never competes with foreground user interactions on the Main thread.
---
### 6.2 Authentication Sequence Diagram

```mermaid
sequenceDiagram
    actor User
    participant LA as LunchActivity
    participant FBAUTH as Firebase Auth
    participant FBUI as FirebaseUI Auth
    participant FS as Firestore
    participant SA as ShoppingActivity

    User->>LA: App Launch
    LA->>FBAUTH: Check auth state

    alt Not authenticated
        FBAUTH-->>LA: Unauthenticated
        LA->>FBUI: Launch KickoffActivity
        User->>FBUI: Enter credentials (Email / Google / Phone)
        FBUI->>FBAUTH: Authenticate
        FBAUTH-->>FBUI: JWT Token issued
        FBUI->>FS: Create or fetch user document
        FS-->>FBUI: User record
    else Already authenticated
        FBAUTH-->>LA: Valid token
        LA->>FS: Fetch user profile
        FS-->>LA: User document
    end

    LA->>SA: Navigate to ShoppingActivity
    SA-->>User: HomeFragment displayed
```
---
### 6.3 Buyer Purchase Flow Sequence Diagram

```mermaid
sequenceDiagram
    actor Buyer
    participant SA as ShoppingActivity
    participant VM as ViewModel
    participant FS as Firestore

    Buyer->>SA: Browse catalogue (HomeFragment)
    SA->>VM: requestProducts(category)
    VM->>FS: addSnapshotListener(products)
    FS-->>VM: Product list (real-time)
    VM-->>SA: LiveData update - UI render

    Buyer->>SA: Open product (ProductPreviewFragment)
    Buyer->>SA: Select size and colour - Add to Cart
    SA->>VM: addToCart(CartItem)
    VM->>VM: Update cart state (in-memory)

    Buyer->>SA: Proceed to Checkout (CartFragment)
    SA-->>Buyer: Display cart summary

    Buyer->>SA: Confirm address (AddressFragment)
    Buyer->>SA: Confirm order (BillingFragment)
    SA->>VM: placeOrder(cart, address)
    VM->>FS: setDocument(orders/{orderId})
    FS-->>VM: Write acknowledged
    VM-->>SA: Order confirmed
    SA-->>Buyer: OrderCompletion screen
```
---
### 6.4 Activity Diagram — End-to-End Buyer Journey

```mermaid
flowchart TD
    A([App Launch]) --> B{Auth State?}
    B -->|Not authenticated| C[Show Onboarding]
    C --> D[Login / Register]
    D --> E{Success?}
    E -->|No| D
    E -->|Yes| F[HomeFragment]
    B -->|Authenticated| F

    F --> G[Browse Category / Search]
    G --> H[View Product Detail]
    H --> I{Add to Cart?}
    I -->|No| G
    I -->|Yes| J[CartFragment]
    J --> K{Checkout?}
    K -->|No - continue shopping| G
    K -->|Yes| L[Enter Address]
    L --> M[Review Billing]
    M --> N{Confirm Order?}
    N -->|No| J
    N -->|Yes| O[Write Order to Firestore]
    O --> P([Order Completion])
```
---

## 7. Development View
The development view illustrates the system from a programmer's perspective — how the software is organised into packages and components.

## 7.1 Layered Architecture

The system follows a strict **layered architecture**, where each layer depends only on the layer directly below it and never on layers above. This enforces a clean separation of concerns, makes individual layers independently testable, and allows Firebase to be substituted in future versions without touching the UI or ViewModel layers.

<img src="assets/android_architecture_layered.png" alt="thing. — Android Layered Architecture" width="600"/>

*Android Layered Architecture*

---

### Layer responsibilities

| Layer | Role | Boundary rule |
|---|---|---|
| **UI Layer** | Renders state emitted by LiveData; routes user events to ViewModels; owns navigation transitions | No knowledge of Firestore or Firebase internals |
| **ViewModel Layer** | Holds and transforms application state; calls the Data Layer via suspend functions; survives configuration changes | No knowledge of Fragment or View references |
| **Data Layer** | Abstracts all Firestore reads and writes behind a repository interface; manages local cache synchronisation | Only layer that imports Firebase SDK types directly |
| **Firebase — Remote Services** | Google-managed cloud infrastructure; accessed exclusively through the Data Layer | Firestore · Auth · Storage · Crashlytics |

This four-tier layering was chosen over a flat structure because it enforces the Dependency Inversion Principle: the UI and ViewModel layers depend on abstractions (LiveData, repository interfaces), not on concrete Firebase SDK calls. This is what allows the ViewModel layer to be unit-tested with mock repositories without spinning up an emulator.

### 7.2 Package Diagram

```mermaid
graph TD
    subgraph App["com.example.thingapp"]
        subgraph ACT["activities"]
            LA[LunchActivity]
            SA[ShoppingActivity]
        end

        subgraph FRAG["fragments"]
            subgraph AUTH_FRAG["auth"]
                SP[SplashFragment]
                FS1[FirstScreenFragment]
                FS2[SecondScreenFragment]
                LF[LoginFragment]
                RF[RegisterFragment]
            end
            subgraph SHOP_FRAG["shopping"]
                HF[HomeFragment]
                CF[ChairFragment]
                CPF[CupboardFragment]
                TF[TableFragment]
                FF[FurnitureFragment]
                AF[AccessoryFragment]
                PPF[ProductPreviewFragment]
                CARTF[CartFragment]
                BF[BillingFragment]
                ADDRF[AddressFragment]
                OCF[OrderCompletionFragment]
                AOF[AllOrdersFragment]
                ODF[OrderDetailsFragment]
            end
            subgraph PROFILE_FRAG["profile"]
                PF[ProfileFragment]
                EUF[EditUserInformationFragment]
                LangF[LanguageFragment]
                HelpF[HelpFragment]
            end
        end

        subgraph VM["viewmodel"]
            PVM[ProductViewModel]
            OVM[OrderViewModel]
            UVM[UserViewModel]
            CVM[CartViewModel]
        end

        subgraph MODEL["model"]
            PM[Product]
            OM[Order]
            UM[User]
            CIM[CartItem]
        end

        subgraph ADAPT["adapters"]
            PA[ProductAdapter]
            OA[OrderAdapter]
            CA[CartAdapter]
        end

        subgraph UTILS["utils"]
            IMG[ImageLoader]
            VAL[Validator]
            FMT[Formatter]
        end
    end

    subgraph Firebase["Firebase SDKs"]
        FSDK[Firestore SDK]
        ASDK[Auth SDK]
        STGSDK[Storage SDK]
        CSDK[Crashlytics SDK]
    end

    ACT --> FRAG
    FRAG --> VM
    VM --> MODEL
    FRAG --> ADAPT
    ADAPT --> MODEL
    FRAG --> UTILS
    VM --> FSDK
    ACT --> ASDK
    FRAG --> STGSDK
    App --> CSDK
```

### 7.3 Component Diagram

```mermaid
graph LR
    subgraph UILayer["UI Layer"]
        ACTS[Activities\nNavigation Hosts]
        FRAGS[Fragments\nUI Screens]
        ADAPTS[RecyclerView\nAdapters]
    end

    subgraph VMLayer["ViewModel Layer"]
        VMS[ViewModels\nLifecycle-Aware State]
        LDATA[LiveData\nObservables]
    end

    subgraph DataLayer["Data Layer"]
        REPO[Firestore\nRepository]
        CACHE[Local\nPersistence Cache]
        STG[Firebase\nStorage]
    end

    subgraph FirebaseLayer["Firebase (Remote)"]
        AUTH_SVC[Auth Service]
        FS_SVC[Firestore Service]
        STG_SVC[Storage Service]
        CRASH_SVC[Crashlytics Service]
    end

    FRAGS -->|"observes"| LDATA
    LDATA -->|"owned by"| VMS
    FRAGS -->|"populates"| ADAPTS
    VMS -->|"queries/writes"| REPO
    REPO <-->|"sync"| CACHE
    REPO <-->|"HTTPS/TLS"| FS_SVC
    STG <-->|"HTTPS/TLS"| STG_SVC
    ACTS <-->|"tokens"| AUTH_SVC
    UILayer -->|"telemetry"| CRASH_SVC
```

### 7.4 Navigation Flow

```mermaid
flowchart TD
    SPLASH[SplashFragment] --> AUTH{Authenticated?}
    AUTH -->|No| ONBOARD[FirstScreenFragment]
    ONBOARD --> SECOND[SecondScreenFragment]
    SECOND --> LOGIN[LoginFragment]
    LOGIN --> REGISTER[RegisterFragment]
    LOGIN --> HOME
    REGISTER --> HOME

    AUTH -->|Yes| HOME[HomeFragment]

    HOME --> CHAIR[ChairFragment]
    HOME --> CUPBOARD[CupboardFragment]
    HOME --> TABLE[TableFragment]
    HOME --> FURNITURE[FurnitureFragment]
    HOME --> ACCESSORY[AccessoryFragment]

    CHAIR & CUPBOARD & TABLE & FURNITURE & ACCESSORY --> PREVIEW[ProductPreviewFragment]
    PREVIEW --> CART[CartFragment]
    CART --> BILLING[BillingFragment]
    BILLING --> ADDRESS[AddressFragment]
    ADDRESS --> COMPLETION[OrderCompletionFragment]

    HOME --> ORDERS[AllOrdersFragment]
    ORDERS --> DETAIL[OrderDetailsFragment]

    HOME --> PROFILE[ProfileFragment]
    PROFILE --> EDIT[EditUserInformationFragment]
    PROFILE --> LANG[LanguageFragment]
    PROFILE --> HELP[HelpFragment]
```

### 7.5 Key Libraries & Dependencies

| Library | Purpose |
|---------|---------|
| **Firebase Firestore** | Primary NoSQL document database for all app data |
| **Firebase Auth + FirebaseUI** | Email, Google, and phone authentication flows |
| **Firebase Storage** | Cloud storage for product images and user profile pictures |
| **Firebase Crashlytics** | Production crash monitoring and reporting |
| **Google Sign-In SDK** | OAuth-based Google login |
| **AndroidX Navigation** | Fragment back stack management via navigation graph |
| **Material Components (MDC 3)** | UI component library throughout |
| **RecyclerView** | Efficient product grid and order list rendering |
| **ViewBinding** | Type-safe view references in Fragments |
| **Lifecycle — ViewModel / LiveData** | Lifecycle-aware data observation |
| **Glide** | Image loading and caching into ImageViews |
| **Kotlin Coroutines** | Background async operations |

### 7.6 Build Configuration

| Property | Value |
|----------|-------|
| Package | `com.example.thingapp` |
| Version | 1.0 |
| Min SDK | API 24 |
| Target SDK | API 35 (Android 15) |
| Compile SDK | API 35 |
| Build System | Gradle — Kotlin DSL (`build.gradle.kts`) |
| APK size | ~12.6 MB |
| Signing | Debug (academic build) |

---

## 8. Physical View
The physical view depicts the deployment topology — how software artefacts are distributed across physical or virtual nodes.

### 8.1 Deployment Diagram

```mermaid
graph TD
    subgraph Device["Node: Android Device — API 24+"]
        APP["artifact: thingapp APK\ncom.example.thingapp"]
        GPS["component: Google Play Services"]
        NAVCMP["component: Navigation Component"]
        VMCMP["component: ViewModel / LiveData"]
        FSCACHE["component: Firestore Local Cache"]
        APP --> GPS
        APP --> NAVCMP
        APP --> VMCMP
        APP --> FSCACHE
    end

    subgraph GCloud["Node: Google Cloud Platform"]
        subgraph Firebase["Firebase Project"]
            FS[("database: Firestore\nNoSQL Document Store")]
            AUTH["service: Firebase Auth\nIdentity Provider"]
            STG["service: Firebase Storage\nImage and Media Files"]
            CRASH["service: Crashlytics\nCrash Dashboard"]
        end
    end

    APP <-->|"HTTPS / TLS\nRead and Write"| FS
    APP <-->|"HTTPS / TLS\nToken Exchange"| AUTH
    APP <-->|"HTTPS / TLS\nImage Upload and Download"| STG
    APP -->|"Crash Telemetry\nAsync Batch via JobScheduler"| CRASH
    FSCACHE <-->|"Sync on reconnect"| FS
```

### 8.2 Network & Permissions

| Permission | Purpose |
|------------|---------|
| `INTERNET` | All Firebase communication |
| `ACCESS_NETWORK_STATE` | Detect connectivity; fall back to Firestore local cache |

All client-to-Firebase traffic is encrypted via **HTTPS / TLS**. Firestore's **local persistence cache** serves reads during network loss. Firebase DataTransport batches Crashlytics payloads via `JobScheduler` to preserve battery life.
---

## 9. Scenarios (Use Case View)
---

## 10. Size & Performance

### 10.1 APK Metrics

| Metric | Value |
|--------|-------|
| Raw APK size | ~12.6 MB |
| Min SDK coverage | API 24+ (~99% of active Android devices) |
| Permissions required | 2 |

### 10.2 Performance Strategies

| Concern | Strategy |
|---------|----------|
| **Cold start** | LaunchActivity is a lightweight router — minimal initialisation before first screen render |
| **List rendering** | RecyclerView with view recycling across all product grids and order lists |
| **Image loading** | Glide handles disk and memory caching of product images from Firebase Storage |
| **Database reads** | Firestore listeners removed in onStop to avoid unnecessary reads when app is backgrounded |
| **Offline reads** | Firestore local persistence cache serves data during network interruption |
| **Background work** | Firebase DataTransport uses JobScheduler to batch uploads and preserve battery |

---

## 11. Quality

| Quality Attribute | Strategy | Evidence |
|-------------------|----------|----------|
| **Security** | Firebase Auth JWT tokens; Firestore Security Rules enforce per-uid data access | FirebaseUI Auth integration |
| **Reliability** | Crashlytics captures all uncaught exceptions; Firebase infrastructure SLA | crashlytics-build.properties |
| **Maintainability** | Single-Activity + Navigation Component; Fragment modularity eases feature addition | 1 Activity, 20+ Fragments |
| **Testability** | ViewModel separation from Fragments enables unit testing without instrumentation | androidx.lifecycle present |
| **Accessibility** | Material Design 3 built-in: content descriptions, contrast ratios, touch targets | com.google.android.material |
| **Portability** | Min SDK 24 covers ~99% of active Android devices | minSdkVersion="24" |
| **Observability** | Crashlytics + Firebase Analytics for runtime crash and usage insight | DataTransport services |
| **Extensibility** | Architecture is structured to accommodate merchant and admin features in future releases without core restructuring | Layered MVVM + Firebase design |

---

## Appendix A — Acronyms & Abbreviations

| Term | Definition |
|------|------------|
| **APK** | Android Package Kit — distribution format for Android applications |
| **API** | Application Programming Interface |
| **ART** | Android Runtime — executes DEX bytecode on-device |
| **DEX** | Dalvik Executable — bytecode format used by ART |
| **JWT** | JSON Web Token — compact token used for authentication |
| **MDC** | Material Design Components — Google's UI component library |
| **MVVM** | Model-View-ViewModel — UI architectural pattern |
| **NoSQL** | Non-relational database; Firestore is document-based |
| **SDK** | Software Development Kit |
| **TLS** | Transport Layer Security — protocol for encrypted network communication |
| **UID** | User Identifier — unique string assigned by Firebase Auth per registered user |
| **UC** | Use Case — a discrete unit of functionality |
| **SC** | Scenario — a concrete instantiation of one or more use cases |

---

## Appendix B — Definitions

| Term | Definition |
|------|------------|
| **Activity** | An Android component acting as a navigation host |
| **Fragment** | A modular UI screen hosted inside an Activity; every screen in thing. is a Fragment |
| **Firestore** | Google Firebase's managed document-oriented cloud database — the primary data backend for thing. |
| **Firebase Storage** | Google Firebase's cloud file storage — used for product images and profile pictures |
| **Navigation Component** | AndroidX library managing Fragment transactions and the back stack via a navigation graph |
| **Buyer** | An end consumer who browses and purchases furniture through the thing. marketplace |
| **Cart** | A transient collection of CartItem objects held in memory prior to checkout confirmation |
| **Order** | A confirmed purchase record persisted to Firestore after checkout |
| **ViewBinding** | AndroidX feature generating type-safe binding classes per layout, eliminating findViewById |
| **LiveData** | Lifecycle-aware observable from AndroidX Lifecycle; updates UI safely from ViewModel |
| **4+1 View Model** | Philippe Kruchten's architectural framework: Logical, Process, Development, Physical + Scenarios |

---

## Appendix C — Design Principles

| Principle | Application in thing. |
|-----------|----------------------|
| **Separation of Concerns** | Activities own navigation; Fragments own UI; ViewModels own data; Firestore owns persistence |
| **Single Activity Pattern** | One Activity hosts all Fragments, reducing lifecycle complexity |
| **Serverless First** | Firebase eliminates custom backend infrastructure — appropriate for this project scale |
| **Offline-First Reads** | Firestore local cache consulted before network; improves perceived responsiveness |
| **Fail-Safe Monitoring** | Crashlytics integrated from the first release, not added retrospectively |
| **Progressive Disclosure** | Onboarding screens introduce the marketplace before requesting registration |
| **Layered Architecture** | UI → ViewModel → Data → Firebase; each layer depends only on the layer below it, enabling independent testability and future substitution of Firebase services |

---

<div align="center">

*Document maintained by Team thing. — SWE332, Altinbas University, Spring 2026.*

</div>
