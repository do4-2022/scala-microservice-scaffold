resource "kubernetes_namespace" "app" {
  metadata {
    name = "$name$"
  }
}

// Postgres
resource "kubernetes_persistent_volume" "postgres" {
  count = var.need_persistent_volumes ? 1 : 0

  metadata {
    name = "postgres-pv"
  }

  spec {
    access_modes       = ["ReadWriteOnce"]
    storage_class_name = var.persistent_volume_storage_class
    capacity = {
      storage = var.postgres_volume_size
    }
    persistent_volume_reclaim_policy = "Retain"

    persistent_volume_source {
      local {
        path = var.postgres_volume_local_path
      }
    }
  }
}

resource "kubernetes_persistent_volume_claim" "postgres_pvc" {
  metadata {
    name      = "postgres-pvc"
    namespace = "$name$"
  }

  spec {
    access_modes = ["ReadWriteOnce"]
    resources {
      requests = {
        storage = var.postgres_volume_size
      }
    }
  }
}

resource "kubernetes_deployment" "postgres" {
  metadata {
    name      = "postgres-deployment"
    namespace = "$name$"
  }

  spec {
    replicas = 1

    selector {
      match_labels = {
        app = "postgres"
      }
    }

    template {
      metadata {
        labels = {
          app = "postgres"
        }
      }

      spec {
        container {
          image = "postgres:15.3-alpine"
          name  = "postgres"

          env {
            name  = "POSTGRES_PASSWORD"
            value = var.postgres_password
          }
          env {
            name  = "POSTGRES_USER"
            value = var.postgres_user
          }
          env {
            name  = "POSTGRES_DB"
            value = var.postgres_database
          }

          port {
            container_port = 5432
          }

          volume_mount {
            name       = "postgres"
            mount_path = "/var/lib/postgresql/data"
          }
        }

        volume {
          name = "postgres"

          persistent_volume_claim {
            claim_name = kubernetes_persistent_volume_claim.postgres_pvc.metadata.0.name
          }
        }
      }
    }
  }
}

resource "kubernetes_service" "postgres" {
  metadata {
    name      = "postgres-service"
    namespace = "$name$"
  }

  spec {
    selector = {
      app = "postgres"
    }

    port {
      port        = 5432
      target_port = 5432
    }
  }
}

// RabbitMQ
resource "kubernetes_persistent_volume" "rabbitmq" {
  count = var.need_persistent_volumes ? 1 : 0

  metadata {
    name = "rabbitmq-pv"
  }

  spec {
    access_modes       = ["ReadWriteOnce"]
    storage_class_name = var.persistent_volume_storage_class
    capacity = {
      storage = var.rabbitmq_volume_size
    }
    persistent_volume_reclaim_policy = "Retain"

    persistent_volume_source {
      local {
        path = var.rabbitmq_volume_local_path
      }
    }
  }
}

resource "kubernetes_persistent_volume_claim" "rabbitmq" {
  metadata {
    name      = "rabbitmq-pvc"
    namespace = "$name$"
  }

  spec {
    access_modes = ["ReadWriteOnce"]
    resources {
      requests = {
        storage = var.rabbitmq_volume_size
      }
    }
  }
}

resource "kubernetes_deployment" "rabbitmq" {
  metadata {
    name      = "rabbitmq-deployment"
    namespace = "$name$"
  }

  spec {
    replicas = 1

    selector {
      match_labels = {
        app = "rabbitmq"
      }
    }

    template {
      metadata {
        labels = {
          app = "rabbitmq"
        }
      }

      spec {
        container {
          image = "rabbitmq:3.9.8-alpine"
          name  = "rabbitmq"

          env {
            name  = "RABBITMQ_DEFAULT_USER"
            value = var.rabbitmq_user
          }
          env {
            name  = "RABBITMQ_DEFAULT_PASS"
            value = var.rabbitmq_password
          }

          port {
            container_port = 5672
          }
          port {
            container_port = 15672
          }
        }

        volume {
          name = "rabbitmq"

          persistent_volume_claim {
            claim_name = kubernetes_persistent_volume_claim.rabbitmq.metadata.0.name
          }
        }
      }
    }
  }
}

resource "kubernetes_service" "rabbitmq" {
  metadata {
    name      = "rabbitmq-service"
    namespace = "$name$"
  }

  spec {
    selector = {
      app = "rabbitmq"
    }

    port {
      name        = "amqp"
      port        = 5672
      target_port = 5672
    }
    port {
      name        = "management"
      port        = 15672
      target_port = 15672
    }
  }
}

// App
resource "kubernetes_deployment" "app" {
  metadata {
    name      = "$name$-deployment"
    namespace = "$name$"
  }

  spec {
    replicas = 1

    selector {
      match_labels = {
        app = "$name$"
      }
    }

    template {
      metadata {
        labels = {
          app = "$name$"
        }
      }

      spec {
        container {
          image = "nginx:latest"
          name  = "$name$"

          env {
            name  = "POSTGRES_PASSWORD"
            value = var.postgres_password
          }
          env {
            name  = "POSTGRES_USER"
            value = var.postgres_user
          }
          env {
            name  = "POSTGRES_DB"
            value = var.postgres_database
          }
          env {
            name  = "POSTGRES_HOST"
            value = kubernetes_service.postgres.metadata.0.name
          }
          env {
            name  = "POSTGRES_PORT"
            value = "5432"
          }

          env {
            name  = "RABBITMQ_HOST"
            value = kubernetes_service.rabbitmq.metadata.0.name
          }
          env {
            name  = "RABBITMQ_PORT"
            value = "5672"
          }
          env {
            name  = "RABBITMQ_USER"
            value = var.rabbitmq_user
          }
          env {
            name  = "RABBITMQ_PASSWORD"
            value = var.rabbitmq_password
          }

          port {
            container_port = 8080
          }
        }
      }
    }
  }
}

resource "kubernetes_service" "app" {
  metadata {
    name      = "$name$-service"
    namespace = "$name$"
  }

  spec {
    selector = {
      app = "$name$"
    }

    port {
      port        = 80
      target_port = kubernetes_deployment.app.spec.0.template.0.spec.0.container.0.port.0.container_port
    }
  }
}


// Ingress
resource "kubernetes_ingress_v1" "app" {
  metadata {
    name      = "ingress"
    namespace = "$name$"
    annotations = {
      "kubernetes.io/ingress.class" = "nginx"
    }
  }

  spec {
    rule {
      host = var.ingress_host

      http {
        path {
          path = "/"

          backend {
            service {
              name = kubernetes_service.app.metadata.0.name
              port {
                number = kubernetes_service.app.spec.0.port.0.port
              }
            }
          }
        }
      }
    }
  }
}
