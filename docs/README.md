# desafioQuality

- Path: /api/v1
- [Coverage Report HTML](../reports/index.html)


## Endpoints

For request/response format details refer to the [documentation](./docs.yaml)

### /hotels

- GET:

    - With no parameters returns json array of hotels with `reserved=false`.
    - Accepts 3 query parameters (all must be sent in request):
    `dateFrom`, `dateTo`, `destination`
      - Dates string pattern `dd/MM/YYYY`
      - Dates must not be the same or overlap
      - There has to be at least one hotel with the specified `destination` (reserved or not)

Example request:

```
/api/v1/hotels?dateTo=19/02/2021&destination=buenos aires&dateFrom=10/02/2021
```

### /booking (Hotel Booking)

- POST:
    - `dni` must be a numeric string (no special characters)
    - Accepts `CREDIT` and `DEBIT` payment types
    - Accepts up to 6 payment dues 
    - `dues=1` returns 0% `interest for both `CREDIT` and `DEBIT` types
    - Dates string pattern `dd/MM/YYYY`
    - `dateFrom` and `dateTo` must not be the same or overlap

Example request body:

```
{
   "username": "email_232@mail.com.ar",
   "booking": {
       "dateFrom": "12/03/2021",
       "dateTo": "14/03/2021",
       "destination": "Buenos Aires",
       "hotelCode": "bh-0002",
       "peopleAmount": 2,
       "roomType": "DOUBLE",
       "people": [
          {
               "dni": "34567452",
               "name": "Carlos",
               "lastname": "López",
               "birthDate": "10/11/1982",
               "mail": "mail1@mail.com"
           },
           {
               "dni": "13456346",
               "name": "María Juana",
               "lastname": "Martínez",
               "birthDate": "20/11/1972",
               "mail": "mail2@mail.com"
           }
       ],
       "paymentMethod": {
           "type": "debit",
           "number": "131414552",
           "dues": 1
       }
   }
}
```

### /flights

- GET:

  - With no parameters returns json array of flights with `availableSeats` greater than 0.
  - Accepts 4 query parameters (all must be sent in request):
    `dateFrom`, `dateTo`, `origin`, `destination`
    - Dates string pattern `dd/MM/YYYY`
    - Dates must not be the same or overlap
    - There has to be at least one flight with the specified `origin` (available or not)
    - There has to be at least one flight with the specified `destination` (reserved or not)

Example request:

```
/api/v1/flights?dateTo=20/02/2021&dateFrom=19/02/2021&origin=puerto iguazú&destination=bogota
```

### /flight-reservation (Flight Booking)

- POST:
  - `dni` must be a numeric string (no special characters)
  - Accepts `CREDIT` and `DEBIT` payment types
  - Accepts up to 6 payment dues
  - `dues=1` returns 0% `interest for both `CREDIT` and `DEBIT` types
  - Dates string pattern `dd/MM/YYYY`
  - `dateFrom` and `dateTo` must not be the same or overlap
  
Example request body:

```
{
   "username": "email232@mail.com",
   "flightReservation": {
       "dateFrom": "12/02/2021",
       "dateTo": "14/02/2021",
       "origin": "buenos aires",
       "destination": "puerto iguazu",
       "flightNumber": "bapi-1235",
       "seats": 2,
       "seatType": "economy",
       "people": [
           {
               "dni": "34567452",
               "name": "Carlos",
               "lastname": "López",
               "birthDate": "10/11/1982",
               "mail": "mail1@mail.com"
           },
           {
               "dni": "13456346",
               "name": "María Juana",
               "lastname": "Martínez",
               "birthDate": "20/11/1972",
               "mail": "mail2@mail.com"
           }
       ],
       "paymentMethod": {
           "type": "credit",
           "number": "131414552",
           "dues": 3
       }
   }
}
```
 
