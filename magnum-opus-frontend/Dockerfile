FROM node:24.15-alpine AS build

WORKDIR /app

RUN npm install --global bun@1.3.14

COPY package.json bun.lock ./
RUN bun install --frozen-lockfile

COPY . .
RUN bun run build

FROM nginxinc/nginx-unprivileged:1.29.0-alpine3.22

COPY nginx/default.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist/magnum-opus-frontend/browser /usr/share/nginx/html

EXPOSE 8080
