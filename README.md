# InfluxDB Data Visualization Guide

This guide provides instructions on how to check and visualize data in InfluxDB.

## Table of Contents
1. [Check Data Using InfluxDB CLI](#1-check-data-using-influxdb-cli)
2. [Use the InfluxDB HTTP API](#2-use-the-influxdb-http-api)
3. [Visualize Data with Grafana](#3-visualize-data-with-grafana)
4. [Other Visualization Tools](#4-other-visualization-tools)

## 1. Check Data Using InfluxDB CLI

### Access the InfluxDB CLI
```sh
docker exec -it influxdb influx
```

### Run CLI Commands
- List Databases:
  ```sql
  SHOW DATABASES;
  ```
- Select Database:
  ```sql
  USE mydatabase;
  ```
- Show Measurements:
  ```sql
  SHOW MEASUREMENTS;
  ```
- Query Data:
  ```sql
  SELECT * FROM cpu_load_short;
  ```

## 2. Use the InfluxDB HTTP API

Query data using curl:
```sh
curl -G http://localhost:8086/query --data-urlencode "db=mydatabase" --data-urlencode "q=SELECT * FROM cpu_load_short"
```

## 3. Visualize Data with Grafana

### Install Grafana
Add to your `docker-compose.yml`:
```yaml
version: '3.7'

services:
  grafana:
    image: grafana/grafana:latest
    ports:
      - "3000:3000"
    depends_on:
      - influxdb
    environment:
      GF_SECURITY_ADMIN_PASSWORD: 'admin'
```

Run:
```sh
docker-compose up -d
```

### Configure InfluxDB as a Data Source in Grafana
1. Open Grafana in a browser: `http://localhost:3000`
2. Go to Configuration -> Data Sources
3. Add InfluxDB data source:
   - URL: `http://influxdb:8086`
   - Database: `mydatabase`
   - User & Password (if authentication is enabled)

### Create a Dashboard
1. Go to Dashboards -> New Dashboard
2. Add a new panel
3. Configure the panel:
   - Select InfluxDB as the data source
   - Write your query
   - Customize visualization
4. Save the dashboard

## 4. Other Visualization Tools
- **Chronograf**: UI tool for InfluxDB 1.x
- **Custom Applications**: Build using InfluxDB's HTTP API and libraries

---

For more detailed information, refer to the official documentation for [InfluxDB](https://docs.influxdata.com/) and [Grafana](https://grafana.com/docs/).