# The RED Method
Like the Four Golden signals from Google’s SRE book, The RED Method defines the three key metrics you should measure for every microservice in your architecture. Those metrics are:

* Request Rate – the number of requests per second your application is serving.
* Error Rate – the number of failed requests per second. For web applications, it’s the responses that result in a 5xx HTTP status code.
* Duration – the whole distribution of how long it took to send the response.

## Motivation
I'm going to deploy a Spring Boot application in a Kubernetes cluster with Prometheus and Grafana, and generated a dashboard with Golden Signals.

## Prerequisites
* Java 8
* Maven
* Docker
* Kubernetes
* Helm

## Application
For this example, I’m going to use a sample Spring Boot application https://github.com/bendahl/docker-todo/tree/master/backend.

###Instrumenting code with Prometheus metrics

```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

Making the Micrometer library to produce a full Prometheus Histogram

```
management.endpoints.web.exposure.include=prometheus
management.metrics.distribution.percentiles-histogram.http.server.requests=true
```

## Kubernetes
I'm going to enable Kubernetes inside Docker

## Build application

- `mvn clean package`

## Build docker image

- `docker build -t todo/todo-backend .`

## Deploying to Kubernetes

- `kubectl apply -f k8s` deploy service and deployment configs

## Installing Prometheus

- `kubectl create namespace monitoring` create separate kubernetes namespace
- `helm install prometheus -f k8s/monitoring/values-prometheus.yml stable/prometheus -n monitoring` install prometheus with extraScrapeConfigs into cluster

## Installing Grafana

- `kubectl apply -f k8s/monitoring/grafana/config.yml` deploy Prometheus data source config map
- `kubectl apply -f k8s/monitoring/grafana/prometheus-overview-dashboard-configmap.yml` deploy Grafana config map for RED dashboard
- `helm install grafana stable/grafana -f k8s/monitoring/grafana/values.yml --namespace monitoring` install Grafana with instruction to watch for dashboard config maps
  
- `export POD_NAME=$(kubectl get pods --namespace monitoring -l "app.kubernetes.io/name=grafana,app.kubernetes.io/instance=grafana" -o jsonpath="{.items[0].metadata.name}")`
- `kubectl --namespace monitoring port-forward $POD_NAME 3000`

## RED signals:

In order to obtain the data for the dashboards, I used this PromQL sentences:

* **Latency**
  `histogram_quantile(0.95, sum by (le) (rate(http_server_requests_seconds_bucket[1m])))`
  
* **Request rate**
  `sum(rate(http_server_requests_seconds_count[1m]))`

* **Errors per second**
  `sum(rate(http_server_requests_seconds_count{status="5.."}[1m]))`
  


