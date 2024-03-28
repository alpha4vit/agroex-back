# Metrics monitoring setup

## Services:

### Micrometer

* #### For metrics collection

### Prometheus

* #### For metrics gathering

### Grafana

* #### For building graphs and datagrams
# Setup steps
* ### Add prometheus and micrometer dependency from mvn repository
> ### [Prometheus and micrometer dependency in mvn repository](https://mvnrepository.com/artifact/io.micrometer/micrometer-registry-prometheus) 
* ### Add spring actuator dependency from mvn repository
> ### [Actuator in mvn repository](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-actuator/3.2.4) 
* ### Add data scrape config file for prometheus
```yaml
global:
    scrape_interval: 5s

scrape_configs:
    - job_name: 'spring_micrometer'
      metrics_path: '/sys/metrics'
      scrape_interval: 30s
      static_configs:
          - targets:
                - [app-container-name]:8080
    - job_name: 'prometheus'
      static_configs:
          - targets:
                - 'prometheus:9090'
```
* ### Add datasources file for grafana 
```yaml
apiVersion: 1
datasources:
    - access: proxy
      basicAuth: false
      editable: true
      isDefault: false
      name: prometheus
      type: prometheus
      uid: prometheus
      url: http://prometheus:9090
      version: 1
```
# Docker-compose changes
* ### Add prometheus service in docker-compose
```yaml
  prometheus:
      image: prom/prometheus:v2.27.0
      ports:
          - "9090:9090"
      volumes:
          - ./config/prometheus.yaml:/etc/prometheus/prometheus.yml
          - prometheus:/prometheus
      command:
              [
                  '--log.level=debug',
                  '--config.file=/etc/prometheus/prometheus.yml',
                  '--enable-feature=remote-write-receiver',
                  '--query.lookback-delta=30s'
              ]
      networks:
          - app-network
```
* ### Add grafana service in docker-compose
```yaml
  grafana:
      image: grafana/grafana:9.1.6
      ports:
          - "3000:3000"
      environment:
          GF_AUTH_ANONYMOUS_ENABLED: "true"
          GF_AUTH_DISABLE_LOGIN_FORM: "true"
          GF_AUTH_ANONYMOUS_ORG_ROLE: "Admin"
      volumes:
          - ./config/datasources.yaml:/etc/grafana/provisioning/datasources/datasources.yml
          - grafana:/var/lib/grafana
      networks:
          - app-network
```
## Now you can:
* ### reach http://localhost:9090 to open prometheus and see your app metrics
* ### reach http://localhost:3000 to build dashboards and see metrics graphs