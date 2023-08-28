#!/bin/bash

# Check if an environment variable is set and not empty
check_variable() {
  if [[ -z "${!1}" ]]; then
    echo "Error: $1 is not set or is empty."
    exit 1
  fi
}

# Check all the environment variables
check_variable "PUBLIC_IP"

check_variable "JWT_SECRET"

check_variable "RDS_HOST"
check_variable "RDS_PASSWORD"
check_variable "RDS_USERNAME"

check_variable "S3_BUCKET_NAME"
check_variable "S3_ACCESS_KEY"
check_variable "S3_SECRET_KEY"

check_variable "AWS_ACCESS_KEY"
check_variable "AWS_SECRET_KEY"

check_variable "TYPE"
check_variable "PROJECT_ID"
check_variable "PRIVATE_KEY_ID"
check_variable "PRIVATE_KEY"
check_variable "CLIENT_EMAIL"
check_variable "CLIENT_ID"
check_variable "AUTH_URI"
check_variable "TOKEN_URI"
check_variable "AUTH_PROVIDER_X509_CERT_URL"
check_variable "CLIENT_X509_CERT_URL"
check_variable "UNIVERSE_DOMAIN"

check_variable "DISCORD_WEBHOOK_ID"
check_variable "DISCORD_WEBHOOK_TOKEN"

# If all checks pass, display success message
echo "All environment variables are set and not empty."
