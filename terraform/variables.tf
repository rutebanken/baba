#Enviroment variables
variable "gcp_project" {
  description = "The GCP project hosting the workloads"
}

variable "gcp_resources_project" {
  description = "The GCP project hosting the project resources"
}

variable "kube_namespace" {
  description = "The Kubernetes namespace"
}

variable "labels" {
  description = "Labels used in all resources"
  type = map(string)
  default = {
    manager = "terraform"
    team = "ror"
    slack = "talk-ror"
    app = "baba"
  }
}

variable "force_destroy" {
  description = "(Optional, Default: false) When deleting a bucket, this boolean option will delete all contained objects. If you try to delete a bucket that contains objects, Terraform will fail that run"
  default = false
}

variable "prevent_destroy" {
  description = "Prevent destruction of bucket"
  type = bool
  default = false
}

variable "load_config_file" {
  description = "Do not load kube config file"
  default = false
}

variable "service_account_cloudsql_role" {
  description = "Role of the Service Account - more about roles https://cloud.google.com/pubsub/docs/access-control"
  default = "roles/cloudsql.client"
}

variable ror-baba-db-username {
  description = "baba database username"
}

variable ror-baba-db-password {
  description = "baba database password"
}

variable ror-baba-smtp-username {
  description = "baba smtp username"
}

variable ror-baba-smtp-password {
  description = "baba smtp password"
}

variable ror-baba-keycloak-secret {
  description = "baba keycloak secret"
}

variable ror-baba-auth0-secret {
  description = "baba Auth0 secret"
}

variable "db_tier" {
  description = "Database instance tier"
  default = "db-custom-1-3840"
}

variable "db_availability" {
  description = "Database availablity"
  default = "ZONAL"
}
