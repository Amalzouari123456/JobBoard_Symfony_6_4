# Arrete tous les microservices de l'atelier Eureka (Eureka en dernier).
# Utilisation (depuis le dossier microservices) :  .\stop-all.ps1

$ports = 8085, 8081, 8091, 8082, 8083, 8093, 8084, 8094, 8761

foreach ($port in $ports) {
    $conn = Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($conn) {
        Stop-Process -Id $conn.OwningProcess -Force -ErrorAction SilentlyContinue
        Write-Host "Port $port : arrete" -ForegroundColor Green
    } else {
        Write-Host "Port $port : rien a arreter"
    }
}
