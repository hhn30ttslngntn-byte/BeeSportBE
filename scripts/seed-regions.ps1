param(
    [string]$Server = 'localhost,1433',
    [string]$Database = 'BeeSport',
    [string]$Username = 'sa',
    [string]$Password = '1234',
    [string]$DataUrl = 'https://raw.githubusercontent.com/daohoangson/dvhcvn/master/data/dvhcvn.json'
)

$ErrorActionPreference = 'Stop'

function New-SqlConnection {
    param(
        [string]$Server,
        [string]$Database,
        [string]$Username,
        [string]$Password
    )

    $connectionString = "Server=$Server;Database=$Database;User ID=$Username;Password=$Password;TrustServerCertificate=True;"
    $connection = New-Object System.Data.SqlClient.SqlConnection $connectionString
    $connection.Open()
    return $connection
}

function Invoke-NonQuery {
    param(
        [System.Data.SqlClient.SqlConnection]$Connection,
        [System.Data.SqlClient.SqlTransaction]$Transaction,
        [string]$Sql,
        [hashtable]$Parameters = @{}
    )

    $command = $Connection.CreateCommand()
    $command.Transaction = $Transaction
    $command.CommandText = $Sql

    foreach ($entry in $Parameters.GetEnumerator()) {
        $parameter = $command.Parameters.Add("@$($entry.Key)", [System.Data.SqlDbType]::NVarChar, -1)
        if ($null -eq $entry.Value) {
            $parameter.Value = [DBNull]::Value
        } else {
            $parameter.Value = [string]$entry.Value
        }
    }

    [void]$command.ExecuteNonQuery()
}

Write-Host "Downloading administrative units from $DataUrl ..."
$payload = Invoke-RestMethod -Uri $DataUrl
$provinces = $payload.data

if (-not $provinces -or $provinces.Count -eq 0) {
    throw 'No administrative data received from remote source.'
}

$provinceUpsertSql = @"
IF EXISTS (SELECT 1 FROM tinh WHERE ma_tinh = @ma_tinh)
BEGIN
    UPDATE tinh
    SET ten_tinh = @ten_tinh,
        trang_thai = 1
    WHERE ma_tinh = @ma_tinh;
END
ELSE
BEGIN
    INSERT INTO tinh (ma_tinh, ten_tinh, trang_thai)
    VALUES (@ma_tinh, @ten_tinh, 1);
END
"@

$districtUpsertSql = @"
DECLARE @id_tinh INT;
SELECT @id_tinh = id_tinh FROM tinh WHERE ma_tinh = @ma_tinh;

IF @id_tinh IS NULL
    THROW 50001, N'Province not found while seeding district.', 1;

IF EXISTS (SELECT 1 FROM huyen WHERE ma_huyen = @ma_huyen)
BEGIN
    UPDATE huyen
    SET ten_huyen = @ten_huyen,
        id_tinh = @id_tinh,
        trang_thai = 1
    WHERE ma_huyen = @ma_huyen;
END
ELSE
BEGIN
    INSERT INTO huyen (ma_huyen, id_tinh, ten_huyen, trang_thai)
    VALUES (@ma_huyen, @id_tinh, @ten_huyen, 1);
END
"@

$wardUpsertSql = @"
DECLARE @id_huyen INT;
SELECT @id_huyen = id_huyen FROM huyen WHERE ma_huyen = @ma_huyen;

IF @id_huyen IS NULL
    THROW 50002, N'District not found while seeding ward.', 1;

IF EXISTS (SELECT 1 FROM xa WHERE ma_xa = @ma_xa)
BEGIN
    UPDATE xa
    SET ten_xa = @ten_xa,
        id_huyen = @id_huyen,
        trang_thai = 1
    WHERE ma_xa = @ma_xa;
END
ELSE
BEGIN
    INSERT INTO xa (ma_xa, id_huyen, ten_xa, trang_thai)
    VALUES (@ma_xa, @id_huyen, @ten_xa, 1);
END
"@

$connection = New-SqlConnection -Server $Server -Database $Database -Username $Username -Password $Password
$transaction = $connection.BeginTransaction()

$provinceCount = 0
$districtCount = 0
$wardCount = 0

try {
    foreach ($province in $provinces) {
        Invoke-NonQuery -Connection $connection -Transaction $transaction -Sql $provinceUpsertSql -Parameters @{
            ma_tinh = $province.level1_id
            ten_tinh = $province.name
        }
        $provinceCount++

        foreach ($district in $province.level2s) {
            Invoke-NonQuery -Connection $connection -Transaction $transaction -Sql $districtUpsertSql -Parameters @{
                ma_tinh = $province.level1_id
                ma_huyen = $district.level2_id
                ten_huyen = $district.name
            }
            $districtCount++

            foreach ($ward in $district.level3s) {
                Invoke-NonQuery -Connection $connection -Transaction $transaction -Sql $wardUpsertSql -Parameters @{
                    ma_huyen = $district.level2_id
                    ma_xa = $ward.level3_id
                    ten_xa = $ward.name
                }
                $wardCount++
            }
        }
    }

    $transaction.Commit()
    Write-Host "Seed completed: $provinceCount provinces, $districtCount districts, $wardCount wards."
}
catch {
    try {
        $transaction.Rollback()
    }
    catch {
    }

    throw
}
finally {
    $connection.Close()
    $connection.Dispose()
}
