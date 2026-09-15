# My Expense Tracker

A personal, multi-user expense tracking web application built with Spring Boot. Users can register an account, log expenses, categorize spending, set budgets, track recurring bills, and export their data

---

## Tech Stack

**Language & Build**
- Java
- Gradle

**Backend**
- Spring Boot
- Spring Data JPA — database access
- Spring Security — authentication
- OpenPDF — PDF report generation


**Frontend**
- Thymeleaf — server-rendered HTML templates
- Plain CSS
- Chart.js (via CDN) — category spending doughnut chart

**Database**
- H2 — embedded, file-based database


## Core Features

### Authentication
- User registration and login
- Passwords encrypted
- Each user's data is private to their account

### Expense Management
- Add, edit, and delete expenses
- Fields: description, amount, category, date

### Dashboard & Insights
- Total spent, number of expenses, and average expense
- Spending by category
- Spending by month
- Month-over-month comparison with highlighted percentage change per category

### Budgets
- Set a monthly spending limit per category
- Visual progress bar shows how much of the budget has been used
- Bar turns red when a category goes over budget


### Search, Filter & Export
- Search expenses by description
- Filter by category
- Export all expenses as a CSV file
- Export a styled PDF report

