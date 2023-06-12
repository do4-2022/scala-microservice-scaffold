// Kubernetes
variable "kube_config_path" {
  type    = string
  default = "~/.kube/config"
}

// Ingress
variable "ingress_host" {
  type    = string
  default = "localhost"
}

// Postgres
variable "postgres_user" {
  type    = string
  default = "postgres"
}

variable "postgres_password" {
  type    = string
  default = "postgres"
}

variable "postgres_database" {
  type    = string
  default = "postgres"
}

variable "postgres_volume_size" {
  type    = string
  default = "5Gi"
}

variable "postgres_volume_local_path" {
  type    = string
  default = "/mnt/postgres-data"
}

// RabbitMQ
variable "rabbitmq_user" {
  type    = string
  default = "rabbitmq"
}

variable "rabbitmq_password" {
  type    = string
  default = "rabbitmq"
}

variable "rabbitmq_volume_size" {
  type    = string
  default = "5Gi"
}

variable "rabbitmq_volume_local_path" {
  type    = string
  default = "/mnt/rabbitmq-data"
}

// Volumes
variable "need_persistent_volumes" {
  type    = bool
  default = true
}

variable "persistent_volume_storage_class" {
  type    = string
  default = "standard"
}
