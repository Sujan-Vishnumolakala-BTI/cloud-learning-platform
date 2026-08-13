# Use Node 22 as the base environment
FROM node:22-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy package files and install dependencies
COPY package*.json ./
# ADDED --legacy-peer-deps TO BYPASS THE VERSION MATCHING CONFLICT
RUN npm install --legacy-peer-deps

# Install Angular CLI globally inside the container
RUN npm install -g @angular/cli@20

# Copy the rest of your application code
COPY . .

# Expose port 4200 for the Angular dev server
EXPOSE 4200

# Start the application and allow external connections
CMD ["ng", "serve", "--host", "0.0.0.0", "--poll", "2000"]
