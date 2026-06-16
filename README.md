# KheloMilo 🎲

> **"Thoda Khelo, Zyada Milo"**

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![License: CC BY 4.0](https://img.shields.io/badge/License-CC_BY_4.0-lightgrey.svg)](https://creativecommons.org/licenses/by/4.0/)
[![Java 25](https://img.shields.io/badge/Java-25-blue.svg)](https://adoptium.net/)
[![Javalin](https://img.shields.io/badge/Javalin-7.2.0-red.svg)](https://javalin.io/)

KheloMilo is a real-time, web-based multiplayer gaming platform built with modern Java. It provides a seamless, low-latency gaming experience using WebSockets. 

Currently, the platform features a real-time **1v1 Multiplayer Bingo** game, where players can create rooms, invite friends, and play against each other instantly.

🌐 **Live Demo:** [https://khelomilo.onrender.com](https://khelomilo.onrender.com)

---

## 🚀 Features

*   **Real-Time Multiplayer:** Instant gameplay powered by WebSockets.
*   **Room-Based Matchmaking:** Create a private room and share the ID with a friend to play.
*   **Server-Side Validation:** Game states and win conditions are securely managed by the server to prevent cheating.
*   **Hot-Reloading Templates:** Uses JTE (Java Template Engine) for blazing-fast server-side rendering with hot-reloading in development.
*   **Responsive UI:** Clean, vanilla HTML/CSS/JS frontend that works perfectly on desktop and mobile browsers.

---

## 🛠️ Tech Stack

**Backend:**
*   **[Java 25](https://openjdk.org/projects/jdk/25/)** - The core language.
*   **[Javalin](https://javalin.io/)** - A lightweight web framework for REST APIs and WebSockets.
*   **[Google Guice](https://github.com/google/guice)** - Dependency Injection.
*   **[Gson](https://github.com/google/gson)** - JSON serialization/deserialization.

**Frontend:**
*   **[JTE (Java Template Engine)](https://jte.gg/)** - Fast and type-safe server-side HTML rendering.
*   **Vanilla HTML / CSS / JavaScript** - Lightweight, zero-dependency client logic.

**DevOps & Deployment:**
*   **Maven** - Build tool and dependency management.
*   **Docker** - Containerization (multi-stage builds for a tiny Alpine JRE footprint).
*   **GitHub Actions** - CI/CD pipeline for building and pushing to GitHub Container Registry (GHCR).

---

## 💻 Getting Started (Local Development)

### Prerequisites
*   Java Development Kit (JDK) 25
*   Maven 3.9+
*   *(Optional)* Docker

### Running the Project

1. **Clone the repository:**
   ```bash
   git clone https://github.com/wulfcodes/khelomilo.git
   cd khelomilo
   ```

2. **Set the PORT environment variable:**
   The application requires a `PORT` environment variable to start.
   *   **Windows (PowerShell):** `$env:PORT="8080"`
   *   **Linux/Mac:** `export PORT=8080`

3. **Build and Run:**
   ```bash
   mvn clean compile exec:java -Dexec.mainClass="io.wulfcodes.khelomilo.Main"
   ```

4. **Access the application:**
   Open your browser and navigate to `http://localhost:8080`.

*(Note: In development mode, JTE templates in `src/main/jte` will automatically hot-reload when you save them!)*

---

## 🐳 Docker Support

You can easily run KheloMilo in a Docker container.

**Build the image:**
```bash
docker build -t khelomilo:latest .
```

**Run the container:**
```bash
docker run -p 8080:8080 -e PORT=8080 khelomilo:latest
```

---

## 📂 Project Structure

```text
khelomilo/
├── .github/workflows/       # CI/CD pipelines
├── src/
│   ├── main/
│   │   ├── java/io/wulfcodes/khelomilo/
│   │   │   ├── config/      # Guice dependency injection modules
│   │   │   ├── controller/  # Handlers for Web, API, and WebSocket routes
│   │   │   ├── factory/     # Builders for JTE and Gson
│   │   │   ├── model/       # Game state models (e.g., BingoRoom, BingoPlayer)
│   │   │   ├── router/      # Javalin EndpointGroups for route definitions
│   │   │   └── service/     # Core business logic and in-memory state managers
│   │   ├── jte/             # JTE HTML templates (Views)
│   │   └── resources/
│   │       └── public/      # Static assets (CSS, JS, Images)
├── Dockerfile               # Multi-stage Docker build configuration
└── pom.xml                  # Maven dependencies and build plugins
```

---

## 🗺️ Roadmap / Future Games
We plan to expand the platform with more classic real-time games. Upcoming planned features:
- [ ] Dots and Boxes (Dot Connect)
- [ ] Connect Four
- [ ] Ultimate Tic-Tac-Toe

---

## 🤝 Contributors

*   **Swayamsidh Nayak** - Architect / Developer
*   **Srujanee Nayak** - Developer

---

## 📜 License

This project uses a dual-license approach:
* The **source code** is licensed under the [MIT License](LICENSE).
* The **assets, documentation, and templates** are licensed under the **[Creative Commons Attribution 4.0 International (CC BY 4.0)](https://creativecommons.org/licenses/by/4.0/)** license.

*The Creative Commons license is dedicated in honor of Aaron Swartz.*
