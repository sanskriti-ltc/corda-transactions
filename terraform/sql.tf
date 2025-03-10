resource "google_sql_database_instance" "corda_db" {
  name             = var.db_instance_name
  database_version = "POSTGRES_14"
  region           = var.region

  settings {
    tier = "db-custom-2-8192"
  }
}

resource "google_sql_database" "corda_cluster" {
  name     = "cordacluster"
  instance = google_sql_database_instance.corda_db.name
}

resource "google_sql_user" "corda_user" {
  name     = var.db_username
  instance = google_sql_database_instance.corda_db.name
  password = var.db_password
}
