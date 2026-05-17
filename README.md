# 📚 Full-Stack Online Bookstore

A production-style full-stack order processing system built as a Database Course Final Project.

The platform supports secure authentication, role-based access control, order processing, analytics dashboards, and a fully containerized deployment setup.

---

# 🚀 Features

- JWT Authentication with refresh tokens
- Role-Based Access Control (Admin & Customer)
- Shopping cart and secure checkout
- Order history and inventory management
- Admin analytics dashboard
- PostgreSQL triggers, ENUMs, and transactions
- Fully Dockerized 3-tier architecture

---

# 🛠 Tech Stack

### Backend
- Java 17
- Spring Boot
- Spring Security
- JDBC Template

### Frontend
- Next.js 14
- TypeScript
- Tailwind CSS
- shadcn/ui

### Database & DevOps
- PostgreSQL
- Docker & Docker Compose

---

# 🖼️ Screenshots

## 📊 Admin Reports Dashboard
![Admin Reports Dashboard](./screenshots/admin-reports-dashboard.png)

## 🔎 Book Search Page
![Book Search Page](./screenshots/book-search-page.png)

## 🗂️ Database ERD
![Database ERD](./screenshots/database-erd.png)

## 📦 Publisher Order Page
![Publisher Order Page](./screenshots/publisher-order-page.png)

## 📚 Manage Books Page
![Manage Books Page](./screenshots/manage-books-page.png)

---

# ⚙️ Run Locally

```bash
docker compose up --build
```

Then go to this [http://localhost:3000](http://localhost:3000) 