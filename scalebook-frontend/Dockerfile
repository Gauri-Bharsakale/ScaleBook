# scalebook-frontend/Dockerfile

# ─────────────────────────────────────────────
# STAGE 1: Build React app
# ─────────────────────────────────────────────
FROM node:22-alpine AS builder

WORKDIR /app

# Copy package files first (same caching trick as backend)
COPY package.json package-lock.json ./
RUN npm install

# Copy source and build
COPY . .
RUN npm run build
# This creates a /app/dist folder with static files

# ─────────────────────────────────────────────
# STAGE 2: Serve with Nginx
# ─────────────────────────────────────────────
FROM nginx:alpine

# Remove default Nginx welcome page
RUN rm -rf /usr/share/nginx/html/*

# Copy built files from stage 1 into Nginx's serving directory
COPY --from=builder /app/dist /usr/share/nginx/html

# Copy our custom Nginx config (explained below)
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]