#!/usr/bin/env bash

curl -v -X POST -H "Content-Type: application/json" -d '{"token":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6ImphbWVzLmtpZWxleSIsInVzZXJfaWQiOiI1OWUxYWE5N2VhNGNhMDBjMzc4NmRiNDAiLCJlbWFpbCI6ImpraWVsZXlAYXN1LmVkdSIsImV4cCI6MTUwNzk4MzE1OX0.mKTLS46RACbgzyKdSTg0TRQ5KuXyjGSaGfifgzIoLYQ"}' http://localhost:8000/api-token-verify/
