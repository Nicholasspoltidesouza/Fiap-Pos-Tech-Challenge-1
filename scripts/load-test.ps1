<#
.SYNOPSIS
    Gera carga concorrente na API para demonstrar a escalabilidade automatica (HPA).

.DESCRIPTION
    Dispara requisicoes concorrentes em POST /api/auth/login (autenticacao com BCrypt,
    intensiva em CPU). O aumento de uso de CPU faz o HorizontalPodAutoscaler criar novas
    replicas do pod oficina-api. Use em conjunto com scripts/watch-scaling.ps1 para
    visualizar o escalonamento ao vivo (tela dividida no video).

.PARAMETER BaseUrl
    URL base da API. Padrao: http://localhost:30080 (NodePort do cluster kind).

.PARAMETER Email
    E-mail de login. Padrao: admin@oficina.com

.PARAMETER Password
    Senha de login. Padrao: admin123

.PARAMETER Concurrency
    Numero de workers simultaneos. Padrao: 20

.PARAMETER DurationSeconds
    Duracao da geracao de carga, em segundos. Padrao: 120

.EXAMPLE
    .\scripts\load-test.ps1

.EXAMPLE
    .\scripts\load-test.ps1 -Concurrency 40 -DurationSeconds 180
#>
[CmdletBinding()]
param(
    [string]$BaseUrl = "http://localhost:30080",
    [string]$Email = "admin@oficina.com",
    [string]$Password = "admin123",
    [int]$Concurrency = 20,
    [int]$DurationSeconds = 120
)

$ErrorActionPreference = "Stop"
$loginUrl = "$BaseUrl/api/auth/login"
$body = @{ email = $Email; password = $Password } | ConvertTo-Json -Compress

Write-Host "Validando conectividade e credenciais em $loginUrl ..." -ForegroundColor Cyan
try {
    $warm = Invoke-RestMethod -Uri $loginUrl -Method Post -Body $body -ContentType "application/json" -TimeoutSec 15
    if (-not $warm.accessToken) { throw "resposta sem accessToken" }
    Write-Host "OK: autenticacao bem-sucedida." -ForegroundColor Green
}
catch {
    Write-Host "FALHA ao autenticar: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "Verifique se a API esta no ar (NodePort 30080)." -ForegroundColor Yellow
    Write-Host "Alternativa: kubectl -n oficina port-forward svc/oficina-api 30080:80" -ForegroundColor Yellow
    exit 1
}

$shared = [hashtable]::Synchronized(@{ ok = 0; err = 0 })
$deadline = (Get-Date).AddSeconds($DurationSeconds)

$worker = {
    param($url, $json, $shared, $deadline)
    Add-Type -AssemblyName System.Net.Http
    $client = New-Object System.Net.Http.HttpClient
    $client.Timeout = [TimeSpan]::FromSeconds(20)
    while ((Get-Date) -lt $deadline) {
        try {
            $content = New-Object System.Net.Http.StringContent($json, [System.Text.Encoding]::UTF8, "application/json")
            $resp = $client.PostAsync($url, $content).GetAwaiter().GetResult()
            [System.Threading.Monitor]::Enter($shared)
            try {
                if ($resp.IsSuccessStatusCode) { $shared.ok++ } else { $shared.err++ }
            }
            finally { [System.Threading.Monitor]::Exit($shared) }
            $resp.Dispose()
            $content.Dispose()
        }
        catch {
            [System.Threading.Monitor]::Enter($shared)
            try { $shared.err++ } finally { [System.Threading.Monitor]::Exit($shared) }
        }
    }
    $client.Dispose()
}

Write-Host ("Iniciando carga: {0} workers por {1}s (Ctrl+C para abortar)" -f $Concurrency, $DurationSeconds) -ForegroundColor Cyan

$pool = [runspacefactory]::CreateRunspacePool(1, $Concurrency)
$pool.Open()
$runspaces = @()
for ($i = 0; $i -lt $Concurrency; $i++) {
    $ps = [powershell]::Create()
    $ps.RunspacePool = $pool
    [void]$ps.AddScript($worker).AddArgument($loginUrl).AddArgument($body).AddArgument($shared).AddArgument($deadline)
    $runspaces += [pscustomobject]@{ PS = $ps; Handle = $ps.BeginInvoke() }
}

$start = Get-Date
while ((Get-Date) -lt $deadline) {
    Start-Sleep -Seconds 2
    $elapsed = [int]((Get-Date) - $start).TotalSeconds
    $rps = if ($elapsed -gt 0) { [math]::Round($shared.ok / $elapsed, 1) } else { 0 }
    Write-Host ("[{0,3}s] sucesso={1} erro={2} (~{3} req/s)" -f $elapsed, $shared.ok, $shared.err, $rps)
}

foreach ($r in $runspaces) {
    try { $r.PS.EndInvoke($r.Handle) } catch { }
    $r.PS.Dispose()
}
$pool.Close()
$pool.Dispose()

$total = $shared.ok + $shared.err
$avgRps = [math]::Round($shared.ok / [math]::Max($DurationSeconds, 1), 1)
Write-Host ""
Write-Host "===== RESUMO =====" -ForegroundColor Cyan
Write-Host ("Requisicoes totais : {0}" -f $total)
Write-Host ("Sucesso            : {0}" -f $shared.ok)
Write-Host ("Erro               : {0}" -f $shared.err)
Write-Host ("Vazao media        : ~{0} req/s" -f $avgRps)
Write-Host ""
Write-Host "Acompanhe as replicas: kubectl -n oficina get hpa,pods" -ForegroundColor Yellow
