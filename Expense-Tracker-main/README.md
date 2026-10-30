
**My Expense Tracker**

A personal, multi-user finance tracker built with Spring Boot. Users can register an account, log expenses, categorize spending, set budgets, track recurring bills, save toward goals, track income, and export their data

**Tech Stack**

*Language & Build*
- Java
- Gradle

*Backend*
- Spring Boot
- Spring Data JPA — database access
- Spring Security — session-based login for the web app, plus JWT authentication for the API
- OpenPDF — PDF report generation
- Spring Scheduling — daily automation for recurring expenses
- Flyway — version-controlled database migrations

*Frontend
- Thymeleaf — server-rendered HTML templates, with sidebar navigation
- Plain CSS
- Chart.js — category spending doughnut chart
- React (built with Vite) — a newer, separate frontend consuming a JWT-secured REST API
- React Router, Axios, Recharts — used in the React app

*Database*
- MySQL — a real, persistent relational database
- Schema managed with Flyway migrations rather than auto-generated

**Core Features**

*Authentication*
- User registration and login
- Passwords encrypted
- Each user's data is private to their account

*Expense Management*
- Add, edit, and delete expenses
- Fields: description, amount, category, date
- 15 categories
- Attach a receipt photo to any expense
- Mark an expense as recurring (weekly/monthly) — automatically repeats on its own

*Budgets, Goals, Accounts & Income*
- Set a monthly spending limit per category
- Create savings goals with a target amount and optional date/category, and log contributions
- Add bank, cash, and wallet accounts with balances
- Log income sources; see Total Income, Total Expenses, and Net Balance

*Dashboard & Insights*
- Total spent, number of expenses, and average expense
- Spending by category
- Spending by month
- Month-over-month comparison with highlighted percentage change per category
- A monthly surplus target tracker, showing income vs. expenses vs. target

*Budgets*
- Visual progress bar shows how much of the budget has been used
- Bar turns red when a category goes over budget

*Search, Filter & Export*
- Search expenses by description
- Filter by category
- Export all expenses as a CSV file
- Export a styled PDF report

*REST API*
- A full JWT-secured JSON API mirroring the app's features: expenses, budgets, goals, categories, and a reports summary
- Consistent error responses

*React App*
- A second, separate frontend with its own login, expense management, dashboard, and budgets/goals pages, using the API above
