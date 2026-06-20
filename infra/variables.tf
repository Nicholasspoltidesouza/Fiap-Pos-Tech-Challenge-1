variable "cluster_name" {
  description = "Name of the local kind cluster"
  type        = string
  default     = "oficina-cluster"
}

variable "namespace" {
  description = "Kubernetes namespace for the application resources"
  type        = string
  default     = "oficina"
}

variable "node_http_port" {
  description = "Host port mapped to the application NodePort (30080) inside the kind cluster"
  type        = number
  default     = 30080
}

variable "postgres_db" {
  description = "Database name"
  type        = string
  default     = "oficina_mec_db"
}

variable "postgres_user" {
  description = "Database user"
  type        = string
  default     = "admin"
}

variable "postgres_password" {
  description = "Database password"
  type        = string
  default     = "admin"
  sensitive   = true
}

variable "jwt_secret" {
  description = "JWT signing secret consumed by the application"
  type        = string
  default     = "localDevelopmentSecretChangeMeInProductionEnvironments123456"
  sensitive   = true
}
