#!/usr/bin/env bash

curl -v -X POST -H "Content-Type: application/json" -d '{"username":"james.kieley","password":"asdf1234"}' http://localhost:8000/api-token-auth/