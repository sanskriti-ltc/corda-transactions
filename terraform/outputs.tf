output "corda_api_url" {
  value = "http://${kubernetes_service.corda_service.status.0.load_balancer.0.ingress.0.ip}:8888"
}

output "kafka_broker_url" {
  value = "http://${helm_release.kafka.metadata.0.name}.default.svc.cluster.local:9092"
}
