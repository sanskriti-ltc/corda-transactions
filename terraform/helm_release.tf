resource "helm_release" "corda" {
  name       = "corda"
  namespace  = "corda"
  repository = "oci://registry-1.docker.io/corda"
  chart      = "corda"  # Chart name within the OCI registry
  version    = "5.1.0"  # Chart version
  values     = [file("values.yaml")]  # Path to your custom values.yaml file

  depends_on = [
    google_container_cluster.gke_cluster
  ]
}
