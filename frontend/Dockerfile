# Multi-stage build for React/Vite development/production
FROM node:20-alpine

WORKDIR /app

# Copy packages and install dependencies
COPY package*.json ./
RUN npm install

# Copy source code
COPY . .

# Expose Vite dev server port
EXPOSE 3000

# Start Vite with --host to allow external traffic to bind
CMD ["npm", "run", "dev", "--", "--host"]
