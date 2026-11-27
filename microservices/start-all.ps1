# Lance tous les microservices de l'atelier Eureka, chacun dans sa propre fenetre.
# Utilisation (depuis le dossier microservices) :  .\start-all.ps1
# Pour tout arreter :                               .\stop-all.ps1

$root = $PSScriptRoot

function Test-Port($port) {
    [bool](Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction SilentlyContinue)
}

function Start-Micro($title, $folder, $command, $port) {
    if (Test-Port $port) {
        Write-Host "  $title : deja lance sur le port $port (ignore)" -ForegroundColor Yellow
        return
    }
    $dir = Join-Path $root $folder
    $full = "`$Host.UI.RawUI.WindowTitle = '$title'; Set-Location '$dir'; $command"
    Start-Process powershell -ArgumentList '-NoExit', '-Command', $full
    Write-Host "  $title : lance (port $port)" -ForegroundColor Green
}

# 0. MySQL (necessaire pour Job)
if (-not (Test-Port 3306)) {
    Write-Host "ATTENTION : MySQL n'est pas demarre. Lance-le dans XAMPP, sinon Job echouera." -ForegroundColor Red
}

# 1. Eureka en premier, puis on attend qu'il reponde
Write-Host "1) Eureka Server"
Start-Micro 'EUREKA 8761' 'eureka-server' 'mvn spring-boot:run' 8761

Write-Host "   Attente du demarrage d'Eureka (max 3 min)..."
$ok = $false
for ($i = 0; $i -lt 180; $i++) {
    try { Invoke-WebRequest http://localhost:8761 -UseBasicParsing -TimeoutSec 2 | Out-Null; $ok = $true; break } catch { Start-Sleep 1 }
}
if (-not $ok) {
    Write-Host "Eureka ne repond pas. Regarde la fenetre 'EUREKA 8761' pour voir l'erreur." -ForegroundColor Red
    exit 1
}
Write-Host "   Eureka est pret." -ForegroundColor Green

# 2. Les autres services (2 instances pour Meeting et Notification)
Write-Host "2) Microservices"
Start-Micro 'CANDIDAT 8081'     'candidat'     'mvn spring-boot:run'                 8081
Start-Micro 'JOB 8082'          'job'          'mvn spring-boot:run'                 8082
Start-Micro 'MEETING 8083'      'meeting'      'npm.cmd start'                       8083
Start-Micro 'MEETING 8093'      'meeting'      '$env:PORT=8093; npm.cmd start'       8093
Start-Micro 'NOTIFICATION 8084' 'notification' 'python main.py'                      8084
Start-Micro 'NOTIFICATION 8094' 'notification' '$env:PORT=8094; python main.py'      8094

# 3. API Gateway (point d'entree unique)
Write-Host "3) API Gateway"
Start-Micro 'GATEWAY 8085'      'gateway'      'mvn spring-boot:run'                 8085

# 2e instance de Candidat (pour observer le load balancing) : .\start-all.ps1 -lb
if ($args -contains '-lb') {
    Start-Micro 'CANDIDAT 8091' 'candidat'     '$env:SERVER_PORT=8091; mvn spring-boot:run' 8091
}

Write-Host ""
Write-Host "Tout est lance. Attends ~1 minute puis ouvre :" -ForegroundColor Cyan
Write-Host "  Eureka  : http://localhost:8761" -ForegroundColor Cyan
Write-Host "  Swagger : http://localhost:8085/swagger-ui.html" -ForegroundColor Cyan

