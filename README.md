# Healthtech AI-Powered Application

A hospital medical supply inventory system built to solve a real, specific problem: **hospitals lose money and stock through untracked, unaccountable inventory movements.** This system makes every unit of stock traceable, every action attributable to a real person, and flags unusual activity automatically — without ever pretending a black-box AI model is more trustworthy than it actually is.

Built end-to-end: database design → backend API → authentication and role-based access → a full Angular frontend → explainable anomaly detection → physical stock reconciliation.

**Frontend repo:** [healthtech-dashboard](https://github.com/EngReteti/healthtech-dashboard)

## The Problem This Solves

A hardware-store-style "stock in, stock out" system isn't enough for a hospital: medication expires, controlled substances need stricter tracking, and stock moves between departments, not just in and out of one building. Most inventory tools also let someone silently overwrite a stock number when a count looks wrong, erasing exactly the evidence needed to catch theft, waste, or error.

This system is built around one core design decision: **current stock is never stored directly — it is always calculated from a permanent, append-only ledger of movements.** Nobody, not even an Admin, can quietly change how much stock exists. Every change is a timestamped, attributed event.

## Key Features

- **Ledger-based stock tracking** — current stock is calculated live from every IN, DISPENSED, TRANSFER, DAMAGE, EXPIRED, and ADJUSTMENT movement ever recorded, never stored as an editable number
- **Role-based access control** (JWT + Spring Security) — three real roles with genuinely different permissions:
  - **Storekeeper**: records stock movements, performs physical counts
  - **Auditor**: approves or rejects pending adjustments and controlled-substance movements (two-person accountability)
  - **Admin**: manages users, suppliers, products, and departments — no public self-registration
- **Batch and expiry tracking** — stock is tracked per delivery batch, not just in aggregate, so expiring medication is visible before it becomes a write-off
- **Department transfers** — stock moving between hospital units (ICU, Pharmacy, Surgery) is tracked to a real destination, not just marked "out"
- **Physical reconciliation** — a Storekeeper's real shelf count is compared against the system's calculated stock; any mismatch automatically becomes a pending adjustment routed to an Auditor for approval
- **Explainable anomaly detection** — statistical, rule-based flags (unusually large quantities, large single-transaction depletions) on real movement data, each with a plain-English reason attached. No black-box model, no unexplainable "trust me" scores.
- **Safety-first movement logic** — the system physically refuses to let stock go negative, and requires approval before any controlled-substance movement or manual adjustment counts toward real stock
- **User deactivation, not deletion** — accounts are deactivated rather than deleted, since deleting a user would break the audit trail of every movement they ever performed

## Tech Stack

**Backend:** Java, Spring Boot, Spring Security, PostgreSQL, JWT
**Frontend:** Angular, TypeScript, Tailwind CSS

## ArchitectureUser → Angular Frontend → JWT Auth → Spring Boot REST API → StockService (business rules) → PostgreSQLThe `StockService` is the single entry point for every stock change — no controller or repository is allowed to bypass it, which is what makes the negative-stock protection and approval rules impossible to accidentally skip.

## Roles at a Glance

| Action | Storekeeper | Auditor | Admin |
|---|---|---|---|
| Record stock movements | Yes | No | Yes |
| Perform physical counts | Yes | No | Yes |
| Approve/reject adjustments | No | Yes | No |
| Manage users, suppliers, departments | No | No | Yes |
| View dashboard, history, batches | Yes | Yes | Yes |

## Running Locally

**Requirements:** Java 17+, Maven, PostgreSQL

```bash
# Create the database
createdb hospital_inventory

# Configure src/main/resources/application.properties with your 
# database username/password

# Run
./mvnw spring-boot:runThe API runs on http://localhost:8080. See the frontend repo for the Angular client.What's Not Built Yet
Being upfront about scope: predictive reordering, auto-generated purchase orders, barcode/QR scanning, and containerized deployment are planned but not yet implemented. The anomaly detection is intentionally simple and statistical, not a trained machine learning model — with the amount of data a system like this realistically has early on, an honest, explainable rule beats an impressive-sounding but unverifiable prediction.
Why This Project
Built as a learning project to genuinely understand full-stack engineering: schema design that prevents entire categories of bugs, authentication done properly (hashed passwords, real JWT verification, enforced roles), and features scoped to what the data can actually support rather than what sounds most impressive.
cat > README.md << 'EOF'
# Healthtech AI-Powered Application

A hospital medical supply inventory system built to solve a real, specific problem: **hospitals lose money and stock through untracked, unaccountable inventory movements.** This system makes every unit of stock traceable, every action attributable to a real person, and flags unusual activity automatically — without ever pretending a black-box AI model is more trustworthy than it actually is.

Built end-to-end: database design → backend API → authentication and role-based access → a full Angular frontend → explainable anomaly detection → physical stock reconciliation.

**Frontend repo:** [healthtech-dashboard](https://github.com/EngReteti/healthtech-dashboard)

## The Problem This Solves

A hardware-store-style "stock in, stock out" system isn't enough for a hospital: medication expires, controlled substances need stricter tracking, and stock moves between departments, not just in and out of one building. Most inventory tools also let someone silently overwrite a stock number when a count looks wrong, erasing exactly the evidence needed to catch theft, waste, or error.

This system is built around one core design decision: **current stock is never stored directly — it is always calculated from a permanent, append-only ledger of movements.** Nobody, not even an Admin, can quietly change how much stock exists. Every change is a timestamped, attributed event.

## Key Features

- **Ledger-based stock tracking** — current stock is calculated live from every IN, DISPENSED, TRANSFER, DAMAGE, EXPIRED, and ADJUSTMENT movement ever recorded, never stored as an editable number
- **Role-based access control** (JWT + Spring Security) — three real roles with genuinely different permissions:
  - **Storekeeper**: records stock movements, performs physical counts
  - **Auditor**: approves or rejects pending adjustments and controlled-substance movements (two-person accountability)
  - **Admin**: manages users, suppliers, products, and departments — no public self-registration
- **Batch and expiry tracking** — stock is tracked per delivery batch, not just in aggregate, so expiring medication is visible before it becomes a write-off
- **Department transfers** — stock moving between hospital units (ICU, Pharmacy, Surgery) is tracked to a real destination, not just marked "out"
- **Physical reconciliation** — a Storekeeper's real shelf count is compared against the system's calculated stock; any mismatch automatically becomes a pending adjustment routed to an Auditor for approval
- **Explainable anomaly detection** — statistical, rule-based flags (unusually large quantities, large single-transaction depletions) on real movement data, each with a plain-English reason attached. No black-box model, no unexplainable "trust me" scores.
- **Safety-first movement logic** — the system physically refuses to let stock go negative, and requires approval before any controlled-substance movement or manual adjustment counts toward real stock
- **User deactivation, not deletion** — accounts are deactivated rather than deleted, since deleting a user would break the audit trail of every movement they ever performed

## Tech Stack

**Backend:** Java, Spring Boot, Spring Security, PostgreSQL, JWT
**Frontend:** Angular, TypeScript, Tailwind CSS

## ArchitectureUser → Angular Frontend → JWT Auth → Spring Boot REST API → StockService (business rules) → PostgreSQLThe `StockService` is the single entry point for every stock change — no controller or repository is allowed to bypass it, which is what makes the negative-stock protection and approval rules impossible to accidentally skip.

## Roles at a Glance

| Action | Storekeeper | Auditor | Admin |
|---|---|---|---|
| Record stock movements | Yes | No | Yes |
| Perform physical counts | Yes | No | Yes |
| Approve/reject adjustments | No | Yes | No |
| Manage users, suppliers, departments | No | No | Yes |
| View dashboard, history, batches | Yes | Yes | Yes |

## Running Locally

**Requirements:** Java 17+, Maven, PostgreSQL

```bash
# Create the database
createdb hospital_inventory

# Configure src/main/resources/application.properties with your 
# database username/password

# Run
./mvnw spring-boot:runThe API runs on http://localhost:8080. See the frontend repo for the Angular client.What's Not Built Yet
Being upfront about scope: predictive reordering, auto-generated purchase orders, barcode/QR scanning, and containerized deployment are planned but not yet implemented. The anomaly detection is intentionally simple and statistical, not a trained machine learning model — with the amount of data a system like this realistically has early on, an honest, explainable rule beats an impressive-sounding but unverifiable prediction.
Why This Project
Built as a learning project to genuinely understand full-stack engineering: schema design that prevents entire categories of bugs, authentication done properly (hashed passwords, real JWT verification, enforced roles), and features scoped to what the data can actually support rather than what sounds most impressive.
