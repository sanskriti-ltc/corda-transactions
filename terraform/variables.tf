variable "project_id" {
  description = "trade-finance-452012"
}

variable "region" {
  description = "GCP region"
  default     = "us-central1"
}

variable "cluster_name" {
  description = "GKE Cluster Name"
  default     = "corda-cluster"
}

variable "db_instance_name" {
  description = "Cloud SQL Instance Name"
  default     = "corda-db"
}

variable "db_username" {
  description = "Cloud SQL Database Username"
  default     = "corda-user"
}

variable "db_password" {
  description = "Cloud SQL Database Password"
  default     = "cordapassword"
}
