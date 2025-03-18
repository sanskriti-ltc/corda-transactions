provider "helm" {
  kubernetes {
    config_path = "~/.kube/config"  # Update with your Kubernetes config path if needed
  }
}

resource "helm_release" "kafka" {
  name       = "my-kafka"
  chart      = "bitnami/kafka"
  version    = "latest"  # You can specify a specific version here, if needed
  namespace  = "default" # You can specify a namespace here if needed
  
  set {
    name  = "replicaCount"
    value = "1"
  }

  set {
    name  = "persistence.enabled"
    value = "false"
  }

  set {
    name  = "resources.requests.cpu"
    value = "50m"
  }

  set {
    name  = "resources.requests.memory"
    value = "64Mi"
  }

  set {
    name  = "listeners.client.protocol"
    value = "PLAINTEXT"
  }

  set {
    name  = "autoCreateTopicsEnable"
    value = "true"
  }
}
