resource "helm_release" "nginx_ingress" {
  name       = "nginx-ingress"
  namespace  = "ingress-nginx"
  repository = "https://kubernetes.github.io/ingress-nginx"
  chart      = "ingress-nginx"
  version    = "4.0.13"  # Ensure you're using the latest stable version
  create_namespace = true

  values = [
    <<-EOF
    controller:
      ingressClass: "nginx"
      service:
        externalTrafficPolicy: Local
        annotations:
          cloud.google.com/load-balancer-type: "Internal"  # Use "External" for external load balancer
    EOF
  ]

  depends_on = [
    google_container_cluster.gke_cluster
  ]
}
