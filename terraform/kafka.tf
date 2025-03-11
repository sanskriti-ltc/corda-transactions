resource "helm_release" "kafka" {
  name       = "kafka"
  repository = "https://charts.bitnami.com/bitnami"
  chart      = "kafka"

  set {
    name  = "replicaCount"
    value = 1
  }

  set {
    name  = "service.type"
    value = "LoadBalancer"
  }
}
