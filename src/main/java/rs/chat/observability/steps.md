# Configure all the Grafana stack

# Create Tempo data source

```yaml
traces:
  configs:
    - name: default
      remote_write:
        - endpoint: tempo-eu-west-0.grafana.net:443
          basic_auth:
            username: 329370
            password: ${GRAFANA_API_KEY}
```

# Create Loki for standalone host

```yaml
server:
  http_listen_port: 0
  grpc_listen_port: 0

positions:
  filename: /tmp/positions.yaml

client:
  url: https://332857:${GRAFANA_API_KEY}@logs-prod-eu-west-0.grafana.net/loki/api/v1/push

scrape_configs:
  - job_name: system
    static_configs:
      - targets:
          - localhost
        labels:
          job: varlogs
          __path__: /var/log/*.log
```

And run promtail with the following command:

```bash
docker run --name promtail --volume "$PWD/promtail:/etc/promtail" --volume "/var/log:/var/log" grafana/promtail:master -config.file=/etc/promtail/config.yaml
```

# Prometheus

```yaml
remote_write:
  - url: https://prometheus-prod-01-eu-west-0.grafana.net/api/prom/push
    basic_auth:
      username: 667773
      password: ${GRAFANA_API_KEY}
```
