<#
.SYNOPSIS
    Exibe ao vivo o HPA, os pods e o consumo de recursos do namespace da aplicacao.

.DESCRIPTION
    Atualiza periodicamente a saida de kubectl (HPA, pods e top pods), permitindo
    acompanhar o escalonamento automatico enquanto scripts/load-test.ps1 gera carga.
    Ideal para demonstracao em tela dividida no video.

.PARAMETER Namespace
    Namespace do Kubernetes. Padrao: oficina

.PARAMETER IntervalSeconds
    Intervalo de atualizacao, em segundos. Padrao: 2

.EXAMPLE
    .\scripts\watch-scaling.ps1

.EXAMPLE
    .\scripts\watch-scaling.ps1 -IntervalSeconds 3
#>
[CmdletBinding()]
param(
    [string]$Namespace = "oficina",
    [int]$IntervalSeconds = 2
)

if (-not (Get-Command kubectl -ErrorAction SilentlyContinue)) {
    Write-Host "kubectl nao encontrado no PATH." -ForegroundColor Red
    exit 1
}

Write-Host "Monitorando escalonamento no namespace '$Namespace' (Ctrl+C para sair)..." -ForegroundColor Cyan
while ($true) {
    Clear-Host
    Write-Host ("== Escalabilidade automatica | {0:HH:mm:ss} | namespace: {1} ==" -f (Get-Date), $Namespace) -ForegroundColor Cyan

    Write-Host "`n-- HorizontalPodAutoscaler --" -ForegroundColor Yellow
    kubectl -n $Namespace get hpa

    Write-Host "`n-- Pods --" -ForegroundColor Yellow
    kubectl -n $Namespace get pods -o wide

    Write-Host "`n-- Uso de recursos (metrics-server) --" -ForegroundColor Yellow
    kubectl -n $Namespace top pods 2>$null
    if ($LASTEXITCODE -ne 0) {
        Write-Host "(metrics-server ainda inicializando ou indisponivel)" -ForegroundColor DarkGray
    }

    Start-Sleep -Seconds $IntervalSeconds
}
