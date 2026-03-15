<table>
<tr>
<td width="160">
<img src="https://raw.githubusercontent.com/thing-mobile-app/.github/main/Assets/thing.png" width="150" height="150" style="object-fit:cover; border-radius:12px;"/>
</td>
<td>

# thing.

![Android](https://img.shields.io/badge/Android-3DDC84?style=flat&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=flat&logo=kotlin&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=flat&logo=firebase&logoColor=black)
![MVVM](https://img.shields.io/badge/Architecture-MVVM-4A90E2?style=flat)
![Min SDK](https://img.shields.io/badge/Min_SDK-24-4A90E2?style=flat&logo=android&logoColor=white)
![Status](https://img.shields.io/badge/Status-In_Development-E67E22?style=flat)

</td>
</tr>
</table>

---

## Project Introduction

Furniture shopping today is a fragmented experience — users browse across multiple websites and physical stores, struggle with inconsistent product information, and have no unified way to discover and compare home furniture in one place.

**thing.** solves this by providing a dedicated mobile marketplace for home furniture. Users can browse curated product listings, search and filter by category, view detailed product pages, and complete purchases — all within a single, clean Android application.

The app is built with **Kotlin** and **Firebase**, following the **MVVM** architecture pattern to ensure a maintainable and scalable codebase. It targets everyday consumers looking for a straightforward and trustworthy way to furnish their homes.

> *"Furniture may be just a thing — but it's **the thing.** that turns a house into a home."*

📐 Architecture documentation → [![ARCHITECTURE.md](https://img.shields.io/badge/ARCHITECTURE.md-4A90E2?style=flat&logo=readthedocs&logoColor=white)](./ARCHITECTURE.md)

---

## Team

| | Name | Student ID | GitHub |
|:---:|:---|:---:|:---|
| <img src="https://github.com/samedTevin.png" width="48" height="48" style="border-radius:50%"/> | **Samed Tevin** | 230513327 | [![GitHub](https://img.shields.io/badge/-samedTevin-181717?style=flat-square&logo=github)](https://github.com/samedTevin) |
| <img src="https://github.com/hasanackl.png" width="48" height="48" style="border-radius:50%"/> | **Hasan Açıkel** | 220513343 | [![GitHub](https://img.shields.io/badge/-hasanackl-181717?style=flat-square&logo=github)](https://github.com/hasanackl) |
| <img src="https://github.com/CoderRoninn.png" width="48" height="48" style="border-radius:50%"/> | **Doğukan Süme** | 210513243 | [![GitHub](https://img.shields.io/badge/-CoderRoninn-181717?style=flat-square&logo=github)](https://github.com/CoderRoninn) |
| <img src="https://github.com/KaganxSahin.png" width="48" height="48" style="border-radius:50%"/> | **Kağan Şahin** | 220513375 | [![GitHub](https://img.shields.io/badge/-KaganxSahin-181717?style=flat-square&logo=github)](https://github.com/KaganxSahin) |

---

## Tech Stack

| Category | Technologies |
|----------|-------------|
| Language | ![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=flat&logo=kotlin&logoColor=white) |
| Architecture | ![MVVM](https://img.shields.io/badge/MVVM-4A90E2?style=flat) ![LiveData](https://img.shields.io/badge/LiveData-1E88E5?style=flat) ![Navigation](https://img.shields.io/badge/Navigation_Component-3949AB?style=flat) |
| Backend | ![Firebase Auth](https://img.shields.io/badge/Firebase_Auth-FFCA28?style=flat&logo=firebase&logoColor=black) ![Firestore](https://img.shields.io/badge/Firestore-FFA000?style=flat&logo=firebase&logoColor=black) ![Firebase Storage](https://img.shields.io/badge/Firebase_Storage-F57C00?style=flat&logo=firebase&logoColor=black) |
| DI | ![Hilt](https://img.shields.io/badge/Hilt-E91E63?style=flat&logo=android&logoColor=white) |
| UI | ![View Binding](https://img.shields.io/badge/View_Binding-43A047?style=flat&logo=android&logoColor=white) ![Glide](https://img.shields.io/badge/Glide-2E7D32?style=flat) |
| Tools | ![Android Studio](https://img.shields.io/badge/Android_Studio-3DDC84?style=flat&logo=android-studio&logoColor=white) ![Git](https://img.shields.io/badge/Git-F05032?style=flat&logo=git&logoColor=white) ![Jira](https://img.shields.io/badge/Jira-0052CC?style=flat&logo=jira&logoColor=white) |

---

## Documents

| Document | Link |
|:---------|:-----|
| 📋 Project Proposal | [SWE332_Project_Proposal.pdf](./ProjectManagement/Documents/SWE332_Project_Proposal.pdf) |
| 👤 Persona File | [thingPersonaFile.pdf](./ProjectManagement/Documents/thingPersonaFile.pdf) |
| 📐 Architecture | [ARCHITECTURE.md](./ARCHITECTURE.md) |

---

## Project Structure

```
thing-furniture-app/
├── app/
│   └── src/main/java/com/thing/app/
│       ├── di/           # Hilt modules
│       ├── model/        # Data classes
│       ├── repository/   # Data access layer
│       ├── viewmodel/    # MVVM ViewModels
│       └── ui/           # Fragments & Activities
├── ARCHITECTURE.md
└── README.md
```

---

*SWE332 — Software Architecture · Altınbaş University · Spring 2026*

