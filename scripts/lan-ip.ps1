$nics = [System.Net.NetworkInformation.NetworkInterface]::GetAllNetworkInterfaces() |
    Where-Object {
        $_.OperationalStatus -eq [System.Net.NetworkInformation.OperationalStatus]::Up -and
        $_.NetworkInterfaceType -ne [System.Net.NetworkInformation.NetworkInterfaceType]::Loopback -and
        $_.NetworkInterfaceType -ne [System.Net.NetworkInformation.NetworkInterfaceType]::Tunnel
    }

foreach ($nic in $nics) {
    $properties = $nic.GetIPProperties()
    if ($properties.GatewayAddresses.Count -eq 0) {
        continue
    }
    foreach ($address in $properties.UnicastAddresses) {
        $ip = $address.Address
        if ($ip.AddressFamily -eq [System.Net.Sockets.AddressFamily]::InterNetwork -and
            $ip.ToString() -notlike '127.*' -and
            $ip.ToString() -notlike '169.254.*') {
            Write-Output $ip.ToString()
            exit 0
        }
    }
}
