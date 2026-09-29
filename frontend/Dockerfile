FROM node:20-alpine AS build
WORKDIR /app
COPY package.json package-lock.json* ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:alpine
RUN addgroup -S appuser && adduser -S appuser -G appuser
COPY --from=build /app/dist/stock-market-analyzer/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
RUN chown -R appuser:appuser /usr/share/nginx/html \
    && chown -R appuser:appuser /var/cache/nginx \
    && chown -R appuser:appuser /var/log/nginx \
    && touch /var/run/nginx.pid && chown appuser:appuser /var/run/nginx.pid
USER appuser
EXPOSE 80
HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
  CMD curl -f http://127.0.0.1:80/ || exit 1
