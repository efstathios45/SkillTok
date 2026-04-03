# SkillTok - Where Udemy and TikTok Meet

SkillTok is a revolutionary educational platform that combines the addictive, high-engagement format of TikTok-style short-form video (Reels) with the structured depth of online learning platforms like Udemy.

## 🚀 Vision
To make learning as effortless and engaging as scrolling through your social feed. SkillTok focuses on micro-learning, allowing users to master new skills through bite-sized, high-quality video content.

## ✨ Core Features

### 🎬 TikTok-Style Learning Feed
*   **Vertical Pager**: Seamlessly scroll through educational reels.
*   **Interactive Controls**: Tap to pause/play, double-tap to seek (forward/back) just like YouTube.
*   **Engagement**: Like, save, and comment on reels to interact with the learning community.
*   **Progress Tracking**: Real-time progress bar and lesson counter at the top of every reel.

### 📚 Structured Course Paths
*   **Modern Hero Layout**: Industry-standard course detail pages with high-impact hero images and smooth overlapping content.
*   **Comprehensive Curriculum**: Clear module and lesson breakdowns for structured deep-dives.
*   **One-Tap Enrollment**: Join courses instantly with professional sound feedback.

### 🧠 Gamified Experience & Personalization
*   **Smart Onboarding**: Personalized learning feed based on user-selected interests and goals, persisted to Firebase.
*   **User Progression**: Track your growth with XP (Experience Points), Day Streaks, and Levels.
*   **Saved Library**: A professional 3-column grid of your saved reels for quick reference.
*   **Interactive Quizzes**: Test your knowledge at the end of lessons to earn extra XP.

## 🛠 Tech Stack

*   **Language**: Kotlin
*   **UI Framework**: Jetpack Compose (Material 3)
*   **Backend**: 
    *   **Firebase Auth**: Secure user authentication.
    *   **Firebase Data Connect**: Type-safe SDK generation with a **PostgreSQL** backend.
*   **Local Database**: Room for offline caching and performance.
*   **Video Engine**: Custom WebView-based YouTube Player integration with low-latency playback controls.
*   **Media**: Coil for high-performance image loading.
*   **Audio**: Custom `SoundManager` using `SoundPool` for responsive UI interaction sounds.

## 📦 Setup & Installation

### Prerequisites
1.  Android Studio Ladybug or newer.
2.  Firebase CLI installed.
3.  A Firebase project with Data Connect and Authentication enabled.

### Getting Started
1.  **Clone the repository**:
    ```bash
    git clone https://github.com/your-repo/skilltok.git
    ```
2.  **Configure Firebase**:
    *   Add your `google-services.json` to the `app/` folder.
    *   Login to Firebase CLI: `firebase login`.
3.  **Generate Data Connect SDK**:
    ```bash
    firebase dataconnect:sdk:generate
    ```
4.  **Add Sound Assets**:
    *   Create `app/src/main/res/raw/` if it doesn't exist.
    *   Add `like.mp3`, `save.mp3`, `comment.mp3`, and `enroll.mp3`.
5.  **Build & Run**:
    Open the project in Android Studio and click **Run**.

## 🎨 UI/UX Design Principles
*   **Micro-Interactions**: Haptic feedback and custom sound effects for every key action.
*   **Immersive Video**: Edge-to-edge video playback in the reels feed.
*   **Clarity & Focus**: Minimalist design that highlights the educational content.

---
*Under Construction - Building the future of micro-learning.*
