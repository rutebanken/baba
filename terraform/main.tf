terraform {
  required_version = ">= 0.13.2"
}

provider "google" {
  version = "~> 4.7.0"
}
provider "kubernetes" {
  load_config_file = var.load_config_file
  version = "~> 1.13.4"
}

# create service account
resource "google_service_account" "baba_service_account" {
  account_id = "${var.labels.team}-${var.labels.app}-sa"
  display_name = "${var.labels.team}-${var.labels.app} service account"
  project = var.gcp_resources_project
}

# add service account as member to the cloudsql client
resource "google_project_iam_member" "project" {
  project = var.gcp_resources_project
  role = var.service_account_cloudsql_role
  member = "serviceAccount:${google_service_account.baba_service_account.email}"
}

# create key for service account
resource "google_service_account_key" "baba_service_account_key" {
  service_account_id = google_service_account.baba_service_account.name
}

# Add SA key to to k8s
resource "kubernetes_secret" "baba_service_account_credentials" {
  metadata {
    name = "${var.labels.team}-${var.labels.app}-sa-key"
    namespace = var.kube_namespace
  }
  data = {
    "credentials.json" = base64decode(google_service_account_key.baba_service_account_key.private_key)
  }
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
