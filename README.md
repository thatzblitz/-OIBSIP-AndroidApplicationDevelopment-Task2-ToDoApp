# Tasks

> A minimalist, distraction-free To-Do application for Android inspired by the Nothing OS aesthetic with high-contrast monochrome surfaces and smooth interactions.

---

## Overview

Tasks is a lightweight, clean Android productivity tool designed for efficient day-to-day organization. Built with a distinctive pure black and grey interface, custom rounded components, and precise touch controls, it provides seamless task management backed by local SQLite persistence and a striking minimalist visual style.

---

## Download the app from here

Get the pre-compiled APK directly on your device:
**[Tasks.apk](https://github.com/thatzblitz/-OIBSIP-AndroidApplicationDevelopment-Task2-ToDoApp/releases/download/apk/Tasks.v1.apk)**

---

## Features

- **Nothing OS Aesthetic:** High-contrast monochrome surfaces, custom rounded dialogs, and striking red accent highlights.
- **Dynamic Task Management:** Create, update, and organize tasks with custom names, optional detailed descriptions, and integrated date-time tracking.
- **Smooth Completion Toggles:** Interactive sliding switches (`SwitchMaterial`) engineered with safe view binding for fluid check-off animations.
- **Bulk Deletion Controls:** Custom popup dialogs to cleanly purge completed tasks or clear all records at once.
- **Local Persistence:** Reliable offline data management powered by a custom SQLite database architecture (`DBHelper`).
- **Adaptive Insets:** Edge-to-edge layout calibrated with system bar safe zones (`fitsSystemWindows`) to avoid notches, cutouts, and gesture bars.

---

## Tech Stack

- **Language:** Java
- **UI Framework:** Android Material Components (Material 3) & ConstraintLayout
- **Database:** SQLite (`DBHelper`)
- **Minimum SDK:** Android 7.0 (API 24)
- **Target SDK:** Android 14+ (API 34)
- **Build System:** Gradle

---

## Getting Started

### Prerequisites

- Android Studio Iguana (or newer)
- JDK 17+
- Android SDK with API 34 platform tools installed
- Physical Android device (USB debugging enabled) or an Android Virtual Device (AVD)

### Installation

1. Clone the repository:
   ```bash
   git clone [https://github.com/thatzblitz/-OIBSIP-AndroidApplicationDevelopment-Task2-ToDoApp.git](https://github.com/thatzblitz/-OIBSIP-AndroidApplicationDevelopment-Task2-ToDoApp.git)
   cd -OIBSIP-AndroidApplicationDevelopment-Task2-ToDoApp
