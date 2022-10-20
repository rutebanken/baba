#Enviroment variables
variable "gcp_resources_project" {
  description = "The GCP project hosting the project resources"
}

variable "kube_namespace" {
  description = "The Kubernetes namespace"
  default = "baba"
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

variable ror-baba-auth0-secret {
  description = "baba Auth0 secret"
}

variable "db_region" {
  description = "GCP  region"
  default = "europe-west1"
}

variable "db_zone" {
  description = "GCP zone letter"
  default = "europe-west1-b"
}

variable "db_tier" {
  description = "Database instance tier"
  default = "db-custom-1-3840"
}

variable "db_availability" {
  description = "Database availability"
  default = "ZONAL"
}
