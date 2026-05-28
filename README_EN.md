# Sentinel

A lightweight health monitoring and auto-recovery platform for distributed services. When a server reboots, Sentinel automatically restarts all services in the correct dependency order.

**v1.0.0** is now complete with all core features. Contributions are welcome.

*[中文文档](README.md)*

---

## Core Features

### Cluster & Node Management
- **Service Clusters** — Group multiple service nodes into a cluster (microservice group), set minimum alive count, configure upstream dependencies
- **Service Nodes** — Configure independent health check and restart methods per node (Shell commands or Docker remote API)
- **Web Dashboard** — Built with Bootstrap 5.3 + Thymeleaf: paginated lists, modal forms, manual check/restart triggers, Toast notifications

### Health Checks

| Method | Description |
|--------|-------------|
| **BASH** | Execute a shell command on the Sentinel host; healthy if exit code = 0 |
| **DOCKER_CHECKER** | Connect via Docker TLS, run `docker ps -a`, match target container and inspect its running state |

### Auto Restart

| Method | Description |
|--------|-------------|
| **BASH** | Execute a custom shell command to restart the service |
| **DOCKER_CLIENT** | Call Docker Remote API to `docker start` a container |

### DAG Dependency Graph
- All clusters are composed into a **Directed Acyclic Graph (DAG)** based on upstream dependencies
- Visualized with AntV G6; node colors indicate status (green=ok, yellow=up, red=down, blue=wait)
- Circular dependency detection when adding clusters
- Downstream dependency check when deleting clusters

### Scheduled Scanning & Cascading Recovery
- **Heartbeat Task**: every 60s, BFS-traverse the DAG from start node, health-check all nodes, update cluster statuses
- **Recovery Task**: detect `down` clusters, prioritize the most upstream failed cluster for recovery, then cascade to downstream `wait` clusters
- **Docker Connection Cleanup**: every 10 minutes, evict idle Docker client connections

### Docker TLS Integration
- **Global default certificate** and **per-host certificate** configuration
- Automatic cert path resolution (relative → absolute)
- Connection caching with health checks and auto-eviction on failure
- Configurable timeouts (connect: 10s, response: 30s max)

---

![Design Diagram](images/design_diagram.png)

---

## Tech Stack

| Layer | Technology |
|-------|------------|
| Framework | Spring Boot 3.4.6 |
| Java | 21 |
| Database | SQLite (file-based, zero external dependencies) |
| ORM | MyBatis-Flex 1.10.9 |
| Template | Thymeleaf |
| UI | Bootstrap 5.3 + Font Awesome 6 |
| Graph | AntV G6 |
| Shell Execution | Apache Commons Exec 1.5.0 |
| Docker SDK | docker-java 3.7.1 + Apache HttpClient 5 |
| Build | Maven |

---

## Quick Start

### Prerequisites
- JDK 21+
- Maven 3.6+

### Run

```bash
git clone https://github.com/hancher/sentinel.git
cd sentinel

# Run directly
./mvnw spring-boot:run

# Or package and run
./mvnw package -DskipTests
java -jar target/sentinel-1.0.0-SNAPSHOT.jar
```

Open: **http://localhost:6060**

---

## Configuration

`src/main/resources/application.properties`:

```properties
# Server port
server.port=6060

# SQLite database (auto-created on first run)
spring.datasource.url=jdbc:sqlite:db.sqlite

# Sentinel version
sentinel.version=1.0.0

# Docker TLS global certificate path
sentinel.processor.docker.cert_path=/path/to/certs

# Docker connection idle eviction interval (minutes), -1 to disable
sentinel.processor.docker.max_idle_minutes=30

# Per-host certificate paths (optional)
# sentinel.processor.docker.host-certs[tcp://192.168.1.1:2376]=/path/to/certs-a
```

### Docker TLS Certificates

The certificate directory must contain:
```
certs/
├── ca.pem              # CA certificate
├── cert.pem            # Client certificate
├── key.pem             # Client key
```

Target Docker daemon must have TCP + TLS enabled:
```json
{
  "hosts": ["unix:///var/run/docker.sock", "tcp://0.0.0.0:2376"],
  "tls": true,
  "tlsverify": true,
  "tlscacert": "/etc/docker/certs/ca.pem",
  "tlscert": "/etc/docker/certs/server-cert.pem",
  "tlskey": "/etc/docker/certs/server-key.pem"
}
```

---

## Key Concepts

### Clusters and Nodes

```
┌────────── Cluster A ──────────┐
│  ├ Node a-1 (BASH ping)       │
│  ├ Node a-2 (BASH ping)       │    depends on
│  └ Node a-3 (BASH ping)       │──────────▶ Cluster B
└───────────────────────────────┘            ├ Node b-1
                                             └ Node b-2
```

- **Cluster**: an abstract grouping of service nodes; each cluster has a `minAliveNum`
- **Node**: a concrete service instance with its own health check and restart configuration
- **Dependencies** are defined at the cluster level via `dependClusters`

### Cluster Statuses

| Status | Meaning |
|--------|---------|
| `ok` | All nodes healthy |
| `up` | Some nodes healthy, meets `minAliveNum` |
| `down` | Below minAliveNum |
| `wait` | Waiting for upstream dependency to recover |

### Node Command Format

**BASH mode** — plain shell command:
```
ping baidu.com
```

**DOCKER_CHECKER / DOCKER_CLIENT** — JSON:
```json
{
  "tcpHost": "tcp://192.168.100.100:2376",
  "certPath": "certs",
  "containerIdOrName": "postgres"
}
```

Docker restart additionally requires `cmd`:
```json
{
  "tcpHost": "tcp://192.168.100.100:2376",
  "certPath": "certs",
  "containerIdOrName": "postgres",
  "cmd": "start"
}
```

---

## Scheduled Tasks

| Task | Initial Delay | Interval | Description |
|------|--------------|----------|-------------|
| Heartbeat scan | 5s | 60s | Health checks + failure recovery |
| Docker cleanup | 60s | 10min | Evict idle Docker clients |

---


## Design Philosophy

1. **Built for small-scale distributed systems** — for large clusters, consider k3s/k8s
2. **Dependency-aware auto-recovery** — restarts services in correct order after server reboot
3. **Configuration-driven DAG** — all dependencies managed via Web UI, automatically composed into an acyclic graph
4. **Sentinel self-resilience** — Sentinel itself should be a standalone service with zero external dependencies, auto-starting with the OS
5. **Restart only, not initialization** — focuses solely on restarting existing services, not deploying or provisioning new ones

---

## License

This project is open source. See the [LICENSE](LICENSE) file for details.
