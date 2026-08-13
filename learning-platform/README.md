# Learning Platform

## Start

```powershell
docker compose -f infra/docker-compose.yml up --build
```

## Start in Background

```powershell
docker compose -f infra/docker-compose.yml up -d --build
```

## Check Configuration

```powershell
docker compose -f infra/docker-compose.yml config
```

## Check Containers

```powershell
docker compose -f infra/docker-compose.yml ps
```

## View Logs

```powershell
docker compose -f infra/docker-compose.yml logs
```

```powershell
docker compose -f infra/docker-compose.yml logs --tail=50
```

```powershell
docker compose -f infra/docker-compose.yml logs -f
```

## Service Logs

```powershell
docker compose -f infra/docker-compose.yml logs -f user-service
```

```powershell
docker compose -f infra/docker-compose.yml logs -f course-service
```

```powershell
docker compose -f infra/docker-compose.yml logs -f enroll-service
```

```powershell
docker compose -f infra/docker-compose.yml logs -f recommendation-service
```

## Stop

```powershell
docker compose -f infra/docker-compose.yml stop
```

## Start Again

```powershell
docker compose -f infra/docker-compose.yml start
```

## Restart

```powershell
docker compose -f infra/docker-compose.yml restart
```

## Stop and Remove Containers

```powershell
docker compose -f infra/docker-compose.yml down
```

## Stop and Remove Containers and Volumes

```powershell
docker compose -f infra/docker-compose.yml down -v
```

## Rebuild

```powershell
docker compose -f infra/docker-compose.yml build --no-cache
```

```powershell
docker compose -f infra/docker-compose.yml up -d --build
```

## Service URLs

```text
http://localhost:8081
http://localhost:8082
http://localhost:8083
http://localhost:8000
```

## Ports

```text
PostgreSQL       5432
User Service     8081
Course Service   8082
Enroll Service   8083
Recommendation   8000
```

## Check PostgreSQL

```powershell
docker exec -it learning-platform-postgres psql -U learning_admin -d learning_platform
```

## Check Docker Images

```powershell
docker images
```

## Check Docker Volumes

```powershell
docker volume ls
```

## Check Docker Networks

```powershell
docker network ls
```

## Clean Unused Docker Resources

```powershell
docker system prune
```
