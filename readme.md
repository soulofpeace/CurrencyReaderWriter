## Assumptions:
1. Negative amount are allowed
2. Exchange Rate is static currently but can be replaced with a
webservice call to retrieve exchange rate. If so the implementation need
to be changed, instead of a normal hashmap, a concurrent hashmap need to
be used since different threads would be updating and reading the
currency
