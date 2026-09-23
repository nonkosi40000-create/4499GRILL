# 4499GRILL - Premium Wood-Fired Experience

4499GRILL is a cutting-edge Android application designed for a premium wood-fired grill restaurant. It transcends traditional food ordering by integrating social community features, AI-driven recommendations, and a robust real-time order management system. Built with Kotlin and powered by Firebase, the app provides a seamless, high-performance experience from the first flame to the final delivery.

## 🚀 Key Features & Deep Dive

### 1. Smart Authentication & Role Management
The app features a secure, Firebase-backed authentication system supporting **Multi-Role Dashboards**:
*   **Customers:** Access to ordering, community, and personal history.
*   **Workers & Managers:** Specialized interfaces for order processing and restaurant oversight.
*   **Logic:** Users are verified upon sign-in, and the app dynamically routes them to the appropriate dashboard based on their verified role in Firestore.

### 2. Intelligent Menu & Customization
*   **Dynamic Discovery:** High-performance menu rendering using Glide for image caching.
*   **Precision Customization:** Users don't just pick an item; they build it. The `ItemDetailActivity` supports required selections (like patty type) and optional add-ons, recalculating totals in real-time.

### 3. AI-Powered Concierge
*   **Indecision Solver:** Integrated AI Chat Assistant that suggests meals based on user cravings and preferences. It bridges the gap between a static menu and a personalized dining consultation.

### 4. Community Hub (Social Dining)
*   **Social Interaction:** A full social feed where users can post their meals, rate dishes with a 5-star system, and leave comments.
*   **Social Proof:** Real-time like counters and community ratings help new users discover the best "Flame-Grilled Masterpieces."

### 5. Advanced Order & Payment System
*   **Multi-Select Payments:** The `OrdersActivity` allows users to view their entire history and select multiple "Awaiting Payment" orders to pay concurrently—a significant efficiency upgrade over traditional one-by-one checkouts.
*   **Live Tracking:** Powered by Firestore Snapshots, order status updates (Received -> Grilling -> Delivered) reflect instantly without refreshing the page.

### 6. Personalization & Accessibility
*   **Theme Control:** Full Dark Mode and Light Mode support, controllable via a toggle in the Profile settings. The preference is persistent across app launches using SharedPreferences.

---

## 🏗 Project Architecture & File Structure

```text
4499GRILL
├── app
│   ├── src/main/java/com/example/a4499grill
│   │   ├── SplashActivity.kt        (App Entry & Theme Loading)
│   │   ├── MainActivity.kt          (Secure Login)
│   │   ├── RegisterActivity.kt      (Account Creation)
│   │   ├── HomeActivity.kt          (Dashboard & Favorites)
│   │   ├── MenuActivity.kt          (Full Menu Discovery)
│   │   ├── ItemDetailActivity.kt    (Customization & Basket Add)
│   │   ├── CartActivity.kt          (Basket Management)
│   │   ├── OrdersActivity.kt        (History & Multi-Payment)
│   │   ├── CommunityActivity.kt     (Social Feed & Interaction)
│   │   ├── AIChatActivity.kt        (AI Recommendation Engine)
│   │   ├── ProfileActivity.kt       (Settings & Theme Control)
│   │   ├── Models.kt                (Data Structures: Order, Post, User)
│   │   └── CartManager.kt           (Singleton Basket Logic)
│   └── src/main/res
│       ├── layout/                      (UI XML Definitions)
│       ├── drawable/                    (Vector Icons & PNG Assets)
│       ├── values/                      (Colors, Strings, Light Theme)
│       └── values-night/                (Premium Dark Theme)
└── build.gradle                         (Project Dependencies)
```

---

## 💎 What Makes This App Unique?

1.  **The "Social Grill":** Most food apps are transactional. 4499GRILL is social. By allowing users to rate and comment on specific grill sessions, it creates a community around the brand.
2.  **Serverless Real-Time Sync:** By leveraging Firebase Firestore's `SnapshotListeners`, the app eliminates "pull-to-refresh." Data flows like water—when a chef starts grilling, the customer's phone vibrates instantly.
3.  **Hybrid Logic:** The app uses a "Local-First" fallback strategy. If the cloud is unreachable, the app provides high-quality mock content to ensure the UI remains vibrant and functional.

---

## 🛠 Tech Stack: SDKs & APIs

Because this project is linked directly to the **Firebase Ecosystem**, we utilize a suite of powerful Google Cloud APIs:

*   **Android SDK (Kotlin):** The foundation for high-performance, modern mobile development.
*   **Firebase Authentication:** Handles secure credential management and OAuth handshakes.
*   **Cloud Firestore (NoSQL):** Stores all order, user, and community data with real-time synchronization.
*   **Firebase Storage:** Used for hosting and serving high-resolution food imagery and user-uploaded community content.
*   **Glide API:** For sophisticated image loading, circular cropping, and memory-efficient caching.
*   **Material Design 3:** To ensure the UI adheres to the latest Google design standards for accessibility and aesthetics.

---
*Developed for 4499GRILL - Premium Taste, Digital Precision.*
