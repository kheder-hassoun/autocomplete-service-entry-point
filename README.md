# Autocomplete API Service

This service powers the real-time autocomplete API by querying a Redis Cluster that holds top-K completions for user-provided prefixes. It is designed for high availability, autoscaling, and observability in Kubernetes.

---

##  Overview

* Exposes a REST API endpoint to fetch completions for a given prefix
* Queries Redis Cluster for the results
* Integrated with Prometheus metrics
* Supports both HPA and VPA autoscaling

---

##  Stack

* Java 17 + Spring Boot
* Redis Cluster (via Spring Data Redis)
* Kubernetes (HPA + VPA)
* Prometheus (ServiceMonitor integration)

---

##  API Endpoint

| Method | Endpoint        | Description              |
| ------ | --------------- | ------------------------ |
| GET    | `/api?q=prefix` | Get autocomplete results |

Example:

```
GET /api?q=hel
```

Response:

```json
{
  "prefix": "hel",
  "results": ["hello", "help", "helium"]
}
```

---

##  Redis Configuration

Pulled from `application.properties`:

```properties
server.port=8082
spring.data.redis.cluster.nodes=redis-redis-cluster-0.redis-redis-cluster-headless:6379,redis-redis-cluster-1.redis-redis-cluster-headless:6379,redis-redis-cluster-2.redis-redis-cluster-headless:6379
spring.data.redis.cluster.max-redirects=3
spring.redis.timeout=60000

# Prometheus
management.endpoints.web.exposure.include=prometheus
management.endpoint.prometheus.enabled=true
management.endpoint.health.show.details=always
```

---

##  Observability

### Prometheus Integration

Expose metrics at `/actuator/prometheus` via Spring Boot Actuator.

```yaml
apiVersion: monitoring.coreos.com/v1
kind: ServiceMonitor
metadata:
  name: autocomplete
  namespace: kh-pipeline
  labels:
    app.kubernetes.io/instance: prometheus
    release: prometheus
spec:
  endpoints:
  - interval: 30s
    port: autocompleteport
    path: /actuator/prometheus
  namespaceSelector:
    matchNames:
    - kh-pipeline
  selector:
    matchLabels:
      app: autocompletelabel
```

---

##  Autoscaling

### Horizontal Pod Autoscaler (HPA)

```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: autocomplete-service-hpa
  namespace: kh-pipeline
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: autocomplete-service
  minReplicas: 2
  maxReplicas: 4
  behavior:
    scaleUp:
      stabilizationWindowSeconds: 0
      policies:
        - type: Percent
          value: 50
          periodSeconds: 15
    scaleDown:
      stabilizationWindowSeconds: 300
      policies:
        - type: Percent
          value: 50
          periodSeconds: 15
  metrics:
    - type: Resource
      resource:
        name: cpu
        target:
          type: Utilization
          averageUtilization: 70
```

### Vertical Pod Autoscaler (VPA)

```yaml
apiVersion: autoscaling.k8s.io/v1
kind: VerticalPodAutoscaler
metadata:
  name: autocomplete-vpa
  namespace: kh-pipeline
spec:
  targetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: autocomplete-service
  updatePolicy:
    updateMode: "Auto"
  resourcePolicy:
    containerPolicies:
      - containerName: "autocomplete-service"
        controlledValues: "RequestsOnly"
        minAllowed:
          cpu: "100m"
          memory: "256Mi"
        maxAllowed:
          cpu: "1500m"
          memory: "1Gi"
```

---

## Pod Disruption Budget

```yaml
apiVersion: policy/v1
kind: PodDisruptionBudget
metadata:
  name: autocomplete-pdb
  namespace: kh-pipeline
spec:
  minAvailable: 1
  selector:
    matchLabels:
      app: autocomplete-service
```

---

##  License

kheder khdrhswn32@gmail.com 
