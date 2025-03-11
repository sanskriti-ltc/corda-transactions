resource "kubernetes_deployment" "corda" {
  metadata {
    name = "corda-node"
    labels = {
      app = "corda"
    }
  }

  spec {
    replicas = 1

    selector {
      match_labels = {
        app = "corda"
      }
    }

    template {
      metadata {
        labels = {
          app = "corda"
        }
      }

      spec {
        container {
          image = "corda/corda-os-combined-worker-kafka:5.2.0.0"
          name  = "corda"

          env {
            name  = "DATABASE_URL"
            value = "jdbc:postgresql://google_sql_database_instance.corda_db.public_ip:5432/cordacluster"
          }

          env {
            name  = "DATABASE_USER"
            value = var.db_username
          }

          env {
            name  = "DATABASE_PASSWORD"
            value = var.db_password
          }

          env {
            name  = "KAFKA_SERVERS"
            value = "kafka.default.svc.cluster.local:9092"
          }

          port {
            container_port = 8888
          }

          port {
            container_port = 7004
          }
        }
      }
    }
  }
}

resource "kubernetes_service" "corda_service" {
  metadata {
    name = "corda-service"
  }

  spec {
    selector = {
      app = "corda"
    }

    port {
      protocol    = "TCP"
      port        = 8888
      target_port = 8888
    }

    type = "LoadBalancer"
  }
}
