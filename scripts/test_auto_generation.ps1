# ============================================================================
# SCRIPT DE PRUEBA: Auto-Generación de Asientos
# ============================================================================
# Versión: 1.0
# Fecha: 20 de noviembre de 2025
# Descripción: Prueba completa del sistema de auto-generación de asientos
# ============================================================================

param(
    [Parameter(Mandatory=$false)]
    [ValidateSet("all", "batch", "new", "verify")]
    [string]$Action = "all",
    
    [Parameter(Mandatory=$false)]
    [int]$ShowtimeId = 0
)

# Colores para output
$ColorSuccess = "Green"
$ColorError = "Red"
$ColorInfo = "Cyan"
$ColorWarning = "Yellow"

# Configuración
$BaseUrl = "http://localhost:8080/api"
$ContentType = "application/json"

# ============================================================================
# FUNCIONES AUXILIARES
# ============================================================================

function Write-Header {
    param([string]$Text)
    Write-Host ""
    Write-Host "============================================================================" -ForegroundColor $ColorInfo
    Write-Host " $Text" -ForegroundColor $ColorInfo
    Write-Host "============================================================================" -ForegroundColor $ColorInfo
    Write-Host ""
}

function Write-Step {
    param([string]$Text)
    Write-Host "🔹 $Text" -ForegroundColor $ColorInfo
}

function Write-Success {
    param([string]$Text)
    Write-Host "✅ $Text" -ForegroundColor $ColorSuccess
}

function Write-Error {
    param([string]$Text)
    Write-Host "❌ $Text" -ForegroundColor $ColorError
}

function Write-Warning {
    param([string]$Text)
    Write-Host "⚠️  $Text" -ForegroundColor $ColorWarning
}

function Test-BackendHealth {
    Write-Step "Verificando estado del backend..."
    try {
        $response = Invoke-WebRequest -Uri "$BaseUrl/../actuator/health" -UseBasicParsing -TimeoutSec 5
        if ($response.StatusCode -eq 200) {
            Write-Success "Backend está corriendo correctamente"
            return $true
        }
    } catch {
        Write-Error "Backend no está disponible. Asegúrate de que esté corriendo en puerto 8080"
        Write-Host "Inicia el backend con: .\mvnw.cmd spring-boot:run" -ForegroundColor $ColorWarning
        return $false
    }
}

# ============================================================================
# OPCIÓN 1: GENERAR ASIENTOS PARA TODOS LOS SHOWTIMES EXISTENTES
# ============================================================================

function Test-BatchGeneration {
    Write-Header "PRUEBA 1: Generación en Lote (Showtimes Existentes)"
    
    Write-Step "Llamando al endpoint de generación en lote..."
    Write-Host "POST $BaseUrl/showtimes/seats/generate-all" -ForegroundColor Gray
    
    try {
        $response = Invoke-WebRequest `
            -Uri "$BaseUrl/showtimes/seats/generate-all" `
            -Method POST `
            -UseBasicParsing `
            -TimeoutSec 60
        
        $result = $response.Content
        Write-Success "Respuesta del servidor:"
        Write-Host "   $result" -ForegroundColor White
        
        # Extraer número de funciones
        if ($result -match '(\d+)') {
            $count = $matches[1]
            if ([int]$count -gt 0) {
                Write-Success "Se generaron asientos para $count funciones"
                Write-Host ""
                Write-Host "📝 Para verificar en MySQL, ejecuta:" -ForegroundColor $ColorWarning
                Write-Host "   SELECT s.id, COUNT(seats.id) as total_asientos" -ForegroundColor White
                Write-Host "   FROM showtimes s" -ForegroundColor White
                Write-Host "   LEFT JOIN seats ON seats.showtime_id = s.id" -ForegroundColor White
                Write-Host "   GROUP BY s.id;" -ForegroundColor White
                Write-Host ""
                Write-Host "   ✅ Todas las funciones deberían tener 330 asientos" -ForegroundColor $ColorSuccess
            } else {
                Write-Warning "No había funciones sin asientos (0 funciones procesadas)"
                Write-Host "Esto es normal si ya habías ejecutado la generación antes" -ForegroundColor Gray
            }
        }
        
        return $true
    } catch {
        Write-Error "Error al generar asientos en lote"
        Write-Host "Detalles: $($_.Exception.Message)" -ForegroundColor $ColorError
        return $false
    }
}

# ============================================================================
# OPCIÓN 2: CREAR UN SHOWTIME NUEVO Y VERIFICAR AUTO-GENERACIÓN
# ============================================================================

function Test-AutoGeneration {
    Write-Header "PRUEBA 2: Auto-Generación al Crear Showtime Nuevo"
    
    Write-Step "Creando un nuevo showtime..."
    
    # Obtener fecha futura (7 días desde hoy)
    $futureDate = (Get-Date).AddDays(7).ToString("yyyy-MM-dd")
    
    $newShowtime = @{
        movieId = 1
        theaterId = 1
        date = $futureDate
        time = "20:30:00"
        format = "_2D"
        availableSeats = 330
    } | ConvertTo-Json
    
    Write-Host "Datos del showtime:" -ForegroundColor Gray
    Write-Host $newShowtime -ForegroundColor Gray
    Write-Host ""
    
    try {
        $response = Invoke-WebRequest `
            -Uri "$BaseUrl/showtimes" `
            -Method POST `
            -Body $newShowtime `
            -ContentType $ContentType `
            -UseBasicParsing `
            -TimeoutSec 30
        
        $showtime = $response.Content | ConvertFrom-Json
        $showtimeId = $showtime.id
        
        Write-Success "Showtime creado exitosamente"
        Write-Host "   ID: $showtimeId" -ForegroundColor White
        Write-Host "   Fecha: $($showtime.date)" -ForegroundColor White
        Write-Host "   Hora: $($showtime.time)" -ForegroundColor White
        Write-Host ""
        
        # Esperar a que termine la generación de asientos
        Write-Step "Esperando generación automática de asientos..."
        Start-Sleep -Seconds 3
        
        # Verificar si se generaron los asientos
        Write-Step "Verificando asientos generados..."
        Write-Host ""
        Write-Host "📝 Para verificar en MySQL, ejecuta:" -ForegroundColor $ColorWarning
        Write-Host "   SELECT COUNT(*) as total_asientos" -ForegroundColor White
        Write-Host "   FROM seats" -ForegroundColor White
        Write-Host "   WHERE showtime_id = $showtimeId;" -ForegroundColor White
        Write-Host ""
        Write-Host "   ✅ Deberías ver: 330 asientos" -ForegroundColor $ColorSuccess
        Write-Host ""
        
        # Mostrar query para ver distribución por fila
        Write-Host "📝 Para ver la distribución por fila:" -ForegroundColor $ColorWarning
        Write-Host "   SELECT SUBSTRING(seat_identifier, 1, 1) as fila," -ForegroundColor White
        Write-Host "          COUNT(*) as cantidad" -ForegroundColor White
        Write-Host "   FROM seats" -ForegroundColor White
        Write-Host "   WHERE showtime_id = $showtimeId" -ForegroundColor White
        Write-Host "   GROUP BY fila" -ForegroundColor White
        Write-Host "   ORDER BY fila;" -ForegroundColor White
        Write-Host ""
        Write-Host "   ✅ Filas A-C: 20 asientos | Filas D-R: 18 asientos" -ForegroundColor $ColorSuccess
        
        return @{
            Success = $true
            ShowtimeId = $showtimeId
        }
    } catch {
        Write-Error "Error al crear el showtime"
        Write-Host "Detalles: $($_.Exception.Message)" -ForegroundColor $ColorError
        
        # Mostrar más detalles si están disponibles
        if ($_.Exception.Response) {
            $reader = [System.IO.StreamReader]::new($_.Exception.Response.GetResponseStream())
            $errorBody = $reader.ReadToEnd()
            Write-Host "Respuesta del servidor:" -ForegroundColor $ColorError
            Write-Host $errorBody -ForegroundColor $ColorError
        }
        
        return @{
            Success = $false
            ShowtimeId = 0
        }
    }
}

# ============================================================================
# OPCIÓN 3: VERIFICAR SHOWTIME ESPECÍFICO
# ============================================================================

function Test-VerifyShowtime {
    param([int]$Id)
    
    Write-Header "VERIFICACIÓN: Showtime ID $Id"
    
    if ($Id -le 0) {
        Write-Error "Debes proporcionar un ID de showtime válido"
        Write-Host "Uso: .\test_auto_generation.ps1 -Action verify -ShowtimeId 123" -ForegroundColor $ColorWarning
        return $false
    }
    
    Write-Host ""
    Write-Host "📝 Ejecuta estos queries en MySQL para verificar:" -ForegroundColor $ColorInfo
    Write-Host ""
    
    # Query 1: Total de asientos
    Write-Host "1️⃣  TOTAL DE ASIENTOS (debe ser 330):" -ForegroundColor $ColorWarning
    Write-Host "   SELECT COUNT(*) as total_asientos" -ForegroundColor White
    Write-Host "   FROM seats" -ForegroundColor White
    Write-Host "   WHERE showtime_id = $Id;" -ForegroundColor White
    Write-Host ""
    
    # Query 2: Distribución por fila
    Write-Host "2️⃣  DISTRIBUCIÓN POR FILA:" -ForegroundColor $ColorWarning
    Write-Host "   SELECT SUBSTRING(seat_identifier, 1, 1) as fila," -ForegroundColor White
    Write-Host "          COUNT(*) as cantidad" -ForegroundColor White
    Write-Host "   FROM seats" -ForegroundColor White
    Write-Host "   WHERE showtime_id = $Id" -ForegroundColor White
    Write-Host "   GROUP BY fila" -ForegroundColor White
    Write-Host "   ORDER BY fila;" -ForegroundColor White
    Write-Host ""
    Write-Host "   ✅ A-C: 20 asientos | D-R: 18 asientos" -ForegroundColor $ColorSuccess
    Write-Host ""
    
    # Query 3: Verificar pasillo
    Write-Host "3️⃣  VERIFICAR PASILLO CENTRAL (debe ser 0):" -ForegroundColor $ColorWarning
    Write-Host "   SELECT COUNT(*) as asientos_en_pasillo" -ForegroundColor White
    Write-Host "   FROM seats" -ForegroundColor White
    Write-Host "   WHERE showtime_id = $Id" -ForegroundColor White
    Write-Host "     AND col_position IN (14, 15);" -ForegroundColor White
    Write-Host ""
    
    # Query 4: Ver algunos asientos de ejemplo
    Write-Host "4️⃣  VER ASIENTOS DE FILA A:" -ForegroundColor $ColorWarning
    Write-Host "   SELECT seat_identifier, row_position, col_position, status" -ForegroundColor White
    Write-Host "   FROM seats" -ForegroundColor White
    Write-Host "   WHERE showtime_id = $Id" -ForegroundColor White
    Write-Host "     AND seat_identifier LIKE 'A%'" -ForegroundColor White
    Write-Host "   ORDER BY col_position;" -ForegroundColor White
    Write-Host ""
    
    return $true
}

# ============================================================================
# EJECUCIÓN PRINCIPAL
# ============================================================================

Write-Header "PRUEBA DE AUTO-GENERACIÓN DE ASIENTOS"

# Verificar que el backend esté corriendo
if (-not (Test-BackendHealth)) {
    Write-Host ""
    Write-Host "👉 Inicia el backend primero:" -ForegroundColor $ColorWarning
    Write-Host "   cd C:\Github\hdd-backend" -ForegroundColor White
    Write-Host "   .\mvnw.cmd spring-boot:run" -ForegroundColor White
    Write-Host ""
    exit 1
}

# Ejecutar según la acción solicitada
switch ($Action) {
    "batch" {
        Test-BatchGeneration
    }
    "new" {
        Test-AutoGeneration
    }
    "verify" {
        Test-VerifyShowtime -Id $ShowtimeId
    }
    "all" {
        # Ejecutar todas las pruebas
        Write-Host "🎯 Ejecutando TODAS las pruebas..." -ForegroundColor $ColorInfo
        Write-Host ""
        
        # Prueba 1: Generación en lote
        $batch = Test-BatchGeneration
        Start-Sleep -Seconds 2
        
        # Prueba 2: Auto-generación
        $auto = Test-AutoGeneration
        
        # Resumen final
        Write-Header "RESUMEN DE PRUEBAS"
        
        if ($batch) {
            Write-Success "Generación en lote: OK"
        } else {
            Write-Error "Generación en lote: FALLIDA"
        }
        
        if ($auto.Success) {
            Write-Success "Auto-generación: OK (Showtime ID: $($auto.ShowtimeId))"
        } else {
            Write-Error "Auto-generación: FALLIDA"
        }
        
        Write-Host ""
        Write-Host "📊 Para verificar resultados completos, ejecuta:" -ForegroundColor $ColorInfo
        Write-Host "   .\test_auto_generation.ps1 -Action verify -ShowtimeId $($auto.ShowtimeId)" -ForegroundColor White
        Write-Host ""
    }
}

Write-Host ""
Write-Host "✅ Script finalizado" -ForegroundColor $ColorSuccess
Write-Host ""
