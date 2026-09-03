# booking-service

Spring Boot-mikroservicen för **rum och bokningar** i Niklas Bodega.

**Port:** 8083  
**Databas:** egen MySQL (rumstyper, rum, bokningar)  
**Stack:** Java 21, Spring Boot, Spring Security, JPA, JWT

## Vad den här tjänsten gör

- Lista rum och rumstyper (`GET /api/rooms`, `GET /api/rooms/roomTypes`)
- Söka lediga rum efter in-/utcheckning och antal gäster (`GET /api/rooms/roomTypes/available`)
- Skapa, läsa, uppdatera och avboka (`/api/bookings`)
- Lista den inloggade användarens bokningar (`GET /api/bookings/my`)
- Svara om användaren har aktiva bokningar (`GET /api/bookings/active`) — används av user-service
- Admin-endpoints för att lägga till/ändra/ta bort rum (kräver roll `ADMIN`)

Vid start seedas rumstyper och rum om tabellerna är tomma. Bokningar kopplas till ett `userId` (från JWT), inte till en user-rad i den här databasen.

Rumslistor är öppna utan inloggning. Att skapa eller ändra bokning kräver giltig JWT-cookie.

## Vad de andra tjänsterna gör

| Tjänst | Ansvar |
|--------|--------|
| **user-service** (8084) | Registrering, inloggning, JWT, användarprofil |
| **review-service** (8086) | Recensioner kopplade till rumstyp och bokningsnummer |
| **frontend** (8087) | Sök, rumskatalog, ny bokning, mina bokningar |

## Hur tjänsterna pratar med varandra

```
Frontend ──► booking-service   rum, tillgänglighet, CRUD på bokningar
Frontend ──► user-service      inloggning (JWT-cookie)
Frontend ──► review-service    recensioner efter avslutad vistelse

booking-service ──GET /api/user──► user-service
  createBooking anropar UserServiceClient.isUserExists(jwt).
  Finns inte användaren (t.ex. raderat konto, giltig token kvar) avbryts bokningen.

user-service ──GET /api/bookings/active──► booking-service
  Innan konto raderas: true/false om användaren har aktiva bokningar.
```

JWT skapas av user-service. Booking-service validerar samma hemlighet (`JWT_SECRET`) i `JwtAuthenticationFilter`.

Intern URL mot den här tjänsten i Docker: `http://booking-service:8083`.  
User-service nås via `USER_SERVICE_URL` / `USER_INTERNAL_ADDRESS` (`http://user-service:8084`).

## Starta hela systemet

Tjänsten körs tillsammans med resten via Docker Compose i infra-repot. Clone alla repos som syskonmappar:

```
niklas-bodega/
├── niklas-bodega-infra/
├── user/
├── booking/              ← du är här
├── review-service/
└── frontend/
```

```bash
docker network create proxy-network   # om nätverket inte redan finns
cd ../niklas-bodega-infra
cp .env.example .env
docker compose up --build
```

I `.env` ska booking-service nås som:

```env
USER_INTERNAL_ADDRESS=http://user-service:8084
BOOKING_INTERNAL_ADDRESS=http://booking-service:8083
```

Öppna http://localhost:8087. Booking API ligger på http://localhost:8083.

Se [niklas-bodega-infra/README.md](../niklas-bodega-infra/README.md) för miljövariabler och portar.

## Köra bara den här tjänsten (IDE)

Kräver MySQL (t.ex. Compose-databasen på `localhost:3309`) och att `user.service.url` pekar på en körande user-service.

```bash
mvn spring-boot:run
```
