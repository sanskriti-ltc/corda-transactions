provider "google" {
  project = var.project_id
  region  = var.region
}

provider "kubernetes" {
  host                   = google_container_cluster.primary.endpoint
  token                  = data.google_client_config.default.access_token
  cluster_ca_certificate = base64decode(google_container_cluster.primary.master_auth.0.cluster_ca_certificate)
}

data "google_client_config" "default" {}

resource "google_container_cluster" "primary" {
  name     = "corda-cluster"
  location = var.region

  remove_default_node_pool = true
  initial_node_count       = 1

  networking_mode = "VPC_NATIVE"

  master_auth {
    client_certificate_config {
      issue_client_certificate = false
    }
  }
}

resource "google_container_node_pool" "primary_nodes" {
  name       = "primary-node-pool"
  location   = var.region
  cluster    = google_container_cluster.primary.name
  node_count = 1

  node_config {
    machine_type = "e2-standard-4"
    disk_size_gb = 50
    preemptible  = true

    oauth_scopes = [
      "https://www.googleapis.com/auth/cloud-platform"
    ]
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
      port        = 8888
      target_port = 8888
    }

    type = "LoadBalancer"
  }
}

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
          name  = "corda"
          image = "corda/corda-enterprise:latest"

          port {
            container_port = 8888
          }
        }
      }
    }
  }
}
