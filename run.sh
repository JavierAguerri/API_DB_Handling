#!/bin/bash

# Set environment variables
export DB_URL="jdbc:mysql://localhost:3306/"
export DB_NAME="ordersDB"
export DB_USER="javier"
export DB_PASS="kl3y-i_WMnD3CtpN@B2r-"

# Set working directory to the root of your project
cd apidbhandling

# Run Java program
time java -jar target/apidbhandling-1.0-jar-with-dependencies.jar


