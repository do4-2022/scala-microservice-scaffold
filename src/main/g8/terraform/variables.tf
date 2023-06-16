// Kubernetes
variable "kube_config_path" {
  type    = string
  default = "~/.kube/config"
}
$if(add_http_server.truthy)$

// Ingress
variable "ingress_host" {
  type    = string
  default = "localhost"
}

variable "http_server_port" {
  type    = string
  default = "8080"
}
$endif$$if(add_sql_orm.truthy)$

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
$endif$$if(add_message_queue.truthy)$

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
$endif$$if(add_sql_orm.truthy||add_message_queue.truthy)$

// Volumes
variable "need_persistent_volumes" {
  type    = bool
  default = true
}

variable "persistent_volume_storage_class" {
  type    = string
  default = "standard"
}
$endif$