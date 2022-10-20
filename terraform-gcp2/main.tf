terraform {
  required_version = ">= 0.13.2"
}

provider "google" {
  version = ">= 4.26"
}
provider "kubernetes" {
  version = ">= 2.13.1"
}

resource "kubernetes_secret" "ror-baba-secret" {
  metadata {
    name = "${var.labels.team}-${var.labels.app}-secret"
    namespace = var.kube_namespace
  }

  data = {
    "baba-db-username" = var.ror-baba-db-username
    "baba-db-password" = var.ror-baba-db-password
    "baba-smtp-username" = var.ror-baba-smtp-username
    "baba-smtp-password" = var.ror-baba-smtp-password
    "baba-auth0-secret" = var.ror-baba-auth0-secret

  }
}

resource "google_sql_database_instance" "db_instance" {
  name = "baba-db-pg13"
  database_version = "POSTGRES_13"
  project = var.gcp_resources_project
  region = var.db_region

  settings {
    location_preference {
      zone = var.db_zone
    }
    tier = var.db_tier
    user_labels = var.labels
    availability_type = var.db_availability
    backup_configuration {
      enabled = true
      // 01:00 UTC
      start_time = "01:00"
    }
    maintenance_window {
      // Sunday
      day = 7
      // 02:00 UTC
      hour = 2
    }
    ip_configuration {
      require_ssl = true
    }
  }

}

resource "google_sql_database" "db" {
  name = "baba"
  project = var.gcp_resources_project
  instance = google_sql_database_instance.db_instance.name
}

resource "google_sql_user" "db-user" {
  name = var.ror-baba-db-username
  project = var.gcp_resources_project
  instance = google_sql_database_instance.db_instance.name
  password = var.ror-baba-db-password
}
