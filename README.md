# desafioQuality

- Path: /api/v1`

[comment]: <> (- [./target/coverage-reports/index.html]&#40;Coverage Report&#41;)
- [Coverage Report](./target/coverage-reports/index.html)
##Endpoints

### /hotels

- GET:

    - With no parameters returns json array of hotels with `reserved=false`.
    - Accepts 3 query parameters (all must be sent in request):
    `dateFrom`, `dateTo`, `destination`
      - Dates string pattern `dd/MM/YYYY`
      - Dates must not be the same or overlap
      - There has to be at least one hotel with the specified `destination` (reserved or not)
    
### /booking (Hotel booking)

- POST:
    - `dni` must be a numeric string (no special characters)
    - Accepts `CREDIT` and `DEBIT` payment types
    - Accepts up to 6 payment dues 
    - `dues=1` returns 0% `interest for both `CREDIT` and `DEBIT` types
    - Dates string pattern `dd/MM/YYYY`
    - `dateFrom` and `dateTo` must not be the same or overlap
 
