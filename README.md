# 🩺 Doctor-Ai: Smart Medical Management with Spring Boot

[![GitHub Repo](https://img.shields.io/badge/github-zeelapatel/Doctor--Ai-blue?logo=github)](https://github.com/zeelapatel/Doctor-Ai)
[![Maven Central](https://img.shields.io/maven-central/v/com.example/doctor-ai.svg?label=Maven%20Central)]()
[![Java](https://img.shields.io/badge/java-17+-blue?logo=java)]()
[![Spring Boot](https://img.shields.io/badge/spring_boot-2.7.x-green?logo=springboot)]()

> Doctor-Ai is a modern Spring Boot web application for managing medical appointments, prescriptions, patient profiles, and notifications — empowering patients, doctors, and pharmacists with seamless digital healthcare.

---

## 📋 Table of Contents

- [✨ Features](#-features)
- [🛠️ Tech Stack](#️-tech-stack)
- [⚙️ Requirements](#️-requirements)
- [🚀 Quickstart](#-quickstart)
- [🔧 Configuration](#-configuration-env-vars)
- [📡 API Endpoints](#-api-endpoints-summary)
- [📦 Scripts & Commands](#-scriptscommands)
- [📁 Folder Structure](#-folder-structure)
- [🤝 Contributing](#-contributing)
- [📄 License](#-license)
- [🙏 Acknowledgements](#-acknowledgements)

---

## ✨ Features

- 🧑‍⚕️ **Role-based Access:** Separate dashboards and features for Patients, Doctors, and Pharmacists.
- 📅 **Appointment Management:** Book, view, and manage appointments with doctors.
- 💊 **Prescription Handling:** Doctors can create prescriptions; patients can view and download PDFs.
- 📝 **Patient Profiles:** View and update detailed patient profiles.
- 🔔 **Notifications:** Real-time notification system with read/unread status.
- 🔐 **Secure Authentication:** Spring Security integration with role-based authorization.
- 📄 **PDF Generation:** Generate and download prescriptions as PDF documents.
- 📧 **Email Notifications:** Automated email alerts for appointment and prescription updates.
- 🧪 **Unit Testing:** Basic Spring Boot test coverage included.

---

## 🛠️ Tech Stack

- **Backend:** Java 17, Spring Boot 2.7.x, Spring MVC, Spring Security, Spring Data JPA, Hibernate
- **Build Tool:** Maven (with Maven Wrapper)
- **Database:** JPA-compatible (e.g., MySQL, PostgreSQL)
- **Templating:** Thymeleaf
- **PDF Generation:** iText PDF
- **Frontend:** Thymeleaf HTML templates with CSS styling
- **Version Control:** Git

---

## ⚙️ Requirements

- Java 17 or higher
- Maven 3.6+ (or use included Maven Wrapper)
- Database (MySQL/PostgreSQL recommended)
- Internet connection for Maven dependencies on first build

---

## 🚀 Quickstart

### 1. Clone the repository

```bash
git clone https://github.com/zeelapatel/Doctor-Ai.git
cd Doctor-Ai
```

### 2. Build the project

Use Maven Wrapper (recommended):

```bash
./mvnw clean install
```

Or with Maven directly:

```bash
mvn clean install
```

### 3. Configure environment variables

Create a `.env` file or set environment variables as per [Configuration](#-configuration-env-vars).

### 4. Run the application

```bash
./mvnw spring-boot:run
```

Or run the generated jar:

```bash
java -jar target/doctor-ai-0.0.1-SNAPSHOT.jar
```

### 5. Access the app

Open your browser at:

```
http://localhost:8080/
```

### 6. Run tests

```bash
./mvnw test
```

---

## 🔧 Configuration (.env vars)

| Variable               | Description                                  | Example                  |
|------------------------|----------------------------------------------|--------------------------|
| `SPRING_DATASOURCE_URL` | JDBC URL for your database                   | `jdbc:mysql://localhost:3306/doctor_ai` |
| `SPRING_DATASOURCE_USERNAME` | DB username                              | `root`                   |
| `SPRING_DATASOURCE_PASSWORD` | DB password                              | `password`               |
| `SPRING_MAIL_HOST`      | SMTP mail server host                        | `smtp.gmail.com`         |
| `SPRING_MAIL_PORT`      | SMTP mail server port                        | `587`                    |
| `SPRING_MAIL_USERNAME`  | Email username for sending notifications    | `your-email@gmail.com`   |
| `SPRING_MAIL_PASSWORD`  | Email password or app-specific password      | `your-email-password`    |
| `SERVER_PORT`           | Port to run the Spring Boot app (default 8080) | `8080`                |

> **Note:** Configure your mail credentials carefully to enable email notifications.

---

## 📡 API Endpoints (Summary)

### Patient Routes

| Method | Endpoint                        | Description                        |
|--------|--------------------------------|----------------------------------|
| GET    | `/patient/dashboard`            | Patient dashboard with notifications |
| GET    | `/patient/profile/view`         | View patient profile              |
| GET/POST | `/patient/profile/update`      | Update patient profile           |
| GET    | `/patient/appointments`         | List patient appointments        |
| GET/POST | `/patient/appointment/book`    | Book a new appointment           |
| GET    | `/patient/notes`                | View patient notes               |
| GET    | `/patient/prescriptions`        | View prescriptions               |
| GET    | `/patient/prescription/download/{id}` | Download prescription PDF  |
| POST   | `/patient/notification/read/{id}` | Mark notification as read      |

### Doctor Routes

| Method | Endpoint                        | Description                        |
|--------|--------------------------------|----------------------------------|
| GET    | `/doctor/dashboard`             | Doctor dashboard                 |
| GET/POST | `/doctor/prescriptions/create` | Create new prescription          |
| ...    | ...                            | ...                              |

### Pharmacist Routes

| Method | Endpoint                        | Description                        |
|--------|--------------------------------|----------------------------------|
| GET    | `/pharmacist/dashboard`         | Pharmacist dashboard             |
| ...    | ...                            | ...                              |

> Full API documentation is recommended to be generated with Swagger/OpenAPI in future releases.

---

## 📦 Scripts & Commands

| Command                 | Description                          |
|-------------------------|------------------------------------|
| `./mvnw clean install`  | Build the project                   |
| `./mvnw spring-boot:run`| Run the Spring Boot application    |
| `./mvnw test`           | Run unit tests                     |
| `git status`            | Check git repository status        |

---

## 📁 Folder Structure

```
Doctor-Ai/
├── src/
│   ├── main/
│   │   ├── java/com/esd/mediconnect1/
│   │   │   ├── controller/          # REST Controllers (Patient, Doctor, Auth, etc.)
│   │   │   ├── dao/                 # Data Access Objects (DAO implementations)
│   │   │   ├── model/               # Entity and model classes
│   │   │   ├── security/            # Security configuration and filters
│   │   │   ├── service/             # Business logic services
│   │   │   └── DoctorAiApplication.java # Main Spring Boot app
│   │   └── resources/
│   │       ├── templates/           # Thymeleaf HTML templates
│   │       ├── static/              # CSS, JS, images
│   │       └── application.properties # Spring Boot config
├── .mvn/                         # Maven Wrapper files
├── mvnw, mvnw.cmd                # Maven Wrapper scripts
├── pom.xml                      # Maven project descriptor
├── .gitignore                   # Git ignore rules
└── README.md                    # This file
```

---

## 🤝 Contributing

Contributions, issues, and feature requests are welcome!  
Feel free to check [issues](https://github.com/zeelapatel/Doctor-Ai/issues) and submit pull requests.

**Guidelines:**

- Follow Java and Spring Boot best practices.
- Write clear commit messages.
- Include tests for new features or bug fixes.
- Respect existing code style and formatting.

---

## 📄 License

This project is currently **Unassigned** license.  
Please contact the repository owner for licensing details.

---

## 🙏 Acknowledgements

- [Spring Boot](https://spring.io/projects/spring-boot) – The backbone framework
- [Thymeleaf](https://www.thymeleaf.org/) – Template engine for UI
- [iText PDF](https://itextpdf.com/) – PDF generation library
- [Maven Wrapper](https://github.com/takari/maven-wrapper) – Simplified build setup
- Open source community for inspiration and libraries

---

Thank you for checking out **Doctor-Ai**! 🚀  
Feel free to reach out or contribute to make healthcare smarter and more accessible.
```
